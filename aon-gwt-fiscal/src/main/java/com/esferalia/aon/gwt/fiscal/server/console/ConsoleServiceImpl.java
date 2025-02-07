package com.esferalia.aon.gwt.fiscal.server.console;


import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleService;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleSchema;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.j2html.tags.specialized.DivTag;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import static com.esferalia.aon.watson.j2html.TagCreator.*;

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
	public Boolean switchRemoteAccess(DomainParams params, Integer domainId) {
		return CONSOLE.switchRemoteAccess(params, domainId);
	}
	@Override
	public LinkedList<User> availableUsers(Occam occam, Integer domainId) throws AonCoreException {
		return CONSOLE.availableUsers(occam, domainId);
	}
	
	@Override
	public ConsoleTableRow getTableRow(ConsoleTableRow row) throws AonCoreException {
		return CONSOLE.getTableRow(row);
	}
	
	@Override
	public LinkedList<ConsoleTableRow> getTableRows(ConsoleTableRow row) throws AonCoreException {
		return CONSOLE.getTableRows(row);
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

	@Override
	public String testConnections() {
		List<ConsoleSchema> css = Arrays.asList(ConsoleSchema.values());
		return div(
			each( css , cs -> {
				String result = "";
				try (CloseableAONContext ctx = AONContext.getAONContext(cs.getDomainName(), cs.getUser())) {
					result = "OK";
				} catch (Exception e) {
					result = "ERROR " + e.getMessage();
				}
				return div( "Testing ...: " + cs.getSchema()  + " --> " + result);
			}) 
		).render();
	}
}
