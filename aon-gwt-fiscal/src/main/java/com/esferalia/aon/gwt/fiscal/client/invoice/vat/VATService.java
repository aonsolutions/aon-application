package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/VAT")
public interface VATService extends RemoteService {

	AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException;
	LinkedList<VatSummaryContext> getVatSummaryContext(Occam occam,AccountingReportParams params) throws AonCoreException;
	LinkedList<VatContext> getVatContext(Occam occam,AccountingReportParams params) throws AonCoreException;
//	String getVatContextReport(Occam occam,AccountingReportParams params) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;
	
}
