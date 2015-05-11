package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.util.Date;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.AccountPeriodRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.validation.AccountPeriodValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AccountPeriodDAO {

	public static AccountPeriod fetchOne(AONContext ctx, Date date) {
		ctx.checkRead();
		Condition condition = ACCOUNT_PERIOD.DOMAIN.equal(ctx.getDomainId())
				.and(ACCOUNT_PERIOD.INITIATION_DATE.lessOrEqual(AonDateUtils.toSql(date)))
				.and(ACCOUNT_PERIOD.DEADLINE.greaterOrEqual(AonDateUtils.toSql(date)) );
		return fetchOne(ctx,condition);
	}

	public static AccountPeriod fetchOne(AONContext ctx, Integer id) {
		ctx.checkRead();
		return populateRecord(ctx.getDslContext().
				fetchOne(ACCOUNT_PERIOD,ACCOUNT_PERIOD.ID.equal(id)));
	}

	public static AccountPeriod fetchOne(AONContext ctx, Condition condition) {
		ctx.checkRead();
		return populateRecord(ctx.getDslContext().fetchOne(ACCOUNT_PERIOD, condition));
	}

	public static AccountPeriod fetchOneByYear(AONContext ctx, int year) {
		ctx.checkRead();
		Date date = AonDateUtils.getYearLastDay(year);
		return fetchOne(ctx,date);
	}

	private static AccountPeriod populateRecord(AccountPeriodRecord record) {
		if (record == null) return null;
		AccountPeriod period = new AccountPeriod();
		return populateRecord(record, period);
	}

	private static AccountPeriod populateRecord(AccountPeriodRecord record,
			AccountPeriod period) {
		period.setId(record.getId());
		period.setDomain(record.getDomain());
		period.setName(record.getName());
		period.setInitiationDate(record.getInitiationDate());
		period.setDeadline(record.getDeadline());
		period.setStatus(AccountPeriodStatus.values()[record.getStatus()]);
		return period;
	}

	public static void insert(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodValidation.validatePeriod(ctx, ap);
			AccountPeriodRecord record = ctx.getDslContext()
					.insertInto(ACCOUNT_PERIOD)
					.set(ACCOUNT_PERIOD.DOMAIN, ap.getDomain())
					.set(ACCOUNT_PERIOD.NAME, ap.getName())
					.set(ACCOUNT_PERIOD.INITIATION_DATE,
							AonDateUtils.toSql(ap.getInitiationDate()))
					.set(ACCOUNT_PERIOD.DEADLINE,
							AonDateUtils.toSql(ap.getDeadline()))
					.set(ACCOUNT_PERIOD.STATUS,
							AonEnumUtils.getByte(ap.getStatus()))
					.returning()
					.fetchOne();
			populateRecord(record, ap);
		});
	}

	public static void update(AONContext ctx, AccountPeriod ap) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodValidation.validatePeriod(ctx, ap);
			ctx.getDslContext()
				.update(ACCOUNT_PERIOD)
					.set(ACCOUNT_PERIOD.DOMAIN, ap.getDomain())
					.set(ACCOUNT_PERIOD.NAME, ap.getName())
					.set(ACCOUNT_PERIOD.INITIATION_DATE,
							AonDateUtils.toSql(ap.getInitiationDate()))
					.set(ACCOUNT_PERIOD.DEADLINE,
							AonDateUtils.toSql(ap.getDeadline()))
					.set(ACCOUNT_PERIOD.STATUS,
							AonEnumUtils.getByte(ap.getStatus()))
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

}
