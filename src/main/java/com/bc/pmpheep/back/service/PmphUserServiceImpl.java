package com.bc.pmpheep.back.service;

import java.util.List;

import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bc.pmpheep.back.dao.PmphRoleDao;
import com.bc.pmpheep.back.dao.PmphUserDao;
import com.bc.pmpheep.back.po.PmphPermission;
import com.bc.pmpheep.back.po.PmphRole;
import com.bc.pmpheep.back.po.PmphUser;
import com.bc.pmpheep.back.shiro.kit.ShiroKit;
import com.bc.pmpheep.service.exception.CheckedExceptionBusiness;
import com.bc.pmpheep.service.exception.CheckedExceptionResult;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * PmphUserService 实现
 * 
 * @author 曾庆峰
 * 
 */
@Service
public class PmphUserServiceImpl implements PmphUserService {

    @Autowired
    PmphUserDao userDao;
    @Autowired
    PmphRoleDao roleDao;

    /**
     * 返回新插入用户数据的主键
     * 
     * @param user
     * @return
     */
    @Override
    public PmphUser add(PmphUser user) throws CheckedServiceException {
        if (null == user.getUsername()) {
            throw new CheckedServiceException(CheckedExceptionBusiness.USER_MANAGEMENT,
                                              CheckedExceptionResult.NULL_PARAM, "用户名为空时禁止新增用户");
        }
        if (null == user.getPassword()) {
            throw new CheckedServiceException(CheckedExceptionBusiness.USER_MANAGEMENT,
                                              CheckedExceptionResult.NULL_PARAM, "密码为空时禁止新增用户");
        }
        if (null == user.getRealname()) {
            user.setRealname(user.getUsername());
        }
        // 使用用户名作为盐值，MD5 算法加密
        user.setPassword(ShiroKit.md5(user.getPassword(), user.getUsername()));
        userDao.add(user);
        return user;
    }

    /**
     * 为单个用户设置多个角色
     * 
     * @param user
     * @param rids
     */
    @Transactional
    @Override
    public PmphUser add(PmphUser user, List<Integer> rids) throws CheckedServiceException {
        Long userId = this.add(user).getId();
        String uid = String.valueOf(userId);
        roleDao.addUserRoles(Integer.valueOf(uid), rids);
        return user;
    }

    /**
     * 根据 user_id 删除用户数据
     * 
     * @param id
     */
    @Override
    public void delete(int id) throws CheckedServiceException {
        if (id == 1) {
            throw new RuntimeException("不能删除管理员用户");
        }
        userDao.delete(id);

    }

    @Transactional
    @Override
    public void deleteUserAndRole(List<Integer> ids) throws CheckedServiceException {
        if (ids.contains(1)) {
            throw new RuntimeException("不能删除管理员用户");
        }
        // 删除用户列表
        userDao.batchDelete(ids);
        // 依次删除这些用户所绑定的角色
        for (Integer userId : ids) {
            roleDao.deleteUserRoles(userId);
        }

    }

    /**
     * 
     * 更新用户数据 1、更新用户基本信息 2、更新用户所属角色 （1）先删除所有的角色 （2）再添加绑定的角色
     * 
     * @param user
     * @param rids
     */
    @Transactional
    @Override
    public PmphUser update(PmphUser user, List<Integer> rids) throws CheckedServiceException {
        Long userId = user.getId();
        String uid = String.valueOf(userId);
        roleDao.deleteUserRoles(Integer.valueOf(uid));
        roleDao.addUserRoles(Integer.valueOf(uid), rids);
        this.update(user);
        return user;
    }

    /**
     * 更新单个用户信息
     * 
     * @param user
     * @return
     */
    @Override
    public PmphUser update(PmphUser user) throws CheckedServiceException {
        String password = user.getPassword();
        if (password != null) {
            user.setPassword(ShiroKit.md5(user.getPassword(), user.getUsername()));
        }
        userDao.update(user);
        return user;
    }

    /**
     * 根据主键 id 加载用户对象
     * 
     * @param id
     * @return
     */
    @Override
    public PmphUser get(int id) throws CheckedServiceException {
        return userDao.get(id);
    }

    /**
     * 根据用户名加载用户对象（用于登录使用）
     * 
     * @param username
     * @return
     */
    @Override
    public PmphUser getByUsername(String username) throws CheckedServiceException {
        return userDao.getByUserName(username);
    }

    /**
     * 登录逻辑 1、先根据用户名查询用户对象 2、如果有用户对象，则继续匹配密码 如果没有用户对象，则抛出异常
     * 
     * @param username
     * @param password
     * @return
     */
    @Override
    public PmphUser login(String username, String password) throws CheckedServiceException {
        PmphUser user = userDao.getByUserName(username);
        // 密码匹配的工作交给 Shiro 去完成
        if (user == null) {
            // 因为缓存切面的原因,在这里就抛出用户名不存在的异常
            throw new UnknownAccountException("用户名不存在(生产环境中应该写:用户名和密码的组合不正确)");
        } else if (user.getIsDisabled() == 1) {
            throw new LockedAccountException("用户已经被禁用，请联系管理员启用该账号");
        }
        return user;
    }

    /**
     * 查询所有的用户对象列表
     * 
     * @return
     */
    @Override
    public List<PmphUser> getList() throws CheckedServiceException {
        return userDao.getListUser();
    }

    /**
     * 根据角色 id 查询是这个角色的所有用户
     * 
     * @param id
     * @return
     */
    @Override
    public List<PmphUser> getListByRole(int id) throws CheckedServiceException {
        return userDao.getListByRole(id);
    }

    /**
     * 查询指定用户 id 所拥有的权限
     * 
     * @param uid
     * @return
     */
    @Override
    public List<PmphPermission> getListAllResource(int uid) throws CheckedServiceException {
        return userDao.getListAllResources(uid);
    }

    /**
     * 查询指定用户所指定的角色字符串列表
     * 
     * @param uid
     * @return
     */
    @Override
    public List<String> getListRoleSnByUser(int uid) throws CheckedServiceException {
        return userDao.getListRoleSnByUser(uid);
    }

    /**
     * 查询指定用户所绑定的角色列表
     * 
     * @param uid
     * @return
     */
    @Override
    public List<PmphRole> getListUserRole(int uid) throws CheckedServiceException {
        return userDao.getListUserRole(uid);
    }

}
