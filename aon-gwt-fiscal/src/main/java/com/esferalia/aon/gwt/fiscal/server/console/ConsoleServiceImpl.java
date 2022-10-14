package com.esferalia.aon.gwt.fiscal.server.console;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleService;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Aon MS Console Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Console" })
public class ConsoleServiceImpl extends AonStatelessRemoteServiceServlet implements ConsoleService {

	private static final long serialVersionUID = -1495086199296794184L;

	@Override
	public String[] getSchemas(Occam occam) throws AonCoreException {
		return CONSOLE.getSchemaNames(occam);
	}

	@Override
	public LinkedList<ConsoleDomain> getDomains(DomainParams params) throws AonCoreException {
		return CONSOLE.getDomains(params)
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public Boolean deleteDomain(DomainParams params, Integer domainId) throws AonCoreException {
		return CONSOLE.deleteDomain(params, domainId);
	}
	
	@Override
	public Domain changeActive(DomainParams params, Integer domainId, boolean active) throws AonCoreException {
		return CONSOLE.changeActive(params, domainId, active);
	}
	
	@Override
	public Domain changeExpirationDate(DomainParams params, Integer domainId, Date expireDate ) throws AonCoreException {
		return CONSOLE.changeExpirationDate(params, domainId, expireDate);
	}
	
	@Override
	public String remoteAccess(DomainParams params, Integer domainId) {
		return CONSOLE.remoteAccess(params, domainId);
	}
	@Override
	public Boolean fix(ConsoleDomainMessage consoleMessage) throws AonCoreException {
		return CONSOLE.fix(consoleMessage);
	}
	
	@Override
	public LinkedHashMap<String, Object> viewRow(String schema, String tableName, Integer id) throws AonCoreException {
		return CONSOLE.viewRow(schema, tableName, id);
	}
}
