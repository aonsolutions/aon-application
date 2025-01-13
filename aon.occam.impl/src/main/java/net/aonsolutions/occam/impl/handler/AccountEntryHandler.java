package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryBankStatement.ACCOUNT_ENTRY_BANK_STATEMENT;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.esferalia.aon.jooq.tables.records.AccountEntryDetailRecord;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountEntryDetail;
import net.aonsolutions.occam.api.model.AccountEntryParams;
import net.aonsolutions.occam.api.model.AccountPeriod;
import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.Filter;
import net.aonsolutions.occam.api.model.Filter.AccountEntryDetailFilter;
import net.aonsolutions.occam.api.model.Filter.AccountEntryFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.FlatAccountEntryDetail;
import net.aonsolutions.occam.api.model.Properties.AccountEntryDetailProperties;
import net.aonsolutions.occam.api.model.Properties.AccountEntryProperties;
import net.aonsolutions.occam.api.model.type.AccountEntryOrder;
import net.aonsolutions.occam.api.model.type.AccountEntryOrder.AccountEntryOrderVisitor;
import net.aonsolutions.occam.api.model.type.AccountEntryType;
import net.aonsolutions.occam.api.model.type.AccountEntryType.AccountEntryTypeVisitor;
import net.aonsolutions.occam.api.model.type.AccountPeriodStatus;
import net.aonsolutions.occam.api.model.type.SecurityLevel;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.IHandlerCallback;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;
import net.aonsolutions.occam.impl.handler.AccountPeriodHandler.AccountPeriodFiller;
import net.aonsolutions.occam.impl.handler.ActivityHandler.ActivityFiller;

class AccountEntryHandler {
	private AccountEntryHandler() {
	}
	
	private static final com.esferalia.aon.jooq.tables.Account DET_ACCOUNT = ACCOUNT.as("detAcc");
	private static final com.esferalia.aon.jooq.tables.Account BAL_ACCOUNT = ACCOUNT.as("balAcc");
	
	// ---------------------------------------------------------- FILTROS
	private static final AccountEntryPropertiesHandler ACCOUNT_ENTRY_PROPERTIES = new AccountEntryPropertiesHandler();
	private static class AccountEntryPropertiesHandler implements AccountEntryProperties {

		private Condition getCondition(AccountEntryFilter filter) {
			FilterImpl filterHandler = (FilterImpl) filter.filter(this);
			if (filterHandler == null) return DSL.trueCondition();
			return filterHandler.getCondition();
		}

		@Override public Property<Integer> getIdProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.ID);}
		@Override public Property<Integer> getJournalProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.JOURNAL);}
		@Override public Property<Integer> getActivityProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.ACTIVITY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.DOMAIN);}
		@Override public Property<Integer> getAccountPeriodProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.ACCOUNT_PERIOD);}
		@Override public Property<Date> getEntryDateProperty() {return new FilterImpl.DatePropertyDAO(ACCOUNT_ENTRY.ENTRY_DATE);}
		@Override public Property<Byte> getEntryTypeProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.ENTRY_TYPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.SECURITY_LEVEL);}
		@Override public Property<String> getCommentsProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.COMMENTS);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterImpl.TimestampPropertyDAO(ACCOUNT_ENTRY.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterImpl.TimestampPropertyDAO(ACCOUNT_ENTRY.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY.MODIFICATION_USER);}
	}

	private static final AccountEntryDetailPropertiesHandler ACCOUNT_ENTRY_DETAIL_PROPERTIES = new AccountEntryDetailPropertiesHandler();
	private static class AccountEntryDetailPropertiesHandler extends AccountEntryPropertiesHandler implements AccountEntryDetailProperties {

		private Condition getCondition(AccountEntryDetailFilter filter) {
			FilterImpl filterHandler = (FilterImpl) filter.filter(this);
			if (filterHandler == null) return DSL.trueCondition();
			return filterHandler.getCondition();
		}

		@Override public Property<Integer> getAccountProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.ACCOUNT);}
		@Override public Property<String> getAccountCodeProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.CODE);}
		@Override public Property<String> getAccountDescriptionProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.DESCRIPTION);}
		@Override public Property<String> getConceptProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.CONCEPT);}
		@Override public Property<Double> getDebitProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.DEBIT);}
		@Override public Property<Double> getCreditProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.CREDIT);}
		@Override public Property<String> getDocumentNumberProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER);}
		@Override public Property<Integer> getBalancingAccountProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT);}
		@Override public Property<String> getBalancingAccountCodeProperty() {return new FilterImpl.PropertyDAO<>(BAL_ACCOUNT.CODE);}
		@Override public Property<String> getBalancingAccountDescriptionProperty() {return new FilterImpl.PropertyDAO<>(BAL_ACCOUNT.DESCRIPTION);}
	}
	
	private static class OrderFields implements AccountEntryOrderVisitor<SortField<?>[]> {
		private boolean orderByDetailAlso;
		private OrderFields( boolean orderByDetailAlso ) {
			this.orderByDetailAlso = orderByDetailAlso;
		}
		
		private SortField<?>[] orderByDetailifNeeded(SortField<?>[] sortFields) {
			if (orderByDetailAlso) {
				SortField<?>[] details = Arrays.copyOf(sortFields, (sortFields.length + 1));
				details[ sortFields.length ] = ACCOUNT_ENTRY_DETAIL.LINE.asc();
				return details;
			}
			return sortFields;
		}
		
		@Override
		public SortField<?>[] visitPeriodJournal() {
			return orderByDetailifNeeded(
				new SortField<?>[]{
				 ACCOUNT_ENTRY.ACCOUNT_PERIOD.asc()
				,ACCOUNT_ENTRY.JOURNAL.asc()
				,ACCOUNT_ENTRY.ENTRY_DATE.asc()}
			);
		}

		@Override
		public SortField<?>[] visitCreationDateDesc() {
			return orderByDetailifNeeded(
				new SortField<?>[]{
				 ACCOUNT_ENTRY.CREATION_DATE.desc()
				,ACCOUNT_ENTRY.ID.desc(),}
			);
		}

		@Override
		public SortField<?>[] visitModificationDateDesc() {
			return orderByDetailifNeeded(
				new SortField<?>[]{
				 ACCOUNT_ENTRY.MODIFICATION_DATE.desc()
				,ACCOUNT_ENTRY.CREATION_DATE.desc()}
			);
		}
	}
	
	// --------------------------------------------------------------- FILLER
	static class AccountEntryFiller extends Filler<AccountEntry> {
		@Override
		public AccountEntry apply(Record r) {
			return build(r);
		}
		
		static AccountEntry build(Record r) {
			return new AccountEntry()
				.setId( getValue(r, ACCOUNT_ENTRY.ID) )
				.setDomain( r.getValue(ACCOUNT_ENTRY.DOMAIN))
				.setPeriod( AccountPeriodFiller.build(r))
				.setEntryDate( getValue(r, ACCOUNT_ENTRY.ENTRY_DATE))
				.setEntryType( AccountEntryType.value(getValue(r, ACCOUNT_ENTRY.ENTRY_TYPE)).orElse(null))
				.setActivity( ActivityFiller.build(r))
				.setJournal( getValue(r, ACCOUNT_ENTRY.JOURNAL))
				.setConfidential(SecurityLevel.confidential(getValue(r, ACCOUNT_ENTRY.SECURITY_LEVEL)))
				.setComments( getValue(r, ACCOUNT_ENTRY.COMMENTS))
				.setCreationUser(getValue(r, ACCOUNT_ENTRY.CREATION_USER))
				.setCreationDate(getValue(r, ACCOUNT_ENTRY.CREATION_DATE))
				.setModificationUser(getValue(r, ACCOUNT_ENTRY.MODIFICATION_USER))
				.setModificationDate(getValue(r, ACCOUNT_ENTRY.MODIFICATION_DATE))
				.markAsClean()
			;
		}
	}
	
	static class AccountEntryDetailFiller extends Filler<AccountEntryDetail> {
		@Override
		public AccountEntryDetail apply(Record r) {
			return build(r);
		}
		
		static AccountEntryDetail build(Record r) {
			return new AccountEntryDetail()
				.setId( getValue(r,ACCOUNT_ENTRY_DETAIL.ID) )
				.setDomain( getValue(r,ACCOUNT_ENTRY_DETAIL.DOMAIN))
				.setAccountEntry( getValue(r,ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.setAccount( AccountFiller.build(r, DET_ACCOUNT))
				.setLine( getValue(r,ACCOUNT_ENTRY_DETAIL.LINE).intValue() )
				.setConcept(getValue(r,ACCOUNT_ENTRY_DETAIL.CONCEPT))
				.setDebit(getValue(r,ACCOUNT_ENTRY_DETAIL.DEBIT))
				.setCredit(getValue(r,ACCOUNT_ENTRY_DETAIL.CREDIT))
				.setBalancingAccount( AccountFiller.build(r, BAL_ACCOUNT))
				.setDocumentNumber(getValue(r,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
				.markAsClean()
				;
		}
	}
	
	static class FlatAccountEntryDetailFiller extends Filler<FlatAccountEntryDetail> {
		@Override
		public FlatAccountEntryDetail apply(Record r) {
			return build(r);
		}
		
		static FlatAccountEntryDetail build(Record r) {
			return new FlatAccountEntryDetail( )
				.setEntryId(getValue(r, ACCOUNT_ENTRY.ID))
				.setEntryDomain(getValue(r, ACCOUNT_ENTRY.DOMAIN))
				.setEntryPeriod(getValue(r, ACCOUNT_ENTRY.ACCOUNT_PERIOD))
				.setEntryPeriodName(getValue(r, ACCOUNT_PERIOD.NAME))
				.setEntryDate(getValue(r, ACCOUNT_ENTRY.ENTRY_DATE))
				.setEntryType(AccountEntryType.value( getValue(r, ACCOUNT_ENTRY.ENTRY_TYPE)).orElse(null))
				.setActivity(getValue(r, ACCOUNT_ENTRY.ACTIVITY))
				.setActivityDescription(getValue(r, ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setJournal(getValue(r, ACCOUNT_ENTRY.JOURNAL))
				.setComments(getValue(r, ACCOUNT_ENTRY.COMMENTS))
				.setEntrySecurityLevel(SecurityLevel.value(getValue(r, ACCOUNT_ENTRY.SECURITY_LEVEL)).orElse(null))
				.setEntryCreationUser(getValue(r, ACCOUNT_ENTRY.CREATION_USER))
				.setEntryCreationDate(getValue(r, ACCOUNT_ENTRY.CREATION_DATE))
				.setEntryModificationUser(getValue(r, ACCOUNT_ENTRY.MODIFICATION_USER))
				.setEntryModificationDate(getValue(r, ACCOUNT_ENTRY.MODIFICATION_DATE))
				.setDetailId(getValue(r, ACCOUNT_ENTRY_DETAIL.ID) )
				.setAccount(getValue(r, ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.setAccountCode(getValue(r, DET_ACCOUNT.CODE))
				.setAccountDescription(getValue(r, DET_ACCOUNT.DESCRIPTION))
				.setConcept(getValue(r, ACCOUNT_ENTRY_DETAIL.CONCEPT))
				.setDebit(getValue(r, ACCOUNT_ENTRY_DETAIL.DEBIT))
				.setCredit(getValue(r, ACCOUNT_ENTRY_DETAIL.CREDIT))
				.setBalancingAccount(getValue(r, ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.setBalancingAccountCode(getValue(r, BAL_ACCOUNT.CODE))
				.setBalancingAccountDescription(getValue(r, BAL_ACCOUNT.DESCRIPTION))
				.setDocumentNumber(getValue(r, ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER))
			;
		}
	}
	
	// --------------------------------------------------------------- LECTURA
	static Stream<AccountEntry> stream(AONContext ctx
			, int domain
			, AccountEntryFilter filter
			, int offset
			, int numberOfRows
			, AccountEntryOrder orderBy) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select()
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ACCOUNT_ENTRY.ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
				.where( ACCOUNT_ENTRY.DOMAIN.eq(domain))
				.and(ACCOUNT_ENTRY_PROPERTIES.getCondition(filter))
				.orderBy(orderBy.visit(new OrderFields(false)))
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new AccountEntryFiller() )
				.map( ae -> {
					ctx.getDslContext().select()
						.from(ACCOUNT_ENTRY_DETAIL)
						.join(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
						.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
						.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ae.getId()))
						.orderBy(ACCOUNT_ENTRY_DETAIL.LINE)
						.fetch()
						.stream()
						.map( new AccountEntryDetailFiller() )
						.forEach( ae::addDetail )
					;
					return ae.markAsClean();
				})
			;
	}
	
	static Stream<AccountEntry> stream(AONContext ctx
			, int domain
			, AccountEntryFilter filter
			, int offset
			, int numberOfRows) {
		return stream(ctx, domain, filter, offset, numberOfRows,AccountEntryOrder.PERIOD_JOURNAL);
	}
	
	static Optional<AccountEntry> get(AONContext ctx, int domain, Integer id) {
		return stream(ctx, domain ,p -> p.getIdProperty().eq(id), 0, 1)
			.findFirst();
	}

	static Stream<FlatAccountEntryDetail> flatStream(AONContext ctx, int domain, AccountEntryParams params, int offset, int limit, IHandlerCallback callback) {
		return params.hasDetailProperties()
			?flatStreamByLines (ctx, domain, params ,offset, limit)
			:flatStreamByHeader(ctx, domain, params ,offset, limit)
			.onClose(() -> {if (callback != null) callback.onFinish(); })
		;
	}

	private static Stream<FlatAccountEntryDetail> flatStreamByLines(AONContext ctx, int domain, AccountEntryParams params , int offset, int limit) {
		return ctx.getDslContext()
			.selectDistinct(ACCOUNT_ENTRY.ID)
				.from(ACCOUNT_ENTRY)
				.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.where(ACCOUNT_ENTRY_DETAIL_PROPERTIES.getCondition(p -> getFilterByLines(ctx, domain, p, params)))
				.orderBy(params.getOrder().orElse(AccountEntryOrder.DEFAULT).visit(new OrderFields(true)))
				.limit(offset,limit)
				.fetch()
				.stream()
				.map (rec -> new FlatAccountEntryDetail().setEntryId(rec.getValue(ACCOUNT_ENTRY.ID)))
				.flatMap(flat -> flatStream(ctx
					, ACCOUNT_ENTRY_PROPERTIES.getCondition(p -> p.getDomainProperty().eq(domain).and(p.getIdProperty().eq(flat.getEntryId() ) ))
					, params.getOrder().orElse(AccountEntryOrder.DEFAULT)
					, 0, Integer.MAX_VALUE)
				)
		;
	}

	private static Stream<FlatAccountEntryDetail> flatStreamByHeader(AONContext ctx, int domain, AccountEntryParams params, int offset, int limit) {
		return flatStream(ctx
			, ACCOUNT_ENTRY_PROPERTIES.getCondition(p -> getFilterByHeader(ctx, domain,p, params))
			, params.getOrder().orElse(AccountEntryOrder.DEFAULT)
			, offset, limit)
		;
	}
	
	private static Stream<FlatAccountEntryDetail> flatStream(AONContext ctx
		, Condition condition
		, AccountEntryOrder orderBy
		, int offset, int limit) {
	ctx.checkRead();
	return  ctx.getDslContext()
			.select(ACCOUNT_ENTRY.fields())
			.select(ACCOUNT_PERIOD.fields())
			.select(ENTERPRISE_ACTIVITY.DESCRIPTION)
			.select(ACCOUNT_ENTRY_DETAIL.fields())
			.select(DET_ACCOUNT.CODE,DET_ACCOUNT.DESCRIPTION)
			.select(BAL_ACCOUNT.CODE,BAL_ACCOUNT.DESCRIPTION)
			.from(ACCOUNT_ENTRY)
			.innerJoin(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
			.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
			.innerJoin(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(ACCOUNT_ENTRY.ACTIVITY))
			.where(condition)
			.orderBy(orderBy.visit( new OrderFields(true) ))
			.limit(offset,limit)
			.fetch()
			.stream()
			.map( r -> FlatAccountEntryDetailFiller.build(r))
		;
}

	static AccountEntry reset(AONContext ctx, int domain, Optional<AccountEntry> last) {
		return last
			.map( l -> 
				new AccountEntry()
					.setDomain(domain)
					.setEntryType(AccountEntryType.MANUAL)
					.setPeriod( l.getPeriod().orElse(null) )
					.setEntryDate( l.getEntryDate() )
					.setActivity( l.getActivity().orElse(null))
					.setConfidential(l.isConfidential())
			)
			.orElse(
				new AccountEntry()
					.setDomain(domain)
					.setEntryType(AccountEntryType.MANUAL)
					.setPeriod( ctx.getDefaultAccountPeriod(domain).orElse(null))
					.setEntryDate( new Date() )
					.setActivity( ctx.getDefaultActivity(domain).orElse(null))
					.setConfidential(false)
			)
		;
	}
	// ------------------------------------------------------------- ESCRITURA
	static Integer save(AONContext ctx, int domain, AccountEntry ae) {
		if (ae.getId() == null) {
			return insert(ctx, domain, ae);
		} else {
			update(ctx, domain, ae);
			return ae.getId();
		}
	}
	
	private static Integer insert(AONContext ctx, int domain, AccountEntry ae) {
		ctx.checkWrite();
		AccountEntryValidation.validateEntry(ctx, domain, ae);
		increaseJournal(ctx, ae);
		Integer id = ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY)
				.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getPeriod().map(AccountPeriod::getId).orElse(null))
				.set(ACCOUNT_ENTRY.ENTRY_DATE,AonDateUtils.toSql(ae.getEntryDate()))
				.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
				.set(ACCOUNT_ENTRY.JOURNAL, ae.getJournal())
				.set(ACCOUNT_ENTRY.ACTIVITY, ae.getActivity().map(Activity::getId).orElse(null))
				.set(ACCOUNT_ENTRY.SECURITY_LEVEL,  (byte) (ae.isConfidential()?1:0) )
				.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
				.set(ACCOUNT_ENTRY.CREATION_USER,ctx.getUser())
				.set(ACCOUNT_ENTRY.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(ACCOUNT_ENTRY.ID)
			.fetchOne()
			.getValue(ACCOUNT_ENTRY.ID);
		ae.setId(id);
		ctx.log().debug("INSERT ACCOUNT_ENTRY asiento: {0}",ae.getId());
		batchInsert(ctx, domain, ae);
		return id; 
	}
	
	private static synchronized void increaseJournal(AONContext ctx,AccountEntry accountEntry) {
		// Se averigua el último numero de diario y se incrementa en uno.
		AggregateFunction<Integer> maxFunc = DSL.max(ACCOUNT_ENTRY.JOURNAL);
		Integer lastJournal = ctx.getDslContext()
			.select(maxFunc)
			.from(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.DOMAIN.eq(accountEntry.getDomain()))
			.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(accountEntry.getPeriod().map(AccountPeriod::getId)
				.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage()))))
			.fetch()
			.stream()
			.map(r -> r.getValue(maxFunc))
			.filter( m -> m != null ) 
			.findFirst()
			.orElse(0);
		accountEntry.setJournal(lastJournal + 1);
	}

	private static void batchInsert(AONContext ctx, int domain, AccountEntry ae) {
		InsertSetStep<AccountEntryDetailRecord> insert = ctx.getDslContext().insertInto(ACCOUNT_ENTRY_DETAIL);
		InsertSetMoreStep<AccountEntryDetailRecord>  insertMore = null;
		int line = 0;
		LinkedList<AccountEntryDetail> details = ae.detailStream().collect(Collectors.toCollection(LinkedList::new)); 
		for (AccountEntryDetail detail : details ) {
			AccountEntryValidation.validateDetail(ctx, domain, detail);
			if (insertMore != null) {
				insert = insertMore.newRecord();
			}
			insertMore =  insert
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount().map(Account::getId).orElse(null))
				.set(ACCOUNT_ENTRY_DETAIL.LINE, UInteger.valueOf(++line) )
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount().map(Account::getId).orElse(null))
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
	
	private static void update(AONContext ctx, int domain, AccountEntry ae) {
		ctx.checkWrite();
		AccountEntryValidation.validateEntry(ctx, domain, ae);
		int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
			.set(ACCOUNT_ENTRY.DOMAIN,ae.getDomain())
			.set(ACCOUNT_ENTRY.ACCOUNT_PERIOD,ae.getPeriod().map(AccountPeriod::getId).orElse(null))
			.set(ACCOUNT_ENTRY.ENTRY_DATE,AonDateUtils.toSql(ae.getEntryDate()))
			.set(ACCOUNT_ENTRY.ENTRY_TYPE, AonEnumUtils.getByte(ae.getEntryType())) 
			.set(ACCOUNT_ENTRY.JOURNAL,ae.getJournal())
			.set(ACCOUNT_ENTRY.ACTIVITY, ae.getActivity().map(Activity::getId).orElse(null))
			.set(ACCOUNT_ENTRY.SECURITY_LEVEL, SecurityLevel.value( ae.isConfidential() ))
			.set(ACCOUNT_ENTRY.COMMENTS,ae.getComments())
			.set(ACCOUNT_ENTRY.MODIFICATION_USER,ctx.getUser())
			.set(ACCOUNT_ENTRY.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(ACCOUNT_ENTRY.ID.equal( ae.getId()))
			.execute();
		ctx.log().debug("UPDATE ACCOUNT_ENTRY  ({0}) asiento: {1}",i, ae.getId());
		updateDetails(ctx, domain, ae);
	}
	
	private static void updateDetails(AONContext ctx, int domain, AccountEntry ae) {
		MutableInt line = new MutableInt(0);
		ae.detailStream().forEach(detail -> {
			if (detail.isDeleted()) {
				deleteDetail(ctx, line.getValue(), detail);
			} else {
				line.increment();
				AccountEntryValidation.validateDetail(ctx, domain, detail);
				if (detail.getId() != null) {
					detail.setLine(line.getValue());	
					if (detail.isDirty()) {
						updateDetail(ctx, line.getValue(), ae, detail);
					}
				} else {
					insertDetail(ctx, line.getValue(), ae, detail);
				}
			}
		});
	}
	
	private static void deleteDetail(AONContext ctx, int line, AccountEntryDetail detail) {
		Integer id = detail.getId() * -1;
		int i = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_DETAIL)
				.where(ACCOUNT_ENTRY_DETAIL.ID.equal(id))
			.execute();
		ctx.log().debug("DELETE ACCOUNT_ENTRY_DETAIL ({0} rows ) ({1}) {2}",i,line,id);
	}

	private static void insertDetail(AONContext ctx, int line, AccountEntry ae, AccountEntryDetail detail) {
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount().map(Account::getId).orElse(null))
				.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( line ))
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount().map(Account::getId).orElse(null))
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
				.set(ACCOUNT_ENTRY_DETAIL.CREATION_USER,ctx.getUser())
				.set(ACCOUNT_ENTRY_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.execute();
		ctx.log().debug("INSERT ACCOUNT_ENTRY_DETAIL ({0})",line);
	}

	private static void updateDetail(AONContext ctx, int line, AccountEntry ae, AccountEntryDetail detail) {
		int i = ctx.getDslContext()
			.update(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.DOMAIN,ae.getDomain())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY,ae.getId())
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,detail.getAccount().map(Account::getId).orElse(null))
				.set(ACCOUNT_ENTRY_DETAIL.LINE,UInteger.valueOf( line ))
				.set(ACCOUNT_ENTRY_DETAIL.CONCEPT,detail.getConcept()) 
				.set(ACCOUNT_ENTRY_DETAIL.DEBIT,detail.getDebit())
				.set(ACCOUNT_ENTRY_DETAIL.CREDIT,detail.getCredit())
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,detail.getBalancingAccount().map(Account::getId).orElse(null))
				.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,detail.getDocumentNumber())
				.set(ACCOUNT_ENTRY_DETAIL.MODIFICATION_USER,ctx.getUser())
				.set(ACCOUNT_ENTRY_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
			.execute();
		ctx.log().debug("UPDATE ACCOUNT_ENTRY_DETAIL  ({0} rows) ({1}) {2}",i,line,detail.getId());
	}

	static AccountEntry delete(AONContext ctx, int domain, Integer id) {
		ctx.checkWrite();
		AccountEntry entry = get(ctx, domain, id)
			.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage()));
		AccountEntryValidation.validateDeletion(ctx, domain, entry);
		beforeRemove(ctx, domain, entry);
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
		afterRemove(ctx, domain, entry);
		return entry;
	}
	
	private static void beforeRemove(final AONContext ctx, int domain,final AccountEntry entry) {
		entry.getEntryType().visit(new AccountEntryTypeVisitor<Void>() {

			@Override
			public Void visitTax() {
				FiscalModelHandler.unrecord(ctx, entry.getId());
				return null;
			}
			@Override
			public Void visitFinance() {
				FinanceEntryHandler.deleteAccountEntryFinanceTrackings(ctx, domain, entry.getId());
				return null;
			}

			@Override public Void visitReturnedPayment() 	{ return visitFinance(); }
			@Override public Void visitReturnedCollection() { return visitFinance(); }
			@Override public Void visitCollection() 		{ return visitFinance(); }
			@Override public Void visitPayment() 			{ return visitFinance(); }

			@Override
			public Void visitAmortization() {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage());
			}
			@Override public Void visitExpenseInvoice()		{return removeInvoice();}
			@Override public Void visitSalesInvoice() 		{return removeInvoice();}
			@Override public Void visitPurchaseInvoice()	{return removeInvoice();}

			private Void removeInvoice() {
				Integer invoiceId = ctx.getDslContext()
						.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
						.from( ACCOUNT_ENTRY_INVOICE )
						.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(entry.getId()))
						.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(domain))
						.fetch()
						.stream()
						.mapToInt(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
						.findFirst()
						.orElse( Integer.MIN_VALUE );
				if (invoiceId != null && invoiceId != Integer.MIN_VALUE) {
					int count = ctx.getDslContext()
						.delete(ACCOUNT_ENTRY_INVOICE)
						.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(entry.getId()))
						.execute();
					ctx.log().debug("DELETE ACCOUNT_ENTRY_INVOICE ({0} filas.)",count);
					InvoiceHandler.delete(ctx, domain, invoiceId);
				}
				return null;
			}
			
			@Override public Void visitOpening() 				{return null;}
			@Override public Void visitClosing() 				{return null;}
			@Override public Void visitOperating() 				{return null;}
			@Override public Void visitManual() 				{return null;}
			@Override public Void visitSalary() 				{return null;}
			@Override public Void visitLoan() 					{return null;}
			@Override public Void visitSocialInsurance() 		{return null;}
			@Override public Void visitLoanFee() 				{return null;}
			@Override public Void visitSocialInsuranceAdjust() 	{return null;}
		});	
	}
	
	private static void afterRemove(final AONContext ctx, int domain,final AccountEntry entry) {
		Integer periodId = entry.getPeriod()
			.map( p -> p.getId())
			.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage()));
		
		entry.getEntryType().visit(new AccountEntryTypeVisitor<Void>() {
			
			@Override
			public Void visitOpening() {
				if (AccountEntryHandler.existsAnyEntry(ctx, periodId,AccountEntryType.OPENING)) {
					// Si después de borrar apertura, existe otro apertura, se mantiene 
					// el estado (o se modifica si era errroneo).
					AccountPeriodHandler.open(ctx,domain,periodId);
				} else {
					// Si después de borrar apertura, no existe otro apertura, se activa.
					AccountPeriodHandler.active(ctx,domain,periodId);
				}
				return null;
			}
			
			@Override
			public Void visitClosing() {
				if (AccountEntryHandler.existsAnyEntry(ctx, periodId,AccountEntryType.CLOSING)) {
					// Si después de borrar cierre, existe otro cierre, se mantiene el estado.
					AccountPeriodHandler.close(ctx,domain,periodId);
				} else if (AccountEntryHandler.existsAnyEntry(ctx, periodId,AccountEntryType.OPERATING)) {
					// Si después de borrar cierre, existe explotación.
					AccountPeriodHandler.operating(ctx,domain,periodId);
				} else if (AccountEntryHandler.existsAnyEntry(ctx, periodId,AccountEntryType.OPENING)) {
					// Si después de borrar cierre, no existe explotación y sí apertura.
					AccountPeriodHandler.open(ctx,domain,periodId);
				} else {
					// Si después de borrar cierre, no existe explotación ni apertura. Se activa.
					AccountPeriodHandler.active(ctx,domain,periodId);
				}
				return null;
			}
			
			@Override
			public Void visitOperating() {
				if (AccountEntryHandler.existsAnyEntry(ctx, periodId,AccountEntryType.OPERATING)) {
					// Si después de borrar explotación, existe otro explotación, se mantiene el estado.
					AccountPeriodHandler.operating(ctx, domain,periodId);
				} else if (AccountEntryHandler.existsAnyEntry(ctx, periodId,AccountEntryType.OPENING)) {
					// Si después de borrar explotación, existe apertura.
					AccountPeriodHandler.open(ctx, domain, periodId);
				} else {
					// Si después de borrar cierre, no existe apertura.
					AccountPeriodHandler.active(ctx, domain, periodId);
				}
				return null;
			}
			
			@Override public Void visitManual() 				{return null;}
			@Override public Void visitSalary() 				{return null;}
			@Override public Void visitLoan() 					{return null;}
			@Override public Void visitSocialInsurance() 		{return null;}
			@Override public Void visitLoanFee() 				{return null;}
			@Override public Void visitSocialInsuranceAdjust() 	{return null;}
			@Override public Void visitSalesInvoice() 			{return null;}
			@Override public Void visitPurchaseInvoice() 		{return null;}
			@Override public Void visitExpenseInvoice() 		{return null;}
			@Override public Void visitTax() 					{return null;}
			@Override public Void visitPayment() 				{return null;}
			@Override public Void visitCollection() 			{return null;}
			@Override public Void visitAmortization() 			{return null;}
			@Override public Void visitReturnedPayment() 		{return null;}
			@Override public Void visitReturnedCollection() 	{return null;}
			@Override public Void visitFinance() 				{return null;}
		});
	}
	private static boolean existsAnyEntry(AONContext ctx, Integer period, AccountEntryType accountEntryType) {
		return ctx.getDslContext().select()
			.from(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(period))
			.and(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AonEnumUtils.getByte( accountEntryType)))
			.limit(1)
			.fetch()
			.stream()
			.findFirst()
			.isPresent();
	}
	
	
/*	
	
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


	public static LinkedHashMap<String, AccountBalance> fetchBalance(
			AONContext ctx, AccMiningParameters params) {
		return fetchBalance(ctx, params, false);
	}

	public static LinkedHashMap<String, AccountBalance> fetchBalance(
			AONContext ctx, AccMiningParameters params,boolean pyg) {
		java.sql.Date start = AonDateUtils.toSql(params.getStartDate()!= null? params.getStartDate() : AonDateUtils.getYearFirstDay(0));
		java.sql.Date end = AonDateUtils.toSql(params.getEndDate()!= null? params.getEndDate() : AonDateUtils.getYearLastDay(9999));
		
		Field<String> accountField = DSL.substring(ACCOUNT.CODE, 1, params.getAccountLevel()); 
		Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
		Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT); 
		LinkedHashMap<String, AccountBalance> map = new LinkedHashMap<String, AccountBalance>();
		Condition pygCondition = pyg
				?ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.OPERATING.getValue())
						.and(ACCOUNT.CODE.like("6%").or(ACCOUNT.CODE.like("7%")) )
				:DSL.trueCondition()
		;
		Condition domainCondition = ACCOUNT_ENTRY.DOMAIN.equal(params.getDomain());
		if ( params.getDomains() != null && params.getDomains().length > 0) {
			LinkedList<Integer> domains = new LinkedList<Integer>(); 
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
			.and(ACCOUNT_ENTRY.ENTRY_TYPE.ne(AccountEntryType.CLOSING.getValue()) )
			.and( params.getSecurityLevel()!=null
				? ACCOUNT_ENTRY.SECURITY_LEVEL.eq(params.getSecurityLevel().value())
				: DSL.trueCondition() )
			.and(pygCondition)
			.groupBy(ACCOUNT_ENTRY.ENTRY_TYPE, accountField)
			.fetch()
			.stream()
			.forEach( record -> {
				byte type = record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE);
				String account = record.getValue(accountField);
				double debit = record.getValue(sumDebit).doubleValue();
				double credit = record.getValue(sumCredit).doubleValue();
				if(account.length() > 0) putAccountBalance(map,type,account.substring(0,1), debit,credit);
				if(account.length() > 1) putAccountBalance(map,type,account.substring(0,2), debit,credit);
				if(account.length() > 2) putAccountBalance(map,type,account.substring(0,3), debit,credit);
				putAccountBalance(map,type,account, debit,credit);
			});
		return map;
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
		public AccountingBreakdown apply(Record record) {
			return new AccountingBreakdown()
			.setEntryId(record.getValue(ACCOUNT_ENTRY.ID))
			.setJournal(record.getValue(ACCOUNT_ENTRY.JOURNAL))
			.setIssueDate(record.getValue(ACCOUNT_ENTRY.ENTRY_DATE))
			.setActivity(record.getValue(ENTERPRISE_ACTIVITY.ID))
			.setActivityDescription(record.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
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
	
	public static IAccountEntryWrapper  updateSpecial(AONContext ctx, AccountEntryUpdate operation, IAccountEntryWrapper wrapper) {
		IAccountEntryUpdateVisitor visitor = new IAccountEntryUpdateVisitor() {

			@Override
			public IAccountEntryWrapper visitManualType(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getId() != null) {
					ctx.checkWrite();
					ae.setEntryType(AccountEntryType.MANUAL);
					int i = ctx.getDslContext().update(ACCOUNT_ENTRY)
							.set(ACCOUNT_ENTRY.ENTRY_TYPE, ae.getEntryType().getValue()) 
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
							.set(ACCOUNT_ENTRY.ENTRY_TYPE, ae.getEntryType().getValue()) 
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
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper;
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
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper;
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
				if (wrapper instanceof AccountingInvoice) {
					ctx.checkWrite();
					AccountingInvoice ai = (AccountingInvoice) wrapper;
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
				if (wrapper instanceof AccountingInvoice) {
					ctx.checkWrite();
					AccountingInvoice ai = (AccountingInvoice) wrapper;
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
				if (wrapper instanceof AccountingInvoice) {
					ctx.checkWrite();
					AccountingInvoice ai = (AccountingInvoice) wrapper;
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
				if (wrapper instanceof AccountingInvoice) {
					ctx.checkWrite();
					AccountingInvoice ai = (AccountingInvoice) wrapper;
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
				if (wrapper instanceof AccountingInvoice) {
					ctx.checkWrite();
					AccountingInvoice ai = (AccountingInvoice) wrapper;
					Invoice inv = InvoiceDAO.getFullInvoice(ctx, ai.getInvoice().getId());
					for ( InvoiceDetail detail : inv.getDetails() ) {
						for (InvoiceTax tax : detail.getInvoiceTaxes() ) {
							if (tax.getTaxType() == TaxType.RETENTION) {
								int i = ctx.getDslContext()
									.update(INVOICE_TAX)
									.set(INVOICE_TAX.WITHHOLDING_TYPE,tax.getWithholdingType() == null 
										? WithholdingType.PROFESSIONAL.value() 
										: ai.getWithholdingData().getWithholdingType().value())
									.where(INVOICE_TAX.ID.equal( tax.getId()))
									.execute();
								ctx.log().debug("UPDATE INVOICE_TAX invoice: {0} count({1})",tax.getId(),i);
							}
						}
					}
					
					return AccountingInvoiceDAO.getAccountingInvoiceFromInvoice(ctx, inv.getId());
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitOperatingAccount(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper;
					Integer oldAccountId = null;
					if (ai.getVats() != null && ai.getVats().size() > 0) {
						oldAccountId = ai.getVats().get(0).getExpAccountId();
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
							if (AonNumberUtils.equals( detail.getAccount(), oldAccountId)) {
								int i = ctx.getDslContext()
									.update(ACCOUNT_ENTRY_DETAIL)
									.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT, newAccountId)
									.where(ACCOUNT_ENTRY_DETAIL.ID.equal( detail.getId()))
									.execute();
								ctx.log().debug("UPDATE ACCOUNT ENTRY DETAIL ACCOUNT id: {0} count({1})",detail.getId(),i);
							}
							if (AonNumberUtils.equals( detail.getBalancingAccount(), oldAccountId)) {
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
		final  LinkedList<AccountEntryUpdate> list = new LinkedList<AccountEntryUpdate>();
		IAccountEntryUpdateVisitor visitor = new IAccountEntryUpdateVisitor() {
			
			@Override
			public IAccountEntryWrapper visitManualType(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getPeriod() != null && ae.isPeriodActive() && ae.getEntryType() == AccountEntryType.AMORTIZATION) {
					boolean hasAmortizationDetail = ctx.getDslContext().fetchExists(
						ctx.getDslContext().select()
							.from(AMORTIZATION_DETAIL)
							.where(AMORTIZATION_DETAIL.DOMAIN.eq(ae.getDomain()))
							.and(AMORTIZATION_DETAIL.ACCOUNT_ENTRY.eq(ae.getId())));
					if (!hasAmortizationDetail) {
						list.add(AccountEntryUpdate.MANUAL_TYPE);			
					}
				}
				return wrapper;
			}
			
			@Override
			public IAccountEntryWrapper visitOpeningType(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getPeriod() != null && ae.isPeriodActive() && ae.getEntryType() == AccountEntryType.MANUAL) {
					boolean hasOpeningEntry = ctx.getDslContext().fetchExists(
						ctx.getDslContext().select()
							.from(ACCOUNT_ENTRY)
							.where(ACCOUNT_ENTRY.DOMAIN.eq(ae.getDomain()))
							.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ae.getPeriod()))
							.and(ACCOUNT_ENTRY.ENTRY_TYPE.eq(AccountEntryType.OPENING.getValue())));
					if (!hasOpeningEntry) {
						list.add(AccountEntryUpdate.OPENING_TYPE);			
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitSecurityLevel(IAccountEntryWrapper wrapper) {
				AccountEntry ae = wrapper.getAccountEntry();
				if (ae != null && ae.getPeriod() != null && ae.isPeriodActive()) {
					User user = SecurityDAO.getUser(ctx);
					if (user != null && user.hasConfidentialityRole()) {
						list.add(AccountEntryUpdate.SECURITY_LEVEL);			
					}
				}
				return wrapper;
			}
			
			@Override
			public IAccountEntryWrapper visitInvestment(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper; 
					AccountEntry ae = wrapper.getAccountEntry();
					if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
						list.add(AccountEntryUpdate.INVESTMENT);
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitTaxDate(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper; 
					AccountEntry ae = wrapper.getAccountEntry();
					if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
						list.add(AccountEntryUpdate.TAX_DATE);
					}
				}
				return wrapper;
			}
			
			@Override
			public IAccountEntryWrapper visitActivity(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper; 
					AccountEntry ae = wrapper.getAccountEntry();
					Collection<EnterpriseActivity> activities = CompanyDAO.getEnterpriseActivities(ctx,ctx.getDomainId(),ae.getEntryDate())
							.collect(Collectors.toCollection(LinkedList::new));
					if (activities != null && activities.size() > 1) {
						if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
							list.add(AccountEntryUpdate.ACTIVITY);
						}
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitService(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper;
					AccountEntry ae = wrapper.getAccountEntry();
					if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
						if (ai.isSales() || ai.isPurchase()) {
							list.add(AccountEntryUpdate.SERVICE);
						}
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitVatAccrualPayment(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper; 
					AccountEntry ae = wrapper.getAccountEntry();
					if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
						list.add(AccountEntryUpdate.VAT_ACCRUAL_PAYMENT);
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitWithholdingType(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper; 
					AccountEntry ae = wrapper.getAccountEntry();
					if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
						if (ai.getInvoice().isWithholding()) {
							list.add(AccountEntryUpdate.WITHHOLDING_TYPE);
						}
					}
				}
				return wrapper;
			}

			@Override
			public IAccountEntryWrapper visitOperatingAccount(IAccountEntryWrapper wrapper) {
				if (wrapper instanceof AccountingInvoice) {
					AccountingInvoice ai = (AccountingInvoice) wrapper; 
					AccountEntry ae = wrapper.getAccountEntry();
					if (ae != null && ae.getPeriod() != null && (!ae.isPeriodActive() || !hasPendingFinances(ai.getInvoice()))) {
						boolean add = false;
						Integer account = null;
						for (InvoiceVAT vat : ai.getVats() ) {
							if ( account == null) {
								account = vat.getExpAccountId();
								add = true;
							}
							if ( !AonNumberUtils.equals(account,vat.getExpAccountId())) {
								add = false;
								break;
							}
						}
						if ( add ) {
							list.add(AccountEntryUpdate.OPERATING_ACCOUNT);
						}
					}
				}
				return wrapper;
			}
			
			private boolean hasPendingFinances( Invoice invoice) {
				if (invoice.getFinances() != null && !invoice.getFinances().isEmpty()) {
					boolean pendingFinances = false;
					for (Finance finance : invoice.getFinances()) {
						pendingFinances = pendingFinances || finance.isFullPending();
					}
					return pendingFinances;
				}
				return true;
			}
			
		};
		for (AccountEntryUpdate operation : AccountEntryUpdate.values()) {
			operation.visit(visitor, wrp);
		}
		return list;
	}
*/	

	private class AccountEntryValidation {

		/**
		 * El dominio del apunte no puede estar vacio.
		 */
		private static final Consumer<Context> EMPTY_DOMAIN = c -> {
			if (c.entry.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		/**
		 * La fecha del asiento es un dato obligatorio.
		 */
		private static final Consumer<Context> EMPTY_DATE = c -> {
			if (c.entry.getEntryDate() == null)
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_DATE.getMessage());
		};
		
		/**
		 * El periodod del asiento es un dato obligatorio.
		 */
		private static final Consumer<Context> EMPTY_PERIOD = c -> 
			c.entry.getPeriod().ifPresentOrElse( 
				p -> AccountPeriodHandler.get(c.ctx,c.domain, p.getId())
					.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage()))
				, () -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage()));
		
		/**
		 * El Tipo de asiento no puede ser null.
		 */
		private static final Consumer<Context> EMPTY_ENTRY_TYPE = c -> {
			if (c.entry.getEntryType() == null) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_TYPE.getMessage());
			}
		};
		
		/**
		 * El periodo es una dato obligatorio, debe existir en el mismo dominio,
		 * La fecha de asiento debe estar comprendida entre las fechas de inicio 
		 * y fin del asiento y el estado del periodo debe permitir la entrada del 
		 * tipo de asiento. Tal que:
		 * 		
		 */
		private static final Consumer<Context> ENTRY_PERIOD_CHECK = c -> {
			// El periodo debe existir y tener el mismo dominio que el asiento.
			AccountPeriod period = c.entry.getPeriod()
				.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_WRONG_DOMAIN.format(c.domain)));
			// Se chequea que la fecha del apunte esté comprendida
			// entre la fecha inicial y la fecha final del ejercicio.
			Date periodFrom = period.getInitiationDate();
			Date periodTo = period.getDeadline();
			if (!AonDateUtils.isSameDay(c.entry.getEntryDate(), periodFrom)
					&& !AonDateUtils.isSameDay(c.entry.getEntryDate(), periodTo)
					&& (c.entry.getEntryDate().before(periodFrom) || c.entry.getEntryDate().after(periodTo))) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_DATE_IN_PERIOD.format(period.getName()));
			}
			// Se chequea que el ejericio no esté inactivo.
			if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE.format(period.getName()));
			}

			// Si el ejercicio está en explotación, solo se permite la introducción
			// de apuntes de explotación o cierre.
			if (period.getStatus() == AccountPeriodStatus.OPERATING
					&& c.entry.getEntryType() != AccountEntryType.OPERATING
					&& c.entry.getEntryType() != AccountEntryType.CLOSING) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING.format(period.getName()));
			}
			// Si el ejercicio está cerrado, solo se permite la introducción de
			// apuntes de cierre.
			if (period.getStatus() == AccountPeriodStatus.CLOSED
				&& c.entry.getEntryType() != AccountEntryType.CLOSING) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING.format(period.getName()));
			}
		};

		/**
		 * El apunte debe estar cuadrado.
		 */
		private static final Consumer<Context> ENTRY_SETTLED = c -> {
			MutableDouble sumD = new MutableDouble(0.0);
			MutableDouble sumC = new MutableDouble(0.0);
			MutableBoolean empty = new MutableBoolean(true);
			c.entry.detailStream()
				.filter( aed -> !aed.isDeleted())
				.forEach( aed -> {
					sumD.setValue( AonMathUtils.sum(sumD.getValue(), aed.getDebit()));	
					sumC.setValue( AonMathUtils.sum(sumC.getValue(), aed.getCredit()));
					empty.setValue( false );
				}
			);
			if (empty.getValue().booleanValue()) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_DETAILS.getMessage());
			}
			
			if (!AonMathUtils.isZero( AonMathUtils.round(sumD.getValue() - sumC.getValue()))) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_SETTLED.getMessage());
			}
		};

		/**
		 * El concepto del apunte es un dato obligatorio.
		 */
		private static final Consumer<DetailContext> EMPTY_CONCEPT = c -> {
			if (AonStringUtils.isEmpty(c.detail.getConcept() ))
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_CONCEPT.getMessage());
		};
		
		/**
		 * El concepto no debe superar la longitud de la columan.
		 */
		private static final Consumer<DetailContext> OVERFLOW_CONCEPT = c -> {
			if (AonStringUtils.length(c.detail.getConcept()) > ACCOUNT_ENTRY_DETAIL.CONCEPT.getDataType().length() )
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_OVERFLOW_CONCEPT.getMessage());
		};

		private static void validateAccount(DetailContext c, Optional<Account> acc) {
			Integer accountId = acc.map(Account::getId)
				.orElseThrow( () -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_ACCOUNT.format(c.detail.getLine()
						,c.detail.getConcept(),c.detail.getDebit(),c.detail.getCredit())));
			Account account = AccountHandler.get(c.ctx, c.domain, accountId)
				.orElseThrow( () -> new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_NOT_FOUND
								.format(Objects.toString(accountId),"","")));
			if (!account.isActive())
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_INACTIVE
						.format(Objects.toString(account.getId()),account.getCode(),account.getDescription()));
			if (account.getCode().length() != 9)
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_INVALID_LENGTH
						.format(Objects.toString(account.getId()),account.getCode(),account.getDescription()));
		}
		
		/**
		 * La cuenta contable debe ser una cuenta válida.
		 * La cuenta contable debe pertenecer al dominio del apunte.
		 * La cuenta contable debe estar activa.
		 * La cuenta contable debe ser de último nivel.
		 * 
		 */
		private static final Consumer<DetailContext> VALID_ACCOUNT = c -> 
			validateAccount(c, c.detail.getAccount());

		/**
		 * La contrapartida debe ser una cuenta válida.
		 * La contrapartida debe pertenecer al dominio del apunte.
		 * La contrapartida debe estar activa.
		 * La contrapartida debe ser de último nivel.
		 * 
		 */
		private static final Consumer<DetailContext> VALID_BALANCING_ACCOUNT = c -> {
			if (c.detail.getBalancingAccount().isPresent()) {
				validateAccount(c, c.detail.getBalancingAccount());
			}
		};

		
		private record Context(AONContext ctx, int domain, AccountEntry entry) {}
		public static void validateEntry(AONContext ctx, int domain, AccountEntry ae)
				throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_DATE)
				.andThen(EMPTY_PERIOD)
				.andThen(EMPTY_ENTRY_TYPE)
				.andThen(ENTRY_PERIOD_CHECK)
				.andThen(ENTRY_SETTLED)
				.accept(new Context(ctx, domain, ae));

		}

		private record DetailContext(AONContext ctx, int domain, AccountEntryDetail detail) {}
		public static void validateDetail(AONContext ctx, int domain, AccountEntryDetail detail) {
			VALID_ACCOUNT
				.andThen(EMPTY_CONCEPT)
				.andThen(OVERFLOW_CONCEPT)
				.andThen(VALID_BALANCING_ACCOUNT)
				.accept(new DetailContext(ctx, domain, detail));
		}


		/**
		 * La cuenta contable del apunte es un dato obligatorio.
		 */
		private static final Consumer<DeleteContext> PERIOD_DELETION_ENABLED = dc -> {
			AccountPeriod period =  dc.entry.getPeriod()
				.orElseThrow( () -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage()));
			int domain = dc.entry.getDomain();
			if (domain == 0) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_WRONG_DOMAIN.format(period.getName()));
			}
			AccountPeriod savedPeriod = AccountPeriodHandler.get(dc.ctx, domain, period.getId())
				.orElseThrow( () -> new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage()));
			
			if ( !savedPeriod.isActive()) {
				savedPeriod.getStatus().visit( new AccountPeriodStatus.AccountPeriodStatusVisitor<Void>() {

					@Override public Void visitActive()  {return null;}
					@Override public Void visitOpening() {return null;}
					@Override
					public Void visitInactive() {
						throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE.format(savedPeriod.getName()));
					}
					@Override
					public Void visitOperating() {
						if (dc.entry.getEntryType() != AccountEntryType.OPERATING) {
							throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING.format(savedPeriod.getName()));		
						}
						return null;
					}
					@Override
					public Void visitClosed() {
						if (dc.entry.getEntryType() != AccountEntryType.CLOSING) {
							throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING.format(savedPeriod.getName()));		
						}
						return null;
					}
				});
			}
		};

		/**
		 * La cuenta contable del apunte es un dato obligatorio.
		 */
		private static final Consumer<DeleteContext> CHECK_BANK_STATEMENT_BIND = dc -> {
			Integer bankStatement = dc.ctx.getDslContext().select(ACCOUNT_ENTRY_BANK_STATEMENT.BANK_STATEMENT)
					.from(ACCOUNT_ENTRY_BANK_STATEMENT)
					.where(ACCOUNT_ENTRY_BANK_STATEMENT.ACCOUNT_ENTRY.eq(dc.entry.getId()))
					.fetch()
					.stream()
					.map( rec -> rec.getValue(ACCOUNT_ENTRY_BANK_STATEMENT.BANK_STATEMENT))
					.findFirst()
					.orElse(null);
			if (bankStatement != null) {
				Integer lotNumber = dc.ctx.getDslContext()
					.select( BANK_STATEMENT.LOT_NUMBER )
					.from(BANK_STATEMENT)
					.where(BANK_STATEMENT.ID.eq(bankStatement))
					.fetch()
					.stream()
					.map( rec -> rec.getValue(BANK_STATEMENT.LOT_NUMBER))
					.findFirst()
					.orElse(null);
				String msg = AonError.ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE.getMessage() + " " + AonError.ACCOUNT_ENTRY_BANK_STATEMENT_BOUND.getMessage();
				if (lotNumber != null) {
					msg =  msg + " [Lote: " + lotNumber + "]";
				}
				throw new AonCoreException(msg);
			}
		};
		

		private record DeleteContext ( AONContext ctx, int domain, AccountEntry entry) {}
		
		public static void validateDeletion(AONContext ctx, int domain, AccountEntry entry) {
			PERIOD_DELETION_ENABLED
				.andThen( CHECK_BANK_STATEMENT_BIND) 
				.accept( new DeleteContext(ctx,domain,entry) );
		}

	}
	
	
	private static Filter getFilterByHeader(AONContext ctx, int domain, AccountEntryProperties p, AccountEntryParams params) {
		MutableObject<Filter> m = new MutableObject<>(p.getDomainProperty().eq(params.getDomain())); 
		m.setValue( params.getPeriod()
			.filter(i -> !AonMathUtils.isNullOrZero(i))
			.map( i -> m.getValue().and(p.getAccountPeriodProperty().eq(i)))
			.orElse(m.getValue()));
		m.setValue( params.getAccountEntryId()
			.filter(i -> !AonMathUtils.isNullOrZero(i))
			.map( i -> m.getValue().and(p.getIdProperty().eq(i)))
			.orElse(m.getValue()));
		m.setValue( params.getJournal()
			.filter(i -> !AonMathUtils.isNullOrZero(i))
			.map( i -> m.getValue().and(p.getJournalProperty().eq(i)))
			.orElse(m.getValue()));
		m.setValue( params.getActivity()
			.filter(i -> !AonMathUtils.isNullOrZero(i))
			.map( i -> i == -1
				?m.getValue().and(p.getActivityProperty().isNull())
				:m.getValue().and(p.getActivityProperty().eq(i)))
			.orElse(m.getValue()));
		m.setValue( params.getFromDate()
			.map( d -> m.getValue().and(p.getEntryDateProperty().ge(d)))
			.orElse(m.getValue()));
		m.setValue( params.getToDate()
			.map( d -> m.getValue().and(p.getEntryDateProperty().le(d)))
			.orElse(m.getValue()));
		m.setValue( params.getType()
			.map( t -> m.getValue().and(p.getEntryTypeProperty().eq(t.value())))
			.orElse(m.getValue()));
		
		if (!ctx.hasConfidentialityRole( domain)) {
			m.setValue( m.getValue().and(p.getConfidentialProperty().eq(SecurityLevel.OFFICIAL.value())));
		} else {
			m.setValue( params.getSecurityLevel()
				.map( t -> m.getValue().and(p.getConfidentialProperty().eq(t.value())))
				.orElse(m.getValue()));
		}
		m.setValue( params.getComments()
			.filter(AonStringUtils::isNotBlank)
			.map( s -> m.getValue().and(p.getCommentsProperty().like(AonStringUtils.SQLlike(s))))
			.orElse(m.getValue()));
		m.setValue( params.getFromCreationDate()
			.map( d -> m.getValue().and(p.getCreationDateProperty().ge(new java.sql.Timestamp(d.getTime()))))
			.orElse(m.getValue()));
		m.setValue( params.getToCreationDate()
			.map( d -> m.getValue().and(p.getCreationDateProperty().le(new java.sql.Timestamp(d.getTime()))))
			.orElse(m.getValue()));
		m.setValue( params.getCreationUser()
			.filter(AonStringUtils::isNotBlank)
			.map( s -> m.getValue().and(p.getCreationUserProperty().like(AonStringUtils.SQLlike(s))))
			.orElse(m.getValue()));
		m.setValue( params.getFromModificationDate()
			.map( d -> m.getValue().and(p.getModificationDateProperty().ge(new java.sql.Timestamp(d.getTime()))))
			.orElse(m.getValue()));
		m.setValue( params.getToModificationDate()
			.map( d -> m.getValue().and(p.getModificationDateProperty().le(new java.sql.Timestamp(d.getTime()))))
			.orElse(m.getValue()));
		m.setValue( params.getModificationUser()
			.filter(AonStringUtils::isNotBlank)
			.map( s -> m.getValue().and(p.getModificationUserProperty().like(AonStringUtils.SQLlike(s))))
			.orElse(m.getValue()));
		return m.getValue();
	}

	private static Filter getFilterByLines(AONContext ctx, int domain, AccountEntryDetailProperties p, AccountEntryParams params) {
		MutableObject<Filter> m = new MutableObject<>(getFilterByHeader(ctx, domain, p, params)); 
		m.setValue( params.getAccount()
			.filter(i -> !AonMathUtils.isNullOrZero(i))
			.map( i -> m.getValue().and(p.getAccountProperty().eq(i).or(p.getBalancingAccountProperty().eq(i))))
			.orElse(m.getValue()));
		m.setValue( params.getConcept()
			.filter(AonStringUtils::isNotBlank)
			.map( s -> m.getValue().and(p.getConceptProperty().like(AonStringUtils.SQLlike(s))))
			.orElse(m.getValue()));
		m.setValue( params.getDebit()
			.filter(i -> i != null )
			.filter(i -> !AonMathUtils.isNotZero(i))
			.map( i -> m.getValue().and(p.getDebitProperty().eq(i)))
			.orElse(m.getValue()));
		m.setValue( params.getCredit()
			.filter(i -> i != null )
			.filter(i -> !AonMathUtils.isNotZero(i))
			.map( i -> m.getValue().and(p.getCreditProperty().eq(i)))
			.orElse(m.getValue()));
		m.setValue( params.getDocument()
			.filter(AonStringUtils::isNotBlank)
			.map( s -> m.getValue().and(p.getDocumentNumberProperty().like(AonStringUtils.SQLlike(s))))
			.orElse(m.getValue()));
		return m.getValue();
	}
	
}
