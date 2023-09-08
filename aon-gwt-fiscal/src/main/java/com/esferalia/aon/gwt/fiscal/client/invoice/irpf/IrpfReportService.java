package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/IrpfReport")
public interface IrpfReportService extends RemoteService {

	IrpfSummary getIrpfBreakdownSummary(Occam occam, IRPFParams params) throws AonCoreException;
	LinkedList<IrpfBreakdown> getIrpfBreakdown(Occam occam, IRPFParams params) throws AonCoreException;
	Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException;

}
