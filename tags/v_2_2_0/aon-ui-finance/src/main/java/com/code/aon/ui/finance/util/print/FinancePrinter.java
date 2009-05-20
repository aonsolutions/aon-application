package com.code.aon.ui.finance.util.print;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ui.form.BasicController;

public class FinancePrinter extends BasicController {
	
	private CustomerStatus customerStatus;

	private FinanceStatus financeStatus;

	public CustomerStatus getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatus customerStatus) {
		this.customerStatus = customerStatus;
	}

	public FinanceStatus getFinanceStatus() {
		return financeStatus;
	}

	public void setFinanceStatus(FinanceStatus financeStatus) {
		this.financeStatus = financeStatus;
	}

	@SuppressWarnings("unchecked")
	public void onCustomerStatusChanged(ValueChangeEvent event) throws ManagerBeanException {
    	if(event.getNewValue() != null){
    		setCustomerStatus((CustomerStatus)event.getNewValue());
    	}
    }

	@SuppressWarnings("unchecked")
	public void onFinanceStatusChanged(ValueChangeEvent event) throws ManagerBeanException {
    	if(event.getNewValue() != null){
    		setFinanceStatus((FinanceStatus)event.getNewValue());
    	}
    }

}