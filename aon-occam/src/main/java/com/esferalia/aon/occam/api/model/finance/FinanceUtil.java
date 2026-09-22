package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceUtil {
	
	private FinanceUtil() {
		/* This utility class should not be instantiated */
	}

	public static final String UNKNOWN = "?-";
	public static final String INPUT = "R-";
	public static final String OUTPUT = "E-";
	public static final String UNDEDUCTIBLE = "G-";
	public static final String PROFORMA = "PROFORMA";

	public static String getDocumentNumber(Invoice invoice) {
		if (invoice == null) return UNKNOWN;	
		return getDocumentNumber(invoice.getType(), invoice.getSeries(), invoice.getNumber());
	}

    public static String getDocumentNumber(InvoiceType type, String series, Integer number) {
    	String documentNumber = UNKNOWN;
    	if (type != null) {
    		documentNumber = type.visit(null, new IInvoiceTypeVisitor<String>() {
    			@Override public String visitSales(Invoice invoice) { return OUTPUT; }
    			@Override public String visitPurchase(Invoice invoice) { return INPUT; }
    			@Override public String visitExpenses(Invoice invoice) { return INPUT; }
    			@Override public String visitUndeductible(Invoice invoice) {return UNDEDUCTIBLE; }
    		});
    	}
		if (AonStringUtils.isNotEmpty(series)) documentNumber += series + "/";
		if (number == null) {
			documentNumber += "??????";
		} else {
			documentNumber += (number < 0) 
				? PROFORMA
				: AonStringUtils.leftPad(AonNumberUtils.toString(number), 6, "0");
		}
		return documentNumber;
	}


}
