package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class SaleInvoiceFinanceController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceFinanceController.class.getName());

	private static final String SALE_INVOICE_CONTROLLER_NAME = "saleInvoice";
	
	private RegistryBank registryBank;

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	public boolean isModelToEditable() throws ManagerBeanException{
		if ( this.getModel().getRowCount() > 0) {  
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
			if(!finance.getFinanceStatus().equals(FinanceStatus.PENDING)){
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public List getRegistryBanks(){
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		SaleInvoiceController feeInvoicingController = (SaleInvoiceController) FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
		Invoice invoice = (Invoice)feeInvoicingController.getTo(); 
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), invoice.getRegistry().getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank, rBank.getBank().getName());
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks for registry with id=" + invoice.getRegistry().getId(), e);
		}
		return rBanks;
	}
	
	public void onBankChanged(ValueChangeEvent event) {
		Finance finance= (Finance) getTo();
		if (event.getNewValue() != null) {
			RegistryBank rb = (RegistryBank) event.getNewValue();
			finance.setBank(rb.getBank());
			finance.setBankAccount(rb.getBankAccount());
		} else {
			finance.setBank(null);
			finance.setBankAccount(null);
		}
		
		
		
	}
	
}
