package com.bc.pmpheep.back.dao;


import com.bc.pmpheep.back.po.OrgUser;

/**
 * OrgUser  实体类数据访问层接口
 * @author mryang
 */
public interface  OrgUserDao {
	/**
	 * 
	 * @param OrgUser 实体对象
	 * @return  影响行数
	 * @throws Exception 
	 */
	Integer addOrgUser(OrgUser orgUser) ;
	
	/**
	 * 
	 * @param OrgUser 必须包含主键ID
	 * @return  OrgUser
	 * @throws Exception，NullPointerException(主键为空)
	 */
	OrgUser getOrgUserById(OrgUser orgUser) ;
	
	/**
	 * 
	 * @param OrgUser
	 * @return  影响行数
	 * @throws Exception，NullPointerException(主键为空)
	 */
	Integer deleteOrgUserById(OrgUser orgUser);
	
	/**
	 * @param OrgUser
	 * @return 影响行数
	 * @throws Exception ，NullPointerException(主键为空)
	 */
	Integer updateOrgUserById(OrgUser orgUser);

}