package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;

public class FINANCE {

	private FINANCE() {
		throw new IllegalStateException("Utility class");
	}
	
	private static IFinance getFinance() {
		return new FinanceImpl();
	}

	// -------------------------------------------------------------- [INVOICE SERIES]
	public static List<InvoiceSeries> getInvoiceSalesSeries(Occam occam, int domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return getFinance().getInvoiceSalesSeries(ctx,domainId);
		}
	}
	
	// ------------------------------------------------------------------- [UTILITIES]
	
	public static FinanceUtilitiesResult missingFinanceInvoices(Occam occam, Domain domain,FinanceUtilitiesParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam.getDomainName(), domain.getId(), occam.getUser());) {
			return getFinance().missingFinanceInvoices(ctx,params);
		}
	}
	
	public static Invoice missingFinanceInvoicesFix(Occam occam, Integer invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().missingFinanceInvoicesFix(ctx,invoice);
		}
	}

	public static FinanceUtilitiesResult financeInvoiceIntegrity(Occam occam, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam.getDomainName(), domain.getId(), occam.getUser());) {
			return getFinance().financeInvoiceIntegrity(ctx);
		}
	}
	
	public static Finance financeInvoiceIntegrityFix(Occam occam, Finance finance) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFinance().financeInvoiceIntegrityFix(ctx,finance);
		}
	}

	public static void updateWithholdingType(Occam occam, Integer invoiceId, WithholdingType newType) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getFinance().updateWithholdingType(ctx,invoiceId, newType);
		}
	}

	public static void updateActivity(Occam occam, Integer invoiceId, Integer activity) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getFinance().updateActivity(ctx,invoiceId, activity);
		}
	}
	
}
