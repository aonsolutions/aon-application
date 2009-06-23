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
import com.code.aon.ui.finance.controller.SaleInvoiceFinanceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.FormUtil;

public class SaleInvoiceFinanceControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceFinanceControllerListener.class.getName());
	
	private static final String SALE_INVOICE_CONTROLLER_NAME = "saleInvoice";

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceFinanceController saleInvoiceFinanceController = (SaleInvoiceFinanceController)event.getController();
		Finance finance = (Finance)saleInvoiceFinanceController.getTo();
		fillFinanceData(finance, saleInvoiceFinanceController.getRegistryBank());
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceFinanceController saleInvoiceFinanceController = (SaleInvoiceFinanceController)event.getController();
		Finance finance = (Finance)saleInvoiceFinanceController.getTo();
		fillFinanceData(finance, saleInvoiceFinanceController.getRegistryBank());
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceFinanceController saleInvoiceFinanceController = (SaleInvoiceFinanceController)event.getController();
		saleInvoiceFinanceController.setRegistryBank(obtainRegistryBank((Finance)saleInvoiceFinanceController.getTo()));
	}

	private void fillFinanceData(Finance finance, RegistryBank registryBank) {
		Invoice invoice = (Invoice)FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME).getTo();
		finance.setInvoice(invoice);
		if(invoice.getType().equals(InvoiceType.SALES)){
			finance.setPayment(false);
		}else{
			finance.setPayment(true);
		}
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		if(registryBank == null){
			finance.setBank(null);
			finance.setBankAccount(null);
		}else{
			finance.setBank(registryBank.getBank());
			finance.setBankAccount(registryBank.getBankAccount());
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryBank obtainRegistryBank(Finance finance) {
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), finance.getRegistry().getId());
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_BANK_ID), finance.getBank().getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryBank)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registryBankId for Finance with id= " + finance.getId(), e);
		}
		return null;
	}
	
}
