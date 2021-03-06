/**
 * 
 */
package com.bc.pmpheep.back.controller.orgUser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bc.pmpheep.annotation.LogDetail;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.po.Org;
import com.bc.pmpheep.back.po.OrgUser;
import com.bc.pmpheep.back.service.OrgUserService;
import com.bc.pmpheep.back.sessioncontext.SessionContext;
import com.bc.pmpheep.back.util.CookiesUtil;
import com.bc.pmpheep.back.util.SessionUtil;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.back.vo.OrgAndOrgUserVO;
import com.bc.pmpheep.back.vo.OrgVO;
import com.bc.pmpheep.controller.bean.ResponseBean;
import com.bc.pmpheep.service.exception.CheckedExceptionBusiness;
import com.bc.pmpheep.service.exception.CheckedExceptionResult;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * <p>
 * Description:后台机构用户管理控制层
 * <p>
 * 
 * @author lyc
 * @date 2017年9月26日 下午5:40:52
 */
@Controller
@RequestMapping(value = "/users/org")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class OrgUserController {

	@Autowired
	private OrgUserService orgUserService;
	// 当前业务类型
	private static final String BUSSINESS_TYPE = "机构用户";

	/**
	 * Description:分页查询机构用户
	 * 
	 * @author:lyc
	 * @date:2017年9月26日下午5:43:59
	 * @Param: OrgUserManagerVO
	 * @Return:分页数据集
	 */
	@RequestMapping(value = "/list/orgUser", method = RequestMethod.GET)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "分页查询机构用户")
	@ResponseBody
	public ResponseBean orgUser(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("pageNumber") Integer pageNumber, @RequestParam("name") String name,
			@RequestParam("orgName") String orgName, @RequestParam("orgTypeName") String orgTypeName,
			@RequestParam("isHospital") Integer isHospital) {
		PageParameter pageParameter = new PageParameter<>();
		OrgAndOrgUserVO orgAndOrgUserVO = new OrgAndOrgUserVO();
		if (StringUtil.notEmpty(orgName)) {
			orgAndOrgUserVO.setOrgName(orgName.replaceAll(" ", ""));
		}
		if (StringUtil.notEmpty(name)) {
			orgAndOrgUserVO.setName(name.replaceAll(" ", ""));// 去除空格
		}
		if (StringUtil.notEmpty(orgTypeName)) {
			orgAndOrgUserVO.setOrgTypeName(orgTypeName.replaceAll(" ", ""));// 去除空格
		}
		orgAndOrgUserVO.setIsHospital(isHospital);
		pageParameter.setPageNumber(pageNumber);
		pageParameter.setPageSize(pageSize);
		pageParameter.setParameter(orgAndOrgUserVO);
		return new ResponseBean(orgUserService.getListOrgUser(pageParameter));
	}

	/**
	 * 
	 * Description:新增一个机构用户
	 * 
	 * @author:lyc
	 * @date:2017年9月26日下午5:50:20
	 * @Param: OrgUser
	 * @Return:新增的OrgUser
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "新增一个机构用户")
	@ResponseBody
	public ResponseBean add(OrgUser orgUser) {
		return new ResponseBean(orgUserService.addOrgUser(orgUser));
	}

	/**
	 * 
	 * Description:更新机构用户信息
	 * 
	 * @author:lyc
	 * @date:2017年9月26日下午5:53:44
	 * @Param: OrgUser
	 * @Return:更新影响的行数
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "更新机构用户信息")
	@ResponseBody
	public ResponseBean update(OrgUser orgUser) {
		return new ResponseBean(orgUserService.updateOrgUser(orgUser));
	}

	/**
	 * 
	 * Description:通过id删除一个OrgUser
	 * 
	 * @author:lyc
	 * @date:2017年9月26日下午5:56:38
	 * @Param: id
	 * @Return:影响的行数
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "通过id删除一个机构用户")
	@ResponseBody
	public ResponseBean delete(Long id) {
		return new ResponseBean(orgUserService.deleteOrgUserById(id));
	}

	/**
	 * 
	 * Description:根据id查询一个机构用户信息
	 * 
	 * @author:lyc
	 * @date:2017年9月26日下午5:58:48
	 * @Param: id
	 * @Return:OrgUser
	 */
	@RequestMapping(value = "/orgUser/{id}", method = RequestMethod.GET)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "根据id查询一个机构用户信息")
	@ResponseBody
	public ResponseBean orgUser(Long id) {
		return new ResponseBean(orgUserService.getOrgUserById(id));
	}

	/**
	 * 
	 * 
	 * 功能描述：在机构用户管理页面添加用户
	 * 
	 * @param orgUser
	 *            添加的用户
	 * @return 是否成功
	 * 
	 */
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "添加用户")
	@ResponseBody
	public ResponseBean addUser(OrgUser orgUser) {
		return new ResponseBean(orgUserService.addOrgUserOfBack(orgUser));
	}

	/**
	 * 
	 * 
	 * 功能描述：在机构用户管理页面修改用户
	 * 
	 * @param orgUser
	 *            修改的用户（必须传入用户id）
	 * @return 是否成功
	 * 
	 */
	@RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "修改用户")
	@ResponseBody
	public ResponseBean updateUser(OrgAndOrgUserVO orgAndOrgUserVO) {
		return new ResponseBean(orgUserService.updateOrgUserOfBack(orgAndOrgUserVO));
	}

	/**
	 * 功能描述：在机构用户页面增加机构用户
	 * 
	 * @param orgUser
	 *            org
	 * @return 是否成功
	 */
	@RequestMapping(value = "/add/orgAndUser", method = RequestMethod.POST)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "增加机构用户")
	@ResponseBody
	public ResponseBean orgAndUser(OrgUser orgUser, Org org) {
		return new ResponseBean(orgUserService.addOrgUserAndOrgOfBack(orgUser, org));
	}

	/**
	 * Description:分页查询机构用户
	 * 
	 * @author:lyc
	 * @date:2017年9月26日下午5:43:59
	 * @Param: OrgUserManagerVO
	 * @Return:分页数据集
	 */
	@RequestMapping(value = "/list/allOrgUser", method = RequestMethod.GET)
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "分页查询机构用户")
	@ResponseBody
	public ResponseBean allOrgUser(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("pageNumber") Integer pageNumber, @RequestParam("name") String name,
			@RequestParam("orgName") String orgName) {
		PageParameter pageParameter = new PageParameter<>();
		OrgAndOrgUserVO orgAndOrgUserVO = new OrgAndOrgUserVO();
		if (StringUtil.notEmpty(orgName)) {
			orgAndOrgUserVO.setOrgName(StringUtil.toAllCheck(orgName));
		}
		if (StringUtil.notEmpty(name)) {
			orgAndOrgUserVO.setName(StringUtil.toAllCheck(name));// 去除空格
		}
		pageParameter.setPageNumber(pageNumber);
		pageParameter.setPageSize(pageSize);
		pageParameter.setParameter(orgAndOrgUserVO);
		return new ResponseBean(orgUserService.getListAllOrgUser(pageParameter));
	}

	@ResponseBody
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "机构用户重置密码")
	@RequestMapping(value = "/resetPassword", method = RequestMethod.PUT)
	public ResponseBean resetPassword(Long id) {
		return new ResponseBean<>(orgUserService.resetPassword(id));
	}
	
	@ResponseBody
	@LogDetail(businessType = BUSSINESS_TYPE, logRemark = "机构用户管理界面导入Excel文件")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public ResponseBean importExcel(@RequestParam(name = "file")MultipartFile file, HttpServletRequest request){
		Map<String, Object> map = new HashedMap();
		String sessionId = CookiesUtil.getSessionId(request);
		if (StringUtil.isEmpty(sessionId)){
			throw new CheckedServiceException(CheckedExceptionBusiness.SESSION, 
					CheckedExceptionResult.NULL_PARAM, "用户登陆超时，请重新登陆再试");
		}
		HttpSession session = SessionContext.getSession(sessionId);
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		try {
		List<OrgVO> list = orgUserService.importExcel(file);
		map.put("uuid", uuid);
		map.put("list", list);
		session.setAttribute(uuid, list);
		} catch (CheckedServiceException e) {
			return new ResponseBean(e);
		} catch (IOException e) {
			return new ResponseBean(e);
		}
		return new ResponseBean(map);
	}
}
