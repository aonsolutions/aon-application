package com.esferalia.aon.gwt.common.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.CommonMSService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;

@WebServlet(name = "Aon Common MS Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Common", "/aon_gwt_aio/ms/Common"})
public class CommonMSServiceImpl extends AonStatelessRemoteServiceServlet implements CommonMSService {

	private static final long serialVersionUID = -7754257588392706830L;

	// --------------------------------------------------------- CONFIGURATION
	@Override
	public AonConfiguration getAonConfiguration(String domainName, String user, int domain) {
		return AON.getConfiguration(domainName, domain,user, null);
	}
	
}
