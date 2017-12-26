package com.bc.pmpheep.back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.pmpheep.back.dao.SurveyTemplateDao;
import com.bc.pmpheep.back.dao.SurveyTemplateQuestionDao;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.po.SurveyTemplate;
import com.bc.pmpheep.back.po.SurveyTemplateQuestion;
import com.bc.pmpheep.back.util.CollectionUtil;
import com.bc.pmpheep.back.util.ObjectUtil;
import com.bc.pmpheep.back.util.PageParameterUitl;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.back.vo.SurveyQuestionOptionCategoryVO;
import com.bc.pmpheep.back.vo.SurveyTemplateListVO;
import com.bc.pmpheep.back.vo.SurveyTemplateVO;
import com.bc.pmpheep.service.exception.CheckedExceptionBusiness;
import com.bc.pmpheep.service.exception.CheckedExceptionResult;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * 
 * <pre>
 * 功能描述：问卷调查模版业务层接口实现类
 * 使用示范：
 * 
 * 
 * @author (作者) nyz
 * 
 * @since (该版本支持的JDK版本) ：JDK 1.6或以上
 * @version (版本) 1.0
 * @date (开发日期) 2017-12-21
 * @modify (最后修改时间) 
 * @修改人 ：nyz 
 * @审核人 ：
 * </pre>
 */
@Service
public class SurveyTemplateServiceImpl implements SurveyTemplateService {

    @Autowired
    private SurveyTemplateDao         surveyTemplateDao;
    @Autowired
    private SurveyTemplateQuestionDao surveyTemplateQuestionDao;

    @Override
    public SurveyTemplate addSurveyTemplate(SurveyTemplate surveyTemplate)
    throws CheckedServiceException {
        if (ObjectUtil.isNull(surveyTemplate)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        if (StringUtil.isEmpty(surveyTemplate.getTemplateName())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "模版名称为空");
        }
        if (ObjectUtil.isNull(surveyTemplate.getUserId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "模版创建人为空");
        }
        surveyTemplateDao.addSurveyTemplate(surveyTemplate);
        Long id = surveyTemplate.getId();
        if (ObjectUtil.isNull(id)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "新增id为空");
        }
        return surveyTemplate;
    }

    @Override
    public Integer deleteSurveyTemplateById(Long id) throws CheckedServiceException {
        if (ObjectUtil.isNull(id)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        return surveyTemplateDao.deleteSurveyTemplateById(id);
    }

    @Override
    public Integer updateSurveyTemplate(SurveyTemplate surveyTemplate)
    throws CheckedServiceException {
        if (ObjectUtil.isNull(surveyTemplate)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        return surveyTemplateDao.updateSurveyTemplate(surveyTemplate);
    }

    @Override
    public SurveyTemplate getSurveyTemplateById(Long id) throws CheckedServiceException {
        if (ObjectUtil.isNull(id)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        return surveyTemplateDao.getSurveyTemplateById(id);
    }

    @Override
    public Integer addSurveyTemplateVO(SurveyTemplateVO surveyTemplateVO)
    throws CheckedServiceException {
        if (ObjectUtil.isNull(surveyTemplateVO)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        SurveyTemplate surveyTemplate =
        addSurveyTemplate(new SurveyTemplate(surveyTemplateVO.getTemplateName(),
                                             surveyTemplateVO.getSort(),
                                             surveyTemplateVO.getUserId())); // 添加
        Long id = surveyTemplate.getId(); // 获取模版id
        if (ObjectUtil.isNull(id)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "新增id为空");
        }
        SurveyTemplateQuestion surveyTemplateQuestion =
        new SurveyTemplateQuestion(id, surveyTemplateVO.getQuestionId());
        surveyTemplateQuestionDao.addSurveyTemplateQuestion(surveyTemplateQuestion); // 添加中间
        return 1;
    }

    @Override
    public List<SurveyQuestionOptionCategoryVO> getSurveyTemplateQuestionByTemplateId(
    Long templateId) throws CheckedServiceException {
        if (ObjectUtil.isNull(templateId)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "模版ID为空");
        }
        return surveyTemplateDao.getSurveyTemplateQuestionByTemplateId(templateId);
    }

    @Override
    public PageResult<SurveyTemplateListVO> listSurveyTemplateList(
    PageParameter<SurveyTemplateListVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.QUESTIONNAIRE_SURVEY,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        PageResult<SurveyTemplateListVO> pageResult = new PageResult<SurveyTemplateListVO>();
        // 将页面大小和页面页码拷贝
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        // 包含数据总条数的数据集
        List<SurveyTemplateListVO> surveyTemplateList =
        surveyTemplateDao.listSurveyTemplateList(pageParameter);
        if (CollectionUtil.isNotEmpty(surveyTemplateList)) {
            Integer count = surveyTemplateList.get(0).getCount();
            pageResult.setTotal(count);
            pageResult.setRows(surveyTemplateList);
        }
        return pageResult;
    }

}
