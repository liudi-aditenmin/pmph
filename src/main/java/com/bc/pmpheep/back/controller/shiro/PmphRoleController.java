package com.bc.pmpheep.back.controller.shiro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.pmpheep.back.po.PmphRole;
import com.bc.pmpheep.back.service.PmphPermissionService;
import com.bc.pmpheep.back.service.PmphRoleService;
import com.bc.pmpheep.controller.bean.ResponseBean;

/**
 * 
 * <pre>
 * 功能描述：PmphRole 
 * 使用示范：
 * 
 * 
 * @author (作者) nyz
 * 
 * @since (该版本支持的JDK版本) ：JDK 1.6或以上
 * @version (版本) 1.0
 * @date (开发日期) 2017-9-20
 * @modify (最后修改时间) 
 * @修改人 ：nyz 
 * @审核人 ：
 * </pre>
 */
@SuppressWarnings("all")
@Controller
@RequestMapping("role/pmph")
public class PmphRoleController {
    Logger                logger = LoggerFactory.getLogger(PmphRoleController.class);
    @Autowired
    PmphRoleService       roleService;
    @Autowired
    PmphPermissionService pmphPermissionService;

    /**
     * 
     * <pre>
	 * 功能描述：跳转到查询所有角色的页面
	 * 使用示范：
	 *
	 * @param model
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseBean list() {
        List<PmphRole> roleList = roleService.getList();
        return new ResponseBean(roleList);
    }

    /**
     * 
     * <pre>
	 * 功能描述：跳转到添加角色的页面
	 * 使用示范：
	 *
	 * @param model
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ResponseBean add() {
        return new ResponseBean(new PmphRole());
    }

    /**
     * 
     * <pre>
	 * 功能描述：添加用户角色的后台方法
	 * 使用示范：
	 *
	 * @param role
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseBean add(PmphRole role) {
        logger.debug(role.toString());
        return new ResponseBean(roleService.add(role));
    }

    /**
     * 
     * <pre>
	 * 功能描述：跳转到更新角色的页面
	 * 使用示范：
	 *
	 * @param id
	 * @param model
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public ResponseBean update(@PathVariable("id") Long id) {
        PmphRole role = roleService.get(id);
        return new ResponseBean(role);
    }

    /**
     * 
     * <pre>
	 * 功能描述：修改角色对象的方法
	 * 使用示范：
	 *
	 * @param role
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseBean update(PmphRole role) {
        logger.debug(role.toString());
        return new ResponseBean(roleService.update(role));
    }

    /**
     * 
     * <pre>
	 * 功能描述：获取资源
	 * 使用示范：
	 *
	 * @param id
	 * @param model
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/resources/{roleId}", method = RequestMethod.GET)
    public ResponseBean getListResources(@PathVariable("roleId") Long roleId) {
        return new ResponseBean(roleService.getListPmphRolePermission(roleId));
    }

    /**
     * 设置用户权限
     * 
     * <pre>
	 * 功能描述：
	 * 使用示范：
	 *
	 * @param roleId
	 * @param permissionIds
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    public ResponseBean resource(@RequestParam("roleId") Long roleId,
    @RequestParam("permissionIds") String permissionIds) {
        String[] ssString = permissionIds.split(",");
        List<Long> list = new ArrayList<Long>();
        for (String str : ssString) {
            list.add(Long.valueOf(str));
        }
        return new ResponseBean(roleService.addRoleResource(roleId, list));
    }

    /**
     * 
     * <pre>
	 * 功能描述：角色删除
	 * 使用示范：
	 *
	 * @param roleIds
	 * @return
	 * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseBean deleteRole(@RequestParam("roleIds") List<Long> roleIds) {
        logger.debug(roleIds.toString());
        Map<String, Object> result = new HashMap<String, Object>();
        for (Long roleId : roleIds) {
            logger.debug(roleId.toString());
        }
        // 先批量删除角色,再从角色资源表中删除角色资源数据
        roleService.deleteRoleAndResource(roleIds);
        // 用户绑定到这个角色上,也应该删除
        roleService.deleteRoleAndUser(roleIds);
        result.put("success", true);
        return new ResponseBean(result);
    }

    /**
     * 
     * 
     * 功能描述：社内用户修改时获取所有角色
     * 
     * @return 获取到角色的id与名称
     * 
     */
    @ResponseBody
    @RequestMapping(value = "/list/role", method = RequestMethod.GET)
    public ResponseBean listRole() {
        return new ResponseBean(roleService.getListRole());
    }
}
