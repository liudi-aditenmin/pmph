package com.bc.pmpheep.back.service;

import com.bc.pmpheep.back.po.Product;
import com.bc.pmpheep.back.vo.ProductVO;

public interface ProductService {

    /**
     * 查询或创建此类型的临床决策产品
     * @param product_type 类型 1.人卫临床助手 2.人卫用药助手 3.人卫中医助手
     * @param sessionId
     * @return
     */
    public ProductVO getProductByType(Long product_type, String sessionId);



    /**
     * 更新附件下载次数
     * @param id
     * @return
     */
    int updateProductAttachmenDownLoadCountsByAttachment(String id);
}