package com.code.aon.ui.marketing.print;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET;
import static com.code.aon.ui.common.ICommonConstants.COMMENT;
import static com.code.aon.ui.common.ICommonConstants.COMPANY_DOCUMENT;
import static com.code.aon.ui.common.ICommonConstants.DATE;
import static com.code.aon.ui.common.ICommonConstants.ID;
import static com.code.aon.ui.common.ICommonConstants.LOGIN_USER;
import static com.code.aon.ui.common.ICommonConstants.STATUS;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.ACTION_EXPORT;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.BUNDLE_NAME;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.SURVEY;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.report.dynamic.DynaElements;
import com.code.aon.report.dynamic.DynaReport;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.marketing.controller.IMarketingConstants;
import com.code.aon.ui.report.controller.DynaReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class MarketingActionReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(MarketingActionReport.class.getName());
	
	public String onExcelReport() {
		IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) controller.getTo();
		try {
			DynaElements dyn = new DynaElements();
			DynaReport report = new DynaReport();
			report
				.addColumn(dyn.getIntegerColumn("surveyResponse.target.id", AonUtil.getMessage(ID)))
				.addColumn(dyn.getStringColumn("surveyResponse.target.registry.document", AonUtil.getMessage(COMPANY_DOCUMENT), 100))
				.addColumn(dyn.getStringColumn("surveyResponse.target.registry.fullName", AonUtil.getMessage(ICommercialConstants.BUNDLE_NAME, TARGET), 400))
				.addColumn(dyn.getStringColumn("creationDate", AonUtil.getMessage(DATE), 100))
				.addColumn(dyn.getStringColumn("actionTarget.comments", AonUtil.getMessage(COMMENT), 200))
				.addColumn(dyn.getStringColumn("surveyResponse.user.name", AonUtil.getMessage(LOGIN_USER), 100))
				.addColumn(dyn.getStringColumn("status", AonUtil.getMessage(STATUS), 100))
				.addColumn(dyn.getIntegerColumn("surveyResponse.survey.id", AonUtil.getMessage(ID)))
				.addColumn(dyn.getStringColumn("surveyResponse.survey.description", AonUtil.getMessage(IMarketingConstants.BUNDLE_NAME, SURVEY), 400))
				.addColumn(dyn.getIntegerColumn("question.id", AonUtil.getMessage(ID)))
				.addColumn(dyn.getStringColumn("question.text", AonUtil.getMessage(ICommercialConstants.BUNDLE_NAME, ICommercialConstants.QUESTION), 400))
				.addColumn(dyn.getStringColumn("value", AonUtil.getMessage(ICommercialConstants.BUNDLE_NAME, ICommercialConstants.RESPONSE), 100));
			DynaReportManager drm = new DynaReportManager();
			String fileName = AonUtil.getMessage(BUNDLE_NAME, ACTION_EXPORT, action.getId());
			drm.toExcel(report, fileName, getCollection(action));
		} catch (Throwable e) {
			LOGGER.error( "Error exporting. " + e.getMessage(), e );
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return null;
	}

	private List<QuestionValueReport> getCollection( MarketingAction action ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyResponseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("SurveyResponseDetail.surveyResponse.action.id", action.getId());
		criteria.addOrder("SurveyResponseDetail.surveyResponse.target.id");
		LinkedList<QuestionValueReport> list = new LinkedList<QuestionValueReport>();
		ActionTarget at = null;
		IManagerBean atBean = BeanManager.getManagerBean(ActionTarget.class);
		for( ITransferObject to : bean.getList(criteria) ) {
			SurveyResponseDetail srd = (SurveyResponseDetail) to;
			Target target = srd.getSurveyResponse().getTarget();
			if ( (at == null) || (! at.getTarget().equals(target)) ) {
				Criteria _criteria = new Criteria();
				_criteria.addEqualExpression(atBean.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID), target.getId());
				_criteria.addEqualExpression(atBean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
				List<ITransferObject> atList = atBean.getList(_criteria);
				if (! atList.isEmpty() ) {
					at = (ActionTarget) atList.get(0);
				}
			}
			list.add( new QuestionValueReport(srd, at) );
		}
		return list;
	}
	
}