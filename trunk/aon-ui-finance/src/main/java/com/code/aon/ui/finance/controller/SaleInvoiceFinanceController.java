package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class SaleInvoiceFinanceController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceFinanceController.class.getName());

	private static final String SALE_INVOICE_CONTROLLER_NAME = "saleInvoice";
	
	private Company company;

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
			finance.setBankAccount( rbank.getBankAccount() );			
		}
	}

	public List<SelectItem> getBanks() {
		Finance finance= (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if (pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
				SaleInvoiceController saleInvoicingController = (SaleInvoiceController) FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
				Invoice invoice = (Invoice)saleInvoicingController.getTo(); 
				return getRegistryBanks(invoice.getRegistry());
			}
			return getRegistryBanks(getCompany());
		}
		return new LinkedList<SelectItem>();
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getRegistryBanks(Registry registry){
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank, StringUtils.abbreviate(rBank.getBank().getName(), 30)
						+ " [" + rBank.getBankAccount().toString() + "]");
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks", e);
		}
		return rBanks;
	}
	
	public Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					setCompany((Company) iter.next());
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("Error obtaining Company!");
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
