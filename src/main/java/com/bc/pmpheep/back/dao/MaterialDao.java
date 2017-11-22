package com.bc.pmpheep.back.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.bc.pmpheep.back.po.Material;


/**
 * MaterialDao实体类数据访问层接口
 * 
 * @author 曾庆峰
 * 
 */
@Repository
public interface MaterialDao {

    /**
     * 新增一个Material
     * 
     * @param Material 实体对象
     * @return 影响行数
     */
    Integer addMaterial(Material material);

    /**
     * 删除Material 通过主键id
     * 
     * @param Material
     * @return 影响行数
     */
    Integer deleteMaterialById(Long id);

    /**
     * 通过主键id更新material 不为null 的字段
     * 
     * @param Material
     * @return 影响行数
     */
    Integer updateMaterial(Material material);

    /**
     * 查询一个 Material 通过主键id
     * 
     * @param Material 必须包含主键ID
     * @return Material
     */
    Material getMaterialById(Long id);

    /**
     * 
     * <pre>
     * 功能描述：查询表单的数据总条数
     * 使用示范：
     *
     * @return 表单的总条数
     * </pre>
     */
    Long getMaterialCount();

    /**
     * 
     * <pre>
     * 功能描述：获取教材集合
     * 使用示范：
     *
     * @param materialName 教材名称
     * @return
     * </pre>
     */
    List<Material> getListMaterial(@Param("materialName") String materialName);
    

    /**
     * 获取用户在该教材是几本书的策划编辑
     * @author Mryang
     * @createDate 2017年11月21日 下午2:26:17
     * @param  materialId 教材id 
     * @param  pmphUserId 用户id 
     * @return 担任策划编辑数目本数
     */
    Integer getPlanningEditorSum(@Param("materialId")Long materialId,@Param("pmphUserId")Long pmphUserId) ;
}
