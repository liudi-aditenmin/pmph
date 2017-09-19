package com.bc.pmpheep.back.service.test;
import java.util.Random;
import javax.annotation.Resource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import com.bc.pmpheep.back.po.OrgUser;
import com.bc.pmpheep.test.BaseTest;
import com.bc.pmpheep.back.service.OrgUserService;
import com.bc.pmpheep.back.util.Const;
/**
 * AreaDao 单元测试
 *
 * @author mryang
 */
public class OrgUserSeviceTest extends BaseTest {
	Logger logger = LoggerFactory.getLogger(OrgUserSeviceTest.class);
	
	@Resource
	private OrgUserService testService;
	
    @Test
    @Rollback(Const.ISROLLBACK) 
    public void test() throws Exception {
    	Random r =new Random();
    	OrgUser a=new OrgUser("张珊"+r.nextInt(10000),"999", false, 5L, "李四", 1,"zhiwei", "职称","cahunzehn", "shou", "dianhia","shenfenz", "email",
			"address", "String postcode", "String note",2, false, null,null);
    	testService.addOrgUser(a);
    	logger.info("---OrgUserService--------------------------------新增--------------------------------------------");
    	logger.info(a.toString());
    	a.setRealname("ceshiwwwwwwww"+a.getId());
    	logger.info(testService.updateOrgUser(a).toString());
    	a.setId(2L);
    	logger.info(testService.deleteOrgUserById(1l).toString());
    	logger.info(testService.getOrgUserById(4L).toString());
    }    
}




