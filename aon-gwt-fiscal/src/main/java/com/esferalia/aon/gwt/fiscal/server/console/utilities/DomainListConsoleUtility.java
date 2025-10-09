package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.text.MessageFormat;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

class DomainListConsoleUtility extends AbstractConsoleUtility {

	@Override
	protected String getTitle() {
		return "Información acerca de dominios";
	}
	
	@Override
	protected void doUtility(String processId, ConsoleParams params, DomainParams domainParams) {
		try {
			validate( params );
			AONContext ctx = params.getFromConnection().getAONContext();
			listDomainInfo(ctx, processId, params);
		} catch (AonConsoleUtilityException e) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e.getMessage()));
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, "Final del proceso."));	
			
		}
		
	}

	private void validate(ConsoleParams params) throws AonConsoleUtilityException{
		if (params.getFromConnection() == null) {
			throw new AonConsoleUtilityException("No hay conexión a la base de datos.");
		}
		if (params.getFromConnection().getAONContext() == null) {
			throw new AonConsoleUtilityException("No hay contexto AON.");
		}
	}

	private void listDomainInfo(AONContext ctx, String processId, ConsoleParams params) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, getDomainTotal(ctx)));
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, getInactiveDomains(ctx)));
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, getActiveDomains(ctx)));
		getDomainByType(ctx, processId, params);
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, getNoScopeDomains(ctx)));
		
	}

	private String getDomainTotal(AONContext ctx) {
		return MessageFormat.format("Total dominios: {0}", 
			ctx.getDslContext()
				.select( DSL.count())
				.from( DOMAIN )
				.fetchOne()
				.getValue( DSL.count() )
		);
	}

	private String getActiveDomains(AONContext ctx) {
		return MessageFormat.format("Total dominios activos: {0}", 
			ctx.getDslContext()
				.select( DSL.count())
				.from( DOMAIN )
				.where( DOMAIN.ACTIVE.eq((byte) 1) )
				.fetchOne()
				.getValue( DSL.count() )
		);
	}

	private String getInactiveDomains(AONContext ctx) {
		return MessageFormat.format("Total dominios inactivos: {0}", 
			ctx.getDslContext()
				.select( DSL.count())
				.from( DOMAIN )
				.where( DOMAIN.ACTIVE.eq((byte) 0) )
				.fetchOne()
				.getValue( DSL.count() )
		);
	}
	
	private void getDomainByType(AONContext ctx, String processId, ConsoleParams params) {
		ctx.getDslContext()
			.select( DSL.count(), DOMAIN.TYPE)
			.from( DOMAIN )
			.where( DOMAIN.ACTIVE.eq((byte) 1) )
			.groupBy( DOMAIN.TYPE )
			.fetch()
			.stream()
			.forEach( r -> {
				DomainType dt = DomainType.safeValueOf( r.getValue( DOMAIN.TYPE ) );
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, 
					MessageFormat.format("{0} Dominios activos de tipo: {1}",r.getValue( DSL.count() ),(dt == null ? "DESCONOCIDO" : dt.getName()))
				));
			})
		;
	}
	
	private String getNoScopeDomains(AONContext ctx) {
		return MessageFormat.format("Total dominios activos sin scopes: {0}", 
			ctx.getDslContext()
				.select( DSL.count())
				.from( DOMAIN )
				.leftOuterJoin(SCOPE).on(SCOPE.DOMAIN.eq(DOMAIN.ID))
				.where( DOMAIN.ACTIVE.eq((byte) 0) )
				.and( SCOPE.ID.isNull() )
				.fetchOne()
				.getValue( DSL.count() )
		);
	}

}
