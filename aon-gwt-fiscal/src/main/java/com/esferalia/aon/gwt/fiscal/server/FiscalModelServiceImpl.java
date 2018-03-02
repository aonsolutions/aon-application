package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.matrix.FiscalModelService;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

@WebServlet(name = "Fiscal Models Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/FiscalModels" })
public class FiscalModelServiceImpl extends AonStatelessRemoteServiceServlet implements FiscalModelService {

	private static final long serialVersionUID = -8249245114553689144L;

	// ------------------------------------------------------- FISCAL PARAMETERS
	@Override
	public LinkedList<IFiscalModel> getFiscalPanel(String domainName,String user, int domain, FiscalMatrixParams params) {
		return FISCAL.getFiscalPanel(domainName, domain,params,user);
	}
	
}
