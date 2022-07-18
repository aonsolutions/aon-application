package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.IrpfReportService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Irpf Report Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/IrpfReport" })
public class IrpfReportServiceImpl extends AonStatelessRemoteServiceServlet implements IrpfReportService {


	private static final long serialVersionUID = -3238466552011743676L;

	@Override
	public LinkedList<IrpfBreakdown> getIrpfBreakdownSummary(Occam occam, IRPFParams params) throws AonCoreException {
		return FISCAL.getIrpfBreakdownSummary(occam, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public LinkedList<IrpfBreakdown> getIrpfBreakdown(Occam occam, IRPFParams params) throws AonCoreException {
		return FISCAL.getIrpfBreakdown(occam, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public Invoice getInvoice(Occam occam, int invoiceId) throws AonCoreException {
		return AON.getInvoice(occam, invoiceId);
	}
	
}
