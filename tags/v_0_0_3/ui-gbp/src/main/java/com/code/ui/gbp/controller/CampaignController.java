package com.code.ui.gbp.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.dao.IGBPAlias;

public class CampaignController extends BasicController {
	
	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.CAMPAIGN_STATUS), event.getNewValue());
		}
	}
}