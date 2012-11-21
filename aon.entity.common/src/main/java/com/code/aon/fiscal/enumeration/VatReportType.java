package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.config.enumeration.InvoiceTransactionType;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum VatReportType implements IResourceable {

	GENERAL ( InvoiceTransactionType.NATIONAL),
	SURCHARGE ( null ),
	INTRACOMMUNITY ( InvoiceTransactionType.INTRACOMMUNITY),
	EXTRACOMMUNITY ( InvoiceTransactionType.EXTRACOMMUNITY),
	CAN_CEU_MEL ( InvoiceTransactionType.CAN_CEU_MEL),
	OTHER_ISP ( InvoiceTransactionType.OTHER_ISP);

	
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_vat_report_type_";
    private InvoiceTransactionType transaction; 
    
    private VatReportType(InvoiceTransactionType transaction) {
    	this.transaction = transaction;
	}
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    public boolean accept( InvoiceTransactionType transaction ) {
    	return this.transaction == transaction;
    }
}