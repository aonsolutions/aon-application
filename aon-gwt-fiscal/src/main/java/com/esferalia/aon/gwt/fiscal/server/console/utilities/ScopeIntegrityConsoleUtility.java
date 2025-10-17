package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.text.MessageFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.ScopeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class ScopeIntegrityConsoleUtility extends AbstractConsoleUtility {

	private static final String DOMAIN_NEW_SCOPE = "{0} (New scope: {1})";
	private static final String NO_SCOPES       = " SIN SCOPES";
	private static final String ONE_SCOPE       = " OK 1 SCOPE ({0})";
	private static final String MULTIPLE_SCOPES = "DOMINIO CON {0} SCOPES";
	private static final String UNUSED_SCOPE_DELETED = "Borrando scope {0} no utilizado";
	
	private static record Context(Domain domain, long count, Integer newScope) {}

	@Override
	protected String getTitle() {
		return "Arreglo integridad de SCOPE";
	}
	
	@Override
	protected void doUtility(String processId, ConsoleParams params, DomainParams domainParams) {
		try {
			validate( params, domainParams );
			AONContext ctx = params.getFromConnection().getAONContext();
			check(ctx, processId, params, domainParams);
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Fin del proceso"));
		} catch (AonConsoleUtilityException e) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e.getMessage()));
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, "Final del proceso."));	
			
		}
		
	}

	private void validate(ConsoleParams params, DomainParams domainParams) throws AonConsoleUtilityException{
		if (domainParams == null) {
			throw new AonConsoleUtilityException("No hay condiciones de búsqueda");
		}
		if (domainParams.getId() == null && domainParams.getParent() == null) {
			throw new AonConsoleUtilityException("Debe indicar un ID de dominio o de padre, de manera temporal");
		}
		if (params.getFromConnection() == null) {
			throw new AonConsoleUtilityException("No hay conexi\u00F3n a la base de datos.");
		}
		if (params.getFromConnection().getAONContext() == null) {
			throw new AonConsoleUtilityException("No hay contexto AON.");
		}
	}

	private void check(AONContext ctx, String processId, ConsoleParams params, DomainParams domainParams) {
		Condition cond = DOMAIN.PARENT.isNotNull(); 
		if (domainParams != null && domainParams.getId() != null) {
			cond = cond.and( DOMAIN.ID.eq(domainParams.getId()) );
		}
		if (domainParams != null && domainParams.getParent() != null) {
			cond = cond.and( DOMAIN.PARENT.eq(domainParams.getParent()) );
		}
		ctx.getDslContext()
			.select()
			.from(DOMAIN)
			.where(cond)
			.fetch()
			.stream()
			.map( new DomainFiller() )
			.forEach( d -> checkTables(ctx,processId, params, d))
		;
	}
	
	
	private static void checkTables(AONContext ctx, String processId, ConsoleParams params, Domain d) {
		ctx.getDslContext().transaction( trx -> {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.subtitle(processId,MessageFormat.format("Checking {0} -- {1} domain",d.getId(),d.getName()) ));
			Context c = getDomainContext(ctx, processId, params,d);
			if (c.newScope != null) {
				AonCollectionUtils.stream(ScopeDAO.SCOPE_TABLES)
					.forEach( t -> checkTable(ctx, processId, params, c, t) );
			}
		});
	}
	
	private static void checkTable(AONContext ctx, String processId, ConsoleParams params, Context c, Table<?> table) {
		int allRows = getRows( ctx, c.domain, table);
		if (allRows > 0) {
			Integer[] scopes = getWrongScopes(ctx, c.domain, table);
			if (AonCollectionUtils.size(scopes) > 0) {
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId
					,MessageFormat.format("Checking {0} ... {1} filas",table.getName(),allRows)));
				for (Integer s : scopes) {
					int updated = ctx.getDslContext()
						.update(table)
						.set( getScopeField(table), c.newScope )
						.where( getDomainField(table).eq(c.domain.getId()) )
						.and( getScopeField(table).eq(s) )
						.execute();
					ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId
						,MessageFormat.format("Scope {0} modificado por {1} ({2} filas modificadas en la tabla {3})"
							,s,c.newScope,updated,table.getName())));
				}
			}
		} 
	}
	
	private static Integer[] getWrongScopes(AONContext ctx, Domain domain, Table<?> table) {
		final Field<Integer> domainField = getDomainField(table);
		final Field<Integer> scopeField = getScopeField(table);
		return ctx.getDslContext().select( scopeField )
			.from(table)
			.join( SCOPE ).on( scopeField.eq(SCOPE.ID))
			.where( domainField.eq(domain.getId()))
			.and( domainField.ne(SCOPE.DOMAIN) )
			.groupBy( scopeField )
			.fetch()
			.stream()
			.map( r -> r.getValue(scopeField) )
			.toArray( Integer[]::new )
		;
	}
	
	private static int getRows(AONContext ctx, Domain domain, Table<?> table) {
		final Field<Integer> domainField = getDomainField(table);
		AggregateFunction<Integer> countField = DSL.count();
		return ctx.getDslContext().select( DSL.count() )
			.from(table)
			.where( domainField.eq(domain.getId()))
			.fetchOne()
			.getValue(countField);
	}

	private static Context getDomainContext(AONContext ctx, String processId, ConsoleParams params, Domain d) throws AonConsoleUtilityException {
		tryToDeleteUnusedDomainScopes( ctx, processId, params, d);
		long scopes = ctx.getDslContext()
			.select()
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(d.getId()))
			.fetch()
			.stream()
			.count();
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId
			,MessageFormat.format("{0} (parent: {1}) {2} {3}"
				,AonStringUtils.leftPad( AonNumberUtils.toString(d.getId()), 6) 
				,AonStringUtils.leftPad( AonNumberUtils.toString(d.getParentId()), 6)
				,AonStringUtils.rightPad(AonStringUtils.abbreviate(d.getName(), 80), 80)
				,AonStringUtils.rightPad(AonStringUtils.abbreviate(d.getDescription(), 50), 50)
			)
		));
		
		Integer newScope = null;
		if (scopes < 1) {
			// No hay ningún scope definido en la empresa, se crea uno y todas las 
			// dependencias con scope del dominio se cambiarán por el scope creado. 
			newScope = ctx.getDslContext()
				.insertInto(SCOPE)
				.set(SCOPE.DOMAIN, d.getId())
				.set(SCOPE.DESCRIPTION, "EMPRESA")
				.returning(SCOPE.ID)
				.fetchOne()
				.getValue(SCOPE.ID);
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId
				,MessageFormat.format(DOMAIN_NEW_SCOPE
					,AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(NO_SCOPES), 11)
					,AonStringUtils.leftPad( AonNumberUtils.toString(newScope), 6)
				)
			));
		} else if (scopes == 1) {	
			// Existe un solo scope en el dominio, por lo que todas las
			// dependencias con scope del dominio se cambiarán por el scope existente
			newScope = ctx.getDslContext()
					.select(SCOPE.ID)
					.from(SCOPE)
					.where(SCOPE.DOMAIN.eq(d.getId()))
					.fetchOne()
					.getValue(SCOPE.ID);
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,MessageFormat.format(ONE_SCOPE,newScope)));
		} else {
			// Existe más de un scope en el dominio
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId
				,MessageFormat.format(MULTIPLE_SCOPES,AonStringUtils.leftPad( AonNumberUtils.toString(scopes), 4))
			));
		}
		return new Context(d, scopes, newScope);
	}
	
	private static void tryToDeleteUnusedDomainScopes(AONContext ctx, String processId, ConsoleParams params, Domain d) {
		Map<Integer, Scope> usedScopes = getUsedScopesInDomain( ctx, d )
			.collect( Collectors.toMap(
					Scope::getId
					,Function.identity()
					,(a, b) -> a))
		;
		ctx.getDslContext()
			.select(SCOPE.ID)
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(d.getId()))
			.fetch()
			.stream()
			.map( r -> r.getValue(SCOPE.ID))
			.filter( id -> id != null)
			.filter( id -> !usedScopes.containsKey(id))
			.forEach( id -> {
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId
					,MessageFormat.format(UNUSED_SCOPE_DELETED,id)));
				ScopeDAO.delete( ctx, d.getId(), id);
		});
		
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
				.setActive(getBoolean(r, DOMAIN.ACTIVE))
				.setOwner(getValue(r, DOMAIN.OWNER))
				.setCreationUser(getValue(r, DOMAIN.CREATION_USER))
				.setCreationDate(getValue(r, DOMAIN.CREATION_DATE))
				.setModificationUser(getValue(r, DOMAIN.MODIFICATION_USER))
				.setModificationDate(getValue(r, DOMAIN.MODIFICATION_DATE))
				.setLastAccessUser(getValue(r, DOMAIN.LASTACCESS_USER))
				.setLastAccessDate(getValue(r, DOMAIN.LASTACCESS_DATE))
				.setExpirationDate(getValue(r, DOMAIN.EXPIRATIONDATE))
				;	
		}

	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field("domain");
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getScopeField(Table<T> table) {
		return (TableField<T, Integer>) table.field("scope");
	}
	
	private static Stream<Scope> getUsedScopesInDomain(AONContext ctx, Domain d) {
		Map<Integer,Scope> scopesMap = getAvailableScopes( ctx, d )
			.collect( Collectors.toMap(Scope::getId,Function.identity()));
		if (AonCollectionUtils.isNotEmpty(scopesMap)) {
			return AonCollectionUtils.valuesStream( 
				AonCollectionUtils.stream( ScopeDAO.SCOPE_TABLES )
					.flatMap( t -> {
						Field<Integer> domainField = getDomainField(t);
						Field<Integer> scopeField = getScopeField(t);
						return ctx.getDslContext().select(scopeField)
								.from(t)
								.where( domainField.eq(d.getId()) )
								.groupBy( scopeField )
								.fetch()
								.stream()
								.map( r -> r.getValue(scopeField))
								.filter( i -> i != null);
					})
					.filter( scopesMap::containsKey )		//	Solo se ven los scopes a los que el usuario puede acceder.
					.map( scopesMap::get )
					.collect( Collectors.toMap(
						Scope::getId
						,Function.identity()
						,(a, b) -> a))
			);
		}
		return Stream.empty();
	}
	
	private static Stream<Scope> getAvailableScopes (AONContext ctx, Domain d) {
		boolean parentDomainUser = d.getParentId() != null; 
		Integer[] domains =   parentDomainUser
			? new Integer[] { d.getId(),d.getParentId()}
			:new Integer[] { d.getId()}
		;
		return ctx.getDslContext()
			.select()
			.from(SCOPE)
			.where(SCOPE.DOMAIN.in(domains))
			.fetch()
			.stream()
			.map(new ScopeFiller())
		;
	}
}