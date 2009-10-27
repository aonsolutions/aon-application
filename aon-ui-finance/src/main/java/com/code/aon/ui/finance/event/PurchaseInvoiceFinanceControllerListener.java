package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PurchaseInvoiceFinanceControllerListener extends ControllerAdapter implements IFinanceConstants {
	
	private static final Logger LOGGER = Logger.getLogger(PurchaseInvoiceFinanceControllerListener.class.getName());
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Finance finance = (Finance) event.getController().getTo();
		Invoice invoice = (Invoice)FormUtil.getController(PURCHASE_INVOICE_CONTROLLER_NAME).getTo();
		finance.setInvoice(invoice);
		if (invoice.getType().equals(InvoiceType.SALES)) {
			finance.setPayment(false);
		} else {
			finance.setPayment(true);
		}
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		RegistryBank rbank = obtainRegistryBank(finance);
		if (rbank != null) {
			finance.setBank(rbank.getBank());
			finance.setBankAccount(rbank.getBankAccount());
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryBank obtainRegistryBank(Finance finance) {
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), finance.getRegistry().getId());
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
