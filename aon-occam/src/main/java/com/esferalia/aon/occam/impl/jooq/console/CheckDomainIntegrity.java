package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.util.AonStringUtils;


public class CheckDomainIntegrity {
	
	private static final Logger LOGGER = Logger.getLogger(CheckDomainIntegrity .class.getName());

	private static final String DOMAIN_LABEL = "domain";
	
	private CheckDomainIntegrity() {
	}
	
	public static void check(ConsoleParams params) throws SQLException {
		if (params.getDslContext() == null) {
			params.setDslContext(createDSLContext(params));
		}
 		checkDomain(params);
	}

	private static DSLContext createDSLContext(ConsoleParams params) throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("user", params.getUser());
		properties.setProperty("password", params.getPassword());
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		Connection connection = DriverManager.getConnection(params.getUrl(), properties);
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED);
		settings.setRenderKeywordCase(RenderKeywordCase.UPPER);
		settings.setParamType(ParamType.INLINED);
		return DSL.using(connection, SQLDialect.MYSQL, settings);
	}

	private static void checkDomain(ConsoleParams params) {
		Result<Record> result = params.getDslContext().fetch("SELECT DATABASE();");
		Record rec = result.get(0);
		String schemaName = (String) rec.get(0);
		Schema schema = params.getDslContext().meta()
			.getSchemas()
			.stream()
			.filter(sc -> sc.getName().equals(schemaName))
			.findFirst()
			.orElse(null);
		if (schema != null) {
			Record domainRec = params.getDslContext().select()
				.from(DOMAIN)
				.where(DOMAIN.NAME.equal(params.getDomainName()))
				.fetch()
				.stream()
				.findFirst()
				.orElse(null);
			if (domainRec == null) {
				throw new IllegalArgumentException("No se ha encontrado el dominio \"" + params.getDomainName() + "\"");
			}
			params.setDomain(domainRec.getValue(DOMAIN.ID))
				.setParent(domainRec.getValue(DOMAIN.PARENT))
				.setInhertitanceEnabled( domainRec.getValue(DOMAIN.ENABLEHEREDITY) == 1)
				.setScript( new LinkedHashMap<>() )
				.setSchema(schema);
			params.getDslContext().transaction(conf -> {
				
				fillScript(params);
				
				ConsoleUtils.log(params,MessageFormat.format("Se van a chequear {0} tables", params.getScript().size()));
				
				ConsoleUtils.log(params,"Inicio del proceso de chequeo de integridad de dominios");
				params.getScript()
					.values()
					.stream()
					.forEach(t -> checkTable(params, t));
			});
			
			ConsoleUtils.log(params,"Final del proceso de chequeo de integridad de dominios");
			
			if (!params.getErrors().isEmpty()) {
				ConsoleUtils.log(params, "" );
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
				ConsoleUtils.log(params,"** Se han encontrado incidencias!");
				params.getErrors().stream().forEach( e -> ConsoleUtils.log(params,e));
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
			} else {
				ConsoleUtils.log(params, "" );
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
				ConsoleUtils.log(params,"** NO se han encontrado incidencias!");
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
			}
			
		} else {
			LOGGER.info("ERROR");
			params.addError("No se ha encontrado un schema válido");
		}
		
	}
	
	// **************************************************************
	// ************* [METHODS  FOR GETTING SCRIPT ] *****************
	// **************************************************************
	private static void fillScript(ConsoleParams params) {
		List<Table<?>> tables = params.getSchema().getTables();
		ConsoleUtils.log(params,"** Generating tables script");
		ConsoleUtils.log(params,"** ------------------------");
		tables.stream()
			.filter(Objects::nonNull )
			.forEach(t -> addTable(params, t));
		ConsoleUtils.log(params," [DONE!]");
		ConsoleUtils.log(params,"");
	}

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
		ConsoleUtils.logf(params,".");
	}

	private static void checkTable(ConsoleParams params, ScriptTable table) {
		if (hasDomain(table.getTable())) {
			ConsoleUtils.log(params,MessageFormat.format(" **** Checking {0} table:", table.getTableName()));			
			params.getScript().get(table.getTableName())
				.getReferences()
				.stream()
				.filter(fk -> !DOMAIN_LABEL.equals(fk.getKey().getName()))
				.forEach(fk -> checkForeignKey(params,fk,null));
			if (ConsoleUtils.CUSTOM_FOREIGN_MAP.containsKey(table.getTableName())) {
				Arrays.stream( ConsoleUtils.CUSTOM_FOREIGN_MAP.get(table.getTableName()))
					.forEach(fk -> checkForeignKey(params,fk.getForeignKey(),fk)
				); 
			}
		}
	}
	
	private static void checkForeignKey( ConsoleParams params, ForeignKey<Record, ?> fk, CustomForeignKey customFk) {
		ConsoleUtils.log(params,MessageFormat.format(" \t Checking {0} Foreign key:", fk.getName()));			
		Table<?> fromTable = fk.getTable();
		String fromTableAlias = "fromTable";
		Table<?> fromTableSelect = fromTable.as(fromTableAlias);
		Table<?> toTable = fk.getKey().getTable();
		Table<?> toTableSelect = toTable.as("toTable");
		Field<?> fkField = fromTableSelect.field(fk.getFields().get(0).getName());
		if (hasDomain(toTable))  {
			Field<Integer> fromDomainField = getDomainField(fromTableSelect);
			Field<Integer> toDomainField = getDomainField(toTableSelect);
			LinkedList<Field<?>> selectFields = new LinkedList<>();
			selectFields.add(toDomainField);
			selectFields.add(fkField);
			Condition condition = null;
			if (params.isInhertitanceEnabled()) {
				condition = toDomainField.notIn(0,params.getDomain(),params.getParent());
			} else {
				condition = toDomainField.notIn(0,params.getDomain());
			}
			if (customFk != null) {
				condition = condition.and(customFk.getCondition( fromTableAlias ));
				Arrays.stream(customFk.getInvolvedColumns())
					.forEach(f ->  selectFields.add(fromTableSelect.field(f.getName())) );
			}
			params.getDslContext()
				.select( selectFields )
				.from(fromTableSelect)
				.innerJoin(toTableSelect).onKey(fk)
				.where(fromDomainField.eq(params.getDomain()))
				.and(toDomainField.isNotNull())
				.and(condition)
				.stream()
				.forEach( toRec -> {
					params.addError(
							MessageFormat.format("Tabla {0}: columna {1} -({2}) que referecia a la tabla {3} apunta al dominio {4}."
								,fromTable.getName()
								,fkField.getName()
								,toRec.getValue(fkField)
								,toTable.getName()
								,toRec.getValue(toDomainField))
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

}
