package com.esferalia.aon.occam.impl.jooq.console;


import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.ActionDenied.ACTION_DENIED;
import static com.esferalia.aon.jooq.tables.ActionEntry.ACTION_ENTRY;
import static com.esferalia.aon.jooq.tables.ActionFavorite.ACTION_FAVORITE;
import static com.esferalia.aon.jooq.tables.Alarm.ALARM;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Contact.CONTACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.DomainGserviceaccount.DOMAIN_GSERVICEACCOUNT;
import static com.esferalia.aon.jooq.tables.Favorite.FAVORITE;
import static com.esferalia.aon.jooq.tables.FavoriteCategory.FAVORITE_CATEGORY;
import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.MkActionTarget.MK_ACTION_TARGET;
import static com.esferalia.aon.jooq.tables.Note.NOTE;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProjectReservationDivert.PROJECT_RESERVATION_DIVERT;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Session.SESSION;
import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;
import static com.esferalia.aon.jooq.tables.SurveyResponse.SURVEY_RESPONSE;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserAppRole.USER_APP_ROLE;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.UserWorkgroup.USER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.Keys;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.DomainValidator;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ConsoleIsolateDomain {
	
	private static final String DOMAIN_FIELD = "domain";
	private static final Table<Record> TEMPID = DSL.table("tempId");
	private static final Field<String> TEMPID_TABLE = DSL.field("tempId.table_name", String.class);
	private static final Field<Integer> TEMPID_OLD = DSL.field("tempId.old_id", Integer.class);
	private static final Field<Integer> TEMPID_NEW = DSL.field("tempId.new_id", Integer.class);
	
	private ConsoleIsolateDomain() {
	}
	
	public static void isolate(ConsoleParams params) {
		if (params.getFromConnection() == null || params.getFromConnection().getAONContext().getDslContext() == null) {
			String msg = "[ERROR]: No se ha definido la conexión origen a la BD";
			ConsoleUtils.log(params,msg);
			throw new AonCoreException(msg);
		}
		isolateDomain(params);
	}

	private static void isolateDomain(ConsoleParams params) {
		ConsoleUtils.log(params,"** Start domain isolation!");
		try {
			Domain fullDomain = DomainDAO.getDomain(params.getFromConnection().getAONContext()
					, p -> p.getNameProperty().eq(params.getFromConnection().getDomainName()));
			if (fullDomain == null) {
				throw new IllegalArgumentException("No se ha encontrado el dominio \"" + params.getFromConnection().getDomainName() + "\"");
			}
			params.getFromConnection().setFullDomain(fullDomain);
			params.setScript( new LinkedHashMap<>() );
			
			DomainValidator domainValidator = DomainValidator.getInstance(true);
			if (!domainValidator.isValid(params.getToConnection().getDomainName())) {
				String msg = MessageFormat.format("[ERROR]: El nuevo nombre de dominio [{0}], no es válido", params.getToConnection().getDomainName());
				ConsoleUtils.log(params,msg);
				throw new AonCoreException(msg);
			}
			fillScript(params);
			ConsoleUtils.log (params, "Orden de carga:");
			MutableInt x = new MutableInt(1);
			params.getScript().values().stream()
				.forEach(sc -> {
					x.add(1);
					ConsoleUtils.log (params,("\t" + x.getValue() + " - " + sc.getTable().getName()));
				});
			
			disableForeignKeys(params);
			ConsoleUtils.log(params,"** Start transaction!");
			

			params.getToDslContext().transaction(conf -> {
				createTempTable(params);
				createDomain(params);
				checkProductIndex( params );
				checkNoticeRecipient( params );
				if (params.getFromConnection().getFullDomain().isEnableHeredity()) {
					passHeritableTables( params );
				}
				params.getScript()
					.values()
					.stream()
					.filter(t -> !DOMAIN.getName().equals(t.getTable().getName()))
					.filter(t -> !SESSION.getName().equals(t.getTable().getName()))
					.filter(t -> !ACTION_ENTRY.getName().equals(t.getTable().getName()))
					.forEach(t -> duplicateTable(params, t));
				loopRefInvoice(params,params.getScript().get("invoice"));
				loopFBatch(params,params.getScript().get("fbatch"));
				loopBankStatementLinkFinanceTracking(params,params.getScript().get("bank_statement_link"));
				loopAccAppParamAccount(params,params.getScript().get("app_param"));
				createLoginUser(params);
			});
			ConsoleUtils.log(params,"** Commit!");
			ConsoleUtils.log(params,"** End domain isolation!");
		} catch (Exception e) {
			ConsoleUtils.log(params, e.getMessage() );
			ConsoleUtils.log(params,"** Rollback!");
			e.printStackTrace();
		} finally {
			if (!params.getErrors().isEmpty()) {
				ConsoleUtils.log(params, "" );
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
				ConsoleUtils.log(params,"** Se han producido incidencias!");
				params.getErrors().stream().forEach( e -> ConsoleUtils.log(params,e));
				ConsoleUtils.log(params, AonStringUtils.repeat('*',60));
			}
			ConsoleUtils.log(params,"** Program ended!");
			enableForeignKeys(params);
		}
	}

	private static void checkProductIndex(ConsoleParams params) {
		if (params.getFromConnection().getFullDomain().isEnableHeredity()) {
			AggregateFunction<Integer> count = DSL.count(PRODUCT.ID);
			params.getFromDslContext()
				.select( PRODUCT.CODE, count )
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.in(
					params.getFromConnection().getFullDomain().getId()
					,params.getFromConnection().getFullDomain().getParentId()))
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
	
	private static void checkNoticeRecipient(ConsoleParams params ) {
		Integer[] domainIds = null;
		if (params.getFromConnection().getFullDomain().isEnableHeredity()) {
			domainIds = new Integer[] {params.getFromConnection().getFullDomain().getId()
				,params.getFromConnection().getFullDomain().getParentId()};
		} else {
			domainIds = new Integer[] {params.getFromConnection().getFullDomain().getId()};
			
		}
		
		params.getFromDslContext()
			.select( NOTICE.ID,NOTICE.DATE,NOTICE.SUBJECT,NOTICE.DOMAIN )
			.from(NOTICE)
			.innerJoin(USER).on(USER.ID.eq(NOTICE.RECIPIENT))
			.where(NOTICE.DOMAIN.in(domainIds))
			.and(USER.DOMAIN.notIn(params.getFromConnection().getFullDomain().getId()
					,params.getFromConnection().getFullDomain().getParentId()))
			.fetch()
			.stream()
			.map(rec -> MessageFormat.format("Existe un aviso (tabla \"notice\") cuyo destinatario no pertence al dominio. [id: {0}, dominio: {1}, fecha: \"{2}\", subject: \"{3}\"] "
				,rec.getValue(NOTICE.ID)
				,rec.getValue(NOTICE.DOMAIN)
				,rec.getValue(NOTICE.DATE)
				,rec.getValue(NOTICE.SUBJECT)) )
			.forEach( params::addError);
		if (params.hasErrors()) {
			throw new AonCoreException("Avisos incoherentes");
		}
	}
	
	

	private static void passHeritableTables(ConsoleParams params) {
//		ScriptTable[] tables  = new ScriptTable[] {
//				params.getScript().get("scope"),
//				params.getScript().get("account"),
//				params.getScript().get("geozone"),
//				params.getScript().get("geotree"),
//				params.getScript().get("pcategory"),
//				params.getScript().get("tax"),
//				params.getScript().get("tag"),
//				params.getScript().get("user"),
//				params.getScript().get("series"),
//				params.getScript().get("pay_method"),
//				params.getScript().get("bank_concept"),
//				params.getScript().get("product"),
//				params.getScript().get("item"),
//				params.getScript().get("registry"),
//		};
//		Arrays.stream(tables)
		params.getScript()
			.values()
			.stream()
			.filter(t -> !DOMAIN.getName().equals(t.getTable().getName()))
			.filter(t -> !DOMAIN_APP.getName().equals(t.getTable().getName()))
			.filter(t -> !DOMAIN_APPLICATION.getName().equals(t.getTable().getName()))
			.filter(t -> !DOMAIN_APPLICATION_MODULE.getName().equals(t.getTable().getName()))
			.filter(t -> !DOMAIN_GSERVICEACCOUNT.getName().equals(t.getTable().getName()))
			.filter(t -> !APPLICATION_USER.getName().equals(t.getTable().getName()))
			.filter(t -> !APPLICATION_USER_PROFILE.getName().equals(t.getTable().getName()))
			.filter(t -> !COMPANY.getName().equals(t.getTable().getName()))
			.filter(t -> !ACCOUNT_PERIOD.getName().equals(t.getTable().getName()))
			.filter(t -> !APP_PARAM.getName().equals(t.getTable().getName()))
			.filter(t -> !SESSION.getName().equals(t.getTable().getName()))
			.filter(t -> !ACTION_ENTRY.getName().equals(t.getTable().getName()))
			.filter(t -> !RATTACH.getName().equals(t.getTable().getName()))
			.filter(t -> !RATTACH_TAG.getName().equals(t.getTable().getName()))
			
			.filter(t -> !USER.getName().equals(t.getTable().getName()))
			.filter(t -> !USER.getName().equals(t.getTable().getName()))
			.filter(t -> !ACTION_DENIED.getName().equals(t.getTable().getName()))
			.filter(t -> !SESSION.getName().equals(t.getTable().getName()))
			.filter(t -> !ACTION_FAVORITE.getName().equals(t.getTable().getName()))
			.filter(t -> !ALARM.getName().equals(t.getTable().getName()))
			.filter(t -> !USER_APP_ROLE.getName().equals(t.getTable().getName()))
			.filter(t -> !APPLICATION_USER.getName().equals(t.getTable().getName()))
			.filter(t -> !CONTACT.getName().equals(t.getTable().getName()))
			.filter(t -> !TASK_HOLDER.getName().equals(t.getTable().getName()))
			.filter(t -> !FAVORITE_CATEGORY.getName().equals(t.getTable().getName()))
			.filter(t -> !FAVORITE.getName().equals(t.getTable().getName()))
			.filter(t -> !SIGNATURE.getName().equals(t.getTable().getName()))
			.filter(t -> !MAIL_ACCOUNT.getName().equals(t.getTable().getName()))
			.filter(t -> !SURVEY_RESPONSE.getName().equals(t.getTable().getName()))
			.filter(t -> !MK_ACTION_TARGET.getName().equals(t.getTable().getName()))
			.filter(t -> !NOTE.getName().equals(t.getTable().getName()))
			.filter(t -> !NOTICE.getName().equals(t.getTable().getName()))
			.filter(t -> !NOTICE_TAG.getName().equals(t.getTable().getName()))
			.filter(t -> !PROJECT_RESERVATION_DIVERT.getName().equals(t.getTable().getName()))
			.filter(t -> !USER_SCOPE.getName().equals(t.getTable().getName()))
			.filter(t -> !USER_WORKGROUP.getName().equals(t.getTable().getName()))
			
			.filter(t -> !CRA_BATCH.getName().equals(t.getTable().getName()))
			.filter(t -> !CRA_BATCH_DETAIL.getName().equals(t.getTable().getName()))

			
			
			.filter(t -> hasDomain(t.getTable()))
			.forEach( t -> {
				SelectConditionStep<Record> select = params.getFromDslContext()
						.select()
						.from(t.getTable().asTable())
						.where(getDomainField(t.getTable()).equal(params.getFromConnection().getFullDomain().getParentId()));
				t.setRows(  params.getFromDslContext().fetchCount(select) );
				t.setCurrentRow(0);
				t.setPercent(0);
				ConsoleUtils.log(params,MessageFormat.format(" **** Parent {0} table:", t.getTableName()));
				select
				.fetch()
				.stream()
				.forEach( rec -> duplicateRow(params, t, rec));
			});
	}

	private static void createTempTable(ConsoleParams params) {
		params.getToDslContext().execute("DROP TEMPORARY TABLE IF EXISTS `tempId`");
		String sql =
			"CREATE TEMPORARY TABLE `tempId` ("
				+"`table_name` char(40) NOT NULL,"
				+"`old_id` int(4) NOT NULL,"
				+"`new_id` int(4) NOT NULL,"
				+"PRIMARY KEY (`table_name`,`old_id`)"
			+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;"
		;
		params.getToDslContext().execute(sql);
		ConsoleUtils.log(params,"** IDs Temp table created!");
	}
	
	private static void createDomain(ConsoleParams params) {
		Optional<Record> optRec = params.getFromDslContext().select()
			.from(DOMAIN)
			.where(DOMAIN.NAME.equal(params.getFromConnection().getDomainName()))
			.fetch()
			.stream()
			.findFirst();
		if (optRec.isEmpty()) {
			throw new IllegalArgumentException("Ya existe el dominio \"" + params.getToConnection().getDomainName() + "\"");
		}
		DomainRecord domRec = optRec.get().into(DOMAIN);
		domRec.attach(params.getToDslContext().configuration());
		domRec.setId(null);
		domRec.setName(params.getToConnection().getDomainName());
		domRec.setParent(null);
		domRec.setScope(null);
		domRec.setEnableheredity((byte) 0);
		domRec.store();
		params.getToConnection().setFullDomain( DomainDAO.getDomain(params.getToConnection().getAONContext(), domRec.getId()));
		insertId(params, DOMAIN,
			params.getFromConnection().getFullDomain().getId(),
			params.getToConnection().getFullDomain().getId());
		ConsoleUtils.log(params,MessageFormat.format("DOMAIN {0} creado con ID {1}"
			, params.getToConnection().getFullDomain().getName()
			, params.getToConnection().getFullDomain().getId()));
	}

	private static void insertId(ConsoleParams params, Table<?> table, Integer oldId, Integer newId) {
		params.getToDslContext().insertInto(TEMPID)
			.set(TEMPID_TABLE, table.getName() )
			.set(TEMPID_OLD, oldId )
			.set(TEMPID_NEW, newId )
			.execute();
	}

	private static void duplicateTable(ConsoleParams params, ScriptTable t) {
		if (hasDomain(t.getTable())) {
			ConsoleUtils.log(params,MessageFormat.format(" **** Duplicating {0} table:", t.getTableName()));
			AggregateFunction<Integer> countField = DSL.count();
			Integer selectCount = params.getFromDslContext()
				.select( countField )
				.from(t.getTable().asTable())
				.where(getDomainField(t.getTable()).equal(params.getFromConnection().getFullDomain().getId()))
				.fetch()
				.stream()
				.map(rec -> rec.getValue(countField))
				.findFirst()
				.orElse(Integer.valueOf(0))
			;
			if (selectCount == null) {
				selectCount = Integer.valueOf(0);
			}
			SelectConditionStep<Record> select = params.getFromDslContext()
				.select()
				.from(t.getTable().asTable())
				.where(getDomainField(t.getTable()).equal(params.getFromConnection().getFullDomain().getId()));
			t.setRows(  selectCount );
			t.setCurrentRow(0);
			t.setPercent(0);
			ConsoleUtils.log(params,MessageFormat.format(" **** Duplicating {0} table {1} rows", t.getTableName(),selectCount));
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
		ConsoleUtils.log(params," **** Loop references at invoice table");
		params.getToDslContext().select()
			.from(t.getTable())
			.where(INVOICE.DOMAIN.eq(params.getToConnection().getFullDomain().getId()) )
			.and(INVOICE.RECTIFICATION_INVOICE.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params 
				,t.getTable().getReferences().stream()
					.filter(ref -> Keys.FK_INVOICE_INVOICE.getName().equals(ref.getName()))
					.findFirst()
					.get()
				,rec))
			.forEach(rec ->
				params.getToDslContext()
					.update( INVOICE )
					.set(INVOICE.RECTIFICATION_INVOICE, rec.getValue(INVOICE.RECTIFICATION_INVOICE))
					.where(INVOICE.ID.eq(rec.getValue(INVOICE.ID)))
					.and(INVOICE.DOMAIN.eq(params.getToConnection().getFullDomain().getId()) )
					.execute()
			);
	}

	private static void loopFBatch(ConsoleParams params, ScriptTable t) {
		ConsoleUtils.log(params," **** Loop references at fbatch table");
		params.getToDslContext().select()
			.from(t.getTable())
			.where(FBATCH.DOMAIN.eq(params.getToConnection().getFullDomain().getId()) )
			.and(FBATCH.BANK_STATEMENT_LINK.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params 
				,t.getTable().getReferences().stream()
					.filter(ref -> Keys.FK_FBATCH_BANK_STATEMENT_LINK.getName().equals(ref.getName()))
					.findFirst()
					.get()
				,rec))
			.forEach(rec ->
				params.getToDslContext()
					.update( FBATCH )
					.set(FBATCH.BANK_STATEMENT_LINK, rec.getValue(FBATCH.BANK_STATEMENT_LINK))
					.where(FBATCH.ID.eq(rec.getValue(FBATCH.ID)))
					.and(FBATCH.DOMAIN.eq(params.getToConnection().getFullDomain().getId() ) )
					.execute()
			);
	}
	
	private static void loopBankStatementLinkFinanceTracking(ConsoleParams params, ScriptTable t) {
		ConsoleUtils.log(params," **** Loop references at BankStatementLink --> FinanceTracking table");
		params.getToDslContext().select()
			.from(t.getTable())
			.where(BANK_STATEMENT_LINK.DOMAIN.eq(params.getToConnection().getFullDomain().getId() ) )
			.and(BANK_STATEMENT_LINK.SOURCE.eq((byte) 0))
			.and(BANK_STATEMENT_LINK.SOURCE_ID.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params
				,CustomForeignKey.BANK_STATEMENT_LINK_FINANCE_TRACKING.getForeignKey()
				,rec))
			.forEach(rec ->
				params.getToDslContext()
					.update( BANK_STATEMENT_LINK )
					.set(BANK_STATEMENT_LINK.SOURCE_ID, rec.getValue(BANK_STATEMENT_LINK.SOURCE_ID))
					.where(BANK_STATEMENT_LINK.ID.eq(rec.getValue(BANK_STATEMENT_LINK.ID)))
					.and(BANK_STATEMENT_LINK.DOMAIN.eq(params.getToConnection().getFullDomain().getId() ) )
					.execute()
			);
	}
  
	private static void loopAccAppParamAccount(ConsoleParams params, ScriptTable t) {
		ConsoleUtils.log(params," **** ACC Params --> Account table");
		params.getToDslContext().select()
			.from(t.getTable())
			.where(APP_PARAM.DOMAIN.eq(params.getToConnection().getFullDomain().getId() ) )
			.and(APP_PARAM.NAME.like("ACC%ACC"))
			.and(APP_PARAM.VALUE.isNotNull())
			.fetch()
			.stream()
			.map( rec -> duplicateForeignKey(params
				,CustomForeignKey.ACC_APP_PARAM.getForeignKey()
				,rec))
			.forEach(rec -> params.getToDslContext()
				.update( APP_PARAM )
				.set(APP_PARAM.VALUE, rec.getValue(APP_PARAM.VALUE))
				.where(APP_PARAM.ID.eq(rec.getValue(APP_PARAM.ID)))
				.and(APP_PARAM.DOMAIN.eq(params.getToConnection().getFullDomain().getId() ) )
				.execute());
	}

	private static void disableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=0";
		params.getToDslContext().execute(cmd);
		ConsoleUtils.log(params,"** Foreign keys disabled");
	}
	
	private static void enableForeignKeys(ConsoleParams params) {
		String cmd = "SET FOREIGN_KEY_CHECKS=1";
		params.getToDslContext().execute(cmd);
		ConsoleUtils.log(params,"** Foreign keys enabled");
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field(DOMAIN_FIELD);
	}
	
	private static <T extends Record> boolean hasDomain(Table<T> table) {
		return (getDomainField(table) != null);
	}
	
	private static <T extends Record> boolean isDomainTable(Table<T> table) {
		return (DOMAIN_FIELD.equals(table.getName()));		
	}
	
	private static <T extends Record> Field<Integer> getPrimaryKey(ConsoleParams params, Table<T> table) {
		return params.getScript().get(table.getName())
				.getPrimaryKey();
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
		return params.getToDslContext()
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
		ConsoleUtils.printInfo( params, scriptTable);
	}
	
	private static Integer duplicateRow(ConsoleParams params, Table<?> table, Record rec) {
		Field<Integer> pkField = getPrimaryKey(params,table);
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
			&& ConsoleUtils.CUSTOM_FOREIGN_MAP.containsKey(table.getName())) {
			Arrays.stream( ConsoleUtils.CUSTOM_FOREIGN_MAP.get(table.getName()))
				.filter( en -> en.accept(table,rec))
				.map( CustomForeignKey::getForeignKey)
				.forEach(fk -> duplicateForeignKey(params,fk,rec)
			); 
		}
		
		rec.changed(true);
		Integer pkNewId = rec.getValue(pkField);
		if (pkField.getDataType().identity()) {
			pkNewId = params.getToDslContext().insertInto(table)
				.set(rec)
				.returning(pkField)
				.fetchOne()
				.getValue(pkField);
		} else {
			params.getToDslContext().insertInto(table)
				.set(rec)
				.execute();
		}
		insertId(params, table, pkOldId, pkNewId);
		return pkNewId;
	}
	
	private static Record duplicateForeignKey( ConsoleParams params, ForeignKey<Record, ?> fk, Record rec) {
		if (isDomainTable(fk.getKey().getTable())) {
			rec.setValue(getDomainField(fk.getTable()), params.getToConnection().getFullDomain().getId());
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
		List<Table<?>> tables = params.getFromConnection().getSchema().getTables();
		ConsoleUtils.log(params,"** Generating tables script");
		ConsoleUtils.log(params,"** ------------------------");
		tables.stream()
			.filter(t -> t.field(DOMAIN_FIELD) != null )
			.forEach(t -> addTable(params, t, stack));
		ConsoleUtils.log(params," [DONE!]");
		ConsoleUtils.log(params,"");
	}

	
	@SuppressWarnings("unchecked")
	private static void addTable(ConsoleParams params, Table<?> table, Deque<Table<?>> stack) {
		ConsoleUtils.logf(params,".");
		if (!params.getScript().containsKey(table.getName()) && !stack.contains(table)) {
			Table<Record> tab = (Table<Record>) table.asTable();
			stack.addFirst(table);
			Stream.concat(
				Arrays.stream(ConsoleUtils.CUSTOM_FOREIGN_MAP.getOrDefault(table.getName(),new CustomForeignKey[]{}))
					.map( CustomForeignKey::getForeignKey),
				table.getReferences().stream())
				.map(fk -> fk.getKey().getTable())
				.forEach(t -> addTable(params, t, stack));
			ScriptTable scriptTable = new ScriptTable(tab)
				.setReferences( tab.getReferences());
			if ( tab.getPrimaryKey() != null) {
				// Arriesgado!! Si la PK no es Integer --> FALLO!!!
				scriptTable.setPrimaryKey( (Field<Integer>) tab.getPrimaryKey().getFields().get(0));
			}
			params.getScript().put(table.getName(), scriptTable);
			stack.removeFirst();
		}
	}

	private static void createLoginUser(ConsoleParams params) {
		int newUserId = params.getToDslContext().insertInto(USER)
				.set(USER.DOMAIN , params.getToConnection().getFullDomain().getId())
				.set(USER.NAME, "DEFAULT USER")
				.set(USER.LOGIN, "aon")
				.set(USER.PASSWORD, "0jtZh1BMGz3khL8uR8dvdau3lNM=") // org
				.returning(USER.ID)
				.fetchOne()
				.getId();
		ConsoleUtils.log(params,"**** User insertado correctamente");
		
		params.getToDslContext().select( SCOPE.ID)
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(params.getToConnection().getFullDomain().getId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue( SCOPE.ID ))
			.forEach( scopeId ->params.getToDslContext().insertInto(USER_SCOPE)
				.set(USER_SCOPE.USER_ID, newUserId)
				.set(USER_SCOPE.DOMAIN , params.getToConnection().getFullDomain().getId())
				.set(USER_SCOPE.SCOPE, scopeId)
				.execute());
		ConsoleUtils.log(params,"**** User Scope insertado correctamente");

		params.getToDslContext().select( DOMAIN_APPLICATION.ID)
			.from(DOMAIN_APPLICATION)
			.where(DOMAIN_APPLICATION.DOMAIN.eq(params.getToConnection().getFullDomain().getId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue( DOMAIN_APPLICATION.ID ))
			.map( domainApplicationId -> params.getToDslContext().insertInto(APPLICATION_USER)
				.set(APPLICATION_USER.DOMAIN, params.getToConnection().getFullDomain().getId())
				.set(APPLICATION_USER.USER_ID, newUserId)
				.set(APPLICATION_USER.DOMAIN_APPLICATION, domainApplicationId)
				.set(APPLICATION_USER.ACTIVE, (byte) 1)
				.returning(APPLICATION_USER.ID)
				.fetchOne()
				.getId())
			.forEach(applicationUserId -> params.getToDslContext().insertInto(APPLICATION_USER_PROFILE)
					.set(APPLICATION_USER_PROFILE.DOMAIN, params.getToConnection().getFullDomain().getId())
					.set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUserId)
					.set(APPLICATION_USER_PROFILE.PROFILE, 71)
					.returning(DOMAIN_APPLICATION.ID)
					.fetchOne()
					.getId());
		ConsoleUtils.log(params,"**** Aplicacion de usuario insertada correctamente");
		ConsoleUtils.log(params,"**** Perfil de usuario en la aplicación insertada correctamente");
	}
    
}
