package com.code.aon.ui.finance.event;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
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
		finance.setRegistryName(invoice.getRegistryName());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
        finance.setConcept(invoice.getDocumentNumber()); 
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		try {
			finance.setAmount(invoiceController.getPendingAmount());

			RegistryPayMethod rPayMethod = finance.getRegistry().getPayMethod();
			finance.setPayMethod((rPayMethod==null) ? new PayMethod() : rPayMethod.getPayment());
			finance.setBankAccount((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? new BankAccount() : rPayMethod.getBankAccount());
			finance.setBankAlias((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBankAlias());
			finance.setBic((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBic());

			financeController.setRegistryBank((rPayMethod==null) ? null : rPayMethod.getRegistryBank());
			financeController.setShowBankManualInput(false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)event.getController();
		Finance finance = (Finance)financeController.getTo();
		try {
			financeController.setRegistryBank(null);
			if (StringUtils.isNotEmpty(finance.getBankAccount().getIban())) {
				for (SelectItem selectItem : financeController.getAllBanks()) {
					RegistryBank rBank = (RegistryBank)selectItem.getValue();
					if (finance.getBankAccount().getIban().equals(rBank.getBankAccount().getIban())) {
						financeController.setRegistryBank(rBank);
						break;
					}
				}
			}
			financeController.setShowBankManualInput(financeController.getRegistryBank() == null && StringUtils.isNotEmpty(finance.getBankAccount().getIban()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
