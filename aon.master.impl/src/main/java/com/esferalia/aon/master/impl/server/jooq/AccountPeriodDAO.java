package com.esferalia.aon.master.impl.server.jooq;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.AccountPeriodRecord;
import com.esferalia.aon.master.impl.client.AccountPeriod;
import com.esferalia.aon.master.impl.jooq.validation.AccountPeriodValidation;
import com.esferalia.aon.shared.commons.AonDateUtils;
import com.esferalia.aon.shared.commons.AonEnumUtils;

public class AccountPeriodDAO {

	public static AccountPeriod fetchOne(DAOContext ctx, Integer id) {
		return populateRecord(ctx.getDslContext().
				fetchOne(ACCOUNT_PERIOD,ACCOUNT_PERIOD.ID.equal(id)));
	}

	public static AccountPeriod fetchOne(DAOContext ctx, Condition condition) {
		return populateRecord(ctx.getDslContext().fetchOne(ACCOUNT_PERIOD, condition));
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
		period.setStatus(AonEnumUtils.getAccountPeriodStatus(record.getStatus()));
		return period;
	}

	public static void insert(DAOContext ctx, AccountPeriod ap) {
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

	public static void update(DAOContext ctx, AccountPeriod ap) {
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

	public static void delete(DAOContext ctx, AccountPeriod ap) {
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(ACCOUNT_PERIOD)
				.where(ACCOUNT_PERIOD.ID.equal(ap.getId())).execute();
		});
	}

}
