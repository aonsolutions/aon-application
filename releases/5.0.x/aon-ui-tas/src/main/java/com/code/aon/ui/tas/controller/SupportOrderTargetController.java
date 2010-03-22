package com.code.aon.ui.tas.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.SupportOrder;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class SupportOrderTargetController extends BasicController {
	
	private static final String SUPPORT_ORDER_CONTROLLER = "SupportOrder";

	public void onTarget(ActionEvent event) throws ManagerBeanException{
		SupportOrderController supportOrderController = (SupportOrderController)AonUtil.getController(SUPPORT_ORDER_CONTROLLER);
		SupportOrder supportOrder = (SupportOrder)supportOrderController.getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(ICommercialAlias.TARGET_ID), supportOrder.getTarget().getId());
		this.setCriteria(criteria);
		this.onSearch(null);
		if(this.getModel().getRowCount() > 0){
			this.getModel().setRowIndex(0);
			this.onSelect(null);
		}
	}
}