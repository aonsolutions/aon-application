package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.impl.jooq.dao.OLDVATDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.finance.FinanceUtils;

public class FiscalImpl implements IFiscal {

	
	// ---------------------------------------------------- [VAT]
	@Override
	public Stream<VatSummaryContext> getVatSummaryContext(AONContext ctx, AccountingReportParams params) {
		return VATDAO.getVatSummary(ctx, params);
//		return OLDVATDAO.getVatSummary(ctx, params.getFromDate()
//				,params.getToDate(),p -> FinanceUtils.getVATFilter(p, params))
//				.stream();
	}
	
	@Override
	public Stream<VatContext> getVatContext(AONContext ctx, AccountingReportParams params) {
		return VATDAO.getVatBreakdown(ctx, params);
//		return OLDVATDAO.getVatBreakdown(ctx, params.getFromDate()
//				,params.getToDate(),p -> FinanceUtils.getVATFilter(p, params));
	}
	
	@Override
	public Stream<VatContext> getSiiVatContext(AONContext ctx, AccountingReportParams params, String sii) {
		return OLDVATDAO.getSiiVatContext(ctx, p -> FinanceUtils.getVATFilter(p, params), sii);
	}

	// ---------------------------------------------------- [IRPF]
	@Override
	public IrpfSummary getIrpfBreakdownSummary(AONContext ctx, IRPFParams params) {
		return IRPFDAO.getIRPFSummary( ctx, params );
	}
	
	@Override
	public Stream<IrpfBreakdown> getIrpfBreakdown(AONContext ctx, IRPFParams params) {
		return  IRPFDAO.getInvoicesIrpfBreakdown(ctx,params);
	}
	
		@Override
	public LinkedList<InvoiceFiscalModels> getInvoicesModels(AONContext ctx, InvoiceModelReportParams params) {
		return com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO.getInvoicesModels(ctx, params);		}
}
