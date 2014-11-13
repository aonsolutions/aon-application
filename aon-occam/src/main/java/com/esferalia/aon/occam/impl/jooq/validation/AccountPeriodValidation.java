package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.util.Date;
import java.util.function.BiConsumer;

import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.core.commons.AonCoreException;
import com.esferalia.aon.core.commons.AonError;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;

public class AccountPeriodValidation {

	/**
	 * La fecha inicial no puede estar vacia
	 */
	public static BiConsumer<AccountPeriod,AONContext> EMPTY_INITIATION_DATE_VALIDATION = (ap,ctx) -> {
		if (ap.getInitiationDate() == null) 
			throw new AonCoreException(AonError.ACCOUNT_PERIOD_EMPTY_INITIATION_DATE);
	};
	
	/**
	 * La fecha final debe ser posterior a la fecha inicial.
	 */
	public static BiConsumer<AccountPeriod,AONContext> EMPTY_DEADLINE = (ap,ctx) -> {
		if (ap.getDeadline() == null)
			throw new AonCoreException(AonError.ACCOUNT_PERIOD_EMPTY_DEADLINE);
	};
	
	/**
	 * La fecha final debe ser posterior a la fecha inicial.
	 */
	public static BiConsumer<AccountPeriod,AONContext> WRONG_RANGE = (ap,ctx) -> {
		if (ap.getInitiationDate().after(ap.getDeadline()))
			throw new AonCoreException(AonError.ACCOUNT_PERIOD_WRONG_RANGE);
	};
	
	/**
	 * El status del periodo no puede estar vacio.
	 */
	public static BiConsumer<AccountPeriod,AONContext> EMPTY_STATUS = (ap,ctx) -> {
		if (ap.getStatus() == null)
			throw new AonCoreException(AonError.ACCOUNT_PERIOD_TYPE_INVALID, "null");
	};

	/**
	 * No debe haber solapes entre las fechas de los diferentes periodos definidos.
	 */
	public static BiConsumer<AccountPeriod,AONContext> OVERLAP = (ap,ctx) -> {
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
						AonError.ACCOUNT_PERIOD_START_OVERLAP,
						record.getValue(ACCOUNT_PERIOD.NAME));
			}
			if (ap.getDeadline().compareTo(pFrom) >= 0 
				&& ap.getDeadline().compareTo(pTo) <= 0) {
				throw new AonCoreException(
						AonError.ACCOUNT_PERIOD_END_OVERLAP,
						record.getValue(ACCOUNT_PERIOD.NAME));
			}
		}
	};

	public static void validatePeriod(AONContext ctx, AccountPeriod ap)
			throws AonCoreException {

		EMPTY_INITIATION_DATE_VALIDATION
			.andThen(EMPTY_DEADLINE)
			.andThen(WRONG_RANGE)
			.andThen(EMPTY_STATUS)
			.andThen(OVERLAP)
			.accept(ap, ctx);

	}

}
