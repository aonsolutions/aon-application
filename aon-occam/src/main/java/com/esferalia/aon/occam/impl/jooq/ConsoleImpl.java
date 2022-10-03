package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Named;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IConsole;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDeleteDomain;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.console.ConsoleDAO;

public class ConsoleImpl implements IConsole {

	@Override
	public String[] getSchemaNames(AONContext ctx) {
		return ConsoleDAO.getAONSchemas(ctx)
			.map( Named::getName )
			.toArray(i -> new String[i]);
	}
	
	@Override
	public Stream<ConsoleDomain> getDomains(CloseableAONContext ctx, DomainParams params ) {
		return ConsoleDAO.getDomains(ctx, params);
	}
	
	@Override
	public boolean deleteDomain(ConsoleParams params) {
		return ConsoleDeleteDomain.delete(params);
	}
	
	@Override
	public Domain changeActive(CloseableAONContext ctx, Integer domainId, boolean active) {
		return ConsoleDAO.changeActive(ctx, domainId, active);
	}
	
	@Override
	public Domain changeExpirationDate(CloseableAONContext ctx, Integer domainId, Date expireDate) {
		return ConsoleDAO.changeExpirationDate(ctx, domainId, expireDate);
	}
	
	@Override
	public String remoteAccess(CloseableAONContext ctx, Integer domainId) {
		return ConsoleDAO.remoteAccess(ctx, domainId);
	}
}
