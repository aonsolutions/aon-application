package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.occam.api.model.AccountPeriod;
import net.aonsolutions.occam.api.model.Filter.AccountPeriodFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Properties.AccountPeriodProperties;
import net.aonsolutions.occam.api.model.type.AccountPeriodStatus;
import net.aonsolutions.occam.impl.AONContext;

class AccountPeriodHandler {

	private AccountPeriodHandler() {

	}

	private static final AccountPeriodPropertiesHandler ACCOUNT_PERIOD_PROPERTIES = new AccountPeriodPropertiesHandler();
	private static class AccountPeriodPropertiesHandler implements AccountPeriodProperties {

		private Condition getCondition(AccountPeriodFilter filter) {
			if (filter == null) return DSL.trueCondition();
			FilterImpl filterHandler = (FilterImpl) filter.filter(this);
			if (filterHandler == null) return DSL.trueCondition();
			return filterHandler.getCondition();
		}

		@Override public Property<Integer> getIdProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_PERIOD.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_PERIOD.DOMAIN);}
		@Override public Property<Date> getInitiationDateProperty() {return new FilterImpl.DatePropertyDAO(ACCOUNT_PERIOD.INITIATION_DATE);}
		@Override public Property<Date> getDeadlineProperty() {return new FilterImpl.DatePropertyDAO(ACCOUNT_PERIOD.DEADLINE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT_PERIOD.STATUS);}
	}

	static class AccountPeriodFiller extends Filler<AccountPeriod> {
		@Override
		public AccountPeriod apply(Record r) {
			return build(r);
		}

		static AccountPeriod build(Record r) {
			if (isNull(r, ACCOUNT_PERIOD.ID)) return null;
			return new AccountPeriod()
				.setId(getValue(r, ACCOUNT_PERIOD.ID))
				.setDomain(getValue(r, ACCOUNT_PERIOD.DOMAIN))
				.setName(getValue(r, ACCOUNT_PERIOD.NAME))
				.setInitiationDate(getValue(r, ACCOUNT_PERIOD.INITIATION_DATE))
				.setDeadline(getValue(r, ACCOUNT_PERIOD.DEADLINE))
				.setStatus(AccountPeriodStatus.value(getValue(r, ACCOUNT_PERIOD.STATUS)).orElse(null))
				.setCreationUser(getValue(r, ACCOUNT_PERIOD.CREATION_USER))
				.setCreationDate(getValue(r, ACCOUNT_PERIOD.CREATION_DATE))
				.setModificationUser(getValue(r, ACCOUNT_PERIOD.MODIFICATION_USER))
				.setModificationDate(getValue(r, ACCOUNT_PERIOD.MODIFICATION_DATE))
			;
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, int domain ) {
		return ctx.getDslContext()
			.select()
			.from(ACCOUNT_PERIOD)
			.where(ACCOUNT_PERIOD.DOMAIN.eq(domain))
		;
	}	  
	  
	static Stream<AccountPeriod> stream(AONContext ctx, int domain, AccountPeriodFilter filter) {
		ctx.checkRead();
		Optional<Integer> defaultPeriod = ctx.getApplicationParameters(domain).getAccountingDefaultPeriod();
		return select(ctx, domain)
			.and(ACCOUNT_PERIOD_PROPERTIES.getCondition(filter))
			.orderBy(ACCOUNT_PERIOD.INITIATION_DATE.desc())
			.fetch()
			.stream()
			.map(new AccountPeriodFiller())
			.map(period -> {
				defaultPeriod.ifPresent(dp -> period.setDefaultPeriod(AonUtils.equals(dp, period.getId())));
				return period;
			});
	}

	static Optional<AccountPeriod> get(AONContext ctx, int domain, Integer id) {
		return stream(ctx, domain, p -> p.getIdProperty().eq(id))
			.findFirst();
	}
	  
	static Optional<AccountPeriod> get(AONContext ctx, int domain, Date entryDate) {
		return stream(ctx, domain, p -> 
				p.getInitiationDateProperty().le(entryDate)
				.and(p.getDeadlineProperty().ge(entryDate)))
			.findFirst();
	}

	/*
	 * public static
	 * SelectConditionStep<Record10<Integer,Integer,String,java.sql.Date,java.sql.
	 * Date,Byte,String,Timestamp,String,Timestamp>> select(AONContext ctx,
	 * AccountPeriodFilter filter) { return ctx.getDslContext()
	 * .select(ACCOUNT_PERIOD.ID,ACCOUNT_PERIOD.DOMAIN,ACCOUNT_PERIOD.NAME,
	 * ACCOUNT_PERIOD.INITIATION_DATE
	 * ,ACCOUNT_PERIOD.DEADLINE,ACCOUNT_PERIOD.STATUS,ACCOUNT_PERIOD.CREATION_USER
	 * ,ACCOUNT_PERIOD.CREATION_DATE,ACCOUNT_PERIOD.MODIFICATION_USER,ACCOUNT_PERIOD
	 * .MODIFICATION_DATE) .from(ACCOUNT_PERIOD)
	 * .where(ACCOUNT_PERIOD_PROPERTIES.getConditions(filter)); }
	 * 
	 * public static AccountPeriod ensurePeriod(AONContext ctx, Integer domain, Date
	 * entryDate) { AccountPeriod period = getPeriod(ctx,entryDate); if (period ==
	 * null || period.getId() == null) { period = new AccountPeriod()
	 * .setDomain(domain)
	 * .setName(Integer.toString(AonDateUtils.getYear(entryDate)))
	 * .setInitiationDate(AonDateUtils.getYearFirstDay(entryDate))
	 * .setDeadline(AonDateUtils.getYearLastDay(entryDate)); period =
	 * AccountPeriodDAO.save(ctx, period); } return period; }
	 * 
	 * public static AccountPeriod getPeriodByYear(AONContext ctx, int year) {
	 * ctx.checkRead(); Date date = AonDateUtils.getYearLastDay(year); return
	 * getPeriod(ctx,date); }
	 * 
	 * 
	 * public static AccountPeriod getActivePeriod(AONContext ctx, Date entryDate) {
	 * ctx.checkRead(); return getPeriods(ctx, p ->
	 * p.getDomainProperty().eq(ctx.getDomainId())
	 * .and(p.getInitiationDateProperty().le(entryDate) )
	 * .and(p.getDeadlineProperty().ge(entryDate) ) .and(p.getStatusProperty().in(
	 * new Byte[] {0,2,3} ))) .findFirst() .orElse(null); }
	 * 
	 * public static LinkedList<AccountPeriod> getDomainPeriods(AONContext ctx) {
	 * ctx.checkRead(); return getPeriods(ctx,p ->
	 * p.getDomainProperty().eq(ctx.getDomainId()))
	 * .collect(Collectors.toCollection(LinkedList::new)); }
	 * 
	 * public static AccountPeriod save(AONContext ctx, AccountPeriod ap) { if
	 * (ap.getId() == null) { ap = insert(ctx, ap); } else { ap = update(ctx, ap); }
	 * return ap;
	 * 
	 * }
	 * 
	 * private static AccountPeriod insert(AONContext ctx, AccountPeriod ap) {
	 * ctx.checkWrite(); AccountPeriodAutoComplete.complete(ctx, ap);
	 * AccountPeriodValidation.validate(ctx, ap); Integer id =
	 * ctx.getDslContext().insertInto(ACCOUNT_PERIOD) .set(ACCOUNT_PERIOD.DOMAIN,
	 * ap.getDomain()) .set(ACCOUNT_PERIOD.NAME, ap.getName())
	 * .set(ACCOUNT_PERIOD.INITIATION_DATE,AonDateUtils.toSql(ap.getInitiationDate()
	 * )) .set(ACCOUNT_PERIOD.DEADLINE,AonDateUtils.toSql(ap.getDeadline()))
	 * .set(ACCOUNT_PERIOD.STATUS,AonEnumUtils.getByte(ap.getStatus()))
	 * .set(ACCOUNT_PERIOD.CREATION_USER,ctx.getUser())
	 * .set(ACCOUNT_PERIOD.CREATION_DATE, new Timestamp( System.currentTimeMillis())
	 * ) .returning(ACCOUNT_PERIOD.ID) .fetchOne() .getValue(ACCOUNT_PERIOD.ID);
	 * return getPeriod(ctx, id); }
	 * 
	 * private static AccountPeriod update(AONContext ctx, AccountPeriod ap) {
	 * ctx.checkWrite(); AccountPeriodAutoComplete.complete(ctx, ap);
	 * AccountPeriodValidation.validate(ctx, ap);
	 * ctx.getDslContext().update(ACCOUNT_PERIOD) .set(ACCOUNT_PERIOD.DOMAIN,
	 * ap.getDomain()) .set(ACCOUNT_PERIOD.NAME, ap.getName())
	 * .set(ACCOUNT_PERIOD.INITIATION_DATE,AonDateUtils.toSql(ap.getInitiationDate()
	 * )) .set(ACCOUNT_PERIOD.DEADLINE,AonDateUtils.toSql(ap.getDeadline()))
	 * .set(ACCOUNT_PERIOD.STATUS,AonEnumUtils.getByte(ap.getStatus()))
	 * .set(ACCOUNT_PERIOD.MODIFICATION_USER,ctx.getUser())
	 * .set(ACCOUNT_PERIOD.MODIFICATION_DATE, new Timestamp(
	 * System.currentTimeMillis()) ) .where(ACCOUNT_PERIOD.ID.equal(ap.getId()))
	 * .execute(); return ap; }
	 * 
	 * public static void delete(AONContext ctx, AccountPeriod ap) {
	 * ctx.checkWrite(); AccountPeriodValidation.validateDeletion(ctx, ap);
	 * ctx.getDslContext().transaction(configuration -> ctx.getDslContext()
	 * .delete(ACCOUNT_PERIOD)
	 * .where(ACCOUNT_PERIOD.ID.equal(ap.getId())).execute()); }
	 * 
	 * public static Date getMinDate(AONContext ctx) { return ctx.getDslContext()
	 * .select( DSL.min(ACCOUNT_PERIOD.INITIATION_DATE) ) .from(ACCOUNT_PERIOD)
	 * .where(ACCOUNT_PERIOD.DOMAIN.eq(ctx.getDomainId())) .fetch() .stream() .map(
	 * rec -> rec.getValue(DSL.min(ACCOUNT_PERIOD.INITIATION_DATE))) .findFirst()
	 * .orElse( AonDateUtils.toSql(AonDateUtils.getYearFirstDay(new Date()))); }
	 * public static Date getMaxDate(AONContext ctx) { return ctx.getDslContext()
	 * .select( DSL.max(ACCOUNT_PERIOD.DEADLINE) ) .from(ACCOUNT_PERIOD)
	 * .where(ACCOUNT_PERIOD.DOMAIN.eq(ctx.getDomainId())) .fetch() .stream() .map(
	 * rec -> rec.getValue(DSL.max(ACCOUNT_PERIOD.DEADLINE))) .findFirst() .orElse(
	 * AonDateUtils.toSql(AonDateUtils.getYearLastDay(new Date()))); } private
	 * static void updateStatus(AONContext ctx, Integer period, AccountPeriodStatus
	 * status) { ctx.checkWrite(); ctx.getDslContext().update(ACCOUNT_PERIOD)
	 * .set(ACCOUNT_PERIOD.STATUS, status.getValue() )
	 * .set(ACCOUNT_PERIOD.MODIFICATION_USER,ctx.getUser())
	 * .set(ACCOUNT_PERIOD.MODIFICATION_DATE, new Timestamp(
	 * System.currentTimeMillis()) ) .where(ACCOUNT_PERIOD.ID.equal(period))
	 * .execute(); } public static void open(AONContext ctx, Integer period) {
	 * updateStatus(ctx, period, AccountPeriodStatus.OPENING); } public static void
	 * close(AONContext ctx, Integer period) { updateStatus(ctx, period,
	 * AccountPeriodStatus.CLOSED); } public static void operating(AONContext ctx,
	 * Integer period) { updateStatus(ctx, period, AccountPeriodStatus.OPERATING); }
	 * public static void inactive(AONContext ctx, Integer period) {
	 * updateStatus(ctx, period, AccountPeriodStatus.INACTIVE); } public static void
	 * active(AONContext ctx, Integer period) { updateStatus(ctx, period,
	 * AccountPeriodStatus.ACTIVE); }
	 * 
	 * private static class AccountPeriodAutoComplete { private static final
	 * BiConsumer<AONContext,AccountPeriod> COMPLETE_STATUS = (ctx,accountPeriod) ->
	 * { if (accountPeriod.getStatus() == null) {
	 * ctx.log().debug("\t saving accounting period: autocomplete status: {0}"
	 * ,AccountPeriodStatus.ACTIVE);
	 * accountPeriod.setStatus(AccountPeriodStatus.ACTIVE); } };
	 * 
	 * private static void complete(AONContext ctx,AccountPeriod accountPeriod)
	 * throws AonCoreException { COMPLETE_STATUS.accept(ctx, accountPeriod); }
	 * 
	 * }
	 */
//	private static class AccountPeriodValidation {
//
//		/**
//		 * El dominio no puede estar vacio
//		 */
//		private static final BiConsumer<AccountPeriod,AONContext> EMPTY_DOMAIN = (ap,ctx) -> {
//			if (ap.getDomain() == null) 
//				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
//		};
//
//		/**
//		 * La fecha inicial no puede estar vacia
//		 */
//		private static final BiConsumer<AccountPeriod,AONContext> EMPTY_INITIATION_DATE_VALIDATION = (ap,ctx) -> {
//			if (ap.getInitiationDate() == null) 
//				throw new AonCoreException(AonError.ACCOUNT_PERIOD_EMPTY_INITIATION_DATE.getMessage());
//		};
//		
//		/**
//		 * La fecha final debe ser posterior a la fecha inicial.
//		 */
//		private static final BiConsumer<AccountPeriod,AONContext> EMPTY_DEADLINE = (ap,ctx) -> {
//			if (ap.getDeadline() == null)
//				throw new AonCoreException(AonError.ACCOUNT_PERIOD_EMPTY_DEADLINE.getMessage());
//		};
//		
//		/**
//		 * La fecha final debe ser posterior a la fecha inicial.
//		 */
//		private static final BiConsumer<AccountPeriod,AONContext> WRONG_RANGE = (ap,ctx) -> {
//			if (ap.getInitiationDate().after(ap.getDeadline()))
//				throw new AonCoreException(AonError.ACCOUNT_PERIOD_WRONG_RANGE.getMessage());
//		};
//		
//		/**
//		 * El status del periodo no puede estar vacio.
//		 */
//		private static final BiConsumer<AccountPeriod,AONContext> EMPTY_STATUS = (ap,ctx) -> {
//			if (ap.getStatus() == null)
//				throw new AonCoreException(AonError.EMPTY_STATUS.getMessage());
//		};
//
//		/**
//		 * No debe haber solapes entre las fechas de los diferentes periodos definidos.
//		 */
//		private static final BiConsumer<AccountPeriod,AONContext> OVERLAP = (ap,ctx) -> {
//			SelectConditionStep<Record3<String, java.sql.Date, java.sql.Date>> select = 
//					ctx.getDslContext()
//					.select(ACCOUNT_PERIOD.NAME, ACCOUNT_PERIOD.INITIATION_DATE,
//							ACCOUNT_PERIOD.DEADLINE).from(ACCOUNT_PERIOD)
//					.where(ACCOUNT_PERIOD.DOMAIN.equal(ap.getDomain()));
//			// Si la validación ha saltado durante un UPDATE, se excluye el periodo en curso.
//			if (ap.getId() != null) {
//				select = select.and(ACCOUNT_PERIOD.ID.notEqual(ap.getId()));
//			}
//			Result<Record3<String, java.sql.Date, java.sql.Date>> result = select
//					.fetch();
//			for (Record3<String, java.sql.Date, java.sql.Date> rec : result) {
//				Date pFrom = rec.getValue(ACCOUNT_PERIOD.INITIATION_DATE);
//				Date pTo = rec.getValue(ACCOUNT_PERIOD.DEADLINE);
//				if (ap.getInitiationDate().compareTo(pFrom) >= 0 
//					&& ap.getInitiationDate().compareTo(pTo) <= 0) {
//					throw new AonCoreException(
//							AonError.ACCOUNT_PERIOD_START_OVERLAP.format(
//							rec.getValue(ACCOUNT_PERIOD.NAME)));
//				}
//				if (ap.getDeadline().compareTo(pFrom) >= 0 
//					&& ap.getDeadline().compareTo(pTo) <= 0) {
//					throw new AonCoreException(
//							AonError.ACCOUNT_PERIOD_END_OVERLAP.format(
//							rec.getValue(ACCOUNT_PERIOD.NAME)));
//				}
//			}
//		};
//		
//		private static final BiConsumer<AccountPeriod,AONContext> HAS_ENTRIES_CHECK = (ap,ctx) -> {
//			Integer count = ctx.getDslContext().select( DSL.count(ACCOUNT_ENTRY.ID) )
//				.from(ACCOUNT_ENTRY)
//				.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ap.getId()))
//				.fetch()
//				.stream()
//				.map( rec -> rec.getValue(DSL.count(ACCOUNT_ENTRY.ID)) )
//				.findFirst()
//				.orElse(0);
//			if (count > 0) {
//				throw new AonCoreException(AonError.ACCOUNT_PERIOD_HAS_ENTRIES.format(count));
//			}
//		};
//
//		private static void validate(AONContext ctx, AccountPeriod ap)
//				throws AonCoreException {
//
//			EMPTY_DOMAIN
//				.andThen(EMPTY_STATUS)
//				.andThen(EMPTY_INITIATION_DATE_VALIDATION)
//				.andThen(EMPTY_DEADLINE)
//				.andThen(WRONG_RANGE)
//				.andThen(OVERLAP)
//				.accept(ap, ctx);
//
//		}
//
//		public static void validateDeletion(AONContext ctx, AccountPeriod ap) {
//			HAS_ENTRIES_CHECK
//				.accept(ap, ctx);
//		}
//
//	}
//	
//	// *************************************************
//	// ********** TEST PURPOSE METHODS *****************
//	// *************************************************
//	public static AccountPeriod getRandom(AONContext ctx, AccountPeriodFilter filter) {
//		return select(ctx,filter)
//			.orderBy( DSL.rand() )
//			.fetch()
//			.stream()
//			.map(new FullAccountPeriodFiller())
//			.findFirst()
//			.orElse(null);
//	}

}
