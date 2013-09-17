package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum StatementConcept implements IResourceable {

	UNKNOWN,
    WITHDRAWAL,
    DEPOSIT,
    PAYMENT,
    COLLECTION, 
    LOAN,
    COLLECTION_BATCH,
    SUBSCRIPTION,
    AMORTIZATION,
    STOCK_EXCHANGE,
    GAS_CHEQUE,
    CASH_POINT,
    CREDIT_CARD,
    FOREIGN_OPERATION,
    RETURNED,
    SALARY,
    FISCAL_STAMP,
    INTEREST_COMMISSION,
    CANCELLATION,
    OTHER; 
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_statement_concept_";
    
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}