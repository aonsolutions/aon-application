package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IConsole;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDeleteDomain;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.console.ConsoleDAO;

public class ConsoleImpl implements IConsole {

	@Override
	public String[] getSchemaNames(AONContext ctx) {
		return ConsoleDAO.getAONSchemas(ctx)
			.map( schema -> schema.getName() )
			.toArray(i -> new String[i]);
	}
	
	@Override
	public LinkedList<Domain> getDomains(CloseableAONContext ctx, DomainParams params ) {
		return ConsoleDAO.getDomains(ctx, params);
	}
	
	@Override
	public boolean deleteDomain(ConsoleParams params) {
		return ConsoleDeleteDomain.delete(params);
	}
}
