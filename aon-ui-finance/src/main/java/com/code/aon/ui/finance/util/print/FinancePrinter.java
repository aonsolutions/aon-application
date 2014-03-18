package com.code.aon.ui.finance.util.print;

import com.code.aon.AonVersion;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ui.form.BasicController;

public class FinancePrinter extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

}