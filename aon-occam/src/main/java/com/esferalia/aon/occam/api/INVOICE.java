package com.esferalia.aon.occam.api;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.InvoiceImpl;

public class INVOICE {
	
	private INVOICE() {
		throw new IllegalStateException("Utility class");
	}
	
	private static ICommon getCommon() {
		return new CommonImpl();
	}
	
	private static IInvoice getInvoice() {
		return new InvoiceImpl();
	}
	

	// ********************************************
	// *************************** CONFIGURATION **
	// ********************************************
	public static AonConfiguration getConfiguration(Occam occam) {
		return getConfiguration(occam, null);
	}
	public static AonConfiguration getConfiguration(Occam occam, Date atDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getCommon().getConfiguration(ctx, atDate);
		}
	}
	
	// ********************************************
	// ********************************* INVOICE **
	// ********************************************
	
	public static Invoice save(Occam occam, Invoice invoice){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getInvoice().save(ctx, invoice);
		}
	}

	public static Invoice delete(Occam occam, Integer invoiceId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getInvoice().delete(ctx, invoiceId);
		}
	}
}
