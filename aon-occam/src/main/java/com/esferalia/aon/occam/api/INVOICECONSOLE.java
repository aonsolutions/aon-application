package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;

public class INVOICECONSOLE {
	
	private INVOICECONSOLE() {
	}
	
	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}
	private static IFinance getFinance() {
		return new FinanceImpl();
	}

	public static List<InvoiceConsole> getInvoiceConsoles(Occam occam, InvoiceConsoleParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getInvoiceHeaders(ctx, params);
		}
	}

	public static Invoice getInvoice(Occam occam, Integer domain, Integer invoiceId){
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().getFullInvoice(ctx, invoiceId);
		}
	}

	public static AccountingInvoice getOrInitializeAccountingInvoiceFromInvoice(Occam occam, Integer domain, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getOrInitializeAccountingInvoiceFromInvoice(ctx, invoiceId);
		}
	}

	public static InvoiceConsoleAnalysis analyze(Occam occam, InvoiceConsoleParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().analyze(ctx, params);
		}
	}

	public static AccountEntry record(Occam occam, Invoice inv) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().record(ctx, inv);
		}
	}

}
