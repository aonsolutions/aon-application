package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;

public class FINANCE {

	private FINANCE() {
		throw new IllegalStateException("Utility class");
	}
	
	private static IFinance getFinance() {
		return new FinanceImpl();
	}

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

}
