package com.esferalia.aon.occam.impl.jooq.console;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.text.MessageFormat;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ConsoleDeleteDomain {
	
	private static final String DOMAIN_FIELD = "domain";
	
	private ConsoleDeleteDomain() {
	}
	
	public static boolean delete(ConsoleParams params) {
		if (params.getFromConnection() == null || params.getFromConnection().getAONContext().getDslContext() == null) {
			String msg = "No se ha definido la conexión origen a la BD";
			ConsoleUtils.log(params,msg);
			throw new AonCoreException(msg);
		}
		return deleteDomain(params);		
	}
		
	private static boolean deleteDomain(ConsoleParams params) {
		try {
			ConsoleUtils.log(params,"** Start domain validation deletion!");
			if (params.getFromConnection() == null || params.getFromConnection().getAONContext().getDslContext() == null) {
				String msg = "No se ha definido la conexión origen a la BD";
				ConsoleUtils.log(params,msg);
				throw new AonCoreException(msg);
			}

			if (params.getFromConnection().getSchema() == null) {
				String schemaName = params.getFromConnection().getSchemaName();
				if (AonStringUtils.isBlank(schemaName)) {
					throw new AonCoreException("No se ha indicado un esquema del que borrar el dominio.");
				}
				Schema fromSchema = params
						.getFromDslContext()
						.meta()
						.getSchemas(schemaName)
						.stream()
						.findFirst()
						.orElse(null);
				if (fromSchema == null) {
					throw new AonCoreException("No se ha encontrado el esquema \""+schemaName+"\".");
				}
				params.getFromConnection().setSchema( fromSchema );
			}

			Domain domain = params.getFromConnection().getFullDomain();
			if (domain == null || domain.getId() == null) {
				throw new AonCoreException("No se ha indicado un dominio que borrar.");
			}
			Integer domainId = domain.getId();
			domain = DomainDAO.getDomain(params.getFromConnection().getAONContext(), domainId);
			if (domain == null) {
				throw new AonCoreException("No se ha encontrado el dominio \"" + domainId + "\"");
			}
			if (domain.getDomainType() == DomainType.ADMIN) {
				throw new AonCoreException("No se puede borrar un dominio de ADMINISTRACION");
			}
			if (domain.isActive()) {
				throw new AonCoreException("No se puede borrar un dominio ACTIVO");
			}
			int count = params.getFromDslContext()
				.select( DSL.count(DOMAIN.ID) )
				.from(DOMAIN)
				.where(DOMAIN.PARENT.eq(domainId))
				.fetch()
				.stream()
				.map( r -> r.get(DSL.count(DOMAIN.ID)) )
				.findFirst()
				.orElse(0);
			if (count > 0) {
				throw new AonCoreException("No se puede borrar un dominio con hijos");
			}
			params.getFromConnection().setFullDomain(domain);
			
			ConsoleUtils.log(params,"** End domain validation!");
			
			
			
			ConsoleUtils.log(params,"** Start domain deletion!");
			ConsoleUtils.disableForeignKeys(params);
			ConsoleUtils.log(params,"** Start transaction!");
			
			
			params.setTotalCount(0);
			params.setTotalProgress(0);
			getStream( params )
				.forEach( t -> params.setTotalCount( params.getTotalCount() + 1) );
			
			params.getToDslContext().transaction(conf -> 
				getStream( params )
					.filter( ConsoleDeleteDomain::hasDomain )
					.forEach(t -> deleteTableRows(params, t))
			);
			params.getFromDslContext()
				.delete(DOMAIN)
				.where( DOMAIN.ID.equal(params.getFromConnection().getFullDomain().getId()))
				.execute();
			ConsoleUtils.log(params,MessageFormat.format(" DOMAIN {0} {1} DELETED!"
				, params.getFromConnection().getFullDomain().getId()
				, params.getFromConnection().getFullDomain().getName()));
			ConsoleUtils.log(params,"** Commit!");
			ConsoleUtils.log(params,"** End domain deletion!");
			return true;
		} catch (Exception e) {
			ConsoleUtils.log(params, e.getMessage() );
			ConsoleUtils.log(params,"** Rollback!");
			e.printStackTrace();
			throw new AonCoreException(e.getMessage(),e);
		} finally {
			if (!params.getErrors().isEmpty()) {
				ConsoleUtils.log(params, "" );
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
				ConsoleUtils.log(params,"** Se han producido incidencias!");
				params.getErrors().stream().forEach( e -> ConsoleUtils.log(params,e));
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
			}
			ConsoleUtils.log(params,"** Program ended!");
			ConsoleUtils.enableForeignKeys(params);
		}
	}

	private static Stream<Table<?>> getStream(ConsoleParams params) {
		return params.getFromConnection()
			.getSchema()
			.getTables()
			.stream()
			.filter( ConsoleDeleteDomain::hasDomain );
	}
	
	private static void deleteTableRows(ConsoleParams params, Table<?> t) {
		params.addTotalProgress();
		int percent = (params.getTotalProgress() * 100 / params.getTotalCount());
		int count = params.getFromDslContext()
			.delete(t)
			.where( getDomainField(t).equal(params.getFromConnection().getFullDomain().getId()))
			.execute();
		ConsoleUtils.log(params,MessageFormat.format("\t [ {0} ] {1}%  ( {2} / {3} ) -- {4} - {5} rows)"
			, (AonStringUtils.repeat('*', percent/2) + AonStringUtils.repeat(' ', 50 - percent/2))
			, percent
			, Integer.toString(params.getTotalProgress())
			, Integer.toString(params.getTotalCount())
			, t.getName()
			, count)
		);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_FIELD);
	}
	
	private static <T extends Record> boolean hasDomain(Table<T> table) {
		return (getDomainField(table) != null);
	}
    
}
