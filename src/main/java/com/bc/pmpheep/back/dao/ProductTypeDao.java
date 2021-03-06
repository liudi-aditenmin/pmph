package com.bc.pmpheep.back.dao;

import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.vo.ProductType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductTypeDao {

    List<ProductType> getSubjectTypeList(PageParameter<ProductType> pageParameter);

    int getSubjectTypeCount(PageParameter<ProductType> pageParameter);

    List<ProductType> getProfessionTypeList(PageParameter<ProductType> pageParameter);

    int getProfessionTypeCount(PageParameter<ProductType> pageParameter);

    List<ProductType> getLeafContentTypeList(PageParameter<ProductType> pageParameter);

    int getLeafContentTypeCount(PageParameter<ProductType> pageParameter);

    int deleteSubjectTypeById(Long id);

    int deleteProfessionTypeById(Long id);

    int deleteLeafContentTypeById(Long id);

    void refreshLeafOfContentType();

    int getSubjectTypeExpertationCount(Long id);

    int getContentTypeExpertationCount(Long id);

    int getProfessionTypeExpertationCount(Long id);

    int insertSubjectTypeBatch(List<ProductType> list);

    int insertProfessionTypeBatch(List<ProductType> list);

    /**
     * 插入一个类，并回填其生成的id到实体类
     * 插入时，根据其全名路径关联查询详情表，作为主键，当冲突时只修改is_deleted
     * 刷新此类的详情表
     * @param productType
     */
    int insertContentType(ProductType productType);

    void callUpdateProductTypeDetail(Long id);

    Long getProductIdByProductType(Long product_type);

    Long queryContentTypeIdByFullNamePath(ProductType productType);


    Map<String,Object> getBtnStatus(Long productType);

    /**
     * 获取产品树信息
     * @param l
     * @return
     */
    List<ProductType> getContentListTree(@Param("parentId") long parentId, @Param("productTypeParam")short productTypeParam, @Param("type_name")String type_name);

    /**
     * 根据id 获取当前级节点数据
     * @param parentId
     * @return
     */
    ProductType getProductConetentTypeVoById(@Param("parentId")Long parentId,@Param("productTypeParam")Short productTypeParam);

    /**
     * 根据id 删除内容分类
     * @param id
     * @return
     */
    Integer deleteProductContentTypeById(Long id);
}
