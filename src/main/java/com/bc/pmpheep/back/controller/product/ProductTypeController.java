package com.bc.pmpheep.back.controller.product;

import com.bc.pmpheep.annotation.LogDetail;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.service.ProductTypeService;
import com.bc.pmpheep.back.sessioncontext.SessionContext;
import com.bc.pmpheep.back.util.CookiesUtil;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.back.vo.MaterialListVO;
import com.bc.pmpheep.back.vo.OrgVO;
import com.bc.pmpheep.back.vo.ProductType;
import com.bc.pmpheep.controller.bean.ResponseBean;
import com.bc.pmpheep.service.exception.CheckedExceptionBusiness;
import com.bc.pmpheep.service.exception.CheckedExceptionResult;
import com.bc.pmpheep.service.exception.CheckedServiceException;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 临床决策产品分类 控制层
 */
@Controller
@RequestMapping("productType")
public class ProductTypeController {

    // 当前业务类型
    private static final String BUSSINESS_TYPE = "临床决策产品分类";

    @Autowired
    ProductTypeService productTypeService;

    /**
     * 分页查询 产品分类
     * @param request
     * @param pathType subject：学科分类；content：内容分类 profession 专业分类
     * @param type_name 分类名称的模糊查询条件
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @RequestMapping("/{ptype}/{ttype}/list")
    @ResponseBody
    public ResponseBean getSubjectList(HttpServletRequest request
                                    , @PathVariable("ptype")Long ptype
                                    , @PathVariable("ttype")String pathType
                                    , String type_name
                                    , Integer pageSize
                                    , Integer pageNumber){
        ResponseBean responseBean = new ResponseBean();
        String sessionId = CookiesUtil.getSessionId(request);
        PageParameter<ProductType> pageParameter = new PageParameter<>(pageNumber, pageSize);
        ProductType productType = new ProductType(ptype);
        pageParameter.setParameter(productType);
        productType.setType_name(type_name!=null?type_name.trim():null);

        if ("subject".equals(pathType)){
            productType.setTypeType(1);
        }else if("content".equals(pathType)){
            productType.setTypeType(2);
        }else if("profession".equals(pathType)){
            productType.setTypeType(3);
        }else{
            responseBean.setCode(ResponseBean.WRONG_REQ_PARA);
            return responseBean;
        }

        PageResult pageResult = productTypeService.getTypeList(pageParameter,sessionId);

        responseBean.setData(pageResult);

        return responseBean;
    }


    @RequestMapping("/clic/tree")
    @LogDetail(businessType = BUSSINESS_TYPE, logRemark = "根据父级id获取下一级所有内容分类")
    @ResponseBody
    public ResponseBean getContentListTree(@RequestParam(value = "parentId",defaultValue = "0") Long parentId,@RequestParam short productType){
         return new ResponseBean(productTypeService.getContentListTree(parentId,productType));

    }

    @RequestMapping("/clic/notLazytree")
    @LogDetail(businessType = BUSSINESS_TYPE, logRemark = "根据父级id获取所有内容分类")
    @ResponseBody
    public ResponseBean getnotLazyContentListTree(@RequestParam(value = "parentId",defaultValue = "0") Long parentId,@RequestParam short productType,String type_name){
        return new ResponseBean(productTypeService.listProductContentType(parentId,productType,"")); //模糊查询分类采用vue的js过滤 一次行将所有分类全部查出

    }

    /**
     * 删除该分类以及下属分类
     *
     * @author Mryang
     * @createDate 2017年9月26日 下午3:38:14
     * @param id
     * @return
     */
    @RequestMapping(value = "/clic/delete", method = RequestMethod.DELETE)
    @LogDetail(businessType = BUSSINESS_TYPE, logRemark = "删除分类")
    @ResponseBody
    public ResponseBean delete(Long id) {
        return new ResponseBean(productTypeService.deleteProductContentTypeById(id));
    }


    @RequestMapping("/{type}/delete")
    @ResponseBody
    public ResponseBean deleteType(HttpServletRequest request
            , @PathVariable("type")String pathType
            , @RequestParam(value = "id",required = true)Long id){

        ResponseBean responseBean = new ResponseBean();
        String sessionId = CookiesUtil.getSessionId(request);
        ProductType productType = new ProductType();
        productType.setId(id);

        if ("subject".equals(pathType)){
            productType.setTypeType(1);
        }else if("content".equals(pathType)){
            productType.setTypeType(2);
        }else if("profession".equals(pathType)){
            productType.setTypeType(3);
        }else{
            responseBean.setCode(ResponseBean.WRONG_REQ_PARA);
            responseBean.setMsg("未知的分类: "+pathType);
            return responseBean;
        }

        responseBean = productTypeService.deleteTypeById(productType,sessionId);

        return responseBean;
    }

    @ResponseBody
    @LogDetail(businessType = BUSSINESS_TYPE, logRemark = "临床决策产品分类导入Excel文件")
    @RequestMapping(value = "/{ptype}/{type}/importExcel", method = RequestMethod.POST)
    public ResponseBean importExcel(@RequestParam(name = "file")MultipartFile file
            , @PathVariable("ptype")Long ptype
            , @PathVariable("type")String pathType, HttpServletRequest request){
        ResponseBean responseBean = null;
        Map<String, Object> map = new HashedMap();
        String sessionId = CookiesUtil.getSessionId(request);
        if (StringUtil.isEmpty(sessionId)){
            throw new CheckedServiceException(CheckedExceptionBusiness.SESSION,
                    CheckedExceptionResult.NULL_PARAM, "用户登陆超时，请重新登陆再试");
        }
        HttpSession session = SessionContext.getSession(sessionId);
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        int typeType= 1;
        if ("subject".equals(pathType)){
            typeType =1 ;
        }else if("content".equals(pathType)){
            typeType = 2 ;
        }else if("profession".equals(pathType)){
            typeType = 3 ;
        }
        try {
            List<ProductType> list = productTypeService.importExcel(file,typeType,ptype);
            map.put("uuid", uuid);
            //如果需要加确认后再存入数据库，这个list可以传到前台显示
            map.put("list", list);
            session.setAttribute(uuid, list);

            //现直接插入此list
            responseBean = productTypeService.insertProductTypeTree(list,typeType);
            responseBean.setData(map);
        } catch (CheckedServiceException e) {
            return new ResponseBean(e);
        }
        return responseBean;
    }




    @ResponseBody
    @LogDetail(businessType = BUSSINESS_TYPE, logRemark = "获取产品状态")
    @RequestMapping(value = "/getBtnStatus", method = RequestMethod.GET)

    public ResponseBean getBtnStatus( Long productType,HttpServletRequest request){
        return  new ResponseBean(this.productTypeService.getBtnStatus(productType));
    }





}
