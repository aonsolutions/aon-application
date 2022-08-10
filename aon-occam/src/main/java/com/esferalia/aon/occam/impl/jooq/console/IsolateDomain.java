package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.Alarm.ALARM;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;

import java.io.PrintStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Schema;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.Keys;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.DomainValidator;
import com.esferalia.aon.watson.util.AonChronometer;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class IsolateDomain {
	
	private static final String DOMAIN_LABEL = "domain";
	private static final Table<Record> TEMPID = DSL.table("tempId");
	private static final Field<String> TEMPID_TABLE = DSL.field("tempId.table_name", String.class);
	private static final Field<Integer> TEMPID_OLD = DSL.field("tempId.old_id", Integer.class);
	private static final Field<Integer> TEMPID_NEW = DSL.field("tempId.new_id", Integer.class);
	
	private IsolateDomain() {
	}
	
	public static void isolate(ConsoleParams params) throws SQLException {
		if (params.getDslContext() == null) {
			String msg = "[ERROR]: No se ha definido la connection a la BD";
			log(params,msg);
			throw new AonCoreException(msg);
		}
		isolateDomain(params);
	}

	private static void isolateDomain(ConsoleParams params) {
		AonChronometer chronometer = new AonChronometer();
		chronometer.start();
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
			log(params,"** Start domain isolation!");
			try {
				params.setSchema(schema)
					.setScript( new LinkedHashMap<>() );
				
				DomainValidator domainValidator = DomainValidator.getInstance();
				if (!domainValidator.isValid(params.getNewDomainName())) {
					String msg = MessageFormat.format("[ERROR]: El nuevo nombre de dominio [{0}], no es válido", params.getNewDomainName());
					log(params,msg);
					throw new AonCoreException(msg);
				}

				fillScript(params);
				
				log (params, "Orden de carga:");
				MutableInt x = new MutableInt(1);
				params.getScript().values().stream()
					.forEach(sc -> {
						x.add(1);
						log (params,("\t" + x.getValue() + " - " + sc.getTable().getName()));
					});
				
				disableForeignKeys(params);
				log(params,"** Start transaction!");
				

				params.getDslContext().transaction(conf -> {
					createTempTable(params);
					createDomain(params);
					checkProductIndex( params );
					if (params.isInhertitanceEnabled()) {
						passHeritableTables( params );
					}
					params.getScript()
						.values()
						.stream()
						.filter(t -> !DOMAIN_LABEL.equals(t.getTable().getName()))
						.filter(t -> !"session".equals(t.getTable().getName()))
						.filter(t -> !"action_entry".equals(t.getTable().getName()))
						.forEach(t -> duplicateTable(params, t));
					
					loopRefInvoice(params,params.getScript().get("invoice"));
					loopFBatch(params,params.getScript().get("fbatch"));
					loopBankStatementLinkFinanceTracking(params,params.getScript().get("bank_statement_link"));
					loopAccAppParamAccount(params,params.getScript().get("app_param"));
					
					createLoginUser(params);
					
				});
				log(params,"** Commit!");
				log(params,"** End domain isolation!");
			} catch (Exception e) {
				log(params, e.getMessage() );
				log(params,"** Rollback!");
				e.printStackTrace();
			} finally {
				if (!params.getErrors().isEmpty()) {
					log(params, "" );
					log(params, AonStringUtils.repeat('*',60));
					log(params,"** Se han producido incidencias!");
					params.getErrors().stream().forEach( e -> log(params,e));
					log(params, AonStringUtils.repeat('*',60));
				}
				log(params,"** Program ended!");
				enableForeignKeys(params);
			}
		} else {
			log(params,"Schema not present -> database : " + params.getDatabase() + ", domainName : "
					+ params.getNewDomainName());
		}
		chronometer.stop();
		log(params, "Process time " + chronometer.getMinutes() + " minutes.");
		
	}

	private static void checkProductIndex(ConsoleParams params) {
		if (params.isInhertitanceEnabled()) {
			AggregateFunction<Integer> count = DSL.count(PRODUCT.ID);
			params.getDslContext()
				.select( PRODUCT.CODE, count )
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.in(params.getDomain(),params.getParent()))
				.groupBy(PRODUCT.CODE)
				.having(count.gt(1))
				.fetch()
				.stream()
				.forEach( rec -> params.addError("El producto \"" + rec.getValue(PRODUCT.CODE) + "\" se encuentra definido en el padre y en el hijo"));
			if (params.hasErrors()) {
				throw new AonCoreException("Productos duplicados");
			}
		}
			
	}

	private static void passHeritableTables(ConsoleParams params) {
		ScriptTable[] tables  = new ScriptTable[] {
				params.getScript().get("account"),
				params.getScript().get("geozone"),
				params.getScript().get("geotree"),
				params.getScript().get("tax"),
				params.getScript().get("series"),
				params.getScript().get("pay_method"),
		};
		Arrays.stream(tables)
			.forEach( t -> {
				SelectConditionStep<Record> select = params.getDslContext()
						.select()
						.from(t.getTable().asTable())
						.where(getDomainField(t.getTable()).equal(params.getParent()));
				t.setRows(  params.getDslContext().fetchCount(select) );
				t.setCurrentRow(0);
				t.setPercent(0);
				log(params,MessageFormat.format(" **** Parent {0} table:", t.getTableName()));
				select
				.fetch()
				.stream()
				.forEach( rec -> duplicateRow(params, t, rec));
			});
	}

	private static void createTempTable(ConsoleParams params) {
		params.getDslContext().execute("DROP TEMPORARY TABLE IF EXISTS `tempId`");
		String sql =
			"CREATE TEMPORARY TABLE `tempId` ("
				+"`table_name` char(40) NOT NULL,"
				+"`old_id` int(4) NOT NULL,"
				+"`new_id` int(4) NOT NULL,"
				+"PRIMARY KEY (`table_name`,`old_id`)"
			+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;"
		;
		params.getDslContext().execute(sql);
		log(params,"** IDs Temp table created!");
	}
	
	private static void createDomain(ConsoleParams params) {
		Record rec = params.getDslContext().select().from(DOMAIN).where(DOMAIN.NAME.equal(params.getDomainName()))
				.fetch().stream().findFirst().orElse(null);
		if (rec == null) {
			throw new IllegalArgumentException("No se ha encontrado el dominio \"" + params.getDomainName() + "\"");
		}
		params.setDomain(rec.getValue(DOMAIN.ID));
		params.setParent(rec.getValue(DOMAIN.PARENT));
		params.setInhertitanceEnabled( rec.getValue(DOMAIN.ENABLEHEREDITY) == 1);
		
		DomainRecord domRec = rec.into(DOMAIN);
		domRec.setId(null);
		domRec.setName(params.getNewDomainName());
		domRec.setParent(null);
		domRec.setScope(null);
		domRec.setEnableheredity((byte) 0);
		domRec.store();
		params.setNewDomain(domRec.getId());
		insertId(params, DOMAIN,params.getDomain(),params.getNewDomain() );
		log(params,MessageFormat.format("DOMAIN {0} creado con ID {1}", params.getNewDomainName(), params.getNewDomain()));
	}

	private static void insertId(ConsoleParams params, Table<?> table, Integer oldId, Integer newId) {
		params.getDslContext().insertInto(TEMPID)
			.set(TEMPID_TABLE, table.getName() )
			.set(TEMPID_OLD, oldId )
			.set(TEMPID_NEW, newId )
			.execute();
	}

	private static void duplicateTable(ConsoleParams params, ScriptTable t) {
		if (hasDomain(t.getTable())) {
			log(params,MessageFormat.format(" **** Duplicating {0} table:", t.getTableName()));
			AggregateFunction<Integer> countField = DSL.count();
			Integer selectCount = params.getDslContext()
				.select( countField )
				.from(t.getTable().asTable())
				.where(getDomainField(t.getTable()).equal(params.getDomain()))
				.fetch()
				.stream()
				.map(rec -> rec.getValue(countField))
				.findFirst()
				.orElse(Integer.valueOf(0))
			;
			if (selectCount == null) {
				selectCount = Integer.valueOf(0);
			}
			SelectConditionStep<Record> select = params.getDslContext()
				.select()
				.from(t.getTable().asTable())
				.where(getDomainField(t.getTable()).equal(params.getDomain()));
			t.setRows(  selectCount );
			t.setCurrentRow(0);
			t.setPercent(0);
			log(params,MessageFormat.format(" **** Duplicating {0} table {1} rows", t.getTableName(),selectCount));
			int offset = 0;
			int limit = getLimit( t );
			while (offset < selectCount) {
				select
					.offset(offset)
					.limit(limit)
					.fetch()
					.stream()
					.forEach( rec -> duplicateRow(params, t, rec));
				offset = offset + limit;
			}
		}
	}
	
	private static int getLimit(ScriptTable t) {
		if (AonStringUtils.contains(t.getTableName(), "attach" )) {
			return 10;
		}
		if (t.getRows() >= 1000) {
			return 500;
		}
		return t.getRows();
	}

	private static void loopRefInvoice(ConsoleParams params, ScriptTable t) {
		log(params," **** Loop references at invoice table");
		params.getDslContext().select()
			.from(t.getTable())
			.where(INVOICE.DOMAIN.eq(params.getNewDomain() ) )
			.and(INVOICE.RECTIFICATION_INVOICE.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params 
				,t.getTable().getReferences().stream().filter(ref -> Keys.FK_INVOICE_INVOICE.getName().equals(ref.getName())).findFirst().get()
				,rec))
			.forEach(rec ->
				params.getDslContext()
					.update( INVOICE )
					.set(INVOICE.RECTIFICATION_INVOICE, rec.getValue(INVOICE.RECTIFICATION_INVOICE))
					.where(INVOICE.ID.eq(rec.getValue(INVOICE.ID)))
					.and(INVOICE.DOMAIN.eq(params.getNewDomain() ) )
					.execute()
			);
	}

	private static void loopFBatch(ConsoleParams params, ScriptTable t) {
		log(params," **** Loop references at fbatch table");
		params.getDslContext().select()
			.from(t.getTable())
			.where(FBATCH.DOMAIN.eq(params.getNewDomain() ) )
			.and(FBATCH.BANK_STATEMENT_LINK.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params 
				,t.getTable().getReferences().stream().filter(ref -> Keys.FK_FBATCH_BANK_STATEMENT_LINK.getName().equals(ref.getName())).findFirst().get()
				,rec))
			.forEach(rec ->
				params.getDslContext()
					.update( FBATCH )
					.set(FBATCH.BANK_STATEMENT_LINK, rec.getValue(FBATCH.BANK_STATEMENT_LINK))
					.where(FBATCH.ID.eq(rec.getValue(FBATCH.ID)))
					.and(FBATCH.DOMAIN.eq(params.getNewDomain() ) )
					.execute()
			);
	}
	
	private static void loopBankStatementLinkFinanceTracking(ConsoleParams params, ScriptTable t) {
		log(params," **** Loop references at BankStatementLink --> FinanceTracking table");
		params.getDslContext().select()
			.from(t.getTable())
			.where(BANK_STATEMENT_LINK.DOMAIN.eq(params.getNewDomain() ) )
			.and(BANK_STATEMENT_LINK.SOURCE.eq((byte) 0))
			.and(BANK_STATEMENT_LINK.SOURCE_ID.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params
				,CustomForeignKey.BANK_STATEMENT_LINK_FINANCE_TRACKING.getForeignKey()
				,rec))
			.forEach(rec ->
				params.getDslContext()
					.update( BANK_STATEMENT_LINK )
					.set(BANK_STATEMENT_LINK.SOURCE_ID, rec.getValue(BANK_STATEMENT_LINK.SOURCE_ID))
					.where(BANK_STATEMENT_LINK.ID.eq(rec.getValue(BANK_STATEMENT_LINK.ID)))
					.and(BANK_STATEMENT_LINK.DOMAIN.eq(params.getNewDomain() ) )
					.execute()
			);
	}
  
	private static void loopAccAppParamAccount(ConsoleParams params, ScriptTable t) {
		log(params," **** ACC Params --> Account table");
		params.getDslContext().select()
			.from(t.getTable())
			.where(APP_PARAM.DOMAIN.eq(params.getNewDomain() ) )
			.and(APP_PARAM.NAME.like("ACC%ACC"))
			.and(APP_PARAM.VALUE.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params
				,CustomForeignKey.ACC_APP_PARAM.getForeignKey()
				,rec))
			.forEach(rec -> {
				int i = params.getDslContext()
						.update( APP_PARAM )
						.set(APP_PARAM.VALUE, rec.getValue(APP_PARAM.VALUE))
						.where(APP_PARAM.ID.eq(rec.getValue(APP_PARAM.ID)))
						.and(APP_PARAM.DOMAIN.eq(params.getNewDomain() ) )
						.execute();
				log(params, 
					MessageFormat.format( 
						"App Param --> Account --> {0} {1} {2} {3}"
						,i
						,rec.getValue(APP_PARAM.ID)
						,rec.getValue(APP_PARAM.NAME)
						,rec.getValue(APP_PARAM.VALUE)));
			});
	}

	private static void disableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=0";
		params.getDslContext().execute(cmd);
		log(params,"** Foreign keys disabled");
	}
	
	private static void enableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=1";
		params.getDslContext().execute(cmd);
		log(params,"** Foreign keys enabled");
	}
	
	private static <T extends Record> Integer getDomainValue(Table<T> table, Record rec) {
		return rec.getValue( getDomainField(table));		
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_LABEL);
	}
	
	private static <T extends Record> boolean hasDomain(Table<T> table) {
		return (getDomainField(table) != null);
	}
	
	private static <T extends Record> boolean isDomainTable(Table<T> table) {
		return (DOMAIN_LABEL.equals(table.getName()));		
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> TableField<T, Integer> getPrimaryKey(Table<T> table) {
		TableField<T, ?> tableField = table.getPrimaryKey().getFields().get(0);
		return (TableField<T, Integer>) tableField;
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

	private static Integer getNewId(ConsoleParams params, Table<?> table, Integer fkOldId) {
		return params.getDslContext()
			.select(TEMPID_NEW)
			.from(TEMPID)
			.where(TEMPID_TABLE.eq(table.getName()))
			.and(TEMPID_OLD.eq(fkOldId))
			.fetch()
			.stream()
			.map(r -> r.getValue(TEMPID_NEW))
			.findFirst()
			.orElse(null);
	}
	
	private static void duplicateRow(ConsoleParams params, ScriptTable scriptTable, Record rec) {
		Table<?> table = scriptTable.getTable();
		duplicateRow(params, table, rec);	
		printInfo( params, scriptTable);
	}
	
	private static Integer duplicateRow(ConsoleParams params, Table<?> table, Record rec) {
//		AonChronometer ch = new AonChronometer();
//		ch.start();
		TableField<?, Integer> pkField = getPrimaryKey(table);
		Integer pkOldId = rec.getValue(pkField);
		if (pkField.getDataType().identity()) {
			rec.setValue(pkField, null);
		}
		params.getScript().get(table.getName())
			.getReferences()
			.stream()
			.filter(fk ->  !Keys.FK_INVOICE_INVOICE.getName().equals(fk.getName()) )
			.filter(fk ->  !Keys.FK_FBATCH_BANK_STATEMENT_LINK.getName().equals(fk.getName()) )
			.forEach(fk -> duplicateForeignKey(params,fk,rec));
		if (!APP_PARAM.getName().equals( table.getName()) 
			&& CUSTOM_FOREIGN_MAP.containsKey(table.getName())) {
			Arrays.stream( CUSTOM_FOREIGN_MAP.get(table.getName()))
				.filter( en -> en.accept(table,rec))
//				.filter(fk ->  fk != CustomForeignKey.BANK_STATEMENT_LINK_FINANCE_TRACKING)
//				.filter(fk ->  fk != CustomForeignKey.BANK_STATEMENT_LINK_FBATCH)
				.map( CustomForeignKey::getForeignKey)
				.forEach(fk -> duplicateForeignKey(params,fk,rec)
			); 
		}
		
		rec.changed(true);
		Integer pkNewId = rec.getValue(pkField);
		if (pkField.getDataType().identity()) {
			pkNewId = params.getDslContext().insertInto(table)
				.set(rec)
				.returning(pkField)
				.fetchOne()
				.getValue(pkField);
		} else {
			params.getDslContext().insertInto(table)
				.set(rec)
				.execute();
		}
		insertId(params, table, pkOldId, pkNewId);
//		ch.stop();
//		System.out.println( table.getName() + " --> " + ch.getMilliseconds() );
		return pkNewId;
	}
	
	private static Record duplicateForeignKey( ConsoleParams params, ForeignKey<Record, ?> fk, Record rec) {
		if (isDomainTable(fk.getKey().getTable())) {
			rec.setValue(getDomainField(fk.getTable()), params.getNewDomain());
		} else {
			duplicateOtherForeignKey(params, fk, rec);
		}
		return rec;
	}
	
	private static Record duplicateOtherForeignKey( ConsoleParams params, ForeignKey<Record, ?> fk, Record rec) {
		Table<?> toTable = fk.getKey().getTable();
		if (hasDomain(toTable))  {
			TableField<?, String> fkStringField = null;
			String fkOldStringId = null;
			TableField<?, Integer> fkIntegerField = null;
			Integer fkOldIntegerId = null;
			boolean isStringFK = fk.getFields().get(0).getDataType().isString();
			if (isStringFK) {
				fkStringField = getFKStringField( fk );
				fkOldStringId = rec.getValue(fkStringField);
				fkOldIntegerId = AonNumberUtils.toInteger(fkOldStringId);
			} else {
				fkIntegerField = getFKIntegerField( fk );
				fkOldIntegerId = rec.getValue(fkIntegerField);
			}
			if (fkOldIntegerId != null) {
				Integer fkNewId = getNewId(params, toTable, fkOldIntegerId);
				if ( fkNewId != null) {
					if (isStringFK) {
						rec.setValue(fkStringField, AonNumberUtils.toString( fkNewId ));
					} else {
						rec.setValue(fkIntegerField, fkNewId);
					}
				} else {
					Field<Integer> toIdField = getPrimaryKey(toTable);
					Record toRec = params.getDslContext().select()
						.from(toTable)
						.where(toIdField.eq(fkOldIntegerId))
						.fetch()
						.stream()
						.findFirst()
						.orElse(null);
					if (toRec != null
						&& getDomainValue(toTable,toRec) != null 
						&& params.getParent() != null
						&& getDomainValue(toTable,toRec).intValue() == params.getParent().intValue()) {
						Integer newID = duplicateRow(params, toTable, toRec);
						if (isStringFK) {
							rec.setValue(fkStringField, AonNumberUtils.toString( newID ));
						} else {
							rec.setValue(fkIntegerField, newID);
						}
					} else {
						if (toRec != null && getDomainValue(toTable,toRec) != null && getDomainValue(toTable,toRec).intValue() != 0) {
							params.addError(MessageFormat.format("ERROR! [{0}] {1} not found in table \"{2}\""
									, fk.getName()
									, Integer.toString(fkOldIntegerId)
									, toTable.getName()) );
						}
					}
				}
			}
		}
		return rec;
	}

	// **************************************************************
	// ************* [METHODS  FOR GETTING SCRIPT ] *****************
	// **************************************************************
	private static void fillScript(ConsoleParams params) {
		fillScript(params,new LinkedList<>());
	}
	
	private static void fillScript(ConsoleParams params, Deque<Table<?>> stack) {
		List<Table<?>> tables = params.getSchema().getTables();
		log(params,"** Generating tables script");
		log(params,"** ------------------------");
		tables.stream()
			.filter(t -> t.field(DOMAIN_LABEL) != null )
			.forEach(t -> addTable(params, t, stack));
		log(params," [DONE!]");
		log(params,"");
	}

	private static void addTable(ConsoleParams params, Table<?> table, Deque<Table<?>> stack) {
		if (!params.getScript().containsKey(table.getName()) && !stack.contains(table)) {
			@SuppressWarnings("unchecked")
			Table<Record> tab = (Table<Record>) table.asTable();
			stack.addFirst(table);
			Stream.concat(
				Arrays.stream(CUSTOM_FOREIGN_MAP.getOrDefault(table.getName(),new CustomForeignKey[]{}))
					.map( CustomForeignKey::getForeignKey),
				table.getReferences().stream())
				.map(fk -> fk.getKey().getTable())
				.forEach(t -> addTable(params, t, stack));
			logf(params,".");
			params.getScript().put(table.getName(),new ScriptTable(tab).setReferences( tab.getReferences()));
			stack.removeFirst();
		}
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
			getPrinter(params).flush();
		}
		getPrinter(params).print(msg);
		getPrinter(params).flush();
		x++;
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
	
	private static void createLoginUser(ConsoleParams params) {
		int newUserId = params.getDslContext().insertInto(USER)
				.set(USER.DOMAIN , params.getNewDomain())
				.set(USER.NAME, "DEFAULT USER")
				.set(USER.LOGIN, "aon")
				.set(USER.PASSWORD, "0jtZh1BMGz3khL8uR8dvdau3lNM=") // org
				.returning(USER.ID)
				.fetchOne()
				.getId();
		log(params,"User insertado correctamente");
		
		params.getDslContext().select( SCOPE.ID)
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(params.getNewDomain()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue( SCOPE.ID ))
			.forEach( scopeId ->params.getDslContext().insertInto(USER_SCOPE)
				.set(USER_SCOPE.USER_ID, newUserId)
				.set(USER_SCOPE.DOMAIN , params.getNewDomain())
				.set(USER_SCOPE.SCOPE, scopeId)
				.execute());
		log(params,"User Scope insertado correctamente");

		params.getDslContext().select( DOMAIN_APPLICATION.ID)
			.from(DOMAIN_APPLICATION)
			.where(DOMAIN_APPLICATION.DOMAIN.eq(params.getNewDomain()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue( DOMAIN_APPLICATION.ID ))
			.map( domainApplicationId -> params.getDslContext().insertInto(APPLICATION_USER)
				.set(APPLICATION_USER.DOMAIN, params.getNewDomain())
				.set(APPLICATION_USER.USER_ID, newUserId)
				.set(APPLICATION_USER.DOMAIN_APPLICATION, domainApplicationId)
				.set(APPLICATION_USER.ACTIVE, (byte) 1)
				.returning(APPLICATION_USER.ID)
				.fetchOne()
				.getId())
			.forEach(applicationUserId -> params.getDslContext().insertInto(APPLICATION_USER_PROFILE)
					.set(APPLICATION_USER_PROFILE.DOMAIN, params.getNewDomain())
					.set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUserId)
					.set(APPLICATION_USER_PROFILE.PROFILE, 71)
					.returning(DOMAIN_APPLICATION.ID)
					.fetchOne()
					.getId());
		log(params,"Aplicacion de usuario insertada correctamente");
		log(params,"Perfil de usuario en la aplicación insertada correctamente");
	}
	

    static <R extends Record, U extends Record> ForeignKey<R, U> createForeignKey(UniqueKey<U> key,
            Table<R> table, String name, TableField<R, ?> field) {
        return new WeakForeignKey<>(key, table, name, field);
    }
	static final HashMap<String, CustomForeignKey[]> CUSTOM_FOREIGN_MAP = new HashMap<>();
	static {
		CUSTOM_FOREIGN_MAP.put(INVOICE_DETAIL.getName(), new CustomForeignKey[] {
			CustomForeignKey.INVOICE_DETAIL_PURCHASE_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_SALES_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_DELIVERY_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_INCOME_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_OFFER_DETAIL,
			CustomForeignKey.INVOICE_DETAIL_PROJECT_RESERVATION});
		CUSTOM_FOREIGN_MAP.put(BANK_STATEMENT_LINK.getName(), new CustomForeignKey[] {
			CustomForeignKey.BANK_STATEMENT_LINK_FINANCE_TRACKING,
			CustomForeignKey.BANK_STATEMENT_LINK_FBATCH,
			CustomForeignKey.BANK_STATEMENT_LINK_BANK_CONCEPT,
			CustomForeignKey.BANK_STATEMENT_LINK_ACCOUNT
		});
		CUSTOM_FOREIGN_MAP.put(FINANCE.getName(), new CustomForeignKey[] {
			CustomForeignKey.FINANCE_SALARY,
			CustomForeignKey.FINANCE_PREPAYMENT
		});
		CUSTOM_FOREIGN_MAP.put(ALARM.getName(), new CustomForeignKey[] {
			CustomForeignKey.ALARM_NOTICE,
			CustomForeignKey.ALARM_TASK,
			CustomForeignKey.ALARM_COMMERCIAL_TRACKING,
			CustomForeignKey.ALARM_MK_ACTION_TARGET
		});
		CUSTOM_FOREIGN_MAP.put(PURCHASE_DETAIL.getName(), new CustomForeignKey[] {
			CustomForeignKey.PURCHASE_DETAIL_PROPOSAL_DETAIL,
			CustomForeignKey.PURCHASE_DETAIL_PURCHASE_DETAIL,
			CustomForeignKey.PURCHASE_DETAIL_SALES_DETAIL
		});
		CUSTOM_FOREIGN_MAP.put(WAREHOUSE_TRANSFER.getName(), new CustomForeignKey[] {
			CustomForeignKey.WAREHOUSE_TRANSFER_INVENTORY_DETAIL,
			CustomForeignKey.WAREHOUSE_TRANSFER_PURCHASE_DETAIL,
			CustomForeignKey.WAREHOUSE_TRANSFER_INVENTORY_DETAIL
		});
		CUSTOM_FOREIGN_MAP.put(APP_PARAM.getName(), new CustomForeignKey[] {
			CustomForeignKey.ACC_APP_PARAM
		});
	}
    
}
