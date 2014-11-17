package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.AonCoreException;
import com.esferalia.aon.watson.AonError;


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
	
	public static AccountEntryType getValue(Byte value) {
		if (value == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_TYPE_INVALID,"NULL");
		}
		try {
			return AccountEntryType.values()[value];
		} catch (IndexOutOfBoundsException e) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_TYPE_INVALID,value);
		}
	}
}