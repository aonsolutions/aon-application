package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record10;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.accounting.AccountPeriodFilter;
import com.esferalia.aon.occam.api.model.accounting.AccountPeriodProperties;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class AccountPeriodDAO {
	
	private static final AccountPeriodPropertiesDAO ACCOUNT_PERIOD_PROPERTIES = new AccountPeriodPropertiesDAO();
	private static class AccountPeriodPropertiesDAO implements AccountPeriodProperties {

		private Condition[] getConditions(AccountPeriodFilter filter) {
			if (filter==null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_PERIOD.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_PERIOD.DOMAIN);}
		@Override public Property<Date> getInitiationDateProperty() {return new FilterDAO.DatePropertyDAO(ACCOUNT_PERIOD.INITIATION_DATE);}
		@Override public Property<Date> getDeadlineProperty() {return new FilterDAO.DatePropertyDAO(ACCOUNT_PERIOD.DEADLINE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_PERIOD.STATUS);}
	}
	public static SelectConditionStep<Record10<Integer,Integer,String,java.sql.Date,java.sql.Date,Byte,String,Timestamp,String,Timestamp>> select(AONContext ctx, AccountPeriodFilter filter) {
		return ctx.getDslContext()
			.select(ACCOUNT_PERIOD.ID,ACCOUNT_PERIOD.DOMAIN,ACCOUNT_PERIOD.NAME,ACCOUNT_PERIOD.INITIATION_DATE
					,ACCOUNT_PERIOD.DEADLINE,ACCOUNT_PERIOD.STATUS,ACCOUNT_PERIOD.CREATION_USER
					,ACCOUNT_PERIOD.CREATION_DATE,ACCOUNT_PERIOD.MODIFICATION_USER,ACCOUNT_PERIOD.MODIFICATION_DATE)
			.from(ACCOUNT_PERIOD)
			.where(ACCOUNT_PERIOD_PROPERTIES.getConditions(filter));
	}

	public static Stream<AccountPeriod> getPeriods(AONContext ctx, AccountPeriodFilter filter) {
		ctx.checkRead();
		ApplicationParameter ap = AppParamDAO.fetchOne(ctx, AppParam.ACC_DEFAULT_PERIOD);
		return select( ctx, filter)
			.orderBy(ACCOUNT_PERIOD.INITIATION_DATE.desc())
			.fetch()
			.stream()
			.map(new FullAccountPeriodFiller())
			.peek( period -> {
				if (ap != null) {
					period.setDefaultPeriod( AonUtils.equals( AonNumberUtils.toInteger( ap.getValue() ),period.getId()));	
				}
			});
		
	}
	public static AccountPeriod getPeriod(AONContext ctx, Date entryDate) {
		ctx.checkRead();
		return getPeriods(ctx,
					p -> p.getDomainProperty().eq(ctx.getDomainId())
						.and(p.getInitiationDateProperty().le(entryDate) )
						.and(p.getDeadlineProperty().ge(entryDate) )
						)
				.findFirst()
				.orElse(null);
	}
	
	public static AccountPeriod getPeriodByYear(AONContext ctx, int year) {
		ctx.checkRead();
		Date date = AonDateUtils.getYearLastDay(year);
		return getPeriod(ctx,date);
	}
	
	public static AccountPeriod getPeriod(AONContext ctx, Integer id) {
		ctx.checkRead();
		return getPeriods(ctx,
					p -> p.getDomainProperty().eq(ctx.getDomainId())
						.and(p.getIdProperty().eq(id) )
						)
				.findFirst()
				.orElse(null);
	}
	
	public static AccountPeriod getActivePeriod(AONContext ctx, Date entryDate) {
		ctx.checkRead();
		return getPeriods(ctx,
					p -> p.getDomainProperty().eq(ctx.getDomainId())
						.and(p.getInitiationDateProperty().le(entryDate) )
						.and(p.getDeadlineProperty().ge(entryDate) )
						.and(p.getStatusProperty().in( new Byte[] {0,2,3} )))
				.findFirst()
				.orElse(null);
	}
	public static LinkedList<AccountPeriod> getDomainPeriods(AONContext ctx) {
		ctx.checkRead();
		return getPeriods(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId()))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static AccountPeriod save(AONContext ctx, AccountPeriod ap) {
		if (ap.getId() == null) {
			ap = insert(ctx, ap);
		} else {
			ap = update(ctx, ap);			
		}
		return ap;
		
	}
	
	private static AccountPeriod insert(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		AccountPeriodAutoComplete.complete(ctx, ap);
		AccountPeriodValidation.validate(ctx, ap);
		Integer id = ctx.getDslContext().insertInto(ACCOUNT_PERIOD)
			.set(ACCOUNT_PERIOD.DOMAIN, ap.getDomain())
			.set(ACCOUNT_PERIOD.NAME, ap.getName())
			.set(ACCOUNT_PERIOD.INITIATION_DATE,AonDateUtils.toSql(ap.getInitiationDate()))
			.set(ACCOUNT_PERIOD.DEADLINE,AonDateUtils.toSql(ap.getDeadline()))
			.set(ACCOUNT_PERIOD.STATUS,AonEnumUtils.getByte(ap.getStatus()))
			.set(ACCOUNT_PERIOD.CREATION_USER,ctx.getUser())
			.set(ACCOUNT_PERIOD.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(ACCOUNT_PERIOD.ID)
			.fetchOne()
			.getValue(ACCOUNT_PERIOD.ID);
		return getPeriod(ctx, id);
	}

	private static AccountPeriod update(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		AccountPeriodAutoComplete.complete(ctx, ap);
		AccountPeriodValidation.validate(ctx, ap);
		ctx.getDslContext().update(ACCOUNT_PERIOD)
			.set(ACCOUNT_PERIOD.DOMAIN, ap.getDomain())
			.set(ACCOUNT_PERIOD.NAME, ap.getName())
			.set(ACCOUNT_PERIOD.INITIATION_DATE,AonDateUtils.toSql(ap.getInitiationDate()))
			.set(ACCOUNT_PERIOD.DEADLINE,AonDateUtils.toSql(ap.getDeadline()))
			.set(ACCOUNT_PERIOD.STATUS,AonEnumUtils.getByte(ap.getStatus()))
			.set(ACCOUNT_PERIOD.MODIFICATION_USER,ctx.getUser())
			.set(ACCOUNT_PERIOD.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(ACCOUNT_PERIOD.ID.equal(ap.getId()))
			.execute();
		return ap; 
	}

	public static void delete(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		AccountPeriodValidation.validateDeletion(ctx, ap);
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(ACCOUNT_PERIOD)
				.where(ACCOUNT_PERIOD.ID.equal(ap.getId())).execute();
		});
	}

	private static class FullAccountPeriodFiller  implements Function<Record,AccountPeriod> {
		@Override
		public AccountPeriod apply(Record record) {
			return new AccountPeriod()
				.setId( record.getValue(ACCOUNT_PERIOD.ID) )
				.setDomain(record.getValue(ACCOUNT_PERIOD.DOMAIN) )
				.setName(record.getValue(ACCOUNT_PERIOD.NAME) )
				.setInitiationDate(record.getValue(ACCOUNT_PERIOD.INITIATION_DATE) )
				.setDeadline(record.getValue(ACCOUNT_PERIOD.DEADLINE) )
				.setStatus(AccountPeriodStatus.safeValueOf(record.getValue(ACCOUNT_PERIOD.STATUS)))
				.setCreationUser(record.getValue(ACCOUNT_PERIOD.CREATION_USER) )
				.setCreationDate(record.getValue(ACCOUNT_PERIOD.CREATION_DATE) )
				.setModificationUser(record.getValue(ACCOUNT_PERIOD.MODIFICATION_USER) )
				.setModificationDate(record.getValue(ACCOUNT_PERIOD.MODIFICATION_DATE) )
				;
		}
	}
	public static Date getMinDate(AONContext ctx) {
		return  ctx.getDslContext()
			.select( DSL.min(ACCOUNT_PERIOD.INITIATION_DATE) )
			.from(ACCOUNT_PERIOD)
			.where(ACCOUNT_PERIOD.DOMAIN.eq(ctx.getDomainId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue(DSL.min(ACCOUNT_PERIOD.INITIATION_DATE)))
			.findFirst()
			.orElse( AonDateUtils.toSql(AonDateUtils.getYearFirstDay(new Date())));
	}
	public static Date getMaxDate(AONContext ctx) {
		return  ctx.getDslContext()
			.select( DSL.max(ACCOUNT_PERIOD.DEADLINE) )
			.from(ACCOUNT_PERIOD)
			.where(ACCOUNT_PERIOD.DOMAIN.eq(ctx.getDomainId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue(DSL.max(ACCOUNT_PERIOD.DEADLINE)))
			.findFirst()
			.orElse( AonDateUtils.toSql(AonDateUtils.getYearLastDay(new Date())));
	}
	private static void updateStatus(AONContext ctx, Integer period, AccountPeriodStatus status) {
		ctx.checkWrite();
		ctx.getDslContext().update(ACCOUNT_PERIOD)
			.set(ACCOUNT_PERIOD.STATUS, status.getValue() )
			.set(ACCOUNT_PERIOD.MODIFICATION_USER,ctx.getUser())
			.set(ACCOUNT_PERIOD.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(ACCOUNT_PERIOD.ID.equal(period))
			.execute();
	}
	public static void open(AONContext ctx, Integer period) {
		updateStatus(ctx, period, AccountPeriodStatus.OPENING);
	}
	public static void close(AONContext ctx, Integer period) {
		updateStatus(ctx, period, AccountPeriodStatus.CLOSED);
	}
	public static void operating(AONContext ctx, Integer period) {
		updateStatus(ctx, period, AccountPeriodStatus.OPERATING);
	}
	public static void inactive(AONContext ctx, Integer period) {
		updateStatus(ctx, period, AccountPeriodStatus.INACTIVE);
	}
	public static void active(AONContext ctx, Integer period) {
		updateStatus(ctx, period, AccountPeriodStatus.ACTIVE);
	}
	
	private static class AccountPeriodAutoComplete {
		private static BiConsumer<AONContext,AccountPeriod> COMPLETE_STATUS  = (ctx,accountPeriod) -> {
			if (accountPeriod.getStatus() == null) {
				ctx.log().info("\t saving accounting period: autocomplete status: " + AccountPeriodStatus.ACTIVE);
				accountPeriod.setStatus(AccountPeriodStatus.ACTIVE);
			}
		};

		private static void complete(AONContext ctx,AccountPeriod accountPeriod) throws AonCoreException {
			COMPLETE_STATUS.accept(ctx, accountPeriod);	
		}

	}
	
	private static class AccountPeriodValidation {

		/**
		 * El dominio no puede estar vacio
		 */
		private static BiConsumer<AccountPeriod,AONContext> EMPTY_DOMAIN = (ap,ctx) -> {
			if (ap.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};

		/**
		 * La fecha inicial no puede estar vacia
		 */
		private static BiConsumer<AccountPeriod,AONContext> EMPTY_INITIATION_DATE_VALIDATION = (ap,ctx) -> {
			if (ap.getInitiationDate() == null) 
				throw new AonCoreException(AonError.ACCOUNT_PERIOD_EMPTY_INITIATION_DATE.getMessage());
		};
		
		/**
		 * La fecha final debe ser posterior a la fecha inicial.
		 */
		private static BiConsumer<AccountPeriod,AONContext> EMPTY_DEADLINE = (ap,ctx) -> {
			if (ap.getDeadline() == null)
				throw new AonCoreException(AonError.ACCOUNT_PERIOD_EMPTY_DEADLINE.getMessage());
		};
		
		/**
		 * La fecha final debe ser posterior a la fecha inicial.
		 */
		private static BiConsumer<AccountPeriod,AONContext> WRONG_RANGE = (ap,ctx) -> {
			if (ap.getInitiationDate().after(ap.getDeadline()))
				throw new AonCoreException(AonError.ACCOUNT_PERIOD_WRONG_RANGE.getMessage());
		};
		
		/**
		 * El status del periodo no puede estar vacio.
		 */
		private static BiConsumer<AccountPeriod,AONContext> EMPTY_STATUS = (ap,ctx) -> {
			if (ap.getStatus() == null)
				throw new AonCoreException(AonError.EMPTY_STATUS.getMessage());
		};

		/**
		 * No debe haber solapes entre las fechas de los diferentes periodos definidos.
		 */
		private static BiConsumer<AccountPeriod,AONContext> OVERLAP = (ap,ctx) -> {
			SelectConditionStep<Record3<String, java.sql.Date, java.sql.Date>> select = 
					ctx.getDslContext()
					.select(ACCOUNT_PERIOD.NAME, ACCOUNT_PERIOD.INITIATION_DATE,
							ACCOUNT_PERIOD.DEADLINE).from(ACCOUNT_PERIOD)
					.where(ACCOUNT_PERIOD.DOMAIN.equal(ap.getDomain()));
			// Si la validación ha saltado durante un UPDATE, se excluye el periodo en curso.
			if (ap.getId() != null) {
				select = select.and(ACCOUNT_PERIOD.ID.notEqual(ap.getId()));
			}
			Result<Record3<String, java.sql.Date, java.sql.Date>> result = select
					.fetch();
			for (Record3<String, java.sql.Date, java.sql.Date> record : result) {
				Date pFrom = record.getValue(ACCOUNT_PERIOD.INITIATION_DATE);
				Date pTo = record.getValue(ACCOUNT_PERIOD.DEADLINE);
				if (ap.getInitiationDate().compareTo(pFrom) >= 0 
					&& ap.getInitiationDate().compareTo(pTo) <= 0) {
					throw new AonCoreException(
							AonError.ACCOUNT_PERIOD_START_OVERLAP.format(
							record.getValue(ACCOUNT_PERIOD.NAME)));
				}
				if (ap.getDeadline().compareTo(pFrom) >= 0 
					&& ap.getDeadline().compareTo(pTo) <= 0) {
					throw new AonCoreException(
							AonError.ACCOUNT_PERIOD_END_OVERLAP.format(
							record.getValue(ACCOUNT_PERIOD.NAME)));
				}
			}
		};
		
		private static BiConsumer<AccountPeriod,AONContext> HAS_ENTRIES_CHECK = (ap,ctx) -> {
			Integer count = ctx.getDslContext().select( DSL.count(ACCOUNT_ENTRY.ID) )
				.from(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ap.getId()))
				.fetch()
				.stream()
				.map( rec -> rec.getValue(DSL.count(ACCOUNT_ENTRY.ID)) )
				.findFirst()
				.orElse(0);
			if (count > 0) {
				throw new AonCoreException(AonError.ACCOUNT_PERIOD_HAS_ENTRIES.format(count));
			}
		};

		private static void validate(AONContext ctx, AccountPeriod ap)
				throws AonCoreException {

			EMPTY_DOMAIN
				.andThen(EMPTY_STATUS)
				.andThen(EMPTY_INITIATION_DATE_VALIDATION)
				.andThen(EMPTY_DEADLINE)
				.andThen(WRONG_RANGE)
				.andThen(OVERLAP)
				.accept(ap, ctx);

		}

		public static void validateDeletion(AONContext ctx, AccountPeriod ap) {
			HAS_ENTRIES_CHECK
				.accept(ap, ctx);
		}

	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static AccountPeriod getRandom(AONContext ctx, AccountPeriodFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new FullAccountPeriodFiller())
			.findFirst()
			.orElse(null);
	}
}
