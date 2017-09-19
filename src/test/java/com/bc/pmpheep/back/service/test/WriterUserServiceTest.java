package com.bc.pmpheep.back.service.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bc.pmpheep.back.po.WriterUser;
import com.bc.pmpheep.back.service.WriterUserService;
import com.bc.pmpheep.test.BaseTest;

/**
 * 
 * @author 曾庆峰 <791038935@qq.com>
 *
 */
public class WriterUserServiceTest extends BaseTest {

	Logger logger = LoggerFactory.getLogger(WriterUserMessageServiceTest.class);

	@Resource
	WriterUserService writerUserService;

	WriterUser writerUser = new WriterUser();

	@Test
	public void addWriterUserService() {
		writerUser.setUsername("zasd");
		writerUser.setPassword("10214");
		writerUser.setRealname("aswwq");
		writerUser.setAvatar("asdadasdasd");
		try {
			writerUser = writerUserService.add(writerUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != writerUser) {
			logger.info("添加了{}", writerUser.toString());
		} else {
			logger.info("失败了");
		}
	}

	@Test
	public void deleteWriterUserServiceById() {
		int num = -1;
		writerUser.setUsername("zasd");
		writerUser.setPassword("10214");
		writerUser.setRealname("aswwq");
		writerUser.setAvatar("asdadasdasd");
		try {
			writerUser = writerUserService.add(writerUser);
			Long id = writerUser.getId();
			writerUserService.delete(id);
			num = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (-1 != num) {
			logger.info("删除成功");
		} else {
			logger.info("失败了");
		}

	}

	@Test
	public void updateWriterUserById() {
		writerUser.setUsername("zasd");
		writerUser.setPassword("10214");
		writerUser.setRealname("aswwq");
		writerUser.setAvatar("asdadasdasd");
		WriterUser writerUser1 = new WriterUser();
		try {
			writerUser = writerUserService.add(writerUser);
			Long id = writerUser.getId();
			writerUser1 = writerUserService.update(writerUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != writerUser1) {
			logger.info("修改成功{}", writerUser1.toString());
		} else {
			logger.info("失败了");
		}

	}

	@Test
	public void getWriterUserByUsername() {
		writerUser.setUsername("zasd");
		writerUser.setPassword("10214");
		writerUser.setRealname("aswwq");
		writerUser.setAvatar("asdadasdasd");
		WriterUser writerUser1 = new WriterUser();
		try {
			writerUser = writerUserService.add(writerUser);
			Long id = writerUser.getId();
			writerUser1 = writerUserService.get(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != writerUser1) {
			logger.info("查找成功{}", writerUser1.toString());
		} else {
			logger.info("失败了");
		}

	}

}