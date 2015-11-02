package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum AccountEntryType  implements Serializable {
	
	OPENING,
	CLOSING,
	OPERATING,
	MANUAL,
	SALES_INVOICE,
	PURCHASE_INVOICE,
	EXPENSE_INVOICE,
	INVESTMENT_INVOICE,
	EXPENSES,
	SALARY,
	TAX,
	LOAN,
	@Deprecated
	LEASING,
	PAYMENT,
	COLLECTION,
	STOCK_VARIATION,
	AMORTIZATION,
	SOCIAL_INSURANCE,
	LOAN_FEE,
	@Deprecated
	LEASING_FEE,
	RETURNED_PAYMENT,
	RETURNED_COLLECTION,
	SOCIAL_INSURANCE_ADJUST;

	public Byte getValue() {
		return (byte) ordinal();
	}
	
	public boolean isManual() {
        return (this == AccountEntryType.MANUAL
       		|| this == AccountEntryType.EXPENSES
       		|| this == AccountEntryType.SALARY
       		|| this == AccountEntryType.SOCIAL_INSURANCE
       		|| this == AccountEntryType.SOCIAL_INSURANCE_ADJUST
       		|| this == AccountEntryType.LOAN
       		|| this == AccountEntryType.LOAN_FEE
        );
    }
	
	public boolean isInvoice() {
		return (this == AccountEntryType.SALES_INVOICE
			|| this == AccountEntryType.PURCHASE_INVOICE
			|| this == AccountEntryType.EXPENSE_INVOICE
			|| this == AccountEntryType.INVESTMENT_INVOICE);
	}
	
}