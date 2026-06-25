package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.AmortizationDetail.AMORTIZATION_DETAIL;
import static com.esferalia.aon.jooq.tables.AutoConcept.AUTO_CONCEPT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

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
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.jooq.tables.records.AccountEntryDetailRecord;
import com.esferalia.aon.jooq.tables.records.AccountEntryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AutoConcept;
import com.esferalia.aon.occam.api.model.Filter.AccountEntryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.IAccountEntryTypeVisitor;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate.AccountEntryUpdateVisitor;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.amortization.AmortizationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.validation.AccountEntryValidation;
import com.esferalia.aon.occam.impl.jooq.validation.InvoiceValidation;
import com.esferalia.aon.occam.server.accounting.AccountEntryUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class AccountEntryDAO {
	private AccountEntryDAO() {
		
	}
	// --------------------------------------------------------------- LECTURA
	public enum AccountEntryOrder {
		 ORDER_PERIOD_JOURNAL( ACCOUNT_ENTRY.ACCOUNT_PERIOD.asc(),ACCOUNT_ENTRY.JOURNAL.asc(),ACCOUNT_ENTRY.ENTRY_DATE.asc())
		,ORDER_CREATION_DATE_DESC ( ACCOUNT_ENTRY.ID.desc())
		,ORDER_MODIFICATION_DATE_DESC ( ACCOUNT_ENTRY.MODIFICATION_DATE.desc(),ACCOUNT_ENTRY.CREATION_DATE.desc())
		;
		SortField<?>[] fields;
		private AccountEntryOrder( SortField<?> ...fields) {
			this.fields = fields;
		}
		public SortField<?>[] getFields() {
			return fields;
		}
		
		public static AccountEntryOrder safeEnum(int order) {
			if (order < 0 || order > AccountEntryOrder.values().length) {
				return ORDER_PERIOD_JOURNAL;
			}
			return AccountEntryOrder.values()[order];
		}
	}
	
	public enum AccountEntryFlatOrder {
		 ORDER_PERIOD_JOURNAL( ACCOUNT_ENTRY.ACCOUNT_PERIOD.asc(),ACCOUNT_ENTRY.JOURNAL.asc(),ACCOUNT_ENTRY.ENTRY_DATE.asc(),ACCOUNT_ENTRY.ID.asc(),ACCOUNT_ENTRY_DETAIL.ID.asc())
		,ORDER_CREATION_DATE_DESC ( ACCOUNT_ENTRY.ID.desc(),ACCOUNT_ENTRY_DETAIL.ID.asc())
		,ORDER_MODIFICATION_DATE_DESC ( ACCOUNT_ENTRY.MODIFICATION_DATE.desc(),ACCOUNT_ENTRY.CREATION_DATE.desc(),ACCOUNT_ENTRY.ID.asc(),ACCOUNT_ENTRY_DETAIL.ID.asc())
		;
		SortField<?>[] fields;
		private AccountEntryFlatOrder( SortField<?> ...fields) {
			this.fields = fields;
		}
		public SortField<?>[] getFields() {
			return fields;
		}
		
		public static AccountEntryFlatOrder safeEnum(int order) {
			if (order < 0 || order > AccountEntryOrder.values().length) {
				return ORDER_PERIOD_JOURNAL;
			}
			return AccountEntryFlatOrder.values()[order];
		}
	}

	private static final Account DET_ACCOUNT = ACCOUNT.as("detAcc");
	private static final Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

	public static AccountEntry getAccountEntry(AONContext ctx,Integer id) {
		return fetch(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId())
				   	.and(p.getIdProperty().eq(id)), 0, 1)
		.findFirst().orElse(null);
	}

	public static Stream<AutoConcept> getAutoConcepts(AONContext ctx) {
		return ctx.getDslContext()
				.selectFrom(AUTO_CONCEPT)
				.where(AUTO_CONCEPT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(AUTO_CONCEPT.DESCRIPTION)
				.fetch()
				.stream()
				.map(rec -> 
					new AutoConcept()
						.setId( rec.getValue(AUTO_CONCEPT.ID))
						.setDomain( rec.getValue(AUTO_CONCEPT.DOMAIN))
						.setDescription( rec.getValue(AUTO_CONCEPT.DESCRIPTION))
				);
	}
	public static Stream<AccountEntry> fetch(AONContext ctx
			, AccountEntryFilter filter
			, int offset
			, int numberOfRows) {
		return fetch(ctx, filter, offset, numberOfRows,AccountEntryOrder.ORDER_PERIOD_JOURNAL);
	}
	
	public static Stream<AccountEntry> fetch(AONContext ctx
			, AccountEntryFilter filter
			, int offset
			, int numberOfRows
			, AccountEntryOrder orderBy) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.DOMAIN,ACCOUNT_ENTRY.ACCOUNT_PERIOD
					,ACCOUNT_PERIOD.NAME,ACCOUNT_PERIOD.STATUS,ACCOUNT_ENTRY.ENTRY_DATE
					,ACCOUNT_ENTRY.ENTRY_TYPE,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY.SECURITY_LEVEL
					,ACCOUNT_ENTRY.ACTIVITY,ACCOUNT_ENTRY.COMMENTS
					,ACCOUNT_ENTRY.CREATION_USER,ACCOUNT_ENTRY.CREATION_DATE
					,ACCOUNT_ENTRY.MODIFICATION_USER,ACCOUNT_ENTRY.MODIFICATION_DATE
					,ENTERPRISE_ACTIVITY.DESCRIPTION)
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ACCOUNT_ENTRY.ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
				.where(ACCOUNT_ENTRY_PROPERTIES.getConditions(filter))
				.orderBy(orderBy.getFields())
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new FullAccountEntryFiller() )
				.map( ae -> ae.setDetails(ctx.getDslContext()
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
	
	public static Stream<FlatAccountEntryDetail> fetchFlatByHeader(AONContext ctx
			, AccountEntryFilter filter
			, AccountEntryOrder orderBy
			, int offset, int limit, IDAOCallback callback) {
		Condition[] conditions = ACCOUNT_ENTRY_PROPERTIES.getConditions(filter);
		return fetchFlat(ctx, conditions, orderBy, offset, limit, callback)
			.onClose(() -> {
				if (callback != null) {
					callback.onFinish();
				}
			})
;
	}
	
	public static Stream<FlatAccountEntryDetail> fetchFlatByLines(AONContext ctx
			, AccountEntryDetailFilter filter
			, AccountEntryOrder orderBy
			, int offset, int limit, IDAOCallback callback) {
		return ctx.getDslContext()
				.selectDistinct(ACCOUNT_ENTRY.ID)
					.from(ACCOUNT_ENTRY)
					.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
					.where(ACCOUNT_ENTRY_DETAIL_PROPERTIES.getConditions(filter))
					.orderBy(orderBy.getFields())
					.limit(offset,limit)
					.fetch()
					.stream()
					.onClose(() -> {
						if (callback != null) {
							callback.onFinish();
						}
					})
					.map (rec -> new FlatAccountEntryDetail().setEntryId(rec.getValue(ACCOUNT_ENTRY.ID)))
					.flatMap(flat -> fetchFlat(ctx
							, ACCOUNT_ENTRY_PROPERTIES.getConditions(p -> p.getDomainProperty().eq(ctx.getDomainId()).and(p.getIdProperty().eq(flat.getEntryId() ) ))
							, orderBy
							, 0
							, Integer.MAX_VALUE
							, () -> {} ))
				;
	}
	
	public static Stream<FlatAccountEntryDetail> fetchFlat(AONContext ctx, AccountEntryParams params, int offset, int limit) {
		Condition[] conditions = AccountEntryDAO.ACCOUNT_ENTRY_DETAIL_PROPERTIES.getConditions(
				p -> AccountEntryUtils.getFilterByLines(ctx,p, params)
		);
		return fetchFlat(ctx
				,conditions
				,AccountEntryOrder.safeEnum(params.getOrder())
				,offset, limit, null
				);
	}

	private static Stream<FlatAccountEntryDetail> fetchFlat(AONContext ctx
			, Condition[] conditions
			, AccountEntryOrder orderBy
			, int offset, int limit, IDAOCallback callback) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.DOMAIN,ACCOUNT_ENTRY.ACCOUNT_PERIOD
					,ACCOUNT_PERIOD.NAME,ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ENTRY_TYPE
					,ACCOUNT_ENTRY.ACTIVITY,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_ENTRY.SECURITY_LEVEL
					,ACCOUNT_ENTRY.COMMENTS
					,ACCOUNT_ENTRY.CREATION_USER,ACCOUNT_ENTRY.CREATION_DATE
					,ACCOUNT_ENTRY.MODIFICATION_USER,ACCOUNT_ENTRY.MODIFICATION_DATE
					,ACCOUNT_ENTRY_DETAIL.ID,ACCOUNT_ENTRY_DETAIL.ACCOUNT,DET_ACCOUNT.CODE
					,DET_ACCOUNT.DESCRIPTION,ACCOUNT_ENTRY_DETAIL.CONCEPT
					,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT
					,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,BAL_ACCOUNT.CODE,BAL_ACCOUNT.DESCRIPTION
					,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER
					,ENTERPRISE_ACTIVITY.DESCRIPTION)
				.from(ACCOUNT_ENTRY)
				.innerJoin(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
				.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.innerJoin(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(ACCOUNT_ENTRY.ACTIVITY))
				.where(conditions)
				.orderBy(AccountEntryFlatOrder.safeEnum( orderBy.ordinal() ).getFields())
				.limit(offset,limit)
				.fetch()
 				.stream()
				.map( rec -> new FlatAccountEntryDetail( )
						.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID))
						.setEntryDomain(rec.getValue(ACCOUNT_ENTRY.DOMAIN))
						.setEntryPperiod(rec.getValue(ACCOUNT_ENTRY.ACCOUNT_PERIOD))
						.setEntryPeriodName(rec.getValue(ACCOUNT_PERIOD.NAME))
						.setEntryDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
						.setEntryType(AccountEntryType.safeValueOf( rec.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)))
						.setActivity(rec.getValue(ACCOUNT_ENTRY.ACTIVITY))
						.setActivityName(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
						.setJournal(rec.getValue(ACCOUNT_ENTRY.JOURNAL))
						.setComments(rec.getValue(ACCOUNT_ENTRY.COMMENTS))
						.setEntrySecurityLevel(SecurityLevel.safeValueOf(rec.getValue(ACCOUNT_ENTRY.SECURITY_LEVEL)))
						.setEntryCreationUser(rec.getValue(ACCOUNT_ENTRY.CREATION_USER))
						.setEntryCreationDate(rec.getValue(ACCOUNT_ENTRY.CREATION_DATE))
						.setEntryModificationUser(rec.getValue(ACCOUNT_ENTRY.MODIFICATION_USER))
						.setEntryModificationDate(rec.getValue(ACCOUNT_ENTRY.MODIFICATION_DATE))
						.setDetailId(rec.getValue(ACCOUNT_ENTRY_DETAIL.ID) )
						.setAccount(rec.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
						.setAccountCode(rec.getValue(DET_ACCOUNT.CODE))
						.setAccountDescription(rec.getValue(DET_ACCOUNT.DESCRIPTION))
						.setConcept(rec.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
						.setDebit(rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT))
						.setCredit(rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT))
						.setBalancingAccount(rec.getValue(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
						.setBalancingAccountCode(rec.getValue(BAL_ACCOUNT.CODE))
						.setBalancingAccountDescription(rec.getValue(BAL_ACCOUNT.DESCRIPTION))
						.setDocumentNumber(rec.getValue(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
					)
			;
	}

	public static Stream<AccountEntry> fetchByLines(AONContext ctx
			, AccountEntryDetailFilter filter
			, int offset
			, int numberOfRows) {
		return fetchByLines(ctx, filter, offset, numberOfRows, AccountEntryOrder.ORDER_PERIOD_JOURNAL);
		
	}
	public static Stream<AccountEntry> fetchByLines(AONContext ctx
				, AccountEntryDetailFilter filter
				, int offset
				, int numberOfRows
				, AccountEntryOrder orderBy) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.selectDistinct(ACCOUNT_ENTRY.ID)
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.where(ACCOUNT_ENTRY_DETAIL_PROPERTIES.getConditions(filter))
				.orderBy(orderBy.getFields())
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
		AccountEntryRecord rec = ctx.getDslContext()
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
		ae.setId(rec.getValue(ACCOUNT_ENTRY.ID));
		ctx.log().debug("INSERT ACCOUNT_ENTRY asiento: {0}",ae.getId());
		batchInsert(ctx, ae);
		return rec.getValue(ACCOUNT_ENTRY.ID); 
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
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccountId())
				.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( ++line ))
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccountId())
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
				.set(ACCOUNT_ENTRY_DETAIL.CREATION_USER,ctx.getUser())
				.set(ACCOUNT_ENTRY_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			;
		}
		if (insertMore != null) {
			int count = insertMore.execute();
			ctx.log().debug("\tINSERT ACCOUNT_ENTRY detalles asiento: {0} ( {1} filas)",ae.getId(),count);			
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
		ctx.log().debug("UPDATE ACCOUNT_ENTRY  ({0}) asiento: {1}",i, ae.getId());
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
							.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccountId())
							.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( line ))
							.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
							.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
							.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
							.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccountId())
							.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
							.set(ACCOUNT_ENTRY_DETAIL.MODIFICATION_USER,ctx.getUser())
							.set(ACCOUNT_ENTRY_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
							.execute();
						ctx.log().debug("UPDATE ACCOUNT_ENTRY_DETAIL  ({0} rows) ({1}) {2}",i,line,detail.getId());
					}
				} else {
					ctx.getDslContext().insertInto(ACCOUNT_ENTRY_DETAIL)
						.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
						.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
						.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccountId())
						.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( line ))
						.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
						.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
						.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
						.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccountId())
						.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
						.set(ACCOUNT_ENTRY_DETAIL.CREATION_USER,ctx.getUser())
						.set(ACCOUNT_ENTRY_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.execute();
					ctx.log().debug("INSERT ACCOUNT_ENTRY_DETAIL ({0})",line);
				}
			} else {
				Integer id = detail.getId() * -1;
				int i = ctx.getDslContext()
					.delete(ACCOUNT_ENTRY_DETAIL)
					.where(ACCOUNT_ENTRY_DETAIL.ID.equal(id))
					.execute();
				ctx.log().debug("DELETE ACCOUNT_ENTRY_DETAIL ({0} rows ) ({1}) {2}",i,line,id);
			}
		}
	}
	public static LinkedHashMap<String, AccountBalance> fetchBalance(AONContext ctx, AccMiningParameters params) {
		return fetchBalance(ctx, params, false);
	}

	public static LinkedHashMap<String, AccountBalance> fetchBalance(AONContext ctx, AccMiningParameters params,boolean pyg) {
		
		java.sql.Date start = AonDateUtils.toSql(params.getStartDate()!= null? params.getStartDate() : AonDateUtils.getYearFirstDay(0));
		java.sql.Date end = AonDateUtils.toSql(params.getEndDate()!= null? params.getEndDate() : AonDateUtils.getYearLastDay(9999));
		
		Field<String> accountField = DSL.substring(ACCOUNT.CODE, 1, params.getAccountLevel()); 
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT); 
		LinkedHashMap<String, AccountBalance> map = new LinkedHashMap<>();
		Condition pygCondition = pyg
				?ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.OPERATING.value())
						.and(ACCOUNT.CODE.like("6%").or(ACCOUNT.CODE.like("7%")) )
				:DSL.trueCondition()
		;
		Condition domainCondition = ACCOUNT_ENTRY.DOMAIN.equal(params.getDomain());
		if ( params.getDomains() != null && params.getDomains().length > 0) {
			LinkedList<Integer> domains = new LinkedList<>(); 
			for (int dom : params.getDomains()) {
				domains.add(dom);
			}
			domainCondition = ACCOUNT_ENTRY.DOMAIN.in(domains);	
		}
		ctx.getDslContext()
			.select(ACCOUNT_ENTRY.ENTRY_TYPE, accountField, sumDebit, sumCredit)
			.from( ACCOUNT_ENTRY )
			.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.equal(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
			.join(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(ACCOUNT.ID))
			.where(domainCondition)
			.and(ACCOUNT_ENTRY.ENTRY_DATE.between(start,end))
			.and(ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.CLOSING.value()) )
			.and( params.getSecurityLevel()!=null
				? ACCOUNT_ENTRY.SECURITY_LEVEL.eq(params.getSecurityLevel().value())
				: DSL.trueCondition() )
			.and(pygCondition)
			.groupBy(ACCOUNT_ENTRY.ENTRY_TYPE, accountField)
			.fetch()
			.stream()
			.forEach( rec -> {
				byte type = rec.getValue(ACCOUNT_ENTRY.ENTRY_TYPE);
				String account = rec.getValue(accountField);
				double debit = rec.getValue(sumDebit).doubleValue();
				double credit = rec.getValue(sumCredit).doubleValue();
				if(account.length() > 0) putAccountBalance(map,type,account.substring(0,1), debit,credit);
				if(account.length() > 1) putAccountBalance(map,type,account.substring(0,2), debit,credit);
				if(account.length() > 2) putAccountBalance(map,type,account.substring(0,3), debit,credit);
				if(account.length() > 3) putAccountBalance(map,type,account.substring(0,4), debit,credit);
				putAccountBalance(map,type,account, debit,credit);
			});
		return map;
	}

	private static synchronized void increaseJournal(AONContext ctx,AccountEntry accountEntry) {
		// Se averigua el último numero de diario y se incrementa en uno.
		AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.JOURNAL);
		Record1<Integer> rec = ctx.getDslContext()
				.select(maxFunc)
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.DOMAIN.equal(accountEntry.getDomain()))
				.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(accountEntry.getPeriod()))
				.fetchOne();
		Integer lastJournal = rec.getValue(maxFunc);
		if (lastJournal == null) {
			lastJournal = 0;
		}
		accountEntry.setJournal(lastJournal + 1);
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		AccountEntry entry = getAccountEntry(ctx, id);
		if (entry == null) throw new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage());
		AccountEntryValidation.validateRemove(ctx, entry);
		beforeRemove(ctx,entry);
		// Se borran las lineas
		int count = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_DETAIL)
			.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(id))
			.execute();
		ctx.log().debug("DELETE ACCOUNT_ENTRY detalles del asiento: {0} ({1} filas)",id,count);
		// Se borra la cabecera
		count = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.ID.equal(id))
			.execute();
		ctx.log().debug("DELETE ACCOUNT_ENTRY asiento: {0} ({1} filas)",id,count);
		afterRemove(ctx, entry);
	}

	private static void beforeRemove(final AONContext ctx,final AccountEntry entry) {
		entry.getEntryType().visit(entry, new IAccountEntryTypeVisitor() {
			
			private void throwAutomaticEntryDelete() {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage());
			}
			
			@Override public void visitLeasingFee(AccountEntry entry) { throwAutomaticEntryDelete(); }
			@Override public void visitLeasing(AccountEntry entry) {throwAutomaticEntryDelete();}
			@Override public void visitInvestmentInvoice(AccountEntry entry) {throwAutomaticEntryDelete();}
			

			@Override public void visitTax(AccountEntry entry) { FiscalModelDAO.unrecord(ctx, entry.getId());}
			@Override public void visitAmortization(AccountEntry entry) { AmortizationDAO.unrecord(ctx, entry.getId());}
			@Override public void visitOtherIncomes(AccountEntry entry) { AccountingIncomeDAO.unrecord( ctx, entry.getDomain(), entry.getId() ); }
			@Override public void visitOtherExpenses(AccountEntry entry) { AccountingExpenseDAO.unrecord( ctx, entry.getDomain(), entry.getId() ); }
			
			private void removeFinance(AccountEntry entry) {
				FinanceEntryDAO.deleteAccountEntryFinanceTrackings(ctx, entry.getId());
			}
			@Override public void visitReturnedPayment(AccountEntry entry) { removeFinance(entry);}
			@Override public void visitReturnedCollection(AccountEntry entry) {removeFinance(entry);}
			@Override public void visitCollection(AccountEntry entry) {removeFinance(entry);}
			@Override public void visitPayment(AccountEntry entry) {removeFinance(entry);}
			@Override public void visitFinance(AccountEntry entry) {removeFinance(entry);}

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
					InvoiceSource source  = InvoiceDAO.getInvoiceSource(ctx, invoiceId).orElse(null);
					int count = ctx.getDslContext()
							.delete(ACCOUNT_ENTRY_INVOICE)
							.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(entry.getId()))
							.execute();
					ctx.log().debug("DELETE ACCOUNT_ENTRY_INVOICE ({0} filas.)",count);
					if (source == InvoiceSource.ACCOUNT) {
						InvoiceDAO.delete(ctx, invoiceId);
					} else {
						InvoiceDAO.unrecord(ctx, invoiceId);
					}
				}
			}
			
			@Override public void visitExpenseInvoice(AccountEntry entry) { removeInvoice(entry); }
			@Override public void visitSalesInvoice(AccountEntry entry) { removeInvoice(entry); }
			@Override public void visitPurchaseInvoice(AccountEntry entry) { removeInvoice(entry); }

			
			
			@Override public void visitOpening(AccountEntry entry) { /* Nothing */ }
			@Override public void visitClosing(AccountEntry entry) { /* Nothing */ }
			@Override public void visitOperating(AccountEntry entry) { /* Nothing */ }
			@Override public void visitManual(AccountEntry entry) { /* Nothing */ }
			@Override public void visitExpenses(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSalary(AccountEntry entry) { /* Nothing */ }
			@Override public void visitLoan(AccountEntry entry) { /* Nothing */ }
			@Override public void visitStockVariation(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSocialInsurance(AccountEntry entry) { /* Nothing */ }
			@Override public void visitLoanFee(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSocialInsuranceAdjust(AccountEntry entry) { /* Nothing */ }
		});	
	}

	private static void afterRemove(final AONContext ctx,final AccountEntry entry) {
		entry.getEntryType().visit(entry, new IAccountEntryTypeVisitor() {
			
			@Override
			public void visitOpening(AccountEntry entry) {
				if (AccountEntryDAO.existsAnyEntry(ctx, entry.getPeriod(),AccountEntryType.OPENING)) {
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

			@Override public void visitManual(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSalesInvoice(AccountEntry entry) { /* Nothing */ }
			@Override public void visitPurchaseInvoice(AccountEntry entry) { /* Nothing */ }
			@Override public void visitExpenseInvoice(AccountEntry entry) { /* Nothing */ }
			@Override public void visitInvestmentInvoice(AccountEntry entry) { /* Nothing */ }
			@Override public void visitExpenses(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSalary(AccountEntry entry) { /* Nothing */ }
			@Override public void visitTax(AccountEntry entry) { /* Nothing */ }
			@Override public void visitLoan(AccountEntry entry) { /* Nothing */ }
			@Override public void visitPayment(AccountEntry entry) { /* Nothing */ }
			@Override public void visitCollection(AccountEntry entry) { /* Nothing */ }
			@Override public void visitStockVariation(AccountEntry entry) { /* Nothing */ }
			@Override public void visitAmortization(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSocialInsurance(AccountEntry entry) { /* Nothing */ }
			@Override public void visitLoanFee(AccountEntry entry) { /* Nothing */ }
			@Override public void visitReturnedPayment(AccountEntry entry) { /* Nothing */ }
			@Override public void visitReturnedCollection(AccountEntry entry) { /* Nothing */ }
			@Override public void visitSocialInsuranceAdjust(AccountEntry entry) { /* Nothing */ }
			@Override public void visitFinance(AccountEntry entry) { /* Nothing */ }
			@Override public void visitOtherExpenses(AccountEntry entry) { /* Nothing */ }
			@Override public void visitOtherIncomes(AccountEntry entry) { /* Nothing */ }
			@Override public void visitLeasing(AccountEntry entry) { /* Nothing */ }
			@Override public void visitLeasingFee(AccountEntry entry) { /* Nothing */ }
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
				ACCOUNT_ENTRY.ID
				,ACCOUNT_ENTRY.JOURNAL
				,ACCOUNT_ENTRY.ENTRY_DATE
				,IAE.EPIGRAPH
				,IAE.SECTION
				,IAE.EPIGRAPH
				,ENTERPRISE_ACTIVITY.ID
				,ENTERPRISE_ACTIVITY.DESCRIPTION
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
		public AccountingBreakdown apply(Record rec) {
			return new AccountingBreakdown()
			.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID))
			.setJournal(rec.getValue(ACCOUNT_ENTRY.JOURNAL))
			.setIssueDate(rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
			.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))
			.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
			.setEpigraph(rec.getValue(IAE.EPIGRAPH))
			.setEpigraphSection(rec.getValue(IAE.SECTION))
			.setRegime( IRPFRegime.safeValueOf(rec.getValue(ENTERPRISE_ACTIVITY.RETENTION_REGIME)))
			.setAccount(rec.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.setAccountCode(rec.getValue(ACCOUNT.CODE))
			.setAccountDescription(rec.getValue(ACCOUNT.DESCRIPTION))
			.setConcept(rec.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
			.setDebit(rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT))
			.setCredit(rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT))
			;
		}

			
	}

	private static class FullAccountEntryFiller  implements Function<Record,AccountEntry> {
		@Override
		public AccountEntry apply(Record rec) {
			return new AccountEntry()
				.setId( rec.getValue(ACCOUNT_ENTRY.ID) )
				.setPeriod( rec.getValue(ACCOUNT_ENTRY.ACCOUNT_PERIOD))
				.setPeriodName( rec.getValue(ACCOUNT_PERIOD.NAME) )
				.setPeriodStatus(AccountPeriodStatus.values()[rec.getValue(ACCOUNT_PERIOD.STATUS)])
				.setDomain( rec.getValue(ACCOUNT_ENTRY.DOMAIN))
				.setEntryDate( rec.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
				.setEntryType( AccountEntryType.values()[rec.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)])
				.setActivity( rec.getValue(ACCOUNT_ENTRY.ACTIVITY))
				.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setJournal( rec.getValue(ACCOUNT_ENTRY.JOURNAL))
				.setSecurityLevel(SecurityLevel.values()[rec.getValue(ACCOUNT_ENTRY.SECURITY_LEVEL)])
				.setComments( rec.getValue(ACCOUNT_ENTRY.COMMENTS))
				.setCreationUser(rec.getValue(ACCOUNT_ENTRY.CREATION_USER))
				.setCreationDate(rec.getValue(ACCOUNT_ENTRY.CREATION_DATE))
				.setModificationUser(rec.getValue(ACCOUNT_ENTRY.MODIFICATION_USER))
				.setModificationDate(rec.getValue(ACCOUNT_ENTRY.MODIFICATION_DATE))
				.setDirty(false)
				;
		}

			
	}

	private static class FullAccountEntryDetailFiller  implements Function<Record,AccountEntryDetail> {
		@Override
		public AccountEntryDetail apply(Record rec) {
			return new AccountEntryDetail()
				.setId( rec.getValue(ACCOUNT_ENTRY_DETAIL.ID) )
				.setDomain( rec.getValue(ACCOUNT_ENTRY_DETAIL.DOMAIN))
				.setAccountEntry( rec.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.setAccountId(rec.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.setAccountCode(rec.getValue(DET_ACCOUNT.CODE))
				.setAccountDescription(rec.getValue(DET_ACCOUNT.DESCRIPTION))
				.setLine( rec.getValue(ACCOUNT_ENTRY_DETAIL.LINE).intValue() )
				.setConcept(rec.getValue(ACCOUNT_ENTRY_DETAIL.CONCEPT))
				.setDebit(rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT))
				.setCredit(rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT))
				.setBalancingAccountId(rec.getValue(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.setBalancingAccountCode(rec.getValue(BAL_ACCOUNT.CODE))
				.setBalancingAccountDescription(rec.getValue(BAL_ACCOUNT.DESCRIPTION))
				.setDocumentNumber(rec.getValue(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
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

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.ID);}
		@Override public Property<Integer> getJournalProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.JOURNAL);}
		@Override public Property<Integer> getActivityProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.ACTIVITY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.DOMAIN);}
		@Override public Property<Integer> getAccountPeriodProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.ACCOUNT_PERIOD);}
		@Override public Property<Date> getEntryDateProperty() {return new FilterDAO.DatePropertyDAO(ACCOUNT_ENTRY.ENTRY_DATE);}
		@Override public Property<Byte> getEntryTypeProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.ENTRY_TYPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.SECURITY_LEVEL);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.COMMENTS);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.TimestampPropertyDAO(ACCOUNT_ENTRY.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.TimestampPropertyDAO(ACCOUNT_ENTRY.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY.MODIFICATION_USER);}
	}

	private static final AccountEntryDetailPropertiesDAO ACCOUNT_ENTRY_DETAIL_PROPERTIES = new AccountEntryDetailPropertiesDAO();
	private static class AccountEntryDetailPropertiesDAO extends AccountEntryPropertiesDAO implements AccountEntryDetailProperties {

		private Condition[] getConditions(AccountEntryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.ACCOUNT);}
		@Override public Property<String> getAccountCodeProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.CODE);}
		@Override public Property<String> getAccountDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.DESCRIPTION);}
		@Override public Property<String> getConceptProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.CONCEPT);}
		@Override public Property<Double> getDebitProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.DEBIT);}
		@Override public Property<Double> getCreditProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.CREDIT);}
		@Override public Property<String> getDocumentNumber() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER);}
		@Override public Property<Integer> getBalancingAccountProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT);}
		@Override public Property<String> getBalancingAccountCodeProperty() {return new FilterDAO.PropertyDAO<>(BAL_ACCOUNT.CODE);}
		@Override public Property<String> getBalancingAccountDescriptionProperty() {return new FilterDAO.PropertyDAO<>(BAL_ACCOUNT.DESCRIPTION);}
	}
	
	
	public static IAccountEntryWrapper  updateSpecial(AONContext ctx, AccountEntryUpdate operation, IAccountEntryWrapper wrapper) {
		AccountEntryUpdateVisitor<IAccountEntryWrapper> visitor = new AccountEntryUpdateVisitor<>() {

			@Override
			public IAccountEntryWrapper visitManualType(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getId() != null) {
					ctx.checkWrite();
					ae.setEntryType(AccountEntryType.MANUAL);
					int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
							.set(ACCOUNT_ENTRY.ENTRY_TYPE, ae.getEntryType().value()) 
							.set(ACCOUNT_ENTRY.MODIFICATION_USER,ctx.getUser())
							.set(ACCOUNT_ENTRY.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
							.execute();
					ctx.log().debug("UPDATE ACCOUNT_ENTRY  ({0}) asiento: [ManualType] {1}",i,ae.getId());
					return new AccountEntryWrapper( getAccountEntry(ctx, ae.getId()) );
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitOpeningType(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getId() != null) {
					ctx.checkWrite();
					ae.setEntryType(AccountEntryType.OPENING);
					int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
							.set(ACCOUNT_ENTRY.ENTRY_TYPE, ae.getEntryType().value()) 
							.set(ACCOUNT_ENTRY.MODIFICATION_USER,ctx.getUser())
							.set(ACCOUNT_ENTRY.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
							.execute();
					ctx.log().debug("UPDATE ACCOUNT_ENTRY  ({0}) asiento: [OpeningType] {1}",i,ae.getId());
					return new AccountEntryWrapper( getAccountEntry(ctx, ae.getId()) );
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitSecurityLevel(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getId() != null) {
					ctx.checkWrite();
					ae.setConfidential(!ae.isConfidential());
					int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
							.set(ACCOUNT_ENTRY.SECURITY_LEVEL, AonEnumUtils.getByte(ae.getSecurityLevel()))
 							.set(ACCOUNT_ENTRY.MODIFICATION_USER,ctx.getUser())
							.set(ACCOUNT_ENTRY.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
							.execute();
					ctx.log().debug("UPDATE ACCOUNT_ENTRY  ({0}) asiento: [Security Level] {1}",i,ae.getId());
					if (wrapper instanceof AccountingInvoice ai) {
						Invoice inv = ai.getInvoice();
						InvoiceValidation.validateUpdateSpecialInvoice(ctx, ConfigurationDAO.getConfiguration(ctx), inv);
						ctx.getDslContext()
							.update(INVOICE)
								.set(INVOICE.SECURITY_LEVEL, AonEnumUtils.getByte(ae.getSecurityLevel()))
								.set(INVOICE.MODIFICATION_USER,ctx.getUser())
								.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
								.where(INVOICE.ID.equal( inv.getId()))
								.execute();
						return AccountingInvoiceDAO.getAccountingInvoice(ctx, ae.getId());
					} else {
						return new AccountEntryWrapper( getAccountEntry(ctx, ae.getId()) );
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitActivity(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getId() != null) {
					ctx.checkWrite();
					int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
							.set(ACCOUNT_ENTRY.ACTIVITY, ae.getActivity())
 							.set(ACCOUNT_ENTRY.MODIFICATION_USER,ctx.getUser())
							.set(ACCOUNT_ENTRY.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
							.execute();
					ctx.log().debug("UPDATE ACCOUNT_ENTRY  ({0}) asiento: [Activity] {1}",i,ae.getId());
					if (wrapper instanceof AccountingInvoice ai) {
						Invoice inv = ai.getInvoice();
						InvoiceValidation.validateUpdateSpecialInvoice(ctx, ConfigurationDAO.getConfiguration(ctx), inv);
						ctx.getDslContext()
							.update(INVOICE)
								.set(INVOICE.ACTIVITY, ae.getActivity() )
								.set(INVOICE.MODIFICATION_USER,ctx.getUser())
								.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
								.where(INVOICE.ID.equal( inv.getId()))
								.execute();
						return AccountingInvoiceDAO.getAccountingInvoice(ctx, ae.getId());
					} else {
						return new AccountEntryWrapper( getAccountEntry(ctx, ae.getId()) );
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitInvestment(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice ai) {
					ctx.checkWrite();
					Invoice inv = ai.getInvoice();
					InvoiceValidation.validateUpdateSpecialInvoice(ctx, ConfigurationDAO.getConfiguration(ctx), inv);
					inv.setInvestment(!inv.isInvestment());
					ctx.getDslContext()
						.update(INVOICE)
							.set(INVOICE.INVESTMENT, AonEnumUtils.getByte( inv.isInvestment() ) )
							.set(INVOICE.MODIFICATION_USER,ctx.getUser())
							.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(INVOICE.ID.equal( inv.getId()))
							.execute();
					return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId());
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitTaxDate(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice ai) {
					ctx.checkWrite();
					Invoice inv = ai.getInvoice();
					InvoiceValidation.validateUpdateSpecialInvoice(ctx, ConfigurationDAO.getConfiguration(ctx), inv);
					ctx.getDslContext()
						.update(INVOICE)
							.set(INVOICE.TAX_DATE, AonDateUtils.toSql(inv.getTaxDate()) )
							.set(INVOICE.MODIFICATION_USER,ctx.getUser())
							.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(INVOICE.ID.equal( inv.getId()))
							.execute();
					return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId());
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitService(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice ai) {
					ctx.checkWrite();
					Invoice inv = ai.getInvoice();
					inv.setService(!inv.isService());
					InvoiceValidation.validateUpdateSpecialInvoice(ctx, ConfigurationDAO.getConfiguration(ctx), inv);
					ctx.getDslContext()
						.update(INVOICE)
							.set(INVOICE.SERVICE, AonEnumUtils.getByte( inv.isService() ) )
							.set(INVOICE.MODIFICATION_USER,ctx.getUser())
							.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(INVOICE.ID.equal( inv.getId()))
							.execute();
					return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId());
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitVatAccrualPayment(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice ai) {
					ctx.checkWrite();
					Invoice inv = ai.getInvoice();
					inv.setVatAccrualPayment(!inv.isVatAccrualPayment());
					InvoiceValidation.validateUpdateSpecialInvoice(ctx, ConfigurationDAO.getConfiguration(ctx), inv);
					ctx.getDslContext()
						.update(INVOICE)
							.set(INVOICE.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( inv.isVatAccrualPayment() ) )
							.set(INVOICE.MODIFICATION_USER,ctx.getUser())
							.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.where(INVOICE.ID.equal( inv.getId()))
							.execute();
					return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId());
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitWithholdingType(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice ai) {
					ctx.checkWrite();
					Invoice inv = InvoiceDAO.getFullInvoice(ctx, ai.getInvoice().getId());
					inv.detailStream()
						.flatMap( d -> d.taxStream())
						.filter( t -> t.getTaxType() == TaxType.RETENTION)
						.forEach( t -> {
							int i = ctx.getDslContext()
								.update(INVOICE_TAX)
								.set(INVOICE_TAX.WITHHOLDING_TYPE, ai.getWithholdingData().getWithholdingType().value())
								.where(INVOICE_TAX.ID.equal( t.getId()))
								.execute();
							ctx.log().debug("UPDATE INVOICE_TAX invoice: {0} count({1})",t.getId(),i);
						});
					
					return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId());
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitOperatingAccount(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice ai) {
					Integer oldAccountId = null;
					if (AonCollectionUtils.isNotEmpty(ai.getVats())) {
						oldAccountId = ai.getVats().get(0).getExpAccount()
							.map(com.esferalia.aon.occam.api.model.Account::getId)
							.orElse(null);
					}
					Integer newAccountId = null;
					String description = null;
					if (ai.getSuggestedAccounts() != null && ai.getSuggestedAccounts().size() > 1) {
						newAccountId = ai.getSuggestedAccounts().get(1).getId();
						description = ai.getSuggestedAccounts().get(1).getDescription();
					}
					final String newAccountDescription = description;
					if (oldAccountId != null && newAccountId != null) {
						AccountEntry ae = wrapper.getAccountEntry();
						for (AccountEntryDetail detail : ae.getDetails()) {
							if (AonNumberUtils.equals( detail.getAccountId(), oldAccountId)) {
								int i = ctx.getDslContext()
									.update(ACCOUNT_ENTRY_DETAIL)
									.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT, newAccountId)
									.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
									.execute();
								ctx.log().debug("UPDATE ACCOUNT ENTRY DETAIL ACCOUNT id: {0} count({1})",detail.getId(),i);
							}
							if (AonNumberUtils.equals( detail.getBalancingAccountId(), oldAccountId)) {
								int i = ctx.getDslContext()
									.update(ACCOUNT_ENTRY_DETAIL)
									.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT, newAccountId)
									.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
									.execute();
								ctx.log().debug("UPDATE ACCOUNT ENTRY DETAIL BALANCING ACCOUNT id: {0} count({1})",detail.getId(),i);
							}
						}
						for (InvoiceVAT vat : ai.getVats() ) {
							
							// Si el origen de la factura es contam también se cambia la description de la linea de factura,.
							ctx.getDslContext()
								.select(INVOICE_DETAIL.SOURCE)
								.from(INVOICE_DETAIL)
								.where(INVOICE_DETAIL.ID.eq(vat.getInvoiceDetailId()))
								.fetch()
								.stream()
								.map(rec -> rec.getValue(INVOICE_DETAIL.SOURCE))
								.map(sourceIndex ->  sourceIndex==null?null:InvoiceSource.values()[sourceIndex])
								.filter( s -> (s == InvoiceSource.ACCOUNT) )
								.forEach( source -> {
									int i = ctx.getDslContext()
											.update(INVOICE_DETAIL)
											.set(INVOICE_DETAIL.DESCRIPTION, newAccountDescription)
											.where(INVOICE_DETAIL.ID.equal( vat.getInvoiceDetailId()))
											.execute();
										ctx.log().debug("UPDATE INVOICE DETAIL DESCRIPTION id: {0} count({1})",vat.getInvoiceDetailId(),i);
								});
							
							int i = ctx.getDslContext()
								.delete(INVOICE_DETAIL_ACCOUNT)
								.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal( vat.getInvoiceDetailId()))
								.and(INVOICE_DETAIL_ACCOUNT.DOMAIN.equal( ae.getDomain()))
								.execute();
							ctx.log().debug("DELETE INVOICE_DETAIL_ACCOUNT count({0})",i);
							ctx.getDslContext().insertInto(INVOICE_DETAIL_ACCOUNT)
								.set(INVOICE_DETAIL_ACCOUNT.DOMAIN, ae.getDomain())
								.set(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL, vat.getInvoiceDetailId())
								.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT, newAccountId)
								.execute();
							ctx.log().debug("INSERT INVOICE_DETAIL_ACCOUNT");
						}
					}
				}
				return wrapper;
			}
		};
		return operation.visit(visitor, wrapper);
	}
	
	public static LinkedList<AccountEntryUpdate> getAvailableAccountEntryUpdates(final AONContext ctx, IAccountEntryWrapper wrp) {
		AccountEntryUpdateVisitor<Boolean> visitor = new AccountEntryUpdateVisitor<>() {
			
			@Override
			public Boolean visitManualType(IAccountEntryWrapper wrapper) {
				return wrapper.getAccountEntry().getEntryType() == AccountEntryType.AMORTIZATION
					&& !(ctx.getDslContext()
						.select(AMORTIZATION_DETAIL.ID)
							.from(AMORTIZATION_DETAIL)
							.where(AMORTIZATION_DETAIL.DOMAIN.eq(wrapper.getAccountEntry().getDomain()))
							.and(AMORTIZATION_DETAIL.ACCOUNT_ENTRY.eq(wrapper.getAccountEntry().getId()))
							.limit(1)
							.fetch()
							.stream()
							.findAny()
							.isPresent()
						);
			}
			
			@Override
			public Boolean visitOpeningType(IAccountEntryWrapper wrapper) {
				return wrapper.getAccountEntry().getEntryType() == AccountEntryType.MANUAL
					&& !(ctx.getDslContext()
						.select(ACCOUNT_ENTRY.ID)
							.from(ACCOUNT_ENTRY)
							.where(ACCOUNT_ENTRY.DOMAIN.eq(wrapper.getAccountEntry().getDomain()))
							.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(wrapper.getAccountEntry().getPeriod()))
							.and(ACCOUNT_ENTRY.ENTRY_TYPE.eq(AccountEntryType.OPENING.value()))
							.limit(1)
							.fetch()
							.stream()
							.findAny()
							.isPresent()
						);
			}

			@Override
			public Boolean visitSecurityLevel(IAccountEntryWrapper wrapper) {
				User user = SecurityDAO.getUser(ctx);
				return (user != null && user.hasConfidentialityRole());
			}
			
			@Override
			public Boolean visitInvestment(IAccountEntryWrapper wrapper) {
				return wrapper instanceof AccountingInvoice ai 
					&& !isAlcatrazGuest(ai.getInvoice());
			}

			@Override
			public Boolean visitTaxDate(IAccountEntryWrapper wrapper) {
				return wrapper instanceof AccountingInvoice ai 
					&& !isAlcatrazGuest(ai.getInvoice());
			}
			
			@Override
			public Boolean visitActivity(IAccountEntryWrapper wrapper) {
				return wrapper.getAccountEntry().getActivity() != null
					&& (
						!(wrapper instanceof AccountingInvoice)		// El apunte no es factura
					  || (wrapper instanceof AccountingInvoice ai && !isAlcatrazGuest(ai.getInvoice()))
					  )
					&& CompanyDAO.getEnterpriseActivities(ctx,ctx.getDomainId(),wrapper.getAccountEntry().getEntryDate()).count() > 1;
			}

			@Override
			public Boolean visitService(IAccountEntryWrapper wrapper) {
				return wrapper instanceof AccountingInvoice ai 
					&& !isAlcatrazGuest(ai.getInvoice())
					&& (ai.isSales() || ai.isPurchase());
			}

			@Override
			public Boolean visitVatAccrualPayment(IAccountEntryWrapper wrapper) {
				return (wrapper instanceof AccountingInvoice ai
					&& !isAlcatrazGuest(ai.getInvoice()) 
					&& hasNotPendingFinances(ai.getInvoice()));
			}

			@Override
			public Boolean visitWithholdingType(IAccountEntryWrapper wrapper) {
				return (wrapper instanceof AccountingInvoice ai) 
					&& !isAlcatrazGuest(ai.getInvoice()) 
					&& ai.getInvoice().isWithholding();
			}

			@Override
			public Boolean visitOperatingAccount(IAccountEntryWrapper wrapper) {
				return wrapper instanceof AccountingInvoice ai 
					&& AonCollectionUtils.isNotEmpty(ai.getVats())
					&& AonCollectionUtils.stream(ai.getVats())
				        .map(vat -> vat.getExpAccount().map(com.esferalia.aon.occam.api.model.Account::getId).orElse(null))
				        .distinct()
				        .count() == 1
				; 
			}
			
			private boolean isAlcatrazGuest( Invoice invoice) {
				return ctx.getDslContext().select( ALCATRAZ.ID )
					.from(ALCATRAZ)
					.where(ALCATRAZ.INVOICE.eq(invoice.getId()))
					.limit(1)
					.fetch()
					.stream()
					.findFirst()
					.isPresent()
				;				
			}
			
			private boolean hasNotPendingFinances( Invoice invoice) {
				return invoice.financeStream()
					.anyMatch(f -> !f.isFullPending());
			}
			
		};
		
		if (wrp.getAccountEntry() != null 
			&& wrp.getAccountEntry().getPeriod() != null 
			&& wrp.getAccountEntry().isPeriodActive() ) {
			
			return AonCollectionUtils.stream( AccountEntryUpdate.values() )
				.filter( op -> op.visit(visitor, wrp) )
				.collect(Collectors.toCollection(LinkedList::new));
				
		}
		return new LinkedList<>();
	}

}
