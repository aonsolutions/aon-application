package com.code.aon.ui.finance.event;

import java.util.Iterator;

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
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceFinanceControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IController masterController = ((LinesController)event.getController()).getMasterController();
		Invoice invoice = (Invoice)masterController.getTo();

		Finance finance = (Finance)event.getController().getTo();
		finance.setPayment((InvoiceType.SALES == invoice.getType()) ? false : true);
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setFinanceStatus(FinanceStatus.PENDING);

		try {
			RegistryBank rbank = obtainRegistryBank(finance);
			if (rbank != null) {
				finance.setBank(rbank.getBank());
				finance.setBankAccount(rbank.getBankAccount());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryBank obtainRegistryBank(Finance finance) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), finance.getRegistry().getId());
		Iterator iterator = rBankBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryBank)iterator.next();
		}
		return null;
	}

}
