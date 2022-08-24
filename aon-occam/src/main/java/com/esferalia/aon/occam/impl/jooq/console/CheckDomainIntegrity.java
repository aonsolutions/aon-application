package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class CheckDomainIntegrity {
	
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
				
				log(params,MessageFormat.format("Se van a chequear {0} tables", params.getScript().size()));
				
				log(params,"Inicio del proceso de chequeo de integridad de dominios");
				params.getScript()
					.values()
					.stream()
					.forEach(t -> checkTable(params, t));
			});
			
			log(params,"Final del proceso de chequeo de integridad de dominios");
			
			if (!params.getErrors().isEmpty()) {
				log(params, "" );
				log(params, AonStringUtils.repeat('*',60));
				log(params,"** Se han encontrado incidencias!");
				params.getErrors().stream().forEach( e -> log(params,e));
				log(params, AonStringUtils.repeat('*',60));
			} else {
				log(params, "" );
				log(params, AonStringUtils.repeat('*',60));
				log(params,"** NO se han encontrado incidencias!");
				log(params, AonStringUtils.repeat('*',60));
			}
			
		} else {
			System.out.println("ERROR");
			params.addError("No se ha encontrado un schema válido");
		}
		
	}
	
	// **************************************************************
	// ************* [METHODS  FOR GETTING SCRIPT ] *****************
	// **************************************************************
	private static void fillScript(ConsoleParams params) {
		List<Table<?>> tables = params.getSchema().getTables();
		log(params,"** Generating tables script");
		log(params,"** ------------------------");
		tables.stream()
			.filter(Objects::nonNull )
			.forEach(t -> addTable(params, t));
		log(params," [DONE!]");
		log(params,"");
	}

	private static void addTable(ConsoleParams params, Table<?> table) {
		@SuppressWarnings("unchecked")
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
				.flatMap( arr -> Arrays.stream( arr ) ) 
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
		logf(params,".");
	}

	private static void checkTable(ConsoleParams params, ScriptTable t) {
		if (hasDomain(t.getTable())) {
			log(params,MessageFormat.format(" **** Checking {0} table:", t.getTableName()));
			Field<Integer> pkField = getPrimaryKey(params, t.getTable());
			SelectConditionStep<Record> select = params.getDslContext()
				.select( pkField )
				.select( t.getReferenceColumns() )
				.from(t.getTable().asTable())
				.where(getDomainField(t.getTable()).equal(params.getDomain()));
			t.setRows(  params.getDslContext().fetchCount(select) );
			t.setCurrentRow(0);
			t.setPercent(0);
			select
				.fetch()
				.stream()
				.forEach( rec -> checkRow(params, t, rec));
		}
	}
	
	private static void checkRow(ConsoleParams params, ScriptTable scriptTable, Record rec) {
		Table<?> table = scriptTable.getTable();
		checkRow(params, table, rec);	
		printInfo( params, scriptTable);
	}

	private static void checkRow(ConsoleParams params, Table<?> table, Record rec) {
		params.getScript().get(table.getName())
			.getReferences()
			.stream()
			.filter(fk -> !DOMAIN_LABEL.equals(fk.getKey().getName()))
			.forEach(fk -> checkForeignKey(params,fk,rec));
		
		if (ConsoleUtils.CUSTOM_FOREIGN_MAP.containsKey(table.getName())) {
			Arrays.stream( ConsoleUtils.CUSTOM_FOREIGN_MAP.get(table.getName()))
				.filter( en -> en.accept(table,rec))
				.map( CustomForeignKey::getForeignKey)
				.forEach(fk -> checkForeignKey(params,fk,rec)
			); 
		}
	}
	
	private static void checkForeignKey( ConsoleParams params, ForeignKey<Record, ?> fk, Record rec) {
		Table<?> fromTable = fk.getTable();
		Table<?> toTable = fk.getKey().getTable();
		if (hasDomain(toTable))  {
			TableField<?, String> fkStringField = null;
			String fkStringId = null;
			TableField<?, Integer> fkIntegerField = null;
			Integer fkIntegerId = null;
			boolean isStringFK = fk.getFields().get(0).getDataType().isString();
			if (isStringFK) {
				fkStringField = getFKStringField( fk );
				fkStringId = rec.getValue(fkStringField);
				fkIntegerId = AonNumberUtils.toInteger(fkStringId);
			} else {
				fkIntegerField = getFKIntegerField( fk );
				fkIntegerId = rec.getValue(fkIntegerField);
			}
			if (fkIntegerId != null) {
				Field<Integer> toIdField = getPrimaryKey(params,toTable);
				Field<Integer> toDomainField = getDomainField(toTable);
				Record toRec = params.getDslContext().select(toDomainField)
						.from(toTable)
						.where(toIdField.eq(fkIntegerId))
						.fetch()
						.stream()
						.findFirst()
						.orElse(null);
				if (toRec == null) {
					params.addError(
							MessageFormat.format("Referencia {0} de la tabla {1} a la tabla {2} no encontrada."
									,AonNumberUtils.toString( fkIntegerId )
									,fromTable.getName()
									,toTable.getName())
							);
				} else {
					Integer toDomain = getDomainValue(toTable,toRec);
					if (AonNumberUtils.equals(toDomain, params.getDomain())
					|| (params.isInhertitanceEnabled() && AonNumberUtils.equals(toDomain, params.getParent()))
					|| AonNumberUtils.equals(0, toDomain)
					|| toDomain == null) {
						
//						Record is fine
						
					} else {
						params.addError(
								MessageFormat.format("Tabla {0}: columna {1} -({2}) que referecia a la tabla {3} apunta al dominio {4}."
									,fromTable.getName()
									,((isStringFK)?fkStringField:fkIntegerField).getName()
									,AonNumberUtils.toString( fkIntegerId )
									,toTable.getName()
									,AonNumberUtils.toString( toDomain))
								);
					}
				}
			}
		}
	}
	
	private static <T extends Record> Field<Integer> getPrimaryKey(ConsoleParams params, Table<T> table) {
		return params.getScript().get(table.getName())
				.getPrimaryKey();
	}
	
	private static <T extends Record> Integer getDomainValue(Table<T> table, Record rec) {
		return rec.getValue( getDomainField(table));		
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_LABEL);
	}
	
	@SuppressWarnings("unchecked")
	private static <F extends Record,T extends Record> TableField<T, Integer> getFKIntegerField(ForeignKey<F, T> fk) {
		TableField<F, ?> fkField = fk.getFields().get(0);
		return (TableField<T, Integer>) fkField;
	}

	@SuppressWarnings("unchecked")
	private static <F extends Record,T extends Record> TableField<T, String> getFKStringField(ForeignKey<F, T> fk) {
		TableField<F, ?> fkField = fk.getFields().get(0);
		return (TableField<T, String>) fkField;
	}

	private static <T extends Record> boolean hasDomain(Table<T> table) {
		return (getDomainField(table) != null);
	}

	// ********************************
	// ************* [LOG] ************
	// ********************************
	private static int x = 0;
	private static PrintStream getPrinter(ConsoleParams params) {
		return Objects.requireNonNullElse(params.getPrinter(), new PrintStream(System.out));
	}
	private static void log(ConsoleParams params,String msg) {
		x = 0;
		getPrinter(params).println(msg);
		getPrinter(params).flush();
	}
	private static void logf(ConsoleParams params,String msg) {
		if (x == 80) {
			x = 0;
			getPrinter(params).println();
		}
		getPrinter(params).print(msg);
		x++;
		getPrinter(params).flush();
	}
	
	private static void printInfo(ConsoleParams params, ScriptTable scriptTable) {
		scriptTable.setCurrentRow((scriptTable.getCurrentRow() + 1));
		int percent = (scriptTable.getCurrentRow() * 100 / scriptTable.getRows());
		if ( percent != scriptTable.getPercent() && percent % 2 == 0) {
			scriptTable.setPercent( percent );
			log(params,MessageFormat.format(("\t [" + AonStringUtils.repeat('*', percent/2) + AonStringUtils.repeat(' ', 50 - percent/2) + "] {0}%  ( {1} / {2} )")
					, percent
					, Integer.toString(scriptTable.getCurrentRow())
					, Integer.toString(scriptTable.getRows())));
		}
		getPrinter(params).flush();
	}

}
