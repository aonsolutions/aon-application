package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.text.MessageFormat;
import java.util.Date;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.server.AonDateUtils;

class DomainListConsoleUtility extends AbstractConsoleUtility {

	@Override
	protected String getTitle() {
		return "Informaci\u00F3n acerca de dominios";
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
			throw new AonConsoleUtilityException("No hay conexi\u00F3n a la base de datos.");
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
		getDomainLastAccessDateRange(ctx, processId, params, 2, 1, "Dominios activos sin acceso en el \u00FAltimo a\u00F1o");
		getDomainLastAccessDateRange(ctx, processId, params, 3, 2, "Dominios activos sin acceso en los \u00FAltimos dos a\u00F1os");
		getDomainLastAccessDateRange(ctx, processId, params, 4, 3, "Dominios activos sin acceso en los \u00FAltimos tres a\u00F1os");
		getDomainLastAccessDateRange(ctx, processId, params, 4, null, "Dominios activos sin acceso en los \u00FAltimos cuatro a\u00F1os");
		getDomainExpiredActives(ctx, processId, params);
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
	
	private void getDomainLastAccessDateRange(AONContext ctx, String processId, ConsoleParams params, int fromYearsAgo, Integer toYearsAgo, String subtitle) {
		MutableBoolean something = new MutableBoolean(false);
		String messageFormat = "\u00DAltimo acceso : {0} - Id: {1} {2} - {3}";
		Date now = new Date();
		Date fromDate = AonDateUtils.addYears(now, -fromYearsAgo);
		Date toDate = toYearsAgo != null ? AonDateUtils.addYears(now, -toYearsAgo) : null;
		Condition condition = DOMAIN.ACTIVE.eq((byte) 1);
		if (toDate != null) {
			condition = condition.and(DOMAIN.LASTACCESS_DATE.between(AonDateUtils.toTimestamp(fromDate), AonDateUtils.toTimestamp(toDate)));
		} else {
			condition = condition.and(DOMAIN.LASTACCESS_DATE.lessThan(AonDateUtils.toTimestamp(fromDate)));
		}
		ctx.getDslContext()
			.select()
			.from(DOMAIN)
			.where(condition)
			.groupBy(DOMAIN.TYPE)
			.fetch()
			.stream()
			.map(new DomainFiller())
			.forEach(d -> { 
				if (something.isFalse()) {
					ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.subtitle(processId, subtitle));
					something.setValue(true);
				}
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,
					MessageFormat.format(messageFormat,
						d.getLastAccessDate(),
						d.getId(),
						d.getName(),
						d.getDescription()
					)));
			}
		);
		if (something.isFalse()) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.subtitle(processId, "SIN " + subtitle));
		}
	}
	
	private void getDomainExpiredActives(AONContext ctx, String processId, ConsoleParams params) {
		MutableBoolean something = new MutableBoolean(false);
		String subtitle = "Dominios activos expirados";
		ctx.getDslContext()
			.select()
			.from(DOMAIN)
			.where(DOMAIN.ACTIVE.eq((byte) 1)
			.and(DOMAIN.EXPIRATIONDATE.lt(AonDateUtils.toSql(new Date()))))
			.fetch()
			.stream()
			.map(new DomainFiller())
			.forEach(d -> {
				if (something.isFalse()) {
					ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.subtitle(processId, subtitle ));
					something.setValue(true);
				}
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,
					MessageFormat.format("Fecha de expiraci\u00F3n : {0} - Id: {1} {2} - {3}",
						d.getExpirationDate(),
						d.getId(),
						d.getName(),
						d.getDescription()
				)));
			}
		);
		if (something.isFalse()) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.subtitle(processId, "SIN " + subtitle));
		}
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

	private static class DomainFiller extends Filler implements Function<Record, Domain> {
		@Override
		public Domain apply(Record r) {
			return build(r);
		}
		
		public static Domain build(Record r) {
			return new Domain()
				.setId(getValue(r, DOMAIN.ID))
				.setName(getValue(r, DOMAIN.NAME))
				.setDescription(getValue(r, DOMAIN.DESCRIPTION))
				.setParentId(getValue(r, DOMAIN.PARENT))
				.setDomainType(DomainType.safeValueOf(getValue(r, DOMAIN.TYPE)))
				.setScope(getValue(r, DOMAIN.SCOPE))
				.setEnableHeredity(getBoolean(r, DOMAIN.ENABLEHEREDITY))
				.setDomainManagement(getBoolean(r, DOMAIN.DOMAINMANAGEMENT))
				.setDisableDomainManagement(getBoolean(r, DOMAIN.DISABLEDOMAINMANAGEMENT))
				.setMaxDocumentSize(getValue(r, DOMAIN.MAXDOCUMENTSIZE))
				.setMaxTotalDocumentSize(getValue(r, DOMAIN.MAXTOTALDOCUMENTSIZE))
				.setMaxDefinedUsers(getValue(r, DOMAIN.MAXDEFINEDUSERS))
				.setActive(getBoolean(r, DOMAIN.ACTIVE))
				.setOwner(getValue(r, DOMAIN.OWNER))
				.setCreationUser(getValue(r, DOMAIN.CREATION_USER))
				.setCreationDate(getValue(r, DOMAIN.CREATION_DATE))
				.setModificationUser(getValue(r, DOMAIN.MODIFICATION_USER))
				.setModificationDate(getValue(r, DOMAIN.MODIFICATION_DATE))
				.setLastAccessUser(getValue(r, DOMAIN.LASTACCESS_USER))
				.setLastAccessDate(getValue(r, DOMAIN.LASTACCESS_DATE))
				.setExpirationDate(getValue(r, DOMAIN.EXPIRATIONDATE))
				.setAonCustomer(getValue(r, DOMAIN.AONCUSTOMER))
				.setAonStatus(AonStatus.safeValueOf(getValue(r, DOMAIN.AONSTATUS)))
				.setSubDomainSuffix(getValue(r, DOMAIN.SUBDOMAINSUFFIX))
				;	
		}

	}
}