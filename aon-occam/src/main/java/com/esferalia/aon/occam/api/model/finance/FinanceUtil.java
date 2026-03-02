package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceUtil {

	private static final String PROFORMA = "PROFORMA";
	private static final int NUMBER_LENGTH = 6;

	private FinanceUtil() {
	}

	/**
	 * @deprecated Use {@link #getDocumentNumber(Invoice)} instead.
	 */
	@Deprecated	
	public static String getDocumentNumber(InvoiceType type, String series, Integer number) {
		String documentNumber = ((InvoiceType.SALES == type) ? "E" : (InvoiceType.UNDEDUCTIBLE == type) ? "G" : "R") + "-";
		if (!AonStringUtils.isEmpty(series)) {
			documentNumber += series + "/";
		}
		documentNumber += AonStringUtils.leftPad(AonNumberUtils.toString(number), 6, "0");
		return documentNumber;
	}

	public static String formatInvoiceNumber( Integer number ) {
		return AonStringUtils.leftPad(AonNumberUtils.toString(number), NUMBER_LENGTH , "0");
	}

	public static String getDocumentNumber(Invoice invoice) {
		if (invoice == null) {
			throw new AonCoreException("Invoice cannot be null");
		}
		if (invoice.getType() == null) {
			throw new AonCoreException("Invoice type cannot be null");
		}
	    StringBuilder sb = new StringBuilder();
	    invoice.getType().visit(null, new IAccountingInvoiceTypeVisitor() {
			@Override public void visitUndeductible(AccountingInvoice invoice) 	{ sb.append("G"); }
			@Override public void visitSales(AccountingInvoice invoice) 		{ sb.append("E"); }
			@Override public void visitPurchase(AccountingInvoice invoice) 		{ sb.append("R"); }
			@Override public void visitExpenses(AccountingInvoice invoice) 		{ sb.append("R"); }
		});
	    sb.append("-");
	    if (!AonStringUtils.isEmpty(invoice.getSeries())) {
	        sb.append(invoice.getSeries()).append("/");
	    }
	    if ( AonMathUtils.isLessThanZero(invoice.getNumber()) ) {
	    	sb.append(PROFORMA);	
	    } else {
	    	sb.append(formatInvoiceNumber( invoice.getNumber() ) );
	    }
	    return sb.toString();
	}
	
	public static String getSalesReferenceCode( Invoice invoice ) {
		if (invoice == null) throw new AonCoreException("Invoice cannot be null");
		if (!invoice.isSales()) return invoice.getReferenceCode();
		String referenceCode = ( AonMathUtils.isLessThanZero(invoice.getNumber()) ) 
	    	? PROFORMA
    		: formatInvoiceNumber( invoice.getNumber() );
		if (!AonStringUtils.isBlank(invoice.getSeries())) {
			referenceCode = invoice.getSeries() + "/" + referenceCode;
		}
		return referenceCode;
	}


}
