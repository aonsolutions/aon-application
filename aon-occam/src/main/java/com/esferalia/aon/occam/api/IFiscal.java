package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;

public interface IFiscal {
	// 			        IRPF
	public Stream<IrpfBreakdown> getIrpfBreakdownSummary(AONContext ctx, IRPFParams params);
	public Stream<IrpfBreakdown> getIrpfBreakdown(AONContext ctx, IRPFParams params);
	
	//					OPERATION (PANEL INGRESOS Y GASTOS)
	@Deprecated
	public Stream<OperationBreakdown> getOperationBreakdown(AONContext ctx, int domain, OperationParams params);
	
	// 			        VAT
	public Stream<VatSummaryContext> getVatSummaryContext(AONContext ctx, AccountingReportParams params);
	public Stream<VatContext> getVatContext(AONContext ctx, AccountingReportParams params);
	
	// 			        FISCAL PANEL
//	public LinkedList<IFiscalModel> getFiscalPanel(AONContext ctx,int domain,FiscalMatrixParams params,int user);
	
	// 			   FISCAL MODEL
//	public FiscalModel save(AONContext ctx, FiscalModel fm);
//	public void delete(AONContext ctx, FiscalModel fm);
//	public FiscalModel getModel(AONContext ctx, int id);
	
	// 						SII
	Stream<VatContext> getSiiVatContext(AONContext ctx, AccountingReportParams params, String sii);
	
	public LinkedList<InvoiceFiscalModels> getInvoicesModels(AONContext ctx, InvoiceModelReportParams params);
	
}
