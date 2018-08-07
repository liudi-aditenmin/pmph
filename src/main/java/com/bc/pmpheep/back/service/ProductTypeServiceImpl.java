package com.bc.pmpheep.back.service;

import com.bc.pmpheep.back.dao.ProductTypeDao;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.vo.ProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

    @Autowired
    ProductTypeDao productTypeDao;

    @Override
    public PageResult getTypeList(PageParameter<ProductType> pageParameter, String sessionId) {

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
        }else{
            //TODO 如果后续有新增其他分类...
        }

        pageResult.setTotal(count);
        pageResult.setRows(list);

        return pageResult;
    }
}
