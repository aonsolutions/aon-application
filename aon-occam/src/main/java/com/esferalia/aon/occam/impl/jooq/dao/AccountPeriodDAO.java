package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.accounting.AccountPeriodFilter;
import com.esferalia.aon.occam.api.model.accounting.AccountPeriodProperties;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.validation.AccountPeriodValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class AccountPeriodDAO {

	public static Stream<AccountPeriod> getPeriods(AONContext ctx, AccountPeriodFilter filter) {
		ctx.checkRead();
		ApplicationParameter ap = AppParamDAO.fetchOne(ctx, AppParam.ACC_DEFAULT_PERIOD);
		return ctx.getDslContext()
			.select(ACCOUNT_PERIOD.ID,ACCOUNT_PERIOD.DOMAIN,ACCOUNT_PERIOD.NAME,ACCOUNT_PERIOD.INITIATION_DATE
					,ACCOUNT_PERIOD.DEADLINE,ACCOUNT_PERIOD.STATUS,ACCOUNT_PERIOD.CREATION_USER
					,ACCOUNT_PERIOD.CREATION_DATE,ACCOUNT_PERIOD.MODIFICATION_USER,ACCOUNT_PERIOD.MODIFICATION_DATE)
			.from(ACCOUNT_PERIOD)
			.where(ACCOUNT_PERIOD_PROPERTIES.getConditions(filter))
			.orderBy(ACCOUNT_PERIOD.INITIATION_DATE.desc())
			.fetch()
			.stream()
			.map(new FullAccountPeriodFiller())
			.peek( period -> period.setDefaultPeriod( AonUtils.equals(
					AonNumberUtils.toInteger( ap.getValue() ),period.getId())) 
			);
		
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
	public static Stream<AccountPeriod> getDomainPeriods(AONContext ctx) {
		ctx.checkRead();
		return getPeriods(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId()));
	}
	
	public static AccountPeriod fetchOne(AONContext ctx, Date date) {
		ctx.checkRead();
		return getPeriods(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getInitiationDateProperty().le(date) )
					.and(p.getDeadlineProperty().ge(date) ))
			.findFirst()
			.orElse(null);
	}

	public static AccountPeriod fetchOne(AONContext ctx, Integer id) {
		ctx.checkRead();
		return getPeriods(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getIdProperty().eq(id) ))
			.findFirst()
			.orElse(null);
	}

	public static AccountPeriod fetchOneByYear(AONContext ctx, int year) {
		ctx.checkRead();
		Date date = AonDateUtils.getYearLastDay(year);
		return fetchOne(ctx,date);
	}

	public static void insert(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodValidation.validatePeriod(ctx, ap);
			ctx.getDslContext().insertInto(ACCOUNT_PERIOD)
				.set(ACCOUNT_PERIOD.DOMAIN, ap.getDomain())
				.set(ACCOUNT_PERIOD.NAME, ap.getName())
				.set(ACCOUNT_PERIOD.INITIATION_DATE,AonDateUtils.toSql(ap.getInitiationDate()))
				.set(ACCOUNT_PERIOD.DEADLINE,AonDateUtils.toSql(ap.getDeadline()))
				.set(ACCOUNT_PERIOD.STATUS,AonEnumUtils.getByte(ap.getStatus()))
				;
		});
	}

	public static void update(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodValidation.validatePeriod(ctx, ap);
			ctx.getDslContext().update(ACCOUNT_PERIOD)
				.set(ACCOUNT_PERIOD.DOMAIN, ap.getDomain())
				.set(ACCOUNT_PERIOD.NAME, ap.getName())
				.set(ACCOUNT_PERIOD.INITIATION_DATE,AonDateUtils.toSql(ap.getInitiationDate()))
				.set(ACCOUNT_PERIOD.DEADLINE,AonDateUtils.toSql(ap.getDeadline()))
				.set(ACCOUNT_PERIOD.STATUS,AonEnumUtils.getByte(ap.getStatus()))
				.where(ACCOUNT_PERIOD.ID.equal(ap.getId()))
				.execute();
		});
	}

	public static void delete(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
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
	// ---------------------------------------------------------- FILTRO
	private static final AccountPeriodPropertiesDAO ACCOUNT_PERIOD_PROPERTIES = new AccountPeriodPropertiesDAO();
	private static class AccountPeriodPropertiesDAO implements AccountPeriodProperties {

		private Condition[] getConditions(AccountPeriodFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_PERIOD.ID);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT_PERIOD.DOMAIN);
		}

		@Override
		public Property<Date> getInitiationDateProperty() {
			return new FilterDAO.DatePropertyDAO(ACCOUNT_PERIOD.INITIATION_DATE);
		}
		
		@Override
		public Property<Date> getDeadlineProperty() {
			return new FilterDAO.DatePropertyDAO(ACCOUNT_PERIOD.DEADLINE);
		}

		@Override
		public Property<Byte> getStatusProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_PERIOD.STATUS);
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
}
