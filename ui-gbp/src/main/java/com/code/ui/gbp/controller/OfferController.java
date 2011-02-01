package com.code.ui.gbp.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.OfferStatus;

public class OfferController extends BasicController {

	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.OFFER_STATUS), event.getNewValue());
		}
	}
	
	public void addOfferDateExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.OFFER_OFFER_DATE), event.getNewValue());
		}
	}
	
	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		getCriteria().addEqualExpression(getFieldName(IGBPAlias.OFFER_STATUS), OfferStatus.PENDING);
		this.onSearch(event);
	}
}