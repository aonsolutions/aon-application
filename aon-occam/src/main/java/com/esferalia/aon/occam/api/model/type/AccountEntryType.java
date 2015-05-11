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
}