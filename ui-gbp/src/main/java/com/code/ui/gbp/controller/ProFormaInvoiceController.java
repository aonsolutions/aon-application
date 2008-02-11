package com.code.ui.gbp.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;

public class ProFormaInvoiceController extends BasicController {

	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), event.getNewValue());
		}
	}
	
	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		getCriteria().addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), ProFormaInvoiceStatus.PENDING);
		this.onSearch(event);
	}
}