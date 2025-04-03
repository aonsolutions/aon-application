package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.QOM.SetCatalog;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class ConsoleDomainCheckScopeIntegrity {

	private static final String DOMAIN_LABEL = "domain";
	private static final String SCOPE_LABEL = "scope";
	
	private ConsoleDomainCheckScopeIntegrity() {
	}
	
	public static void check(ConsoleParams params) {
		String processId = AonRandomStringUtils.randomAlphabetic(4) + "_" + (new Date()).getTime();
		try {
			ConsoleMessageUtils.start(params.getPrinter());
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.title(processId, "VALIDACION DE SCOPES DEL DOMINIO"));
			check(processId, params);
		} finally {
			ConsoleMessageUtils.end(params.getPrinter());
		}
	}
	
	public static void check(String processId, ConsoleParams params) {
		Integer domainId = params.getFromConnection().getDomain().getId();
		Domain fullDomain = DomainDAO.getDomain(params.getFromConnection().getAONContext(), domainId);
		if (fullDomain == null) {
			String msg = "No se ha encontrado el dominio \"" + domainId + "\"";
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
			throw new AonCoreException(msg);
		}
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, MessageFormat.format("Dominio {0} encontrado", domainId)));		
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, MessageFormat.format("Dominio. Herencia:  {0}", fullDomain.isEnableHeredity())));
		params.getFromConnection().setDomain(fullDomain);
		params.setScript( new LinkedHashMap<>() );
		params.getFromDslContext().transaction(conf -> {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Generando lista de tablas"));
			params.setPartialCount(
				(int) AonCollectionUtils.stream(params.getFromConnection().getSchema().getTables())
						.filter(Objects::nonNull )
						.filter(t -> !DOMAIN_LABEL.equals(t.getName()))
						.filter(t ->  AonCollectionUtils.stream(t.fields()).anyMatch( f -> SCOPE_LABEL.equals(f.getName()) ) )  
						.count()
			);
			params.setPartialProgress(0);

			AonCollectionUtils.stream(params.getFromConnection().getSchema().getTables())
				.filter(Objects::nonNull )
				.filter(t -> !DOMAIN_LABEL.equals(t.getName()))
				.filter(t ->  AonCollectionUtils.stream(t.fields()).anyMatch( f -> SCOPE_LABEL.equals(f.getName()) ) )  
				.forEach(t -> checkTable(processId, params, t ))
			;
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Final del proceso de chequeo de integridad de dominios"));
			if (!params.getErrors().isEmpty()) {
				String msg = (params.getErrors().size() == 1)
					?"Se ha encontrado 1 incidencia."
					:"Se han encontrado "+ params.getErrors().size() +" incidencias.";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				params.getErrors().stream().forEach( e -> ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e)));
			} else {
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "NO se han encontrado incidencias!"));
			}
		});
	}
	
	// **************************************************************
	// ************* [METHODS  FOR GETTING SCRIPT ] *****************
	// **************************************************************
	private static void checkTable(String processId, ConsoleParams params, Table<?> t) {
		String msg = MessageFormat.format("Checking {0} table:", t.getName());
		Integer domain = params.getFromConnection().getDomain().getId();
		params.setPartialProgress((params.getPartialProgress() + 1));
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.progress(processId
			,params.getPartialCount()
			,params.getPartialProgress() )
			.setMessage(msg));
		if (hasScope(t)) {
			Field<?> pkField = t.field(t.getPrimaryKey().getFields().get(0).getName());
			AggregateFunction<Integer> count = DSL.count( pkField );
			params.getFromDslContext()
				.select( getScopeField(t), count )
				.from( DOMAIN  )
				.innerJoin( t ).on( getDomainField(t).eq(DOMAIN.ID))
				.innerJoin( SCOPE ).on( SCOPE.ID.eq(getScopeField(t)).and( SCOPE.DOMAIN.ne(DOMAIN.ID)).and( SCOPE.DOMAIN.ne(DOMAIN.PARENT)))
				.where( getDomainField(t).eq(domain) )
				.groupBy( getScopeField(t) )
				.fetch()
				.stream()
				.forEach( r -> {
					Integer s  = r.getValue( getScopeField(t) );
					Integer c = r.getValue( count );
					params.addError(
						new ConsoleDomainMessage()
							.setType(ConsoleDomainMessageType.SCOPE_INTEGRITY)	
							.setSchema( params.getFromConnection().getSchemaName() )
							.setTable( t.getName() )
							.setDomainId( domain )
							.setWrongScopeId(s)
							.setCount( c )
							.setMessage(
								MessageFormat.format("{0} filas en la tabla {1} apuntan a un scope incorrecto {2}."
								,c
								,t.getName()
								,s
								)
							)
					);
				});
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_LABEL);
	}
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getScopeField(Table<T> table) {
		return (TableField<T, Integer>) table.field(SCOPE_LABEL);
	}
	
	private static <T extends Record> boolean hasScope(Table<T> table) {
		return (getScopeField(table) != null);
	}

}
