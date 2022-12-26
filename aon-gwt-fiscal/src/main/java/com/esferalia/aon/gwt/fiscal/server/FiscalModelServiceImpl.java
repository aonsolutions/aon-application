package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.matrix.FiscalModelService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;

@WebServlet(name = "Fiscal Models Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/FiscalModels" })
public class FiscalModelServiceImpl extends AonStatelessRemoteServiceServlet implements FiscalModelService {

	private static final long serialVersionUID = -8249245114553689144L;

	// ------------------------------------------------------- FISCAL PARAMETERS
//	@Override
//	public LinkedList<IFiscalModel> getFiscalPanel(String domainName,String user, int domain, FiscalMatrixParams params) {
//		return FISCAL.getFiscalPanel(domainName, domain,params,user);
//	}

	@Override
	public LinkedList<InvoiceFiscalModels> getInvoicesModels(Occam occam, InvoiceModelReportParams params) {
		return FISCAL.getInvoicesModels(occam,params);
	}
	
	@Override
	public Invoice getInvoice(Occam occam, Integer invoiceId) {
		return AON.getInvoice(occam,invoiceId);
	}
}
