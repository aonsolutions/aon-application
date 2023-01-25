package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;

public class FISCAL {

	private static IFiscal getFiscal() {
		return new FiscalImpl();
	}

	// ********************************************
	// ********************************** FISCAL **
	// ********************************************

	// -------------------------- FISCAL PANEL
//	public static LinkedList<IFiscalModel> getFiscalPanel(String domainName,
//			int domain, FiscalMatrixParams params, String login) {
//		CloseableAONContext ctx = null;
//		try {
//			ctx = AONContext.getAONContext(domainName, domain, login);
//			User user = AON.getUser(domainName, domain, login);
//			return getFiscal().getFiscalPanel(ctx, domain, params, user.getId());
//		} finally {
//			if (ctx != null)
//				ctx.close();
//		}
//	}
	
	// --------------------------------------------------------------------

	public static Stream<VatSummaryContext> getVatSummaryContext(Occam occam, AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFiscal().getVatSummaryContext(ctx, params);
		}
	}
	public static Stream<VatContext> getVatContext(Occam occam, AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFiscal().getVatContext(ctx, params);
		}
	}
	
	public static Stream<VatContext> getSiiVatContext(String domainName, int domainId, String user, AccountingReportParams params, String sii) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getSiiVatContext(ctx, params, sii);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
		
	public static IrpfSummary getIrpfBreakdownSummary(Occam occam, IRPFParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFiscal().getIrpfBreakdownSummary(ctx, params);
		}
	}

	public static Stream<IrpfBreakdown> getIrpfBreakdown(Occam occam, IRPFParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFiscal().getIrpfBreakdown(ctx, params);
		}
	}
		
	public static LinkedList<InvoiceFiscalModels> getInvoicesModels(Occam occam, InvoiceModelReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getFiscal().getInvoicesModels(ctx, params);
		}
	}
		
}
