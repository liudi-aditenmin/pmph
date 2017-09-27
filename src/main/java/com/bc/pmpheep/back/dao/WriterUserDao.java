package com.bc.pmpheep.back.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.bc.pmpheep.back.plugin.Page;
import com.bc.pmpheep.back.po.WriterPermission;
import com.bc.pmpheep.back.po.WriterRole;
import com.bc.pmpheep.back.po.WriterUser;
import com.bc.pmpheep.back.vo.WriterUserManagerVO;

/**
 * WriterUser 实体类的数据访问层接口
 * 
 * @author 曾庆峰
 * 
 */
@Repository
public interface WriterUserDao {
	/**
	 * 添加一个用户
	 * 
	 * @param pmphUser
	 *            添加用户的详细信息
	 * @return 添加的主键
	 */
	Long add(WriterUser user);

	Integer update(WriterUser user);

	Integer delete(Long id);

	Integer batchDelete(@Param("ids") List<Long> ids);

	WriterUser get(Long id);

	List<WriterUser> getListUser();

	WriterUser getByUserName(String username);

	/**
	 * 根据角色 id 查询所有是该角色的用户列表
	 * 
	 * @param rid
	 * @return
	 */
	List<WriterUser> getListByRole(Long rid);

	List<WriterPermission> getListAllResources(Long uid);

	List<String> getListRoleSnByUser(Long uid);

	List<WriterRole> getListUserRole(Long uid);

	/**
	 * 
	 * 功能描述：分页查询作家用户
	 *
	 * @param page
	 *            传入的查询条件
	 * @return 需要的作家用户集合
	 */
	List<WriterUserManagerVO> getListWriterUser(Page<WriterUserManagerVO, WriterUserManagerVO> page);

	/**
	 * 
	 * 功能描述： 查询总共的条数
	 *
	 * @param page
	 *            传入查询条件
	 * @return 查询到的条数
	 */
	Integer getListWriterUserTotal(Page<WriterUserManagerVO, WriterUserManagerVO> page);

	/**
	 * 
	 * 功能描述：查询表单的数据总条数
	 *
	 * @return 表单的数据总条数
	 */
	Long getWriterUserCount();

	// /**
	// *
	// * <pre>
	// * 功能描述：查询总条数
	// * 使用示范：
	// *
	// * @param page传入的查询条件
	// * @return 查询到的条数
	// * </pre>
	// */
	// Integer getListTotal(Page<WriterUser, Map<String, String>> page);
	// /**
	// *
	// * <pre>
	// * 功能描述：分页查询作家用户
	// * 使用示范：
	// *
	// * @param page 传入的查询条件
	// * @return 需要的作家用户集合
	// * </pre>
	// */
	// List<WriterUser> getList(Page<WriterUser,Map<String, String>> page);

}
