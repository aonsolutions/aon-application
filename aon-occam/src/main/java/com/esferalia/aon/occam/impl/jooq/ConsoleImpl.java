package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IConsole;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.impl.jooq.dao.console.ConsoleDAO;

public class ConsoleImpl implements IConsole {

	@Override
	public String[] getSchemaNames(AONContext ctx) {
		return ConsoleDAO.getAONSchemas(ctx)
			.map( schema -> schema.getName() )
			.toArray(i -> new String[i]);
	}
	
	@Override
	public LinkedList<Domain> getDomains(CloseableAONContext ctx, String schema, String query) {
		return ConsoleDAO.getDomains(ctx, schema, query);
	}	
}
