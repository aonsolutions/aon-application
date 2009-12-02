package com.code.aon.ui.marketing.controller;

import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class SurveyResponseController extends BasicController implements IMarketingConstants {
	
	private static final Logger LOGGER = Logger.getLogger(SurveyResponseController.class.getName());

	public void onSelectSurveyResponse( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get("surveyResponseId");		
		Criteria oldCriteria = getCriteria();
		clearCriteria();
		getCriteria().addEqualExpression( getFieldName(IMarketingAlias.SURVEY_RESPONSE_ID), Integer.valueOf(id));
		onSearch( event );
		onSelectFirst( event );
		setCriteria(oldCriteria);
	}
	
}