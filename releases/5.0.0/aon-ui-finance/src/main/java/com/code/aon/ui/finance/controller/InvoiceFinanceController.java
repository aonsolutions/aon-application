package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceFinanceController extends LinesController {

	private RegistryBank registryBank;

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

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
			Finance finance = (Finance) getTo();
			finance.setBank(new Bank());
			finance.setBankAccount(new BankAccount());

			setRegistryBank(null);
		}
	}

	public void onBankChanged(LookupChangeEvent event) {
		Finance finance = (Finance) getTo();
		finance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Bank bank = (Bank) event.getNewValue();
			finance.getBankAccount().setEntity(bank.getCode());			
		}
	}
	
	public void onRBankChanged(ValueChangeEvent event) {
		Finance finance = (Finance) getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			finance.setBank(rbank.getBank());
			finance.setBankAccount(rbank.getBankAccount());			

			setRegistryBank(rbank);
		} else {
			finance.setBank(new Bank());
			finance.setBankAccount(new BankAccount());

			setRegistryBank(null);
		}
	}

	public List<SelectItem> getBanks() throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			if (useRegistryBanks(finance.getInvoice().getType() == InvoiceType.SALES, finance.getPayMethod().getType())) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getRegistryBanks(finance.getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}
	
	private boolean useRegistryBanks(boolean sales, PayMethodType payMethodType) {
		return ((sales && payMethodType == PayMethodType.NEGOTIABLE_DOCUMENT) || (!sales && payMethodType == PayMethodType.BANK_TRANSFER));	
	}

	public boolean isBankCreationEnabled() throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		if (finance == null || finance.getPayMethod() == null || finance.getBank() == null || finance.getBankAccount() == null)
			return false;
		if (!useRegistryBanks(finance.getInvoice().getType() == InvoiceType.SALES, finance.getPayMethod().getType()))
			return false;
		if (!finance.getBankAccount().isValid())
			return false;

		for (SelectItem item : getBanks()) {
			RegistryBank rBank = (RegistryBank)item.getValue();
			BankAccount bankAccount = rBank.getBankAccount();
			if (bankAccount != null) {
				if (StringUtils.equals(finance.getBankAccount().getValue(), bankAccount.getValue())) {
					return false;
				}
			}
		}

		return true;
	}

	public void onAddRBank(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance) getTo();

		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rBank = new RegistryBank();
		rBank.setRegistry(finance.getRegistry());
		rBank.setBank(finance.getBank());
		rBank.setBankAccount(finance.getBankAccount());
		rBank = (RegistryBank)rBankBean.insert(rBank);
		setRegistryBank(rBank);
	}

}
