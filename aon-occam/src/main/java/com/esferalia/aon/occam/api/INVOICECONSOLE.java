package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;

public class INVOICECONSOLE {
	
	private INVOICECONSOLE() {
		
	}
	
	private static IFinance getFinance() {
		return new FinanceImpl();
	}

	public static Stream<InvoiceConsole> getInvoiceConsoles(Occam occam, InvoiceConsoleParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceHeaders(ctx, params);
		}
	}

	public static Invoice getInvoice(Occam occam, Integer domain, Integer invoiceId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getFullInvoice(ctx, invoiceId);
		}
	}

}
