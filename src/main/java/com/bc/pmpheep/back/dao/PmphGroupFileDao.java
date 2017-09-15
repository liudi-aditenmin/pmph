package com.bc.pmpheep.back.dao;

import org.springframework.stereotype.Repository;

import com.bc.pmpheep.back.po.PmphGroupFile;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * PmphGroupFile 实体类数据访问层接口
 * 
 * @author mryang
 */
@Repository
public interface  PmphGroupFileDao {
	/**
	 * 
	 * @param  pmphGroupFile 实体对象
	 * @return  影响行数
	 */
	Integer addPmphGroupFile (PmphGroupFile pmphGroupFile);
	
	/**
	 * 
	 * @param id 主键id
	 * @return  PmphGroupFile
	 */
	PmphGroupFile getPmphGroupFileById(Long  id) ;
	
	/**
	 * 
	 * @param id 主键id
	 * @return  影响行数
	 */
	Integer deletePmphGroupFileById(Long  id) ;
	
	/**
	 * 全字段更新
	 * @param pmphGroupFile
	 * @return 影响行数
	 */
	Integer updatePmphGroupFile(PmphGroupFile pmphGroupFile) ;
}
