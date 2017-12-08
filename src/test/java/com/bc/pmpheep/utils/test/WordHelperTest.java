package com.bc.pmpheep.utils.test;

import com.bc.pmpheep.back.bo.DeclarationEtcBO;
import com.bc.pmpheep.back.po.DecEduExp;
import com.bc.pmpheep.back.service.DeclarationService;
import com.bc.pmpheep.back.util.CollectionUtil;
import com.bc.pmpheep.test.BaseTest;
import com.bc.pmpheep.utils.WordHelper;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Word工具测试类
 *
 * @author L.X <gugia@qq.com>
 */
public class WordHelperTest extends BaseTest {

    Logger logger = LoggerFactory.getLogger(WordHelperTest.class);

    @Resource
    WordHelper wordHelper;
    @Resource
    DeclarationService declarationService;

    @Test
    public void fromDeclarationEtcBOList() throws FileNotFoundException, IOException {
        List<DeclarationEtcBO> list = new ArrayList<>();
        DeclarationEtcBO declarationEtcBO = new DeclarationEtcBO();
        declarationEtcBO.setRealname("欧阳望月");
        declarationEtcBO.setUsername("Smith");
        declarationEtcBO.setTextbookName("人体解剖学与组织胚胎学");
        declarationEtcBO.setPresetPosition("副主编");
        declarationEtcBO.setChosenOrgName("人民卫生出版社");
        declarationEtcBO.setSex("女");
        declarationEtcBO.setBirthday("1975年11月22日");
        declarationEtcBO.setAddress("浙江省金华市婺城区婺州街1188号");
        declarationEtcBO.setExperience(23);
        declarationEtcBO.setOrgName("金华职业技术学院医学院");
        declarationEtcBO.setPosition("教师");
        declarationEtcBO.setTitle("教授");
        declarationEtcBO.setPostcode("321017");
        declarationEtcBO.setHandphone("13857989881");
        declarationEtcBO.setEmail("test10001test@163.com");
        declarationEtcBO.setFax("01065930013");
        declarationEtcBO.setTelephone("010-65930013");
        declarationEtcBO.setDecEduExps(makeFakeDecEduExpList());
        list.add(declarationEtcBO);
        declarationEtcBO = new DeclarationEtcBO();
        declarationEtcBO.setRealname("西门吹雪");
        declarationEtcBO.setUsername("13800009201");
        declarationEtcBO.setTextbookName("医疗化学");
        declarationEtcBO.setPresetPosition("编委");
        declarationEtcBO.setChosenOrgName("首都医科大学");
        list.add(declarationEtcBO);
        HashMap<String, XWPFDocument> map = wordHelper.fromDeclarationEtcBOList(list);
        for (Map.Entry<String, XWPFDocument> entry : map.entrySet()) {
            FileOutputStream out = new FileOutputStream(entry.getKey());
            entry.getValue().write(out);
            out.flush();
            out.close();
        }
    }

    @Test
    public void fromDeclarationEtcBOListAlpha() throws FileNotFoundException, IOException {
        List<DeclarationEtcBO> declarationEtcBOs = declarationService.getDeclarationEtcBOs(128L);
        if (CollectionUtil.isEmpty(declarationEtcBOs)) {
            return;
        }
        HashMap<String, XWPFDocument> map = wordHelper.fromDeclarationEtcBOList(declarationEtcBOs);
        for (Map.Entry<String, XWPFDocument> entry : map.entrySet()) {
            FileOutputStream out = new FileOutputStream(entry.getKey());
            entry.getValue().write(out);
            out.flush();
            out.close();
        }
    }

    private ArrayList<DecEduExp> makeFakeDecEduExpList() {
        ArrayList<DecEduExp> exps = new ArrayList<>(3);
        DecEduExp exp = new DecEduExp();
        exp.setDateBegin("1998-07-01");
        exp.setDateEnd("2003-12-01");
        exp.setDegree("本科");
        exp.setMajor("临床医学");
        exp.setSchoolName("首都医科大学");
        exps.add(exp);
        exp = new DecEduExp();
        exp.setDateBegin("2004-07-01");
        exp.setDateEnd("2007-12-01");
        exp.setDegree("硕士");
        exp.setMajor("临床医学");
        exp.setSchoolName("首都医科大学");
        exps.add(exp);
        exp = new DecEduExp();
        exp.setDateBegin("2008-07-01");
        exp.setDateEnd("2012-12-01");
        exp.setDegree("博士");
        exp.setMajor("临床医学");
        exp.setSchoolName("首都医科大学");
        exps.add(exp);
        return exps;
    }
}
