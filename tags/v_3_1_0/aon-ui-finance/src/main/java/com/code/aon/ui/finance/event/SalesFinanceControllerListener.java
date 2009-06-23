package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.SalesFinanceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.FormUtil;

public class SalesFinanceControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(SalesFinanceControllerListener.class.getName());
	
	private static final String SALES_INVOICING_CONTROLLER_NAME = "salesInvoicing";

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesFinanceController salesFinanceController = (SalesFinanceController)event.getController();
		Finance finance = (Finance)salesFinanceController.getTo();
		fillFinanceData(finance, salesFinanceController.getRegistryBankId());
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesFinanceController salesFinanceController = (SalesFinanceController)event.getController();
		Finance finance = (Finance)salesFinanceController.getTo();
		fillFinanceData(finance, salesFinanceController.getRegistryBankId());
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesFinanceController salesFinanceController = (SalesFinanceController)event.getController();
		salesFinanceController.setRegistryBankId(obtainRegistryBankId((Finance)salesFinanceController.getTo()));
	}

	private void fillFinanceData(Finance finance, Integer registryBankId) {
		Invoice invoice = (Invoice)FormUtil.getController(SALES_INVOICING_CONTROLLER_NAME).getTo();
		finance.setInvoice(invoice);
		if(invoice.getType().equals(InvoiceType.SALES)){
			finance.setPayment(false);
		}else{
			finance.setPayment(true);
		}
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		RegistryBank rBank = obtainRegistryBank(registryBankId);
		if(rBank == null){
			finance.setBank(null);
			finance.setBankAccount(null);
		}else{
			finance.setBank(rBank.getBank());
			finance.setBankAccount(rBank.getBankAccount());
		}
	}

	private Integer obtainRegistryBankId(Finance finance) {
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), finance.getRegistry().getId());
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_BANK_ID), finance.getBank().getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				return rBank.getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registryBankId for Finance with id= " + finance.getId(), e);
		}
		return null;
	}
	
	private RegistryBank obtainRegistryBank(Integer registryBankId) {
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), registryBankId);
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryBank)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registryBank with id= " + registryBankId, e);
		}
		return null;
	}
}