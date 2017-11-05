package com.bc.pmpheep.migration;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.bc.pmpheep.back.po.Textbook;
import com.bc.pmpheep.back.service.TextbookService;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.migration.common.JdbcHelper;
import com.bc.pmpheep.migration.common.SQLParameters;
import com.bc.pmpheep.utils.ExcelHelper;

/**
 * 图五 	教材书籍迁移工具类
 * @author Mr
 *
 */
@Component
public class MigrationStageFive {
	
	private final Logger logger = LoggerFactory.getLogger(MigrationStageFive.class);
	
	@Resource
	TextbookService textbookService;
    @Resource
    ExcelHelper excelHelper;
	
	public void start(){
		textbook();
	}
	/**
	 * 教材书籍表
	 */
	private void textbook() {
		String sql="select a.*,b.new_pk newmaterid,c.new_pk bookcreateuserid,b.createdate newcreatedate,"
				+ " e.new_pk editroid,d.new_pk newcreateuserid,b.createuserid oldcreateuserid"
				+ " from teach_bookinfo a "
				+ " left join teach_material b on b.materid=a.materid "
				+ " left join sys_user c on c.userid=a.createuserid "
				+ " left join sys_user d on d.userid=b.createuserid "
				+ " left join sys_user e on e.userid=a.userid  ";
		String tableName="teach_bookinfo";// 要迁移的旧表名称
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String,Object>> excel = new LinkedList<>();
        int count = 0;//迁移成功的条目数
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
            /* 根据MySQL字段类型进行类型转换 */
        	String id = map.get("bookid").toString();// 旧表主键
        	Textbook textbook = new Textbook();
        	textbook.setId(0L);// 主键
            Integer revision = (Integer) map.get("revision");
            if( null != revision){
            	textbook.setTextbookRound(revision); //书籍轮次
            } else {
            	textbook.setTextbookRound(1);//没有值，则书籍轮次默认为1
            }
            Long c = (Long) map.get("newmaterid");
            if (null != c) {
            	textbook.setMaterialId(c);// 教材id
			} else {
				map.put(SQLParameters.EXCEL_EX_HEADER, "教材id为空");
				excel.add(map);
				logger.error("教材id为空，此结果将将被记录在Excel中");
				continue;
			}
            Long createuserid = (Long) map.get("bookcreateuserid");
            Long newcreateuseid=(Long) map.get("newcreateuserid");
            if (null != createuserid||null != newcreateuseid) {
            	if(createuserid==newcreateuseid){// 如果没有创建者id 就找教材创建者id
            		textbook.setFounderId(createuserid); // 创建者id
            	}else{
            		textbook.setFounderId(newcreateuseid); // 创建者id	
            	}
			} else {
				/*记录教材书籍表没有的创建者id为空*/
				map.put(SQLParameters.EXCEL_EX_HEADER, "创建者id为空，");
				excel.add(map);
				logger.error("创建者id为空，此结果将将被记录在Excel中");
				continue;
			}
            String bookname = (String) map.get("bookname");
            if(StringUtil.isEmpty(bookname)){
            	map.put(SQLParameters.EXCEL_EX_HEADER, "书籍名称为空");
				excel.add(map);
				logger.error("书籍名称为空，此结果将将被记录在Excel中");
				continue;
            }
            java.util.Date ceDate = (java.util.Date) map.get("createdate");
            /*教材表对应书籍创建时间*/
        	java.util.Date createdate = (java.util.Date) map.get("newcreatedate");
            if(null != ceDate||null !=createdate){//如果没有创建时间 就查找关联教材创建时间
            	if(ceDate==createdate){
            		textbook.setGmtCreate((Timestamp) ceDate);// 创建时间
            	}else{
            		textbook.setGmtCreate((Timestamp) createdate);
            	}
            } else {
            	map.put(SQLParameters.EXCEL_EX_HEADER, "创建时间为空");
				excel.add(map);
				logger.error("创建时间为空，因新库不能插入null，去教材表找对应创建时间，此结果将将被记录在Excel中");
				continue;
			}
            Integer xnumber = (Integer) map.get("xnumber");
            if(null != xnumber){
            	textbook.setSort(xnumber);// 图书序号
            } else {
            	map.put(SQLParameters.EXCEL_EX_HEADER, "图书序号为空");
				excel.add(map);
				logger.error("图书序号为空，此结果将将被记录在Excel中");
				continue;
			}
            textbook.setPlanningEditor((Long) map.get("editroid"));// 策划编辑id
            textbook.setTextbookName(bookname);// 书籍名称
            textbook.setGmtPublished((Timestamp) map.get("resultpublishdate"));// 公布时间
            textbook.setIsbn((String) map.get("isbn"));// ISBN号
            textbook.setIsLocked(true);// 是否锁定（通过）旧平台无该状态 默认0
            textbook.setRevisionTimes(0);// 公布后再次修改次数  旧平台无该状态 默认0
            textbook.setRepublishTimes(0);// 公布后再次公布次数 旧平台无该状态 默认0
            textbook.setGmtUpdate((Timestamp) map.get("resultpublishdate"));// 修改时间 旧平台无 默认为公布时间
            /**
             * 旧表处理状态  10：待选主编/副主编；11：已分配主编/副主编；12：已确定主编/副主编 20：已分配策划编辑；30：策划编辑已预选编委；
             * 40：第一主编已选编委；50：策划编辑审核通过编委；60：项目编辑审核通过编委；70：主任审核通过；80：已发布
             */
            Integer dealtype=Integer.parseInt((String) map.get("dealtype"));
            if(dealtype >= 12){
            	textbook.setIsChiefChosen(true);// 是否已选定第一主编
            }
            if(dealtype >= 30){
            	textbook.setIsQualifierSelected(true);// 是否已预选编委
            }
            if(dealtype >= 40){
            	textbook.setIsListSelected(true);// 主编是否选定编委
            }
            if(dealtype >= 50){
            	textbook.setIsPlanningEditorConfirm(true);// 策划编辑是否确定名单
            }
            if(dealtype >= 60){
            	textbook.setIsProjectEditorConfirm(true);// 项目编辑是否确定名单
            }
            if(dealtype >= 80){
            	textbook.setIsPublished(true);// 是否已公布
            }
            /* 开始新增新表对象，并设置属性值 */
            textbook = textbookService.addTextbook(textbook);
        	long pk = textbook.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "bookid", id); // 更新旧表中new_pk字段
        	count++;
        }
        if (excel.size() > 0) {
            try {
                excelHelper.exportFromMaps(excel, tableName, tableName);
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        logger.info("'{}'表迁移完成，异常条目数量：{}", tableName, excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
}