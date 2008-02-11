package com.code.ui.gbp.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.dao.IGBPAlias;

public class OfferController extends BasicController {

	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.OFFER_STATUS), event.getNewValue());
		}
	}
}
