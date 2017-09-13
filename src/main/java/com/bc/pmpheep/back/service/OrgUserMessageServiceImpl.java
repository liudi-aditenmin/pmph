package com.bc.pmpheep.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.pmpheep.back.common.service.BaseService;
import com.bc.pmpheep.back.dao.OrgUserMessageDao;
import com.bc.pmpheep.back.po.OrgUserMessage;


/**
 * PmphGroupService 接口实现
 * @author Mryang
 *
 */
@Service
public class OrgUserMessageServiceImpl extends BaseService implements OrgUserMessageService {
	@Autowired
	private OrgUserMessageDao orgUserMessgeDao;
	
	/**
	 * 
	 * @param  OrgUserMessage 实体对象
	 * @return  带主键的 OrgUserMessage
	 * @throws Exception 
	 */
	@Override
	public OrgUserMessage addOrgUserMessage (OrgUserMessage orgUserMessage) throws Exception{
		return orgUserMessgeDao.addOrgUserMessage(orgUserMessage);
	}
	
	/**
	 * 
	 * @param OrgUserMessage 必须包含主键ID
	 * @return  OrgUserMessage
	 * @throws Exception，NullPointerException(主键为空)
	 */
	@Override
	public OrgUserMessage getOrgUserMessageById(OrgUserMessage orgUserMessage) throws Exception{
		if(null==orgUserMessage.getId()){
			throw new NullPointerException("主键id为空");
		}
		return orgUserMessgeDao.getOrgUserMessageById(orgUserMessage);
	}
	
	/**
	 * 
	 * @param OrgUserMessage
	 * @return  影响行数
	 * @throws Exception，NullPointerException(主键为空)
	 */
	@Override
	public Integer deleteOrgUserMessageById(OrgUserMessage orgUserMessage) throws Exception{
		if(null==orgUserMessage.getId()){
			throw new NullPointerException("主键id为空");
		}
		return orgUserMessgeDao.deleteOrgUserMessageById(orgUserMessage);
	}
	
	/**
	 * @param OrgUserMessage
	 * @return 影响行数
	 * @throws Exception ，NullPointerException(主键为空)
	 */
	@Override 
	public Integer updateOrgUserMessageById(OrgUserMessage orgUserMessage) throws Exception{
		if(null==orgUserMessage.getId()){
			throw new NullPointerException("主键id为空");
		}
		return orgUserMessgeDao.updateOrgUserMessageById(orgUserMessage);
	}
}
