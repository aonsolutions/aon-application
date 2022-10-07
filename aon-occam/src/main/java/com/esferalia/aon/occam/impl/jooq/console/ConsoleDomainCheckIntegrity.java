package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.ActionEntry.ACTION_ENTRY;
import static com.esferalia.aon.jooq.tables.Session.SESSION;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ConsoleDomainCheckIntegrity {

	private static final String DOMAIN_LABEL = "domain";
	
	private ConsoleDomainCheckIntegrity() {
	}
	
	public static void check(ConsoleParams params) {
		String processId = AonRandomStringUtils.randomAlphabetic(4) + "_" + (new Date()).getTime();
		try {
			ConsoleMessageUtils.start(params.getPrinter());
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.title(processId, "VALIDACION DE DOMINIO"));
			check(processId, params, false);
		} finally {
			ConsoleMessageUtils.end(params.getPrinter());
		}
	}
	
	public static void check(String processId, ConsoleParams params, boolean beforeIsolate) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.subtitle(processId, "VALIDACION DE DOMINIO"));
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
			
			List<Table<?>> tables = params.getFromConnection().getSchema().getTables();
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Generating tables script"));			
			tables.stream()
				.filter(Objects::nonNull )
				.filter(t -> !beforeIsolate || (beforeIsolate && !SESSION.getName().equals(t.getName())))
				.filter(t -> !beforeIsolate || (beforeIsolate && !ACTION_ENTRY.getName().equals(t.getName())))
				.forEach(t -> addTable(params, t));
			
			params.setPartialCount( params.getScript().size() );
			params.setPartialProgress(0);
			
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, MessageFormat.format("Se van a chequear {0} tables", params.getScript().size())));			
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Inicio del proceso de chequeo de integridad de dominios"));
			params.getScript()
				.values()
				.stream()
				.forEach(t -> checkTable(processId, params, t));
		});
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Final del proceso de chequeo de integridad de dominios"));
		if (!params.getErrors().isEmpty()) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, "Se han encontrado incidencias"));
			params.getErrors().stream().forEach( e -> ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e)));
		} else {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "NO se han encontrado incidencias!"));
		}
	}
	
	// **************************************************************
	// ************* [METHODS  FOR GETTING SCRIPT ] *****************
	// **************************************************************
	@SuppressWarnings("unchecked")
	private static void addTable(ConsoleParams params, Table<?> table) {
		Table<Record> tab = (Table<Record>) table.asTable();
		ScriptTable scriptTable = new ScriptTable(tab)
				.setReferences( tab.getReferences());
		if ( tab.getPrimaryKey() != null) {
			// Arriesgado!! Si la PK no es Integer --> FALLO!!!
			scriptTable.setPrimaryKey((Field<Integer>) tab.getPrimaryKey().getFields().get(0));
		}
		HashSet<Field<?>> columns = new HashSet<>();
		if (scriptTable.getReferences() != null) {
			scriptTable
				.getReferences()
				.stream()
				.flatMap( ref -> ref.getFields().stream() )
				.forEach( columns::add)
				;
		}
		
		if (ConsoleUtils.CUSTOM_FOREIGN_MAP.containsKey(table.getName())) {
			Arrays.stream( ConsoleUtils.CUSTOM_FOREIGN_MAP.get(table.getName()))
				.map( CustomForeignKey::getInvolvedColumns )
				.flatMap( Arrays::stream ) 
				.forEach(columns::add)
			;
			Arrays.stream( ConsoleUtils.CUSTOM_FOREIGN_MAP.get(table.getName()))
				.map( CustomForeignKey::getForeignKey )
				.flatMap( ref -> ref.getFields().stream() ) 
				.forEach(columns::add)
			;
		}
		scriptTable.setReferenceColumns(columns);	
		params.getScript().put(table.getName(), scriptTable);
	}

	private static void checkTable(String processId, ConsoleParams params, ScriptTable table) {
		String msg = MessageFormat.format("Checking {0} table:", table.getTableName());
		params.setPartialProgress((params.getPartialProgress() + 1));
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.progress(processId
			,params.getPartialCount()
			,params.getPartialProgress() )
			.setMessage(msg));
		if (hasDomain(table.getTable()) && hasPrimaryKey(table.getTable())) {
			params.getScript().get(table.getTableName())
				.getReferences()
				.stream()
				.filter(fk -> !DOMAIN_LABEL.equals(fk.getKey().getName()))
				.forEach(fk -> checkForeignKey(processId, params,fk,null));
			if (ConsoleUtils.CUSTOM_FOREIGN_MAP.containsKey(table.getTableName())) {
				Arrays.stream( ConsoleUtils.CUSTOM_FOREIGN_MAP.get(table.getTableName()))
					.forEach(fk -> checkForeignKey(processId, params,fk.getForeignKey(),fk)
				); 
			}
		}
	}
	
	private static void checkForeignKey(String processId ,ConsoleParams params, ForeignKey<Record, ?> fk, CustomForeignKey customFk) {
		String msg = MessageFormat.format("Checking {0} Foreign key:", fk.getName());
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.progress(processId
				,params.getPartialCount()
				,params.getPartialProgress() )
				.setMessage(msg));
		Table<?> fromTable = fk.getTable();
		String fromTableAlias = "fromTable";
		Table<?> fromTableSelect = fromTable.as(fromTableAlias);
		Table<?> toTable = fk.getKey().getTable();
		Table<?> toTableSelect = toTable.as("toTable");
		Field<?> fkField = fromTableSelect.field(fk.getFields().get(0).getName());
		Field<?> fkPkField = toTableSelect.field(toTableSelect.getPrimaryKey().getFields().get(0).getName());
		Field<?> pkField = fromTableSelect.field(fromTableSelect.getPrimaryKey().getFields().get(0).getName());
		if (hasDomain(toTable))  {
			Field<Integer> fromDomainField = getDomainField(fromTableSelect);
			Field<Integer> toDomainField = getDomainField(toTableSelect);
			LinkedList<Field<?>> selectFields = new LinkedList<>();
			selectFields.add(pkField);
			selectFields.add(fromDomainField);
			selectFields.add(toDomainField);
			selectFields.add(fkField);
			Integer domain = params.getFromConnection().getDomain().getId();
			Integer parent = params.getFromConnection().getDomain().getParentId();
			Condition mainCondition = params.getFromConnection().getDomain().isEnableHeredity()
					?fromDomainField.in(domain,parent)
					:fromDomainField.eq(domain);
			if (customFk != null) {
				mainCondition = mainCondition.and(customFk.getCondition( fromTableAlias ));
				Arrays.stream(customFk.getInvolvedColumns())
				.forEach(f ->  selectFields.add(fromTableSelect.field(f.getName())) );
			}
			mainCondition = mainCondition.and(fkField.isNotNull());
			mainCondition = mainCondition.and(DSL.trim(DSL.cast(fkField, String.class)).notEqual(AonStringUtils.EMPTY));
			mainCondition = mainCondition.and(
					(params.getFromConnection().getDomain().isEnableHeredity()
						?toDomainField.notIn(0,domain,parent)
						:toDomainField.notIn(0,domain)).or(fkPkField.isNull())
					);
			params.getFromDslContext()
				.select( selectFields )
				.from(fromTableSelect)
				.leftOuterJoin(toTableSelect).onKey(fk)
				.where(mainCondition)
				.stream()
				.filter( toRec ->
					AonNumberUtils.equals( domain, toRec.getValue(fromDomainField))
					||  (AonNumberUtils.equals( parent, toRec.getValue(fromDomainField))
					  && ConsoleUtils.PARENT_INCLUDED_TABLES.contains(fromTable.getName()))
				)
				.forEach( toRec -> {
					params.addError(
						new ConsoleDomainMessage()
							.setType(ConsoleDomainMessageType.INTEGRITY)	
							.setSchema( params.getFromConnection().getSchemaName() )
							.setDomainId( toRec.getValue(fromDomainField) )
							.setTable( fromTable.getName() )
							.setPkId( Integer.valueOf(Objects.toString(toRec.getValue(pkField)) ))
							.setFkTable( toTable.getName() )
							.setFkColumn(fkField.getName())
							.setFkId(Integer.valueOf(Objects.toString(toRec.getValue(fkField))))
							.setWrongDomainId(Integer.valueOf(Objects.toString(toRec.getValue(toDomainField))))
							.setMessage(
								MessageFormat.format("Dominio: {0} "
									+ ", tabla: [{1}]"
									+ ", ID: {2}"
									+ ", columna [{3}] ({4}) que referencia a la tabla [{5}] apunta al dominio {6}."
								,Objects.toString(toRec.getValue(fromDomainField))
								,fromTable.getName()
								,Objects.toString(toRec.getValue(pkField))
								,fkField.getName()
								,Objects.toString(toRec.getValue(fkField))
								,toTable.getName()
								,Objects.toString(toRec.getValue(toDomainField)))
							)
					);
				});
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_LABEL);
	}
	
	private static <T extends Record> boolean hasDomain(Table<T> table) {
		return (getDomainField(table) != null);
	}

	private static <T extends Record> boolean hasPrimaryKey(Table<T> table) {
		return (table.getPrimaryKey() != null);
	}
}
