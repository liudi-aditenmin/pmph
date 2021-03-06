package com.bc.pmpheep.back.service;

import com.bc.pmpheep.back.dao.ProductTypeDao;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.po.PmphUser;
import com.bc.pmpheep.back.po.Product;
import com.bc.pmpheep.back.util.ObjectUtil;
import com.bc.pmpheep.back.util.SessionUtil;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.back.vo.*;
import com.bc.pmpheep.controller.bean.ResponseBean;
import com.bc.pmpheep.service.exception.CheckedExceptionBusiness;
import com.bc.pmpheep.service.exception.CheckedExceptionResult;
import com.bc.pmpheep.service.exception.CheckedServiceException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

    @Autowired
    ProductTypeDao productTypeDao;

    @Override
    public PageResult getTypeList(PageParameter<ProductType> pageParameter, String sessionId) {

        PmphUser pmphUser = SessionUtil.getPmphUserBySessionId(sessionId);
        if (ObjectUtil.isNull(pmphUser)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.NULL_PARAM,
                    "用户为空");
        }

        PageResult pageResult = new PageResult();

        int count = 0;
        List<ProductType> list = new ArrayList<>();
        int typeType = pageParameter.getParameter().getTypeType();
        if (typeType==1){ //学科分类
            count = productTypeDao.getSubjectTypeCount(pageParameter);
            if(count>0){
                list = productTypeDao.getSubjectTypeList(pageParameter);
            }
        }else if(typeType==2){ //内容分类
            count = productTypeDao.getLeafContentTypeCount(pageParameter);
            if(count>0){
                list = productTypeDao.getLeafContentTypeList(pageParameter);
            }
        }else if(typeType==3){ //专业分类
            count = productTypeDao.getProfessionTypeCount(pageParameter);
            if(count>0){
                list = productTypeDao.getProfessionTypeList(pageParameter);
            }
        }else{
            //TODO 如果后续有新增其他分类...
        }

        pageResult.setTotal(count);
        pageResult.setRows(list);
        pageResult.setPageNumber(pageParameter.getPageNumber());
        pageResult.setPageSize(pageParameter.getPageSize());

        return pageResult;
    }

    @Override
    public ResponseBean deleteTypeById(ProductType productType,String sessionId) {
        PmphUser pmphUser = SessionUtil.getPmphUserBySessionId(sessionId);
        if (ObjectUtil.isNull(pmphUser)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.NULL_PARAM,
                    "用户为空");
        }
        ResponseBean responseBean = new ResponseBean();
        int typeType = productType.getTypeType();
        int count = 0;
        int experCount=0;
        if (typeType==1){ //学科分类
            experCount = productTypeDao.getSubjectTypeExpertationCount(productType.getId());
            if(experCount>0){
                throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.ILLEGAL_PARAM,
                        "该分类已被临床决策申报引用，无法删除！");
            }
            count = productTypeDao.deleteSubjectTypeById(productType.getId());
        }else if(typeType==2){ //内容分类
            experCount = productTypeDao.getContentTypeExpertationCount(productType.getId());
            if(experCount>0){
                throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.ILLEGAL_PARAM,
                        "该分类已被临床决策申报引用，无法删除！");
            }
            count = productTypeDao.deleteLeafContentTypeById(productType.getId());
            //productTypeDao.refreshLeafOfContentType();
        }else if(typeType==3){ //专业分类
            experCount = productTypeDao.getProfessionTypeExpertationCount(productType.getId());
            if(experCount>0){
                throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.ILLEGAL_PARAM,
                        "该分类已被临床决策申报引用，无法删除！");
            }
            count = productTypeDao.deleteProfessionTypeById(productType.getId());
            //productTypeDao.refreshLeafOfContentType();
        }else{
            //TODO 如果后续有新增其他分类...
        }

        return responseBean;
    }

    @Override
    public List<ProductType> importExcel(MultipartFile file, int typeType,Long ptype) {
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Workbook workbook = null;
        InputStream in = null;
        try {
            in = file.getInputStream();
        } catch (FileNotFoundException e) {
            throw new CheckedServiceException(CheckedExceptionBusiness.EXCEL,
                    CheckedExceptionResult.NULL_PARAM, "获取上传的文件失败");
        } catch (IOException e){
            throw new CheckedServiceException(CheckedExceptionBusiness.EXCEL,
                    CheckedExceptionResult.ILLEGAL_PARAM, "读取文件失败");
        }
        try {
            if (".xls".equals(fileType)){
                workbook = new HSSFWorkbook(in);
            } else if (".xlsx".equals(fileType)){
                workbook = new XSSFWorkbook(in);
            } else {
                throw new CheckedServiceException(CheckedExceptionBusiness.EXCEL,
                        CheckedExceptionResult.ILLEGAL_PARAM, "读取的不是Excel文件");
            }
        } catch (IOException e){
            throw new CheckedServiceException(CheckedExceptionBusiness.EXCEL,
                    CheckedExceptionResult.ILLEGAL_PARAM, "文件读取失败");
        } catch (OfficeXmlFileException e){
            throw new CheckedServiceException(CheckedExceptionBusiness.EXCEL,
                    CheckedExceptionResult.ILLEGAL_PARAM, "此文档不是对应的.xls或.xlsx的Excel文档，请修改为正确的后缀名再进行上传");
        }

        //以fullNamePath为key
        Map<String,ProductType> productTypeMap = new HashMap<String,ProductType>();

        for (int numSheet =0 ; numSheet < workbook.getNumberOfSheets(); numSheet++ ){
            Sheet sheet = workbook.getSheetAt(numSheet);
            if (null == sheet){
                continue;
            }
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++){
                Row row = sheet.getRow(rowNum);
                if (null == row){
                    break;
                }
                for(int cellNum = 0;cellNum < row.getLastCellNum();cellNum++){
                    //给每个单元格创建或关联实体类
                    ProductType cellProductType = new ProductType(ptype);
                   // Long product_id = productTypeDao.getProductIdByProductType(ptype);
                    //cellProductType.setProduct_id(product_id);
                    cellProductType.setTypeType(typeType);
                    Cell cell = row.getCell(cellNum);
                    Cell nextCell = row.getCell(cellNum + 1);
                    String cell_type_name = StringUtil.getCellValue(cell);
                    String next_cell_type_name = StringUtil.getCellValue(nextCell);
                    if (StringUtil.isEmpty(cell_type_name)){
                        /*throw new CheckedServiceException(CheckedExceptionBusiness.EXCEL,
                                CheckedExceptionResult.NULL_PARAM, "Excel文件里序号为" + rowNum + "的分类名称为空");*/
                        break;
                    }else{
                        cellProductType.setType_name(cell_type_name);
                    }

                    if(StringUtil.isEmpty(next_cell_type_name)){
                        cellProductType.setExcel_row_num((long)rowNum);
                    }

                    String parent_name_path = "";
                    for(int ic = 0;ic<cellNum;ic++){
                        parent_name_path += "/"+ StringUtil.getCellValue(row.getCell(ic)).trim();
                    }
                    parent_name_path = parent_name_path.replaceAll("^/","");
                    String full_name_path = parent_name_path+"/"+cell_type_name;
                    full_name_path = full_name_path.replaceAll("^/","");

                    //若full_name_path不重复，则说明此实体类未添加过
                    if(productTypeMap.get(full_name_path)==null){
                        cellProductType.setFullNamePath(full_name_path);
                        productTypeMap.put(full_name_path,cellProductType);
                        //每行都从第0个cell开始遍历cell，故到当前cell时，其父cell一定已经在map中,除了顶层
                        //加入其父cell的子集合
                        ProductType parent_productType = productTypeMap.get(parent_name_path);
                        if(parent_productType!=null){
                            parent_productType.getChildType().add(cellProductType);
                        }
                    }else if(StringUtil.isEmpty(next_cell_type_name)){
                        productTypeMap.get(full_name_path).setExcel_row_num((long)rowNum);
                    }

                }
            }
        }

        Pattern pattern = Pattern.compile("/");

        List<ProductType> list = new ArrayList<ProductType>();

        productTypeMap.keySet();
        for (String key:productTypeMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if(!matcher.find()){ //全名路径里找不到/ 说明是顶级分类
                list.add(productTypeMap.get(key));
            }
        }
        return list;
    }

    @Override
    public ResponseBean insertProductTypeTree(List<ProductType> list, int typeType) {
        ResponseBean responseBean = new ResponseBean();
        try{
            if (typeType==1){ //学科分类
                int count = productTypeDao.insertSubjectTypeBatch(list);
            }else if(typeType==3){ //专业分类
                int count = productTypeDao.insertProfessionTypeBatch(list);
            }else if(typeType==2){ //内容分类
                List<ProductType> tempChildList = new ArrayList<ProductType>();
                /*for (ProductType productType: list) {
                    productType.setParent_id(0L);
                    int count = productTypeDao.insertContentType(productType);
                    if(productType.getId()==null){ //不插入的情况下 将无id返回到productType，说明数据库里已有，要查询
                        Long id = productTypeDao.queryContentTypeIdByFullNamePath(productType);
                        productType.setId(id);
                    }
                    productTypeDao.callUpdateProductTypeDetail(productType.getId());
                    Long parent_id = productType.getId();
                    List<ProductType> childType = productType.getChildType();
                    for (ProductType cp: childType) {
                        cp.setParent_id(parent_id);
                        tempChildList.add(cp);
                    }
                }

                List<ProductType> nowLevelList = tempChildList;*/
                for (ProductType productType: list) {
                    productType.setParent_id(0L);
                }
                List<ProductType> nowLevelList = list;
                while(nowLevelList!=null && nowLevelList.size()>0){
                    tempChildList = new ArrayList<>();
                    for (ProductType productType: nowLevelList) {
                        int count = productTypeDao.insertContentType(productType);
                        if(productType.getId()==null){ //不插入的情况下 将无id返回到productType，说明数据库里已有，要查询
                            Long id = productTypeDao.queryContentTypeIdByFullNamePath(productType);
                            productType.setId(id);
                        }
                        productTypeDao.callUpdateProductTypeDetail(productType.getId());
                        Long parent_id = productType.getId();
                        List<ProductType> childType = productType.getChildType();
                        for (ProductType cp: childType) {
                            cp.setParent_id(parent_id);
                            tempChildList.add(cp);
                        }
                    }
                    nowLevelList = tempChildList;
                }


            }else{
                //TODO 如果后续有新增其他分类...
            }
            responseBean.setCode(ResponseBean.SUCCESS);
            responseBean.setMsg("导入成功");
        }catch (Exception e){
            responseBean.setCode(ResponseBean.UNCHECKED_ERROR);
            responseBean.setMsg("导入数据库失败！");
        }


        return responseBean;
    }

    @Override
    public Map<String,Object> getBtnStatus(Long productType) throws CheckedServiceException{

        return productTypeDao.getBtnStatus(productType);
    }

    @Override
    public ProductType getContentListTree(Long parentId,short productTypeParam) {
        ProductType productTypeVo = new ProductType();
        if(parentId<=0){ // 初始化，数据库并没有id为0 父级为-1的层级 此处生成此对象
            productTypeVo.setId(0L);
            productTypeVo.setParent_id(0L);
            productTypeVo.setlsLeaf(false);
            productTypeVo.setType_name("内容分类目录");
            List<ProductType> pm = productTypeDao.getContentListTree(0L,productTypeParam,"");
            productTypeVo.setChildType(pm);
        }else{ //若有传入 则parentId为当前点击节点的id
            productTypeVo =productTypeDao.getProductConetentTypeVoById(parentId,productTypeParam);
            List<ProductType> cm = productTypeDao.getContentListTree(parentId,productTypeParam,"");
            productTypeVo.setChildType(cm);
        }
        return productTypeVo;
    }

    /**
     * 根据id 删除内容分类
     * @param id
     * @return
     */
    @Override
    public Integer deleteProductContentTypeById(Long id) {
        if (ObjectUtil.isNull(id)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.NULL_PARAM,
                    "主键为空");
        }
        int experCount=0;
        experCount = productTypeDao.getContentTypeExpertationCount(id);
        //分类被引用 不能删除
        if(experCount>0){
            throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.ILLEGAL_PARAM,
                    "该分类已被临床决策申报引用，无法删除！");
        }
        ProductType productTypeVo =productTypeDao.getProductConetentTypeVoById(id,null);
        if(!productTypeVo.getIsLeaf()){
            throw new CheckedServiceException(CheckedExceptionBusiness.CLINICAL_DECISION, CheckedExceptionResult.ILLEGAL_PARAM,
                    "该分类下还有子集分类，无法删除！");
        }
        return productTypeDao.deleteProductContentTypeById(id);
    }

    @Override
    public ProductType listProductContentType(Long parentId,short productTypeParam, String type_name) {
        ProductType productTypeVo  = new ProductType();
        if(parentId<=0||parentId == null){
            productTypeVo.setId(0L);
            productTypeVo.setParent_id(0L);
            productTypeVo.setlsLeaf(false);
            productTypeVo.setType_name("内容分类目录");
        }

        recursionProductTypeVo(productTypeVo,new ArrayList<Long>(16),productTypeParam,type_name);
        return productTypeVo;
    }


    /**
     *
     * 功能描述：使用递归的方法将部门转化为树状图 使用示范：
     *
     * @param productTypeVo
     *            ids (为后面删除做准备) 父级部门
     */
    private void recursionProductTypeVo(ProductType productTypeVo, List<Long> ids,short productTypeParam,String type_name) {
        List<ProductType> list = productTypeDao.getContentListTree(productTypeVo.getId(),productTypeParam,type_name);
        if (null != list && list.size() > 0) {
            productTypeVo.setChildType(list);
            productTypeVo.setlsLeaf(false);
            for (ProductType _productTypeVo : list) {
                ids.add(_productTypeVo.getId());
                recursionProductTypeVo(_productTypeVo, ids,productTypeParam,type_name);
            }
        }
    }

}
