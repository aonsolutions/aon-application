package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.jooq.tables.records.AccountEntryDetailRecord;
import com.esferalia.aon.jooq.tables.records.AccountEntryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryTypeVisitorAdapter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailFilter;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailProperties;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.validation.AccountEntryValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AccountEntryDAO {
	
	// --------------------------------------------------------------- LECTURA

	private static final Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

	public static AccountEntry getAccountEntry(AONContext ctx,Integer id) {
		return fetch(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId())
				   	.and(p.getIdProperty().eq(id)), 0, 1)
		.findFirst().orElse(null);
	}

	public static Stream<AccountEntry> fetch(AONContext ctx
			, AccountEntryFilter filter
			, int offset
			, int numberOfRows) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.DOMAIN,ACCOUNT_ENTRY.ACCOUNT_PERIOD
					,ACCOUNT_PERIOD.NAME,ACCOUNT_PERIOD.STATUS,ACCOUNT_ENTRY.ENTRY_DATE
					,ACCOUNT_ENTRY.ENTRY_TYPE,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY.SECURITY_LEVEL
					,ACCOUNT_ENTRY.ACTIVITY,ACCOUNT_ENTRY.COMMENTS
					,ACCOUNT_ENTRY.CREATION_USER,ACCOUNT_ENTRY.CREATION_DATE
					,ACCOUNT_ENTRY.MODIFICATION_USER,ACCOUNT_ENTRY.MODIFICATION_DATE)
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
				.where(ACCOUNT_ENTRY_PROPERTIES.getConditions(filter))
				.orderBy(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY.ENTRY_DATE)
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new FullAccountEntryFiller() )
				.peek( ae -> ae.setDetails(ctx.getDslContext()
						.select(ACCOUNT_ENTRY_DETAIL.ID,ACCOUNT_ENTRY_DETAIL.DOMAIN,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY
								,ACCOUNT_ENTRY_DETAIL.LINE,ACCOUNT_ENTRY_DETAIL.ACCOUNT,ACCOUNT_ENTRY_DETAIL.CONCEPT
								,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT
								,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,ACCOUNT_ENTRY_DETAIL.CREATION_USER,ACCOUNT_ENTRY_DETAIL.CREATION_DATE
								,ACCOUNT_ENTRY_DETAIL.MODIFICATION_USER,ACCOUNT_ENTRY_DETAIL.MODIFICATION_DATE
								,DET_ACCOUNT.CODE,DET_ACCOUNT.DESCRIPTION
								,BAL_ACCOUNT.CODE,BAL_ACCOUNT.DESCRIPTION
								)
							.from(ACCOUNT_ENTRY_DETAIL)
							.join(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
							.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
							.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ae.getId()))
							.orderBy(ACCOUNT_ENTRY_DETAIL.LINE)
							.fetch()
							.stream()
							.map( new FullAccountEntryDetailFiller() )
							.collect(Collectors.toCollection(LinkedList::new)))
					)
			;
	}

	public static Stream<AccountEntry> fetchByLines(AONContext ctx
			, AccountEntryDetailFilter filter
			, int offset
			, int numberOfRows) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.selectDistinct(ACCOUNT_ENTRY.ID)
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.where(ACCOUNT_ENTRY_DETAIL_PROPERTIES.getConditions(filter))
				.orderBy(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY.ENTRY_DATE)
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( t-> fetch(ctx,p -> 
							p.getDomainProperty().eq(ctx.getDomainId())
							.and(p.getIdProperty().eq(t.getValue(ACCOUNT_ENTRY.ID))), 0, 1)
							.findFirst().orElse(null) )
			;
	}
		
	// ------------------------------------------------------------- ESCRITURA
	public static Integer save(AONContext ctx, AccountEntry ae) {
		if (ae.getId() == null) {
			return insert(ctx, ae);
		} else {
			update(ctx, ae);
			return ae.getId();
		}
	}
	
	public static Integer insert(AONContext ctx, AccountEntry ae) {
		ctx.checkWrite();
		AccountEntryValidation.validateEntry(ctx, ae);
		increaseJournal(ctx, ae);
		AccountEntryRecord record = ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY)
				.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getPeriod())
				.set(ACCOUNT_ENTRY.ENTRY_DATE,AonDateUtils.toSql(ae.getEntryDate()))
				.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
				.set(ACCOUNT_ENTRY.JOURNAL, ae.getJournal())
				.set(ACCOUNT_ENTRY.ACTIVITY, ae.getActivity())
				.set(ACCOUNT_ENTRY.SECURITY_LEVEL,  (byte) (ae.isConfidential()?1:0) )
				.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
				.set(ACCOUNT_ENTRY.CREATION_USER,ctx.getUser())
				.set(ACCOUNT_ENTRY.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(ACCOUNT_ENTRY.ID)
				.fetchOne();
		ae.setId(record.getValue(ACCOUNT_ENTRY.ID));
		ctx.log().info("INSERT ACCOUNT_ENTRY asiento: " + ae.getId());
		batchInsert(ctx, ae);
		return record.getValue(ACCOUNT_ENTRY.ID); 
	}

	private static void batchInsert(AONContext ctx, AccountEntry ae) {
		InsertSetStep<AccountEntryDetailRecord> insert = ctx.getDslContext().insertInto(ACCOUNT_ENTRY_DETAIL);
		InsertSetMoreStep<AccountEntryDetailRecord>  insertMore = null;
		int line = 0;
		for (AccountEntryDetail detail : ae.getDetails()) {
			AccountEntryValidation.validateDetail(ctx, detail);
			if (insertMore != null) {
				insert = insertMore.newRecord();
			}
			insertMore =  insert
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount())
				.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( ++line ))
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount())
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
				.set(ACCOUNT_ENTRY_DETAIL.CREATION_USER,ctx.getUser())
				.set(ACCOUNT_ENTRY_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			;
		}
		if (insertMore != null) {
			int count = insertMore.execute();
			ctx.log().info("INSERT ACCOUNT_ENTRY detalles asiento: " + ae.getId() + " ("+count+" filas)");			
		}
	}

	public static void update(AONContext ctx, AccountEntry ae) {
		ctx.checkWrite();
		AccountEntryValidation.validateEntry(ctx, ae);
		int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
			.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
			.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getPeriod())
			.set(ACCOUNT_ENTRY.ENTRY_DATE,AonDateUtils.toSql(ae.getEntryDate()))
			.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
			.set(ACCOUNT_ENTRY.JOURNAL,ae.getJournal())
			.set(ACCOUNT_ENTRY.ACTIVITY, ae.getActivity())
			.set(ACCOUNT_ENTRY.SECURITY_LEVEL, AonEnumUtils.getByte(ae.getSecurityLevel()))
			.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
			.set(ACCOUNT_ENTRY.MODIFICATION_USER,ctx.getUser())
			.set(ACCOUNT_ENTRY.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
			.execute();
		ctx.log().info("UPDATE ACCOUNT_ENTRY  ("+i+") asiento: " + ae.getId());
		updateDetails(ctx, ae);
	}

	private static void updateDetails(AONContext ctx, AccountEntry ae) {
		int line = 0;
		for (AccountEntryDetail detail : ae.getDetails()) {
			if (!detail.isDeleted()) {
				++line;
				AccountEntryValidation.validateDetail(ctx, detail);
				if (detail.getId() != null) {
					if (!detail.isDirty() && line != detail.getLine() ) {
						detail.setLine(line);	
					}
					if (detail.isDirty()) {
						int i = ctx.getDslContext().update(ACCOUNT_ENTRY_DETAIL)
							.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
							.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
							.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount())
							.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( line ))
							.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
							.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
							.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
							.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount())
							.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
							.set(ACCOUNT_ENTRY_DETAIL.MODIFICATION_USER,ctx.getUser())
							.set(ACCOUNT_ENTRY_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
							.execute();
						ctx.log().info("UPDATE ACCOUNT_ENTRY_DETAIL  ("+i+" rows) ("+line+") " + detail.getId());
					}
				} else {
					ctx.getDslContext().insertInto(ACCOUNT_ENTRY_DETAIL)
						.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
						.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
						.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount())
						.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( line ))
						.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
						.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
						.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
						.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount())
						.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
						.set(ACCOUNT_ENTRY_DETAIL.CREATION_USER,ctx.getUser())
						.set(ACCOUNT_ENTRY_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.execute();
					ctx.log().info("INSERT ACCOUNT_ENTRY_DETAIL ("+line+")");
				}
			} else {
				Integer id = detail.getId() * -1;
				int i = ctx.getDslContext()
					.delete(ACCOUNT_ENTRY_DETAIL)
					.where(ACCOUNT_ENTRY_DETAIL.ID.equal(id))
					.execute();
				ctx.log().info("DELETE ACCOUNT_ENTRY_DETAIL  ("+i+" rows ) ("+line+") " + id);
			}
		}
	}

	public static LinkedHashMap<String, AccountBalance> fetchBalance(
			AONContext ctx, AccMiningParameters params) {
		java.sql.Date start = AonDateUtils.toSql(params.getStartDate()!= null? params.getStartDate() : AonDateUtils.getYearFirstDay(0));
		java.sql.Date end = AonDateUtils.toSql(params.getEndDate()!= null? params.getEndDate() : AonDateUtils.getYearLastDay(9999));
		
		Field<String> accountField = DSL.substring(ACCOUNT.CODE, 1, params.getAccountLevel()); 
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT); 
		LinkedHashMap<String, AccountBalance> map = new LinkedHashMap<String, AccountBalance>();
		ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ENTRY_TYPE, accountField, sumDebit, sumCredit)
			.from( ACCOUNT_ENTRY )
			.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
			.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
			.where(ACCOUNT_ENTRY.DOMAIN.equal(params.getDomain()))
			.and(ACCOUNT_ENTRY.ENTRY_DATE.between(start,end))
			.and(ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.CLOSING.getValue()) )
			.groupBy(ACCOUNT_ENTRY.ENTRY_TYPE, accountField)
			.fetch()
			.stream()
			.forEach( record -> {
				byte type = record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE);
				String account = record.getValue(accountField);
				double debit = record.getValue(sumDebit).doubleValue();
				double credit = record.getValue(sumCredit).doubleValue();
				putAccountBalance(map,type,account.substring(0,1), debit,credit);
				putAccountBalance(map,type,account.substring(0,2), debit,credit);
				putAccountBalance(map,type,account.substring(0,3), debit,credit);
				putAccountBalance(map,type,account, debit,credit);
			});
		return map;
	}

	private static synchronized void increaseJournal(AONContext ctx,AccountEntry accountEntry) {
		// Se averigua el último numero de diario y se incrementa en uno.
		AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.JOURNAL);
		Record1<Integer> record = ctx.getDslContext()
				.select(maxFunc)
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.DOMAIN.equal(accountEntry.getDomain()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(accountEntry.getPeriod()))
				.fetchOne();
		Integer lastJournal = record.getValue(maxFunc);
		if (lastJournal == null) {
			lastJournal = 0;
		}
		accountEntry.setJournal(lastJournal + 1);
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		AccountEntry entry = getAccountEntry(ctx, id);
		if (entry == null) throw new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage());;
		AccountEntryValidation.validateRemove(ctx, entry);
		beforeRemove(ctx,entry);
		// Se borran las lineas
		int count = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_DETAIL)
			.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(id))
			.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY detalles del asiento: " + id + " ("+count+" filas)");
		// Se borra la cabecera
		count = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.ID.equal(id))
			.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY asiento: " + id + " ("+count+" filas)");
		afterRemove(ctx, entry);
	}

	private static void beforeRemove(final AONContext ctx,final AccountEntry entry) {
		entry.getEntryType().visit(entry, new AccountEntryTypeVisitorAdapter() {
			
			@Override
			public void visitReturnedPayment(AccountEntry entry) {
				removeFinance(entry);
			}
			
			@Override
			public void visitReturnedCollection(AccountEntry entry) {
				removeFinance(entry);
			}
			
			@Override
			public void visitCollection(AccountEntry entry) {
				removeFinance(entry);
			}
			
			@Override
			public void visitPayment(AccountEntry entry) {
				removeFinance(entry);
			}
			@Override
			public void visitFinance(AccountEntry entry) {
				removeFinance(entry);
			}

			@Override
			public void visitLeasingFee(AccountEntry entry) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage());
			}
			
			@Override
			public void visitLeasing(AccountEntry entry) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage());
			}
			
			@Override
			public void visitInvestmentInvoice(AccountEntry entry) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage());
			}
			
			
			@Override
			public void visitAmortization(AccountEntry entry) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage());
			}
			@Override
			public void visitExpenseInvoice(AccountEntry entry) {
				removeInvoice(entry);
			}
			@Override
			public void visitSalesInvoice(AccountEntry entry) {
				removeInvoice(entry);
			}
			@Override
			public void visitPurchaseInvoice(AccountEntry entry) {
				removeInvoice(entry);
			}

			private void removeFinance(AccountEntry entry) {
				FinanceDAO.deleteAccountEntryTrackings(ctx, entry.getId());
			}

			
			private void removeInvoice(AccountEntry entry) {
				Integer invoiceId = ctx.getDslContext()
						.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
						.from( ACCOUNT_ENTRY_INVOICE )
						.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(entry.getId()))
						.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(ctx.getDomainId()))
						.fetch()
						.stream()
						.mapToInt(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
						.findFirst()
						.orElse( Integer.MIN_VALUE );
				if (invoiceId != null && invoiceId != Integer.MIN_VALUE) {
					FinanceDAO.deleteAllPendingFinances(ctx,invoiceId);
					int count = ctx.getDslContext()
						.delete(ACCOUNT_ENTRY_INVOICE)
						.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(entry.getId()))
						.execute();
					ctx.log().info("DELETE ACCOUNT_ENTRY_INVOICE ("+count+" filas.)");
					count = ctx.getDslContext()
							.delete(INVOICE_DETAIL_ACCOUNT)
							.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.in( 
								ctx.getDslContext().select(INVOICE_DETAIL.ID)
										.from(INVOICE_DETAIL)
										.where(INVOICE_DETAIL.INVOICE.equal(invoiceId))))
							.execute();
					ctx.log().info("DELETE INVOICE_DETAIL_ACCOUNT ("+count+" filas.)");
					count = ctx.getDslContext()
						.delete(INVOICE_TAX_ACCOUNT)
						.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.in( 
							ctx.getDslContext().select(INVOICE_TAX.ID)
									.from(INVOICE_TAX)
									.join(INVOICE_DETAIL).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL))
									.where(INVOICE_DETAIL.INVOICE.eq(invoiceId))))
						.execute();
					ctx.log().info("DELETE INVOICE_TAX_ACCOUNT ("+count+" filas.)");
					InvoiceDAO.delete(ctx, invoiceId);
				}
			}
		});	
	}

	private static void afterRemove(final AONContext ctx,final AccountEntry entry) {
		entry.getEntryType().visit(entry, new AccountEntryTypeVisitorAdapter() {
			
			@Override
			public void visitOpening(AccountEntry entry) {
				if (!AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.OPENING)) {
					// Si después de borrar apertura, existe otro apertura, se mantiene 
					// el estado (o se modifica si era errroneo).
					AccountPeriodDAO.open(ctx,entry.getPeriod());
				} else {
					// Si después de borrar apertura, no existe otro apertura, se activa.
					AccountPeriodDAO.active(ctx,entry.getPeriod());
				}
			}
			
			@Override
			public void visitClosing(AccountEntry entry) {
				if (AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.CLOSING)) {
					// Si después de borrar cierre, existe otro cierre, se mantiene el estado.
					AccountPeriodDAO.close(ctx,entry.getPeriod());
				} else if (AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.OPERATING)) {
					// Si después de borrar cierre, existe explotación.
					AccountPeriodDAO.operating(ctx,entry.getPeriod());
				} else if (AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.OPENING)) {
					// Si después de borrar cierre, no existe explotación y sí apertura.
					AccountPeriodDAO.open(ctx,entry.getPeriod());
				} else {
					// Si después de borrar cierre, no existe explotación ni apertura. Se activa.
					AccountPeriodDAO.active(ctx,entry.getPeriod());
				}
			}
			
			@Override
			public void visitOperating(AccountEntry entry) {
				if (AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.OPERATING)) {
					// Si después de borrar explotación, existe otro explotación, se mantiene el estado.
					AccountPeriodDAO.operating(ctx,entry.getPeriod());
				} else if (AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.OPENING)) {
					// Si después de borrar explotación, existe apertura.
					AccountPeriodDAO.open(ctx,entry.getPeriod());
				} else {
					// Si después de borrar cierre, no existe apertura.
					AccountPeriodDAO.active(ctx,entry.getPeriod());
				}
			}
		});
	}

	public static boolean existsAnyEntry(AONContext ctx, Integer period, AccountEntryType accountEntryType) {
		ctx.checkRead();
		Select<Record> select = ctx.getDslContext()
				.select()
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period))
				.and(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AonEnumUtils.getByte( accountEntryType)));
		return ctx.getDslContext().fetchCount(select) > 0;
	}

	private static void putAccountBalance(Map<String, AccountBalance> map,int type, String account,double debit, double credit) {
		if (map.containsKey(account)) {
			AccountBalance ac = map.get(account);
			ac.add(type,debit, credit);
		} else {
			map.put(account, new AccountBalance(type,debit,credit));	
		}
	}
	
	public static Stream<AccountingBreakdown> getAccountingBreakdown(AONContext ctx, AccountEntryDetailFilter filter) {
		return ctx.getDslContext().select(
				ACCOUNT_ENTRY.ENTRY_DATE
				,IAE.EPIGRAPH
				,IAE.SECTION
				,IAE.EPIGRAPH
				,ENTERPRISE_ACTIVITY.RETENTION_REGIME
				,ACCOUNT_ENTRY_DETAIL.ACCOUNT
				,ACCOUNT.CODE
				,ACCOUNT.DESCRIPTION
				,ACCOUNT_ENTRY_DETAIL.CONCEPT
				,ACCOUNT_ENTRY_DETAIL.DEBIT
				,ACCOUNT_ENTRY_DETAIL.CREDIT
				)
			.from(ACCOUNT_ENTRY)
			.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID))
			.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(ACCOUNT_ENTRY.ACTIVITY))
			.leftOuterJoin(IAE).on(ENTERPRISE_ACTIVITY.IAE.equal(IAE.ID))
			.where(ACCOUNT_ENTRY_DETAIL_PROPERTIES.getConditions(filter))
			.fetch()
			.stream()
			.map(new AccountingBreakdownFiller());
	}
	

	private static class AccountingBreakdownFiller  implements Function<Record,AccountingBreakdown> {
		@Override
		public AccountingBreakdown apply(Record record) {
			return new AccountingBreakdown()
			.setIssueDate(record.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
			.setEpigraph(record.getValue(IAE.EPIGRAPH))
			.setEpigraphSection(record.getValue(IAE.SECTION))
			.setRegime( IRPFRegime.safeValueOf(record.getValue(ENTERPRISE_ACTIVITY.RETENTION_REGIME)))
			.setAccount(record.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.setAccountCode(record.getValue(ACCOUNT.CODE))
			.setAccountDescription(record.getValue(ACCOUNT.DESCRIPTION))
			.setConcept(record.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
			.setDebit(record.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT))
			.setCredit(record.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT))
			;
		}

			
	}

	private static class FullAccountEntryFiller  implements Function<Record,AccountEntry> {
		@Override
		public AccountEntry apply(Record record) {
			return new AccountEntry()
				.setId( record.getValue(ACCOUNT_ENTRY.ID) )
				.setPeriod( record.getValue(ACCOUNT_ENTRY.ACCOUNT_PERIOD))
				.setPeriodName( record.getValue(ACCOUNT_PERIOD.NAME) )
				.setPeriodStatus(AccountPeriodStatus.values()[record.getValue(ACCOUNT_PERIOD.STATUS)])
				.setDomain( record.getValue(ACCOUNT_ENTRY.DOMAIN))
				.setEntryDate( record.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
				.setEntryType( AccountEntryType.values()[record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)])
				.setActivity( record.getValue(ACCOUNT_ENTRY.ACTIVITY))
				.setJournal( record.getValue(ACCOUNT_ENTRY.JOURNAL))
				.setSecurityLevel(SecurityLevel.values()[record.getValue(ACCOUNT_ENTRY.SECURITY_LEVEL)])
				.setComments( record.getValue(ACCOUNT_ENTRY.COMMENTS))
				.setCreationUser(record.getValue(ACCOUNT_ENTRY.CREATION_USER))
				.setCreationDate(record.getValue(ACCOUNT_ENTRY.CREATION_DATE))
				.setModificationUser(record.getValue(ACCOUNT_ENTRY.MODIFICATION_USER))
				.setModificationDate(record.getValue(ACCOUNT_ENTRY.MODIFICATION_DATE))
				.setDirty(false)
				;
		}

			
	}

	private static class FullAccountEntryDetailFiller  implements Function<Record,AccountEntryDetail> {
		@Override
		public AccountEntryDetail apply(Record record) {
			return new AccountEntryDetail()
				.setId( record.getValue(ACCOUNT_ENTRY_DETAIL.ID) )
				.setDomain( record.getValue(ACCOUNT_ENTRY_DETAIL.DOMAIN))
				.setAccountEntry( record.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.setAccount(record.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.setAccountCode(record.getValue(DET_ACCOUNT.CODE))
				.setAccountDescription(record.getValue(DET_ACCOUNT.DESCRIPTION))
				.setLine( record.getValue(ACCOUNT_ENTRY_DETAIL.LINE).intValue() )
				.setConcept(record.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
				.setDebit(record.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT))
				.setCredit(record.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT))
				.setBalancingAccount(record.getValue(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.setBalancingAccountCode(record.getValue(BAL_ACCOUNT.CODE))
				.setBalancingAccountDescription(record.getValue(BAL_ACCOUNT.DESCRIPTION))
				.setDocumentNumber(record.getValue(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
				.setDirty(false)
				;
		}
	}
	
	// ---------------------------------------------------------- FILTROS
	private static final AccountEntryPropertiesDAO ACCOUNT_ENTRY_PROPERTIES = new AccountEntryPropertiesDAO();
	private static class AccountEntryPropertiesDAO implements AccountEntryProperties {

		private Condition[] getConditions(AccountEntryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.ID);
		}

		@Override
		public Property<Integer> getJournalProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.JOURNAL);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.DOMAIN);
		}

		@Override
		public Property<Integer> getAccountPeriodProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY.ACCOUNT_PERIOD);
		}
		
		@Override
		public Property<Date> getEntryDateProperty() {
			return new FilterDAO.DatePropertyDAO(ACCOUNT_ENTRY.ENTRY_DATE);
		}

		@Override
		public Property<Byte> getEntryTypeProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_ENTRY.ENTRY_TYPE);
		}

		@Override
		public Property<Byte> getConfidentialProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_ENTRY.SECURITY_LEVEL);
		}
	}

	private static final AccountEntryDetailPropertiesDAO ACCOUNT_ENTRY_DETAIL_PROPERTIES 
		= new AccountEntryDetailPropertiesDAO();
	private static class AccountEntryDetailPropertiesDAO extends AccountEntryPropertiesDAO implements AccountEntryDetailProperties {

		private Condition[] getConditions(AccountEntryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getAccountProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_ENTRY_DETAIL.ACCOUNT);
		}

		@Override
		public Property<String> getAccountCodeProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT.CODE);
		}
		
		@Override
		public Property<String> getAccountDescriptionProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT.DESCRIPTION);
		}

		@Override
		public Property<String> getConceptProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT_ENTRY_DETAIL.CONCEPT);
		}

		@Override
		public Property<Double> getDebitProperty() {
			return new FilterDAO.PropertyDAO<Double>(ACCOUNT_ENTRY_DETAIL.DEBIT);
		}

		@Override
		public Property<Double> getCreditProperty() {
			return new FilterDAO.PropertyDAO<Double>(ACCOUNT_ENTRY_DETAIL.CREDIT);
		}

		@Override
		public Property<String> getDocumentNumber() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER);
		}
	}
}

