package com.esferalia.aon.gwt.fiscal.server;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.config.FiscalConfigService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Aon MS Fiscal Config Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/FiscalConfig" })
public class FiscalConfigServiceImpl extends AonStatelessRemoteServiceServlet implements FiscalConfigService {

	private static final long serialVersionUID = -1495086199296794184L;

	@Override
	public AonConfiguration getConfiguration(Occam occam) throws AonCoreException {
		
		return AON.getFiscalConfiguration(occam);
	}

	@Override
	public ApplicationParameter saveParam(Occam occam, ApplicationParameter ap) throws AonCoreException {
		return AON.saveApplicationParameter(occam, ap);
	}


}
