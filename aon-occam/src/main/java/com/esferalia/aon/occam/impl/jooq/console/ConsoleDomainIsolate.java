package com.esferalia.aon.occam.impl.jooq.console;

import static com.esferalia.aon.jooq.tables.ActionEntry.ACTION_ENTRY;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Session.SESSION;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
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
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.server.DomainValidator;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ConsoleDomainIsolate {
	private static final String DOMAIN_FIELD = "domain";
	
	private ConsoleDomainIsolate() {
	}
	
	public static void isolate(ConsoleParams params) {
		String processId = AonRandomStringUtils.randomAlphabetic(4) + "_" + (new Date()).getTime();
		try {
			ConsoleMessageUtils.start(params.getPrinter());
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.title(processId, "DUPLICADO DE DOMINIO"));
			
			if (params.getFromConnection() == null || params.getFromConnection().getAONContext().getDslContext() == null) {
				String msg = "[ERROR]: No se ha definido la conexión origen a la BD";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			
			if (params.isValidate()) {
				ConsoleDomainCheckIntegrity.check(processId, params, true);
				if (!params.hasErrors()) {
					isolateDomain(processId, params);		
				}
			} else {
				isolateDomain(processId, params);
			}
		} finally {
			ConsoleMessageUtils.end(params.getPrinter());
		}
		
	}

	private static void isolateDomain(String processId, ConsoleParams params) {
		ConsoleIDsTableInfo idsTableInfo = new ConsoleIDsTableInfo();
		try {
			Domain fullDomain = DomainDAO.getDomain(params.getFromConnection().getAONContext(), params.getFromConnection().getDomainId());
			if (fullDomain == null) {
				String msg = "No se ha encontrado el dominio \"" + params.getFromConnection().getDomainName() + "\"";
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			params.getFromConnection().setDomain(fullDomain);
			
			params.setScript( new LinkedHashMap<>() );
			DomainValidator domainValidator = DomainValidator.getInstance(true);
			if (!domainValidator.isValid(params.getToConnection().getDomainName())) {
				String msg = MessageFormat.format("[ERROR]: El nuevo nombre de dominio [{0}], no es válido", params.getToConnection().getDomainName());
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, msg));
				throw new AonCoreException(msg);
			}
			
			params.getToDslContext().transaction(conf -> {
				checkProductIndex( params );
				checkNoticeRecipient( params );
				checkAgreements(params);
			});
			
			fillScript(processId,params);
			params.setTotalCount( (int) params.getScript()
				.values()
				.stream()
				.filter(t -> !DOMAIN.getName().equals(t.getTable().getName()))
				.filter(t -> !SESSION.getName().equals(t.getTable().getName()))
				.filter(t -> !ACTION_ENTRY.getName().equals(t.getTable().getName()))
				.count());
			
			if (params.getFromConnection().getDomain().isEnableHeredity() && params.mustFlatten()) {
				long parentTables = params.getScript()
					.values()
					.stream()
					.filter(t -> ConsoleUtils.PARENT_INCLUDED_TABLES.contains(t.getTable().getName()))
					.filter(t -> hasDomain(t.getTable()))
					.count();
				params.setTotalCount( params.getTotalCount() +  ((int) parentTables)  + 1);
			}
			
			params.setTotalCount( params.getTotalCount() +  2);
			
			
			
			ConsoleUtils.disableForeignKeys(params);
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Start transaction"));

			params.getToDslContext().transaction(conf -> {
				
				createDomain(processId,params);
				logMainProgress(processId, params);
				
				ConsoleUtils.initializeConsoleIDsTableInfo(idsTableInfo, params);
				logMainProgress(processId, params);
				
				params.setIdsTableInfo( idsTableInfo );			
				createTempTable(processId,params);
				logMainProgress(processId, params);
				
				insertId(params, DOMAIN,
					params.getFromConnection().getDomain().getId(),
					params.getToConnection().getDomain().getId());

				
				if (params.getFromConnection().getDomain().isEnableHeredity() && params.mustFlatten()) {
					passHeritableTables( processId, params );
				}
				
				if (!AonStringUtils.equals(params.getFromConnection().getSchemaName(), params.getToConnection().getSchemaName())) {
					// Se pasan los datos a otro esquema. Se vinculan los datos de dominio cera de un esquema a otro.
					bindTagTable( params );
					logMainProgress(processId, params);
				}
				
				params.getScript()
					.values()
					.stream()
					.filter(t -> !DOMAIN.getName().equals(t.getTable().getName()))
					.filter(t -> !SESSION.getName().equals(t.getTable().getName()))
					.filter(t -> !ACTION_ENTRY.getName().equals(t.getTable().getName()))
					.forEach(t -> duplicateTable(processId, params, t));
				loopRefInvoice(processId,params,params.getScript().get("invoice"));
				logMainProgress(processId, params);
				loopFBatch(processId,params,params.getScript().get("fbatch"));
				logMainProgress(processId, params);
				loopBankStatementLinkFinanceTracking(processId,params,params.getScript().get("bank_statement_link"));
				logMainProgress(processId, params);
				loopAccAppParamAccount(processId,params,params.getScript().get("app_param"));
				logMainProgress(processId, params);
				if (params.getFromConnection().getDomain().isEnableHeredity() && params.mustFlatten()) {
					fixTaskHolder(processId, params );
					logMainProgress(processId, params);
				}
				createLoginUser(processId,params);
				logMainProgress(processId, params);
			});
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Commit"));
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "FIN DUPLICADO DEL DOMINIO"));
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "HECHO. OK!"));
		} catch (Exception e) {
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e.getMessage()));
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "FIN. ROLLBACK!"));
			e.printStackTrace();
		} finally {
			if (idsTableInfo != null && idsTableInfo.getCtx() != null) {
				idsTableInfo.getCtx().close();
			}
			if (!params.getErrors().isEmpty()) {
				ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "SE HAN PRODUCIDO INCIDENCIAS"));
				params.getErrors().stream().forEach( e -> ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.error(processId, e)));
			}
			ConsoleUtils.enableForeignKeys(params);
		}
	}
	
	private static void fixTaskHolder(String processId, ConsoleParams params) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, "Fix Task HOLDER table"));
		params.getToDslContext()
			.select( TASK_HOLDER.REGISTRY, TASK_HOLDER.USER_ID )
			.from( TASK_HOLDER )
			.where( TASK_HOLDER.DOMAIN.eq(params.getToConnection().getDomain().getId()))
			.and( TASK_HOLDER.USER_ID.isNotNull() )
			.fetch()
			.stream()
			.forEach(r -> {
				Integer taskHolderId = r.getValue(TASK_HOLDER.REGISTRY);
				Integer userId = r.getValue(TASK_HOLDER.USER_ID);
				Integer targetId = params.getToDslContext()
					.select( USER.ID, USER.DOMAIN )
					.from( USER )
					.where(USER.DOMAIN.eq(params.getToConnection().getDomain().getId()))
					.and(USER.ID.eq(userId))
					.fetch()
					.stream()
					.map(rec -> rec.getValue(USER.ID) )
					.findFirst()
					.orElse(null);
				if (targetId == null) {
					int count = params.getToDslContext()
						.update( TASK_HOLDER )
						.set(TASK_HOLDER.USER_ID, (Integer) null)
						.where(TASK_HOLDER.REGISTRY.eq(taskHolderId))
						.execute();
					ConsoleMessageUtils.print(params.getPrinter()
						, ConsoleMessageUtils.message(processId, "- Task HOLDER "+ taskHolderId +" USER set to NULL ("+ count + " rows )"));
				}
			});
	}

	private static void bindTagTable(ConsoleParams params) {
		params.getFromDslContext()
			.select( TAG.ID, TAG.NAME, TAG.TYPE, TAG.COLOR )
			.from( TAG )
			.where(TAG.DOMAIN.eq(0))
			.fetch()
			.stream()
			.forEach(r -> {
				Integer fromId = r.getValue(TAG.ID);
				String name = r.getValue(TAG.NAME);
				Integer toId = params.getToDslContext()
					.select( TAG.ID )
					.from( TAG )
					.where(TAG.DOMAIN.eq(0))
					.and(TAG.NAME.eq(name))
					.fetch()
					.stream()
					.map(rec -> rec.getValue(TAG.ID) )
					.findFirst()
					.orElse(null);
				if (toId != null) {
					insertId(params, TAG, fromId, toId);
				}
			});
			
		
	}

	private static void checkAgreements(ConsoleParams params) {
		if (params.mustFlatten()) {
			AggregateFunction<Integer> count = DSL.count(CONTRACT.ID);
			params.getFromDslContext()
				.select( CONTRACT.AGREEMENT_LEVEL, count )
				.from(CONTRACT)
				.innerJoin(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(CONTRACT.DOMAIN.eq(params.getFromConnection().getDomain().getId()))
				.and(AGREEMENT_LEVEL.DOMAIN.eq(params.getFromConnection().getDomain().getParentId()))
				.groupBy(CONTRACT.AGREEMENT_LEVEL)
				.having(count.gt(0))
				.fetch()
				.stream()
				.forEach( rec -> params.addError(
						new ConsoleDomainMessage()
							.setType(ConsoleDomainMessageType.AGREEMENT)
							.setSchema(params.getFromConnection().getSchemaName())
							.setDomainId(params.getFromConnection().getDomain().getId())
							.setMessage("Existen \"" + rec.getValue(count) + "\" "
								+ "contratos que referencian "
								+ "al agreement_level " + rec.getValue(CONTRACT.AGREEMENT_LEVEL))
				));
				
			AggregateFunction<Integer> count1 = DSL.count(PAYROLL_WORKPLACE.ID);
				params.getFromDslContext()
					.select( PAYROLL_WORKPLACE.AGREEMENT, count1 )
					.from(PAYROLL_WORKPLACE)
					.innerJoin(AGREEMENT).on(AGREEMENT.ID.eq(PAYROLL_WORKPLACE.AGREEMENT))
					.where(PAYROLL_WORKPLACE.DOMAIN.eq(params.getFromConnection().getDomain().getId()))
					.and(AGREEMENT.DOMAIN.eq(params.getFromConnection().getDomain().getParentId()))
					.groupBy(PAYROLL_WORKPLACE.AGREEMENT)
					.having(count1.gt(0))
					.fetch()
					.stream()
					.forEach( rec -> params.addError(
						new ConsoleDomainMessage()
							.setType(ConsoleDomainMessageType.AGREEMENT)
							.setSchema(params.getFromConnection().getSchemaName())
							.setDomainId(params.getFromConnection().getDomain().getId())
							.setMessage("Existen \"" + rec.getValue(count1) + "\" "
								+"centros de trabajo que referencian "
								+ "al convenio " + rec.getValue(PAYROLL_WORKPLACE.AGREEMENT)
					)));
				if (params.hasErrors()) {
					throw new AonCoreException("Revisar convenios");
				}
		}
	}

	private static void checkProductIndex(ConsoleParams params) {
		if (params.getFromConnection().getDomain().isEnableHeredity() && params.mustFlatten()) {
			AggregateFunction<Integer> count = DSL.count(PRODUCT.ID);
			params.getFromDslContext()
				.select( PRODUCT.CODE, count )
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.in(
					params.getFromConnection().getDomain().getId()
					,params.getFromConnection().getDomain().getParentId()))
				.groupBy(PRODUCT.CODE)
				.having(count.gt(1))
				.fetch()
				.stream()
				.forEach( rec -> params.addError(
					new ConsoleDomainMessage()
						.setType(ConsoleDomainMessageType.PRODUCT)
						.setSchema(params.getFromConnection().getSchemaName())
						.setDomainId(params.getFromConnection().getDomain().getId())
						.setPkCode(rec.getValue(PRODUCT.CODE))
						.setMessage("El producto \"" + rec.getValue(PRODUCT.CODE) + "\" se encuentra definido en el padre y en el hijo")
				));
			if (params.hasErrors()) {
				throw new AonCoreException("Productos duplicados");
			}
		}
	}
	
	private static void checkNoticeRecipient(ConsoleParams params ) {
		Integer[] domainIds = null;
		if (params.getFromConnection().getDomain().isEnableHeredity() && params.mustFlatten()) {
			domainIds = new Integer[] {params.getFromConnection().getDomain().getId()
				,params.getFromConnection().getDomain().getParentId()};
		} else {
			domainIds = new Integer[] {params.getFromConnection().getDomain().getId()};
			
		}
		
		params.getFromDslContext()
			.select( NOTICE.ID,NOTICE.DOMAIN,NOTICE.RECIPIENT,NOTICE.DATE,NOTICE.SUBJECT, USER.DOMAIN )
			.from(NOTICE)
			.innerJoin(USER).on(USER.ID.eq(NOTICE.RECIPIENT))
			.where(NOTICE.DOMAIN.in(domainIds))
			.and(USER.DOMAIN.notIn(params.getFromConnection().getDomain().getId()
					,params.getFromConnection().getDomain().getParentId()))
			.fetch()
			.stream()
			.forEach( rec -> params.addError(
				new ConsoleDomainMessage()
					.setType(ConsoleDomainMessageType.INTEGRITY)
					.setSchema( params.getFromConnection().getSchemaName() )
					.setDomainId( rec.getValue(NOTICE.DOMAIN) )
					.setTable( NOTICE.getName() )
					.setPkId( rec.getValue(NOTICE.ID))
					.setFkTable( USER.getName() )
					.setFkColumn(NOTICE.RECIPIENT.getName())
					.setFkId( rec.getValue(NOTICE.RECIPIENT))
					.setWrongDomainId( rec.getValue(USER.DOMAIN))
					.setMessage(
						MessageFormat.format("Existe un aviso (tabla \"notice\") cuyo destinatario no "
								+ "pertence al dominio. [id: {0}, dominio: {1}, fecha: \"{2}\", subject: \"{3}\"] "
							,rec.getValue(NOTICE.ID)
							,rec.getValue(NOTICE.DOMAIN)
							,rec.getValue(NOTICE.DATE)
							,rec.getValue(NOTICE.SUBJECT))							
					)
			));
		if (params.hasErrors()) {
			throw new AonCoreException("Avisos incoherentes");
		}
	}
	
	

	private static void passHeritableTables(String processId, ConsoleParams params) {
		params.getScript()
			.values()
			.stream()
			.filter(t -> ConsoleUtils.PARENT_INCLUDED_TABLES.contains(t.getTable().getName()))
			.filter(t -> hasDomain(t.getTable()))
			.forEach( t -> {
				final String procId = processId + "-" +t.getTableName();
				SelectConditionStep<Record> select = params.getFromDslContext()
						.select()
						.from(t.getTable().asTable())
						.where(getDomainField(t.getTable()).equal(params.getFromConnection().getDomain().getParentId()));
				t.setTotalProgress(  params.getFromDslContext().fetchCount(select) );
				t.setProgress(0);
				select
					.fetch()
					.stream()
					.forEach( rec -> duplicateRow(procId, params, t, rec));
				logMainProgress(procId, params);
			});
	}

	private static void createTempTable(String processId,ConsoleParams params) {
		params.getIdsTableInfo().getCtx().getDslContext()
			.execute("DROP TABLE IF EXISTS `"+params.getIdsTableInfo().getTableName()+"`");
		String sql =
			"CREATE TABLE `"+params.getIdsTableInfo().getTableName()+"` ("
				+"`table_name` char(40) NOT NULL,"
				+"`old_id` int(4) NOT NULL,"
				+"`new_id` int(4) NOT NULL,"
				+"PRIMARY KEY (`old_id`,`table_name`)"
			+"  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;"
		;
		params.getIdsTableInfo().getCtx().getDslContext().execute(sql);
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, 
			"IDs Table created! [" + params.getIdsTableInfo().getTableName() +"]"));
	}
	
	private static void createDomain(String processId, ConsoleParams params) {
		Optional<Record> optRec = params.getFromDslContext().select()
			.from(DOMAIN)
			.where(DOMAIN.ID.equal(params.getFromConnection().getDomain().getId()))
			.fetch()
			.stream()
			.findFirst();
		if (optRec.isEmpty()) {
			throw new IllegalArgumentException("No existe el dominio \"" + params.getFromConnection().getDomainName() + "\"");
		}
		DomainRecord domRec = optRec.get().into(DOMAIN);
		domRec.attach(params.getToDslContext().configuration());
		domRec.setId(null);
		domRec.setName(params.getToConnection().getDomainName());
		domRec.setParent(params.mustFlatten() ? null :domRec.getParent());
		domRec.setScope(null);
		domRec.setEnableheredity(params.mustFlatten()
			? ((byte) 0)
			:domRec.getEnableheredity()
		);
		domRec.store();
		params.getToConnection().setDomain( DomainDAO.getDomain(params.getToConnection().getAONContext(), domRec.getId()));
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId, 
			MessageFormat.format("DOMAIN {0} creado con ID {1}"
					, params.getToConnection().getDomain().getName()
					, params.getToConnection().getDomain().getId())));
	}

	private static void insertId(ConsoleParams params, Table<?> table, Integer oldId, Integer newId) {
		params.getIdsTableInfo().getCtx().getDslContext()
			.insertInto(params.getIdsTableInfo().getTable())
				.set(params.getIdsTableInfo().getTableColumn(), table.getName() )
				.set(params.getIdsTableInfo().getOldIdColumn(), oldId )
				.set(params.getIdsTableInfo().getNewIdColumn(), newId )
			.execute();
	}

	private static void duplicateTable(String processId, ConsoleParams params, ScriptTable t) {
		if (hasDomain(t.getTable())) {
			AggregateFunction<Integer> countField = DSL.count();
			Integer selectCount = params.getFromDslContext()
				.select( countField )
				.from(t.getTable().asTable())
				.where(getDomainField(t.getTable()).equal(params.getFromConnection().getDomain().getId()))
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
				.where(getDomainField(t.getTable()).equal(params.getFromConnection().getDomain().getId()));
			t.setTotalProgress(  selectCount );
			t.setProgress(0);
			int offset = 0;
			int limit = getLimit( t );
			while (offset < selectCount) {
				select	
					.offset(offset)
					.limit(limit)
					.fetch()
					.stream()
					.forEach( rec -> duplicateRow(processId + t.getTableName(), params, t, rec));
				offset = offset + limit;
			}
			logMainProgress(processId + t.getTableName(), params);
		}
	}
	
	private static int getLimit(ScriptTable t) {
		if (AonStringUtils.contains(t.getTableName(), "attach" )) {
			return 10;
		}
		if (t.getTotalProgress() >= 1000) {
			return 500;
		}
		return t.getTotalProgress();
	}

	private static void loopRefInvoice(String processId, ConsoleParams params, ScriptTable t) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"Loop references at invoice table"));
		params.getToDslContext().select()
			.from(t.getTable())
			.where(INVOICE.DOMAIN.eq(params.getToConnection().getDomain().getId()) )
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
					.and(INVOICE.DOMAIN.eq(params.getToConnection().getDomain().getId()) )
					.execute()
			);
	}

	private static void loopFBatch(String processId, ConsoleParams params, ScriptTable t) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"Loop references at fbatch table"));
		params.getToDslContext().select()
			.from(t.getTable())
			.where(FBATCH.DOMAIN.eq(params.getToConnection().getDomain().getId()) )
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
					.and(FBATCH.DOMAIN.eq(params.getToConnection().getDomain().getId() ) )
					.execute()
			);
	}
	
	private static void loopBankStatementLinkFinanceTracking(String processId, ConsoleParams params, ScriptTable t) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"Loop references at BankStatementLink --> FinanceTracking table"));
		params.getToDslContext().select()
			.from(t.getTable())
			.where(BANK_STATEMENT_LINK.DOMAIN.eq(params.getToConnection().getDomain().getId() ) )
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
					.and(BANK_STATEMENT_LINK.DOMAIN.eq(params.getToConnection().getDomain().getId() ) )
					.execute()
			);
	}
  
	private static void loopAccAppParamAccount(String processId, ConsoleParams params, ScriptTable t) {
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"ACC Params --> Account table"));
		params.getToDslContext().select()
			.from(t.getTable())
			.where(APP_PARAM.DOMAIN.eq(params.getToConnection().getDomain().getId() ) )
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
				.and(APP_PARAM.DOMAIN.eq(params.getToConnection().getDomain().getId() ) )
				.execute());
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
		return params.getIdsTableInfo().getCtx().getDslContext()
			.select(params.getIdsTableInfo().getNewIdColumn())
			.from(params.getIdsTableInfo().getTable())
			.where(params.getIdsTableInfo().getTableColumn().eq(table.getName()))
			.and(params.getIdsTableInfo().getOldIdColumn().eq(fkOldId))
			.fetch()
			.stream()
			.map(r -> r.getValue(params.getIdsTableInfo().getNewIdColumn()))
			.findFirst()
			.orElse(null);
	}
	
	private static void duplicateRow(String processId, ConsoleParams params, ScriptTable scriptTable, Record rec) {
		Table<?> table = scriptTable.getTable();
		duplicateRow(params, table, rec);
		scriptTable.setProgress((scriptTable.getProgress() + 1));		
		int percent = (scriptTable.getProgress() * 100 / scriptTable.getTotalProgress());
		if ( percent != scriptTable.getLastMessagePercent()) { 	
			scriptTable.setLastMessagePercent(percent);
			String msg = "- Duplicando tabla " + scriptTable.getTableName() + " (" + scriptTable.getTotalProgress() + " rows )";
			ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.progress(processId
					,scriptTable.getTotalProgress()
					,scriptTable.getProgress() )
					.setMessage(msg));
		}
	}
	
	private static void logMainProgress(String processId, ConsoleParams params) {
		params.setTotalProgress((params.getTotalProgress() + 1));		
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.mainProgress(processId
				,params.getTotalCount()
				,params.getTotalProgress() )
				.setMessage("- Duplicando dominio"));
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
			rec.setValue(getDomainField(fk.getTable()), params.getToConnection().getDomain().getId());
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
	private static void fillScript(String processId, ConsoleParams params) {
		fillScript(processId, params,new LinkedList<>());
	}
	
	private static void fillScript(String processId, ConsoleParams params, Deque<Table<?>> stack) {
		List<Table<?>> tables = params.getFromConnection().getSchema().getTables();
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"Generating tables script"));
		tables.stream()
			.filter(t -> t.field(DOMAIN_FIELD) != null )
			.forEach(t -> addTable(params, t, stack));
	}

	
	@SuppressWarnings("unchecked")
	private static void addTable(ConsoleParams params, Table<?> table, Deque<Table<?>> stack) {
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

	private static void createLoginUser(String processId, ConsoleParams params) {
		int newUserId = params.getToDslContext().insertInto(USER)
				.set(USER.DOMAIN , params.getToConnection().getDomain().getId())
				.set(USER.NAME, "DEFAULT USER")
				.set(USER.LOGIN, "aon")
				.set(USER.PASSWORD, "0jtZh1BMGz3khL8uR8dvdau3lNM=") // org
				.returning(USER.ID)
				.fetchOne()
				.getId();
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"User insertado correctamente"));
		params.getToDslContext().select( SCOPE.ID)
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(params.getToConnection().getDomain().getId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue( SCOPE.ID ))
			.forEach( scopeId ->params.getToDslContext().insertInto(USER_SCOPE)
				.set(USER_SCOPE.USER_ID, newUserId)
				.set(USER_SCOPE.DOMAIN , params.getToConnection().getDomain().getId())
				.set(USER_SCOPE.SCOPE, scopeId)
				.execute());
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"User Scope insertado correctamente"));
		
		params.getToDslContext().select( DOMAIN_APPLICATION.ID)
			.from(DOMAIN_APPLICATION)
			.where(DOMAIN_APPLICATION.DOMAIN.eq(params.getToConnection().getDomain().getId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue( DOMAIN_APPLICATION.ID ))
			.map( domainApplicationId -> params.getToDslContext().insertInto(APPLICATION_USER)
				.set(APPLICATION_USER.DOMAIN, params.getToConnection().getDomain().getId())
				.set(APPLICATION_USER.USER_ID, newUserId)
				.set(APPLICATION_USER.DOMAIN_APPLICATION, domainApplicationId)
				.set(APPLICATION_USER.ACTIVE, (byte) 1)
				.returning(APPLICATION_USER.ID)
				.fetchOne()
				.getId())
			.forEach(applicationUserId -> params.getToDslContext().insertInto(APPLICATION_USER_PROFILE)
					.set(APPLICATION_USER_PROFILE.DOMAIN, params.getToConnection().getDomain().getId())
					.set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUserId)
					.set(APPLICATION_USER_PROFILE.PROFILE, 71)
					.returning(DOMAIN_APPLICATION.ID)
					.fetchOne()
					.getId());
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"Aplicacion de usuario insertada correctamente"));
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.message(processId,"Perfil de usuario en la aplicación insertada correctamente"));
	}
    
}
