package com.esferalia.aon.occam.impl.jooq.console;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
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
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ConsoleDeleteDomain {
	
	private static final String ID = "ID";
	
	private static final String DOMAIN_FIELD = "domain";
	
	private ConsoleDeleteDomain() {
	}
	
	public static boolean delete(ConsoleParams params) {
		if (params.getFromConnection() == null || params.getFromConnection().getAONContext().getDslContext() == null) {
			ConsoleMessageUtils.start(params.getPrinter());
			String msg = "No se ha definido la conexión origen a la BD";
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(ID, msg));
			ConsoleMessageUtils.end(params.getPrinter());
			throw new AonCoreException(msg);
		}
		return deleteDomain(params);		
	}
		
	private static boolean deleteDomain(ConsoleParams params) {
		String processId = AonRandomStringUtils.randomAlphabetic(4) + "_" + (new Date()).getTime();
		try {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.title(processId, "BORRADO DE DOMINIO"));

			ConsoleMessageUtils.start(params.getPrinter());
			
			String title = "Start domain validation deletion";
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, title));
			
			if (params.getFromConnection() == null || params.getFromConnection().getAONContext().getDslContext() == null) {
				String msg = "No se ha definido la conexión origen a la BD";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}

			if (params.getFromConnection().getSchema() == null) {
				String schemaName = params.getFromConnection().getSchemaName();
				if (AonStringUtils.isBlank(schemaName)) {
					String msg = "No se ha indicado un esquema del que borrar el dominio.";
					ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
					throw new AonCoreException(msg);
				}
				Schema fromSchema = params
						.getFromDslContext()
						.meta()
						.getSchemas(schemaName)
						.stream()
						.findFirst()
						.orElse(null);
				if (fromSchema == null) {
					String msg = "No se ha encontrado el esquema \""+schemaName+"\".";
					ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
					throw new AonCoreException(msg);
				}
				params.getFromConnection().setSchema( fromSchema );
			}

			Domain domain = params.getFromConnection().getFullDomain();
			if (domain == null || domain.getId() == null) {
				String msg = "No se ha indicado un dominio que borrar.";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			Integer domainId = domain.getId();
			domain = DomainDAO.getDomain(params.getFromConnection().getAONContext(), domainId);
			if (domain == null) {
				String msg = "No se ha encontrado el dominio \"" + domainId + "\"";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			if (domain.getDomainType() == DomainType.ADMIN) {
				String msg = "No se puede borrar un dominio de ADMINISTRACION";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			if (domain.isActive()) {
				String msg = "No se puede borrar un dominio ACTIVO";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
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
				String msg = "No se puede borrar un dominio con hijos";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			params.getFromConnection().setFullDomain(domain);
			
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "End domain validation!"));
			
			String msg1 = MessageFormat.format(" COMIENZA EL BORRADO DEL DOMINIO \"{0}\" - {1} ({2}) !"
					, params.getFromConnection().getFullDomain().getDescription()
					, params.getFromConnection().getFullDomain().getId()
					, params.getFromConnection().getFullDomain().getName());
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, msg1));
			
			
			
			ConsoleUtils.disableForeignKeys(params);
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Start transaction"));
			
			params.setTotalCount(0);
			params.setTotalProgress(0);
			getStream( params )
				.filter( ConsoleDeleteDomain::hasDomain )
				.forEach( t -> params.setTotalCount( params.getTotalCount() + 1) );
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.progress(processId, params.getTotalCount(),params.getTotalProgress() ));
			
			params.getToDslContext().transaction(conf -> {
				
				getStream( params )
					.filter( ConsoleDeleteDomain::hasDomain )
					.forEach(t -> deleteTableRows(params, processId, t));
				
				params.getFromDslContext()
					.delete(DOMAIN)
					.where( DOMAIN.ID.equal(params.getFromConnection().getFullDomain().getId()))
					.execute();
				
				String msg = MessageFormat.format(" DOMINIO \"{0}\" - {1} ({2}) BORRADO!"
					, params.getFromConnection().getFullDomain().getDescription()
					, params.getFromConnection().getFullDomain().getId()
					, params.getFromConnection().getFullDomain().getName());
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, msg));
				
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Commit"));
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "FIN BORRADO DEL DOMINIO"));
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "HECHO. OK!"));
				
			});
			return true;
		} catch (Exception e) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "FIN. ROLLBACK!"));
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e.getMessage()));
			return false;
		} finally {
			ConsoleUtils.enableForeignKeys(params);
			ConsoleMessageUtils.end(params.getPrinter());
		}
	}

	private static Stream<Table<?>> getStream(ConsoleParams params) {
		return params.getFromConnection()
			.getSchema()
			.getTables()
			.stream()
			.filter( ConsoleDeleteDomain::hasDomain );
	}
	
	private static void deleteTableRows(ConsoleParams params, String processId, Table<?> t) {
		
		// Se añafe un eco por si el proceso de BD tarda mucho, se programa 
		// una salida cada cinco segundos para mantener el flujo.
		TimerTask task = new TimerTask() {
			int times = 0;
	        public void run() {
	        	String msg = "- Borrando tabla " + t.getName() + " " + AonStringUtils.repeat(".", ++times);
	    		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.progress(processId
	    				,params.getTotalCount()
	    				,params.getTotalProgress() )
	    				.setMessage(msg));
	        }
	    };
	    params.addTotalProgress();
	    
	    Timer timer = new Timer("Timer");
	    long delay = 5000L;
	    timer.scheduleAtFixedRate(task, 0, delay);
	    
		params.getFromDslContext()
			.delete(t)
			.where( getDomainField(t).equal(params.getFromConnection().getFullDomain().getId()))
			.execute();
		
		timer.cancel();
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_FIELD);
	}
	
	private static <T extends Record> boolean hasDomain(Table<T> table) {
		return (getDomainField(table) != null);
	}
    
}
