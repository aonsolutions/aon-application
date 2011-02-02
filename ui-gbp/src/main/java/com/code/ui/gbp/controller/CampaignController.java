package com.code.ui.gbp.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.CampaignStatus;

public class CampaignController extends BasicController {
	
	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.CAMPAIGN_STATUS), event.getNewValue());
		}
	}
	
	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		try {
			getCriteria().addOrExpression(getFieldName(IGBPAlias.CAMPAIGN_STATUS),""+CampaignStatus.ACTIVE.ordinal());
			getCriteria().addOrExpression(getFieldName(IGBPAlias.CAMPAIGN_STATUS),""+CampaignStatus.PREPARING.ordinal());
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
		this.onSearch(event);
	}
	
}