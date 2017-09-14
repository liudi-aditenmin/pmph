package com.bc.pmpheep.back.dao;

import org.springframework.stereotype.Repository;

import com.bc.pmpheep.back.po.PmphGroup;
import com.bc.pmpheep.back.po.PmphRolePermission;

/**
 * PmphRolePermission 实体类数据访问层接口
 * 
 * @author mryang
 */
@Repository
public interface  PmphRolePermissionDao {
	/**
	 * 
	 * @param  PmphRolePermission 实体对象
	 * @return  带主键的PmphRolePermission
	 * @throws Exception 
	 */
	PmphGroup addPmphRolePermission (PmphRolePermission pmphRolePermission) ;
	
	/**
	 * 
	 * @param PmphRolePermission 必须包含主键ID
	 * @return  PmphRolePermission
	 * @throws Exception，NullPointerException(主键为空)
	 */
	PmphRolePermission getPmphRolePermissionById(PmphRolePermission pmphRolePermission) ;
	
	/**
	 * 
	 * @param PmphRolePermission
	 * @return  影响行数
	 * @throws Exception，NullPointerException(主键为空)
	 */
	Integer deletePmphRolePermissionById(PmphRolePermission pmphRolePermission) ;
	
	/**
	 * @param PmphRolePermission
	 * @return 影响行数
	 * @throws Exception ，NullPointerException(主键为空)
	 */
	Integer updatePmphRolePermissionById(PmphRolePermission pmphRolePermission) ;
}
