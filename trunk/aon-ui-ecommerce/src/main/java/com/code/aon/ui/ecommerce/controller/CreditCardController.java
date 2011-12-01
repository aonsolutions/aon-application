package com.code.aon.ui.ecommerce.controller;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.EcPaymethod;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;



public class CreditCardController {

	private final String TESTING_URL = "https://tpv2.4b.es/simulador/teargral.exe";
	private final String PRODUCTION_URL = "https://tpv2.4b.es/simulador/teargral.exe";
	private boolean testingUrl = true;
	private boolean payment;
	
	public String getQbUrl(){
		if (testingUrl == true) {
			return TESTING_URL;
		} else {
			return PRODUCTION_URL;
		}
	}
	
	public String getPurchaseRef(){
		return "COMPRA";
	}
		
	public String getCommerceKey(){
		Integer qbId = ((ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER)).getActiveConfig().getCreditCard().getId();
		Criteria criteria = new Criteria();
		EcPaymethod ecp = null;
		try {
			criteria.addEqualExpression(BeanManager.getManagerBean(EcPaymethod.class).getFieldName(IEbackofficeAlias.EC_PAYMETHOD_PAYMETHOD_ID), qbId);
			ecp = (EcPaymethod)BeanManager.getManagerBean(EcPaymethod.class).getList(criteria).get(0);
		} catch (ManagerBeanException e) {
			String message = "Hubo un error de comunicacion con el servidor Passat Internet de 4B";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException();
		}
		return ecp.getUserName();
	}
	
	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
}
