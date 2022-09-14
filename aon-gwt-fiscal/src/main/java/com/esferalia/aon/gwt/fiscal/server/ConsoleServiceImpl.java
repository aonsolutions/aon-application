package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleService;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Aon MS Console Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Console" })
public class ConsoleServiceImpl extends AonStatelessRemoteServiceServlet implements ConsoleService {

	private static final long serialVersionUID = -1495086199296794184L;

	@Override
	public String[] getSchemas(Occam occam) throws AonCoreException {
		return CONSOLE.getSchemaNames(occam);
	}

	@Override
	public LinkedList<Domain> getDomains(DomainParams params) throws AonCoreException {
		return CONSOLE.getDomains(params);
	}
	
	@Override
	public Boolean deleteDomain(DomainParams params, Integer domainId) throws AonCoreException {
		return CONSOLE.deleteDomain(params, domainId);
	}
}
