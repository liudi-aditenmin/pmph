package com.bc.pmpheep.migration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bc.pmpheep.back.po.Area;
import com.bc.pmpheep.back.po.Org;
import com.bc.pmpheep.back.po.OrgType;
import com.bc.pmpheep.back.po.OrgUser;
import com.bc.pmpheep.back.po.WriterPoint;
import com.bc.pmpheep.back.po.WriterUser;
import com.bc.pmpheep.back.service.AreaService;
import com.bc.pmpheep.back.service.OrgService;
import com.bc.pmpheep.back.service.OrgTypeService;
import com.bc.pmpheep.back.service.OrgUserService;
import com.bc.pmpheep.back.service.WriterPointService;
import com.bc.pmpheep.back.service.WriterUserService;
import com.bc.pmpheep.back.util.ObjectUtil;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.general.bean.ImageType;
import com.bc.pmpheep.general.service.FileService;
import com.bc.pmpheep.migration.common.JdbcHelper;
import com.bc.pmpheep.migration.common.SQLParameters;
import com.bc.pmpheep.utils.ExcelHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 区域迁移工具类
 * <p>
 * Description:区域模块数据迁移类，此为所有迁移工具的第一步<p>
 * @author 陶勇诚
 */
@Component
public class MigrationStageOne {

    private final Logger logger = LoggerFactory.getLogger(MigrationStageOne.class);

    @Resource
    AreaService areaService;
    @Resource
    OrgTypeService orgTypeService;
    @Resource
    OrgService orgService;
    @Resource
    OrgUserService orgUserService;
    @Resource
    FileService fileService;
    @Resource
    WriterUserService writerUserService;
    @Resource
    WriterPointService writerPointService;
    @Resource
    ExcelHelper excelHelper;

    public void start() {
        Date begin = new Date();
        area();
        orgType();
        org();
        orgUser();
        writerUser();
        writerPoint();
        logger.info("迁移第一步运行结束，用时：{}", JdbcHelper.getPastTime(begin));
    }
    
    protected void area() {
        String tableName = "ba_areacode"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        List<Map<String, Object>> excel = new LinkedList<>();
        Map<String, Object> result = new LinkedHashMap<>();
        int count = 0;//迁移成功的条目数
        int correctCount = 0;//没有问题的数据
        int[] state = {0,0};//用户判断此表是否用异常数据的标识
        StringBuilder reason = new StringBuilder();
        StringBuilder dealWith = new StringBuilder();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
            /* 根据MySQL字段类型进行类型转换 */
            BigDecimal areaId = (BigDecimal) map.get("AreaID");
            BigDecimal parentCode = (BigDecimal) map.get("ParentCode");
            String areaName = map.get("AreaName").toString();
            if (StringUtil.isEmpty(areaName)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "找不到区域名称。");
                excel.add(map);
                logger.error("区域名称为空，此结果将被记录在Excel中");
                if (state[0] == 0){
                	reason.append("找不到区域名称。");
                	dealWith.append("放弃迁移。");
                	state[0] = 1;
                }
                continue;
            }
            if (ObjectUtil.isNull(parentCode)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "找不到区域省-市-县路径。");
                excel.add(map);
                logger.error("父区域编码为空，此结果将被记录在Excel中");
                if (state[1] == 0){
                	reason.append("找不到区域省-市-县路径。");
                	dealWith.append("设为默认值0迁入数据库。");
                	state[1] = 1;
                }
            }
            /* 开始新增新表对象，并设置属性值 */
            Area area = new Area();
            area.setAreaName(areaName);
            long parentPk = 0L;
            /**
             * 该对象默认为根节点，如果不是根节点，则根据parentCode反查父节点的new_pk。 注意此处由于ba_areacode表爷爷-父亲-儿子是按正序排列的，父节点总是已经被插入到新表，所以不需要再次循环。
             */
            if (parentCode.intValue() != 0) {
                parentPk = JdbcHelper.getPrimaryKey(tableName, "AreaID", parentCode);//返回Long型新主键
            }
            area.setParentId(parentPk);
            area = areaService.addAreaStage(area);
            long pk = area.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "AreaID", areaId);//更新旧表中new_pk字段
            count++;
            if (null == map.get("exception")){
            	correctCount++;
            }
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, "区域表", "area");
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        if (correctCount != maps.size()){
        	result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "area");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "区域表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
        }
        logger.info("area表迁移完成");
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
        //记录信息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("result", "area表迁移完成" + count + "/" + maps.size());
        SQLParameters.STATISTICS.add(msg);
    }

    protected void orgType() {
        String tableName = "ba_organize";
        JdbcHelper.addColumn(tableName);//添加new_pk字段
        String sql = "SELECT orgid,orgname,sortno FROM ba_organize WHERE orgcode NOT LIKE '15%' "
                + "AND parentid=0 ;";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        List<Map<String, Object>> excel = new LinkedList<>();
        Map<String, Object> result = new LinkedHashMap<>();
        int count = 0;
        int correctCount = 0;//统计正常数据的数量
        int[] state = {0};//用户统计该表是否有相对应的异常数据情况的标识
        StringBuilder reason = new StringBuilder();
        StringBuilder dealWith = new StringBuilder();
        for (Map<String, Object> map : maps) {
            String orgTypeId = (String) map.get("orgid");
            String orgName = (String) map.get("orgname");
            if (StringUtil.isEmpty(orgName)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "找不到机构类型名称。");
                excel.add(map);
                logger.error("机构类型名称为空，此结果将被记录在Excel中");
                if (state[0] == 0){
                	reason.append("找不到机构类型名称。");
                	dealWith.append("放弃迁移。");
                	state[0] = 1;
                }
                continue;
            }
            Integer sort = (Integer) map.get("sortno");
            if (ObjectUtil.notNull(sort) && sort < 0) {
                sort = 999;
            }
            OrgType orgType = new OrgType();
            orgType.setTypeName(orgName);
            orgType.setSort(sort);
            orgType = orgTypeService.addOrgType(orgType);
            Long pk = orgType.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "orgid", orgTypeId);
            count++;
            if (null == map.get("exception")){
            	correctCount++;
            }
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, "机构类型表", "org_type");
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        if (correctCount != maps.size()){
        	result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "org_type");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "机构类型表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
        }
        logger.info("org_type表迁移完成");
        logger.info("原数据库表中共有{}条数据，迁移了{}条数据", maps.size(), count);
        //记录信息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("result", "org_type表迁移完成" + count + "/" + maps.size());
        SQLParameters.STATISTICS.add(msg);
    }

    protected void org() {
        String tableName = "ba_organize";//机构类型方法已添加过new_pk，此处无需再添加
        String sql = "SELECT a.*,b.new_pk FROM ba_organize a "
                + "LEFT JOIN ba_areacode b ON b.AreaID =a.orgprovince "
                + "WHERE a.orgcode NOT LIKE '15%' AND a.parentid !=0 ORDER BY a.isdelete,a.orgcode";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        List<Map<String, Object>> excel = new LinkedList<>();
        /*除主键外有其他列有唯一值约束，用此集合放此列已经插入新表的值作为判断重复的条件*/
        List<String> list = new ArrayList<>();
        Map<String, Object> result = new LinkedHashMap<>();
        int count = 0;
        int correctCount = 0;
        int[] state = {0,0,0,0,0,0,0};
        StringBuilder reason = new StringBuilder();
        StringBuilder dealWith = new StringBuilder();
        for (Map<String, Object> map : maps) {
            /*因此表有主要级字段和次要级字段，次要级字段插入新表同时也需导出Excel，因此异常信息不止一条，
			 * 用StringBulider进行拼接成最终的异常信息
             */
            StringBuilder sb = new StringBuilder();
            Integer isDeleted = (Integer) map.get("isdelete");
            if (ObjectUtil.isNull(isDeleted)) {
            	isDeleted = 0;
            }
            /* 此数据为被逻辑删除的数据，待客户反馈再决定是否迁移，现阶段暂时不迁移 */
            if (isDeleted.intValue() == 1){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("被逻辑删除的数据。"));
            	excel.add(map);
            	if (state[0] == 0){
            		reason.append("被逻辑删除的数据。");
            		dealWith.append("放弃迁移。");
            		state[0] = 1;
            	}
            	continue;
            }
            String orgId = (String) map.get("orgid");
            String orgName = (String) map.get("orgname");
            if (StringUtil.isEmpty(orgName)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("找不到机构名称。"));
                excel.add(map);
                if (state[1] == 0){
                	reason.append("找不到机构名称。");
                	dealWith.append("放弃迁移。");
                	state[1] = 1;
                }
                continue;
            }
            if (JdbcHelper.nameDuplicate(list, orgName)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("机构名称重复。"));
                excel.add(map);
                if (state[2] == 0){
                	reason.append("机构名称重复。");
                	dealWith.append("放弃迁移。");
                	state[2] = 1;
                }
                continue;
            }
            if (StringUtil.strLength(orgName) > 20){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("机构名称过长。"));
                excel.add(map);
                if (state[3] == 0){
                	reason.append("机构名称过长。");
                	dealWith.append("放弃迁移。");
                	state[3] = 1;
                }
                continue;
            }
            //依据客户反馈不迁移的机构
            if ("江苏建康职业学院".equals(orgName) || "北京大学".equals(orgName) || "民办山东万杰医学高等专科学校".equals(orgName)
            		|| "天津大学".equals(orgName) || "协和医院".equals(orgName) || "华西医院".equals(orgName)
            		|| "技术学校".equals(orgName) || "qthzyxy".equals(orgName) || "河南中医学院".equals(orgName)
            		|| "陕西中医学院".equals(orgName)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("缺少管理员账号。"));
            	excel.add(map);
            	if (state[4] == 0){
            		reason.append("缺少管理员账号。");
            		dealWith.append("依据客户反馈，放弃迁移。");
            		state[4] = 1;
            	}
            	continue;
            }
            list.add(orgName);
            Integer orgType = (Integer) map.get("orgtype");
            Long areaId = (Long) map.get("new_pk");
            if (ObjectUtil.isNull(orgType)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("找不到机构所属类型。"));
                excel.add(map);
                if (state[5] ==0){
                	reason.append("找不到机构所属类型。");
                	dealWith.append("放弃迁移。");
                	state[5] = 1;
                }
                continue;
            }
            //找不到所属区域根据客户要求删除不导入
            if (ObjectUtil.isNull(areaId)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("已删除。"));
                excel.add(map);
                if (state[6] ==0){
                	reason.append("找不到所属区域。");
                	dealWith.append("依据客户反馈，放弃迁移。");
                	state[6] = 1;
                }
                continue;
            }
            Long orgTypeId = JdbcHelper.getPrimaryKey(tableName, "orgid", String.valueOf(orgType));
            String contactPerson = (String) map.get("linker");
            String contactPhone = (String) map.get("linktel");
            Integer sort = (Integer) map.get("sortno");
            if (ObjectUtil.notNull(sort) && sort < 0) {
                sort = 999;
            }
            String note = (String) map.get("remark");
            Org org = new Org();
            org.setOrgName(orgName);
            org.setOrgTypeId(orgTypeId);
            org.setAreaId(areaId);
            org.setContactPerson(contactPerson);
            org.setContactPhone(contactPhone);
            org.setSort(sort);
            org.setNote(note);
            org.setIsDeleted(isDeleted == 1);
            org = orgService.addOrg(org);
            Long pk = org.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "orgid", orgId);
            count++;
            if (null == map.get("exception")){
            	correctCount++;
            }
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, "机构信息表", "org");
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        if (correctCount != maps.size()){
        	result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "org");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "机构表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
        }
        logger.info("org表迁移完成");
        logger.info("原数据库表共有{}条数据，迁移了{}条数据", maps.size(), count);
        //记录信息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("result", "org表迁移完成" + count + "/" + maps.size());
        SQLParameters.STATISTICS.add(msg);
    }

    protected void orgUser() {
        String tableName = "sys_user";
        JdbcHelper.addColumn(tableName);//添加new_pk字段
        JdbcHelper.addColumn("sys_userext");//因为是用户相关，所以用户拓展表也要添加new_pk字段
        String sql = "SELECT a.userid,a.usercode,a.`password`,a.isvalid,d.new_pk,a.username,d.orgname,b.sex,"
                + "b.duties,b.positional,b.fax,b.handset,b.phone,b.idcard,b.email,b.address,b.postcode,"
                 + "CASE WHEN b.audittype IS NOT NULL THEN 1 ELSE 0 END is_proxy_upload,"
                + "e.filedir,"
                + "CASE WHEN b.audittype=2 THEN 1 WHEN b.audittype=1 THEN 2 ELSE 0 END progress,"
                + "b.auditdate,a.memo,a.sortno,f.filedir avatar "
                + "FROM sys_user a "
                + "LEFT JOIN sys_userext b ON a.userid = b.userid "
                + "LEFT JOIN sys_userorganize c ON a.userid = c.userid "
                + "LEFT JOIN ba_organize d ON c.orgid = d.orgid "
                + "LEFT JOIN pub_addfileinfo e ON a.userid = e.operuserid "
                + "LEFT JOIN (SELECT * FROM pub_addfileinfo x WHERE x.fileid IN (SELECT MAX(o.fileid) "
				+ "FROM pub_addfileinfo o WHERE o.childsystemname='sys_userext_avatar' GROUP BY o.operuserid))f " 
				+ "ON a.userid = f.operuserid "
                + "WHERE a.sysflag=1 AND b.usertype=2 "
                + "AND NOT EXISTS (SELECT * FROM pub_addfileinfo p WHERE e.operdate<p.operdate "
                + "AND e.operuserid=p.operuserid) "
                + "GROUP BY a.usercode ORDER BY a.userid ;";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        List<Map<String, Object>> excel = new LinkedList<>();
        List<String> list = new ArrayList<>();
        Map<String, Object> result = new LinkedHashMap<>();
        int count = 0;
        int correctCount = 0;//统计正常数据数量
        int[] state = {0,0,0,0,0,0,0,0,0};//识别该表是否有相应异常数据的标识
        StringBuilder reason = new StringBuilder();
        StringBuilder dealWith = new StringBuilder();
        for (Map<String, Object> map : maps) {
            StringBuilder sb = new StringBuilder();
            String userId = map.get("userid").toString();
            String username = (String) map.get("usercode");
            if (StringUtil.isEmpty(username)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("找不到机构代码。"));
                excel.add(map);
                if (state[0] == 0){
                	reason.append("找不到机构管理员账号。");
                	dealWith.append("放弃迁移。");
                	state[0] = 1;
                }
                continue;
            }
            if (JdbcHelper.nameDuplicate(list, username)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("机构代码重复。"));
                excel.add(map);
                if (state[1] == 0){
                	reason.append("机构管理员账号重复。");
                	dealWith.append("放弃迁移。");
                	state[1] = 1;
                }
                continue;
            }
            if (StringUtil.strLength(username) > 20){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("机构代码过长。"));
                excel.add(map);
                if (state[2] == 0){
                	reason.append("机构管理员账号过长。");
                	dealWith.append("放弃迁移。");
                	state[2] = 1;
                }
                continue;
            }
            if ("jxyxgdxx".equals(username) || "zyyyzkxy".equals(username) || "qlyyxy".equals(username)
            		|| "shcjzyxy".equals(username)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("依据客户反馈不迁移"));
            	excel.add(map);
            	if (state[3] == 0){
            		reason.append("多余管理员账号。");
            		dealWith.append("依据客户反馈，放弃迁移。");
            		state[3] = 1;
            	}
            	continue;
            }
            list.add(username);
            Long orgId = (Long) map.get("new_pk");
            if (ObjectUtil.isNull(orgId)) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("找不到对应的学校。"));
                excel.add(map);
                if (state[4] == 0){
                	reason.append("找不到对应的机构。");
                	dealWith.append("放弃迁移。");
                	state[4] = 1;
                }
                continue;
            }else{
                if (null != orgUserService.getOrgUserByOrgId(orgId)) {
                    logger.error("迁移org_user时，机构ID(org_id={})出现了重复，机构用户名:{}", orgId, username);
                    continue;
                }
            }
            String password = (String) map.get("password");
            if (StringUtil.isEmpty(password)) {
                password = "888888";
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("机构用户登陆密码为空。"));
                excel.add(map);
                if (state[5] == 0){
                	reason.append("机构用户登陆密码为空。");
                	dealWith.append("设为默认密码迁入数据库。");
                	state[5] = 1;
                }
            }
            String realName = (String) map.get("username");
            String orgName = (String) map.get("orgname");
            if (StringUtil.isEmpty(realName)) {
                realName = orgName;
            }
            String sexNum = (String) map.get("sex");
            Integer sex = null;
            if (StringUtil.notEmpty(sexNum)) {
                sex = Integer.parseInt(sexNum);
            }
            String position = (String) map.get("duties");
            if (StringUtil.notEmpty(position) && ("null".equals(position) || "nu".equals(position))){
            	position = "-";
            }
            String title = (String) map.get("positional");
            if (StringUtil.isEmpty(title)){
            	title = "-";
            }
            if (!"-".equals(title)){
	            switch (title) {
				case "1097":
					title = "教授";
					break;
				case "1098":
					title = "副教授";
	                break;
				case "1099":
					title = "讲师";
					break;
				case "1100":
					title = "正高";
					break;
				case "1101":
					title = "副高";
					break;
				default:
					title = "其他";
				}
            }
            String fax = (String) map.get("fax");
            if (StringUtil.notEmpty(fax) && ("null".equals(fax) || "nu".equals(fax))){
            	fax = "-";
            }
            String handphone = (String) map.get("handset");
            if (StringUtil.notEmpty(handphone) && ("null".equals(handphone) || "nu".equals(handphone))){
            	handphone = "-";
            }
            String telephone = (String) map.get("phone");
            if (StringUtil.notEmpty(telephone) && ("null".equals(telephone) || "nu".equals(telephone))){
            	telephone = "-";
            }
            String idcard = (String) map.get("idcard");
            if (StringUtil.notEmpty(idcard) && ("null".equals(idcard) || "nu".equals(idcard))){
            	idcard = "-";
            }
            String email = (String) map.get("email");
            if (StringUtil.notEmpty(email) && ("null".equals(email) || "nu".equals(email))){
            	email = "-";
            }
            String address = (String) map.get("address");
            if (StringUtil.notEmpty(address) && ("null".equals(address) || "nu".equals(address))){
            	address = "-";
            }
            String postcode = (String) map.get("postcode");
            if (StringUtil.notEmpty(postcode) && ("null".equals(postcode) || "nu".equals(postcode))){
            	postcode = "-";
            }
            Integer isProxyUpload = (Integer) map.get("is_proxy_upload");
            String proxy = (String) map.get("filedir");
            String avatar = (String) map.get("avatar");
            Integer progress = (Integer) map.get("progress");
            Timestamp reviewDate = (Timestamp) map.get("auditdate");
            String note = (String) map.get("memo");
            if (StringUtil.notEmpty(note) && ("null".equals(note) || "nu".equals(note))){
            	note = "-";
            }
            Integer sort = (Integer) map.get("sortno");
            if (ObjectUtil.notNull(sort) && sort < 0) {
                sort = 999;
            }
            OrgUser orgUser = new OrgUser();
            orgUser.setUsername(username);
            orgUser.setPassword(password);
            orgUser.setIsDisabled(false);
            orgUser.setOrgId(orgId);
            orgUser.setRealname(realName);
            orgUser.setSex(sex);
            orgUser.setPosition(position);
            orgUser.setTitle(title);
            orgUser.setFax(fax);
            orgUser.setHandphone(handphone);
            orgUser.setTelephone(telephone);
            orgUser.setIdcard(idcard);
            orgUser.setEmail(email);
            orgUser.setAddress(address);
            orgUser.setPostcode(postcode);
            orgUser.setAvatar("DEFAULT");
            orgUser.setIsProxyUpload(isProxyUpload == 1);
            orgUser.setProgress(progress);
            orgUser.setReviewDate(reviewDate);
            orgUser.setNote(note);
            orgUser.setSort(sort);
            if (orgUser.getProgress() == 1 && ObjectUtil.isNull(orgUser.getReviewDate())) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("审核通过，但审核时间为空。"));
                excel.add(map);
                if (state[6] == 0){
                	reason.append("审核通过但社和时间为空。");
                	dealWith.append("依据客户反馈照常迁入数据库。");
                	state[6] = 1;
                }
            }
            orgUser = orgUserService.addOrgUser(orgUser);
            Long pk = orgUser.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "userid", userId);
            JdbcHelper.updateNewPrimaryKey("sys_userext", pk, "userid", userId);//用户拓展表也要更新new_pk
            count++;
            if (StringUtil.isEmpty(proxy) && isProxyUpload == 1){
            	proxy = "DEFAULT";
            	orgUser.setProxy(proxy);
            } else if (StringUtil.notEmpty(proxy)) {
                String mongoId = "";
                try {
                    mongoId = fileService.migrateFile(proxy, ImageType.ORG_USER_PROXY, pk);
                } catch (IOException ex) {
                    mongoId = "DEFAULT";
                    logger.error("文件读取异常，路径<{}>,异常信息：{}", proxy, ex.getMessage());
                    map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("文件读取异常。"));
                    excel.add(map);
                    if (state[7] == 0){
                    	reason.append("机构委托书丢失。");
                    	dealWith.append("设为默认图片迁入数据库。");
                    	state[7] = 1;
                    }
                } 
                orgUser.setProxy(mongoId);
            }
                if (StringUtil.notEmpty(avatar)) {
                    String avatarMongoId = "";
                    try {
                        avatarMongoId = fileService.migrateFile(avatar, ImageType.ORG_USER_AVATAR, pk);
                    } catch (IOException ex) {
                        avatarMongoId = "DEFAULT";
                        logger.error("文件读取异常，路径<{}>,异常信息：{}", avatar, ex.getMessage());
                        map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("机构用户头像文件读取异常。"));
                        excel.add(map);
                        if (state[8] == 0){
                        	reason.append("机构用户头像文件丢失。");
                        	dealWith.append("设为默认头像迁入数据库。");
                        	state[8] = 1;
                        }
                    } 
                    orgUser.setAvatar(avatarMongoId);
                }
                orgUserService.updateOrgUser(orgUser);
                if (null == map.get("exception")){
                	correctCount++;
                }
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, "机构用户表", "org_user");
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        if (correctCount != maps.size()){
        	result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "org_user");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "机构用户表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
        }
        logger.info("org_user表迁移完成");
        logger.info("原数据库表共有{}条数据，迁移了{}条数据", maps.size(), count);
        //记录信息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("result", "org_user表迁移完成" + count + "/" + maps.size());
        SQLParameters.STATISTICS.add(msg);
    }

    protected void writerUser() {
        String tableName = "sys_user";
        String sql = "SELECT a.userid,a.usercode,a.`password`,a.isvalid,d.new_pk org_new_pk,a.username,b.sex,"
                + "b.birthdate,b.seniority,b.unitid,b.duties,b.positional,b.fax,b.handset,b.phone,b.idcard,b.email,"
                + "b.address,b.postcode,"
                + "CASE WHEN b.usertype=4 THEN 1 WHEN b.usertype=1 OR b.usertype=6 THEN 2 "
                + "WHEN b.usertype=5 OR b.usertype=7 THEN 3 ELSE 0 END rank,"
                + "CASE WHEN b.isteacher=2 THEN 1 ELSE 0 END is_teacher,f.filedir,b.teacherauditdate,"
                + "CASE WHEN g.sysflag=0 THEN 1 WHEN g.sysflag=1 THEN 2 END auth_user_type,"
                + "g.new_pk auth_user_id,"
                + "CASE WHEN b.usertype=1 OR b.usertype=6 THEN 1 ELSE 0 END is_writer,"
                + "CASE WHEN b.usertype=5 OR b.usertype=7 THEN 1 ELSE 0 END is_expert,"
                + "e.filedir avatar,b.usersign,a.memo,a.sortno "
                + "FROM sys_user a "
                + "LEFT JOIN sys_userext b ON a.userid = b.userid "
                + "LEFT JOIN sys_userorganize c ON b.userid = c.userid "
                + "LEFT JOIN ba_organize d ON c.orgid = d.orgid "
                + "LEFT JOIN (SELECT * FROM pub_addfileinfo x WHERE x.fileid IN (SELECT MAX(o.fileid) "
                + "FROM pub_addfileinfo o WHERE o.childsystemname='sys_userext_avatar' GROUP BY o.operuserid))e "
                + "ON a.userid = e.operuserid "
                + "LEFT JOIN (SELECT * FROM pub_addfileinfo y WHERE y.fileid IN (SELECT MAX(p.fileid) "
                + "FROM pub_addfileinfo p WHERE p.childsystemname='sys_userext_teacher' GROUP BY p.operuserid))f "
                + "ON a.userid = f.operuserid "
                + "LEFT JOIN sys_user g ON b.teacheraudituser = g.userid "
                + "WHERE a.sysflag=1 AND b.usertype !=2 AND a.isvalid =1;";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        List<Map<String, Object>> excel = new LinkedList<>();
        List<String> list = new ArrayList<>();
        Map<String, Object> result = new LinkedHashMap<>();
        int count = 0;
        int correctCount = 0;//统计正常数据的数量
        int[] state = {0,0,0,0,0,0,0,0};
        StringBuilder reason = new StringBuilder();
        StringBuilder dealWith = new StringBuilder();
        for (Map<String, Object> map : maps) {
            StringBuilder sb = new StringBuilder();
            String userId = (String) map.get("userid");
            String username = (String) map.get("usercode");
            if (StringUtil.isEmpty(username)) {
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("未找到用户的登陆名。"));
            	excel.add(map);
            	if (state[0] == 0){
            		reason.append("未找到用户的登陆账号。");
            		dealWith.append("放弃迁入。");
            		state[0] = 1;
            	}
            	continue;
            }
            username = username.replace("&middot;", "·");
            if ("admin".equals(username)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, "系统管理员账号。");
            	if (state[1] == 0){
            		reason.append("此账号为系统管理员账号。");
            		dealWith.append("放弃迁入作家用户表，迁入社内用户表。");
            		state[1] = 1;
            	}
            	continue;
            }
            String realName = (String) map.get("username");
            if (StringUtil.isEmpty(realName)) {
            	realName = username;
            }
            Integer sort = (Integer) map.get("sortno");
            if (ObjectUtil.notNull(sort) && sort < 0) {
            	sort = 999;
            }
            //此重复用户只能通过个人信息多少判断保留，保留个人信息较全的一条，另一条删除
            if (("王训".equals(realName) || "赵舒武".equals(realName)) && ObjectUtil.isNull(sort)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("已删除"));
            	excel.add(map);
            	if (state[2] == 0){
            		reason.append("用户名重复。");
            		dealWith.append("放弃迁入。");
            		state[2] = 1;
            	}
            	continue;
            }
            if (JdbcHelper.nameDuplicate(list, username)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("用户的登陆名重复。"));
                excel.add(map);
                if (state[3] == 0){
                	reason.append("用户的登录名重复。");
                	dealWith.append("放弃迁入。");
                	state[3] = 1;
                }
                continue;
            }
            list.add(username);
            String password = "888888";
            Integer isDisabled = (Integer) map.get("isvalid");
            Long orgid = (Long) map.get("org_new_pk");
            String sexNum = (String) map.get("sex");
            Integer sex = 0;
            if (StringUtil.notEmpty(sexNum)) {
                sex = Integer.parseInt(sexNum);
            }
            Date birthday = (Date) map.get("birthdate");
            String experienceNum = (String) map.get("seniority");
            if (JdbcHelper.judgeExperience(experienceNum)) {
                //如果教龄数据不符合规范，调用公共方法将其转变为合乎规范的数据
                experienceNum = JdbcHelper.correctExperience(experienceNum);
                if ("0".equals(experienceNum)){
                	map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("此教龄数据没有值，为“无”，“、”，"
                			+ "“其他”或数字远远超出人的寿命，无法通过合适方法修改插入新数据库，"));
                	excel.add(map);
                	if (state[4] == 0){
                		reason.append("教龄数据不合规范。");
                		dealWith.append("经过合理规则转换迁入数据库。");
                		state[4] = 1;
                	}
                } 
            }
            Integer experience = Integer.parseInt(experienceNum);
            String workPlace = (String) map.get("unitid");
            if (StringUtil.notEmpty(workPlace) && ("null".equals(workPlace) || "nu".equals(workPlace))){
            	workPlace = "-";
            }
            String position = (String) map.get("duties");
            if (StringUtil.notEmpty(position) && ("null".equals(position) || "nu".equals(position))){
            	position = "-";
            }
            String title = (String) map.get("positional");
            if (StringUtil.isEmpty(title)){
            	title = "-";
            }
            if (!"-".equals(title)){
	            switch (title) {
				case "1097":
					title = "教授";
					break;
				case "1098":
					title = "副教授";
	                break;
				case "1099":
					title = "讲师";
					break;
				case "1100":
					title = "正高";
					break;
				case "1101":
					title = "副高";
					break;
				default:
					title = "其他";
				}
            }
            String fax = (String) map.get("fax");
            if (StringUtil.notEmpty(fax) && ("null".equals(fax) || "nu".equals(fax))){
            	fax = "-";
            }
            String handphone = (String) map.get("handset");
            if (StringUtil.notEmpty(handphone) && ("null".equals(handphone) || "nu".equals(handphone))){
            	handphone = "-";
            }
            String telephone = (String) map.get("phone");
            if (StringUtil.notEmpty(telephone) && ("null".equals(telephone) || "nu".equals(telephone))){
            	telephone = "-";
            }
            String idcard = (String) map.get("idcard");
            if (StringUtil.notEmpty(idcard) && ("null".equals(idcard) || "nu".equals(idcard))){
            	idcard = "-";
            }
            String email = (String) map.get("email");
            if (StringUtil.notEmpty(email) && ("null".equals(email) || "nu".equals(email))){
            	email = "-";
            }
            String address = (String) map.get("address");
            if (StringUtil.notEmpty(address) && ("null".equals(address) || "nu".equals(address))){
            	address = "-";
            }
            String postcode = (String) map.get("postcode");
            if (StringUtil.notEmpty(postcode) && ("null".equals(postcode) || "nu".equals(postcode))){
            	postcode = "-";
            }
            Long rankNum = (Long) map.get("rank");
            Integer rank = 0;
            if (ObjectUtil.notNull(rankNum)) {
                rank = rankNum.intValue();
            }
            String cert = (String) map.get("filedir");
            Timestamp authTime = (Timestamp) map.get("teacherauditdate");
            Long isTeacher = (Long) map.get("is_teacher");
            Long authUserTypeNum = (Long) map.get("auth_user_type");
            Integer authUserType = 0;
            if (ObjectUtil.notNull(authUserTypeNum)) {
                authUserType = authUserTypeNum.intValue();
            }
            Long authUserId = (Long) map.get("auth_user_id");
            Long isWriter = (Long) map.get("is_writer");
            Long isExpert = (Long) map.get("is_expert");
            String avatar = (String) map.get("avatar");
            String signature = (String) map.get("usersign");
            String note = (String) map.get("memo");
            if (StringUtil.notEmpty(note) && ("null".equals(note) || "nu".equals(note))){
            	note = "-";
            }
            WriterUser writerUser = new WriterUser();
            writerUser.setUsername(username);
            writerUser.setNickname(username);
            writerUser.setPassword(password);
            writerUser.setIsDisabled(isDisabled != 1);
            writerUser.setOrgId(orgid);
            writerUser.setRealname(realName);
            writerUser.setSex(sex);
            writerUser.setBirthday(birthday);
            writerUser.setExperience(experience);
            writerUser.setWorkPlace(workPlace);
            writerUser.setPosition(position);
            writerUser.setTitle(title);
            writerUser.setFax(fax);
            writerUser.setHandphone(handphone);
            writerUser.setTelephone(telephone);
            writerUser.setIdcard(idcard);
            writerUser.setEmail(email);
            writerUser.setAddress(address);
            writerUser.setPostcode(postcode);
            writerUser.setRank(rank);
            writerUser.setIsTeacher(isTeacher == 1);
            writerUser.setAuthTime(authTime);
            writerUser.setAuthUserType(authUserType);
            writerUser.setAuthUserId(authUserId);
            writerUser.setIsWriter(isWriter == 1);
            writerUser.setIsExpert(isExpert == 1);
            writerUser.setAvatar("DEFAULT");
            writerUser.setSignature(signature);
            writerUser.setNote(note);
            writerUser.setSort(sort);
            if (writerUser.getIsTeacher() && ObjectUtil.isNull(writerUser.getAuthTime())) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("认证通过但认证时间为空。"));
                excel.add(map);
                if (state[5] == 0){
                	reason.append("认证通过但认证时间为空。");
                	dealWith.append("依据客户要求照常迁入数据库。");
                	state[5] = 1;
                }
            }
            writerUser = writerUserService.add(writerUser);
            Long pk = writerUser.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "userid", userId);
            JdbcHelper.updateNewPrimaryKey("sys_userext", pk, "userid", userId);
            count++;
            if (StringUtil.notEmpty(cert)) {
                String certMongoId;
                try {
                    certMongoId = fileService.migrateFile(cert, ImageType.WRITER_USER_CERT, pk);
                } catch (IOException ex) {
                    certMongoId = "DEFAULT";
                    logger.error("文件读取异常，路径<{}>,异常信息：{}", cert, ex.getMessage());
                    map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("教师资格证文件读取异常。"));
                    excel.add(map);
                    if (state[6] == 0){
                    	reason.append("教师资格证文件丢失。");
                    	dealWith.append("设为默认图片迁入数据库。");
                    	state[6] = 1;
                    }
                } 
                writerUser.setCert(certMongoId);
            }
            if (StringUtil.notEmpty(avatar)) {
                String avatarMongoId;
                try {
                    avatarMongoId = fileService.migrateFile(avatar, ImageType.WRITER_USER_AVATAR, pk);
                } catch (IOException ex) {
                    avatarMongoId = "DEFAULT";
                    logger.error("文件读取异常，路径<{}>,异常信息：{}", avatar, ex.getMessage());
                    map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("作家用户头像文件读取异常。"));
                    excel.add(map);
                    if (state[7] == 0){
                    	reason.append("作家用户头像丢失。");
                    	dealWith.append("设为默认头像迁入数据库。");
                    	state[7] = 1;
                    }
                }
                writerUser.setAvatar(avatarMongoId);
            }
            writerUser.setPassword(null);
            writerUserService.update(writerUser);
            if (null == map.get("exception")){
            	correctCount++;
            }
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, "作家用户表", "writer_user");
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        if (correctCount != maps.size()){
        	result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "writer_user");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "作家用户表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
        }
        logger.info("writer_user表迁移完成");
        logger.info("原数据库表共有{}条数据，迁移了{}条数据", maps.size(), count);
        //记录信息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("result", "writer_user表迁移完成" + count + "/" + maps.size());
        SQLParameters.STATISTICS.add(msg);
    }
    
    protected void writerPoint(){
        String tableName = "sys_user";
        String sql = "select sysu.*,sysu.new_pk id from sys_user sysu "
        		+ "left join sys_userext sysue ON sysu.userid = sysue.userid "
        		+ "where sysu.sysflag=1 and sysue.usertype !=2";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        Map<String, Object> result = new LinkedHashMap<>();
        int count = 0;//迁移成功的条目数
        int correctCount = 0;//统计正常数据的数量
        int[] state = {0};
        StringBuilder reason = new StringBuilder();
        StringBuilder dealWith = new StringBuilder();
        List<Map<String, Object>> excel = new LinkedList<>();
        for (Map<String, Object> map : maps) {
        	StringBuilder sb = new StringBuilder();
            String id = (String) map.get("userid"); // 旧表主键值
            Long userId = (Long) map.get("id"); // 用户id
            WriterPoint writerPoint = new WriterPoint();
            if (ObjectUtil.isNull(userId) || userId.intValue() == 0) {
                map.put(SQLParameters.EXCEL_EX_HEADER, sb.append("未找到用户id对应的关联结果。"));
                excel.add(map);
                logger.debug("未找到用户id对应的关联结果，此结果将被记录在Excel中");
                if (state[0] == 0){
                	reason.append("未找到用户的积分。");
                	dealWith.append("放弃迁入。");
                	state[0] = 1;
                }
                continue;
            }
            writerPoint.setUserId(userId);
            writerPoint.setTotal(0);
            writerPoint.setGain(0);
            writerPoint.setLoss(0);
            writerPoint = writerPointService.addWriterPoint(writerPoint);
            long pk = writerPoint.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "userid", id);
            count++;
            if (null == map.get("exception")){
            	correctCount++;
            }
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, "用户积分表", "writer_point");
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        if (correctCount != maps.size()){
        	result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "writer_point");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "用户积分表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
        }
        logger.info("writer_point表迁移完成");
        logger.info("原数据库表共有{}条数据，迁移了{}条数据", maps.size(), count);
        //记录信息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("result", "writer_point表迁移完成" + count + "/" + maps.size());
        SQLParameters.STATISTICS.add(msg);
    }

}
