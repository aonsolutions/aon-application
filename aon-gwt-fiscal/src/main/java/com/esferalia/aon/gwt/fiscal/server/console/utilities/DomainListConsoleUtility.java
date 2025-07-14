package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import org.jooq.conf.ParamType;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.console.ConsoleDAO;

class DomainListConsoleUtility extends AbstractConsoleUtility {

	protected void doUtility(String processId, ConsoleParams params, DomainParams domainParams) {
		AONContext ctx = params.getFromConnection().getAONContext();
		System.out.println( 
				ctx.getDslContext()
				.select(DOMAIN.ID, DOMAIN.NAME,DOMAIN.DESCRIPTION)
				.from(DOMAIN)
				.where( ConsoleDAO.getFilter( domainParams ) )
				.getSQL( ParamType.INLINED) 
				
				);
		ctx.getDslContext()
			.select(DOMAIN.ID, DOMAIN.NAME,DOMAIN.DESCRIPTION)
			.from(DOMAIN)
			.where( ConsoleDAO.getFilter( domainParams ) )
			.fetch()
			.stream()
			.map( r -> r.getValue(DOMAIN.ID) 
					+ " " 
					+ r.getValue(DOMAIN.NAME)
					+ " - " 
					+ r.getValue(DOMAIN.DESCRIPTION))
			.forEach( d -> ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, d)))
		;
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Final del proceso."));
	}
}
