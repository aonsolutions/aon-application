package com.esferalia.aon.gwt.fiscal.server.console;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleService;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
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
	public ConsoleTableRow getTableRow(ConsoleTableRow row) throws AonCoreException {
		return CONSOLE.getTableRow(row);
	}
	
	@Override
	public ConsoleTableRow getTableRowMetadata(ConsoleTableRow row) throws AonCoreException {
		return CONSOLE.getTableRowMetadata(row);
	}

	@Override
	public String[] getAonTables() throws AonCoreException {
		return CONSOLE.getAonTables();
	}

	@Override
	public ConsoleTableRow update(ConsoleTableRow row,ConsoleTableField field) throws AonCoreException {
		return CONSOLE.update(row, field);
	}
	
	@Override
	public Boolean delete(ConsoleTableRow row) throws AonCoreException {
		return CONSOLE.delete(row);
	}
	
}
