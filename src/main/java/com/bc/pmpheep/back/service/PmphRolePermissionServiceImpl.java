package com.bc.pmpheep.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.pmpheep.back.common.service.BaseService;
import com.bc.pmpheep.back.dao.PmphRolePermissionDao;
import com.bc.pmpheep.back.po.PmphGroup;
import com.bc.pmpheep.back.po.PmphRolePermission;


/**
 * PmphRolePermissionService 接口实现
 * @author Mryang
 *
 */
@Service
public class PmphRolePermissionServiceImpl extends BaseService implements PmphRolePermissionService {
	@Autowired
	private PmphRolePermissionDao pmphRolePermissionDao;
	
	/**
	 * 
	 * @param  PmphRolePermission 实体对象
	 * @return  带主键的PmphRolePermission
	 * @throws Exception 
	 */
	@Override
	public PmphGroup addPmphRolePermission (PmphRolePermission pmphRolePermission) throws Exception{
		return pmphRolePermissionDao.addPmphRolePermission(pmphRolePermission);
	}
	
	/**
	 * 
	 * @param PmphRolePermission 必须包含主键ID
	 * @return  PmphRolePermission
	 * @throws Exception，NullPointerException(主键为空)
	 */
	@Override
	public PmphRolePermission getPmphRolePermissionById(PmphRolePermission pmphRolePermission) throws Exception{
		if(null==pmphRolePermission.getId()){
			throw new NullPointerException("主键id为空");
		}
		return pmphRolePermissionDao.getPmphRolePermissionById(pmphRolePermission);
	}
	
	/**
	 * 
	 * @param PmphRolePermission
	 * @return  影响行数
	 * @throws Exception，NullPointerException(主键为空)
	 */
	@Override
	public Integer deletePmphRolePermissionById(PmphRolePermission pmphRolePermission) throws Exception{
		if(null==pmphRolePermission.getId()){
			throw new NullPointerException("主键id为空");
		}
		return pmphRolePermissionDao.deletePmphRolePermissionById(pmphRolePermission);
	}
	
	/**
	 * @param PmphRolePermission
	 * @return 影响行数
	 * @throws Exception ，NullPointerException(主键为空)
	 */
	@Override 
	public Integer updatePmphRolePermissionById(PmphRolePermission pmphRolePermission) throws Exception{
		if(null==pmphRolePermission.getId()){
			throw new NullPointerException("主键id为空");
		}
		return pmphRolePermissionDao.updatePmphRolePermissionById(pmphRolePermission);
	}
}