package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceFinanceController extends LinesController {

	public boolean isModelToEditable() throws ManagerBeanException{
		if (this.getModel().getRowCount() > 0) {  
			Finance finance = (Finance)this.getModel().getRowData(); 
			return (finance.getFinanceStatus().equals(FinanceStatus.PENDING) || finance.getFinanceStatus().equals(FinanceStatus.RETURNED));
		}
		return false;
	}

	public boolean isModelToPending() throws ManagerBeanException{
		Finance finance = (Finance)this.getModel().getRowData(); 
		return (finance.getFinanceStatus().equals(FinanceStatus.PENDING));
	}

	@SuppressWarnings("unchecked")
	public boolean isAllPending() throws ManagerBeanException{
		Iterator iter = ((List)this.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			if(FinanceStatus.PENDING != finance.getFinanceStatus()){
				return false;
			}
		}
		return true;
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			Finance finance= (Finance) getTo();
			finance.setBank(new Bank());
			finance.setBankAccount(new BankAccount());
		}
	}

	public void onBankChanged(LookupChangeEvent event) {
		Finance finance= (Finance) getTo();
		finance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Bank bank = (Bank) event.getNewValue();
			finance.getBankAccount().setEntity(bank.getCode());			
		}
	}
	
	public void onRBankChanged(ValueChangeEvent event) {
		Finance finance= (Finance) getTo();
		finance.setBank(null);
		finance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			finance.setBank(rbank.getBank());
			finance.setBankAccount(rbank.getBankAccount());			
		}
	}

	public List<SelectItem> getBanks() throws ManagerBeanException {
		Finance finance= (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if (pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getRegistryBanks(finance.getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}
	
}
