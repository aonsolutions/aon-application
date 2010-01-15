package com.code.aon.ui.finance.event;

import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoiceFinanceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceFinanceControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)event.getController();
		InvoiceController invoiceController = (InvoiceController)financeController.getMasterController();
		Invoice invoice = (Invoice)invoiceController.getTo();

		Finance finance = (Finance)financeController.getTo();
		finance.setPayment((InvoiceType.SALES == invoice.getType()) ? false : true);
		finance.setInvoice(invoice);
		finance.setRegistry(invoice.getRegistry());
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		try {
			finance.setAmount(CommonUtil.round(invoiceController.getToInvoiceTotalPrice() - invoiceController.getToInvoiceFinanceTotal()));

			RegistryPayMethod rPayMethod = obtainRegistryPayMethod(finance.getRegistry());
			if (rPayMethod != null) {
				finance.setPayMethod(rPayMethod.getPayment());
				finance.setBank(rPayMethod.getRegistryBank() == null ? new Bank() : rPayMethod.getBank());
				finance.setBankAccount(rPayMethod.getRegistryBank() == null ? new BankAccount() : rPayMethod.getBankAccount());

				financeController.setRegistryBank(rPayMethod.getRegistryBank());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)event.getController();
		financeController.setRegistryBank(null);

		Finance finance = (Finance)financeController.getTo();
		try {
			for (SelectItem item : financeController.getBanks()) {
				RegistryBank rBank = (RegistryBank)item.getValue();
				BankAccount bankAccount = rBank.getBankAccount();
				if (bankAccount != null) {
					if (StringUtils.equals(finance.getBankAccount().getValue(), bankAccount.getValue())) {
						financeController.setRegistryBank(rBank);
						break;
					}
				}
			}
		} catch (ManagerBeanException e) {
			financeController.setRegistryBank(null);
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryPayMethod obtainRegistryPayMethod(Registry registry) throws ManagerBeanException {
		IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), registry.getId());
		Iterator iterator = rPayMethodBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryPayMethod)iterator.next();
		}
		return null;
	}

}
