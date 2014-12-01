package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.util.Date;
import java.util.Objects;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.AonCoreException;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryValidation {

	/**
	 * El dominio del apunte no puede estar vacio.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_DOMAIN = (ae,ctx) -> {
		if (ae.getDomain() == null) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_DOMAIN);
	};
	
	/**
	 * La fecha del asiento es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_DATE = (ae,ctx) -> {
		if (ae.getEntryDate() == null)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_DATE);
	};
	
	/**
	 * El periodod del asiento es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_PERIOD = (ae,ctx) -> {
		if (ae.getAccountPeriod() == null) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD);
	};
	
	/**
	 * El Tipo de asiento no puede ser null.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_ENTRY_TYPE = (ae,ctx) -> {
		if (ae.getEntryType() == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_TYPE);
		}
	};
	
	/**
	 * El periodo es una dato obligatorio, debe existir en el mismo dominio,
	 * La fecha de asiento debe estar comprendida entre las fechas de inicio 
	 * y fin del asiento y el estado del periodo debe permitir la entrada del 
	 * tipo de asiento. Tal que:
	 * 		
	 */
	public static BiConsumer<AccountEntry,AONContext> ENTRY_PERIOD_CHECK = (ae,ctx) -> {
		AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
				ACCOUNT_PERIOD.ID.equal(ae.getAccountPeriod())
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ae.getDomain())));
		// El periodo debe existir y tener el mismo dominio que el asiento.
		if (period == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_WRONG_DOMAIN,ae.getDomain());
		}
		// Se chequea que la fecha del apunte esté comprendida
		// entre la fecha inicial y la fecha final del ejercicio.
		Date periodFrom = period.getInitiationDate();
		Date periodTo = period.getDeadline();
		if (!AonDateUtils.isSameDay(ae.getEntryDate(), periodFrom)
				&& !AonDateUtils.isSameDay(ae.getEntryDate(), periodTo)
				&& (ae.getEntryDate().before(periodFrom) || ae.getEntryDate().after(periodTo))) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_DATE_IN_PERIOD,period.getName());
		}
		// Se chequea que el ejericio no esté inactivo.
		if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE,period.getName());
		}

		// Si el ejercicio está en explotación, solo se permite la introducción
		// de apuntes de explotación o cierre.
		if (period.getStatus() == AccountPeriodStatus.OPERATING
				&& ae.getEntryType() != AccountEntryType.OPERATING
				&& ae.getEntryType() != AccountEntryType.CLOSING) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING,period.getName());
		}
		// Si el ejercicio está cerrado, solo se permite la introducción de
		// apuntes de cierre.
		if (period.getStatus() == AccountPeriodStatus.CLOSED
			&& ae.getEntryType() != AccountEntryType.CLOSING) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING,period.getName());
		}
	};

	/**
	 * El concepto del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> EMPTY_CONCEPT = (detail,ctx) -> {
		if (AonStringUtils.isEmpty(detail.getConcept() ))
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_CONCEPT);
	};
	
	/**
	 * La cuenta contable del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> EMPTY_ACCOUNT = (detail,ctx) -> {
		if (detail.getAccount() == null )
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_ACCOUNT,detail.getLine()
				,detail.getConcept(),detail.getDebit(),detail.getCredit());
	};
	

	private static void validateAccount(AccountEntryDetail detail,
			Integer accountId, AONContext ctx) {
		Account account = AccountDAO.fetchOne(ctx, detail.getAccount());
		if (account == null)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_NOT_FOUND
					,Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription());
		if (!account.isActive())
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_INACTIVE
					,Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription());
		if (account.getCode().length() != 9)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_INVALID_LENGTH
					,Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription());
	}
	
	/**
	 * La cuenta contable debe ser una cuenta válida.
	 * La cuenta contable debe pertenecer al dominio del apunte.
	 * La cuenta contable debe estar activa.
	 * La cuenta contable debe ser de último nivel.
	 * 
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> VALID_ACCOUNT = (detail,ctx) -> {
		validateAccount(detail,detail.getAccount(),ctx);
	};

	/**
	 * La contrapartida debe ser una cuenta válida.
	 * La contrapartida debe pertenecer al dominio del apunte.
	 * La contrapartida debe estar activa.
	 * La contrapartida debe ser de último nivel.
	 * 
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> VALID_BALANCING_ACCOUNT = (detail,ctx) -> {
		if (detail.getBalancingAccount() != null) {
			validateAccount(detail,detail.getBalancingAccount(),ctx);
		}
	};

	public static void validateEntry(AONContext ctx, AccountEntry ae)
			throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_DATE)
			.andThen(EMPTY_PERIOD)
			.andThen(EMPTY_ENTRY_TYPE)
			.andThen(ENTRY_PERIOD_CHECK)
			.accept(ae, ctx);

	}


	public static void validateDetail(AONContext ctx, AccountEntryDetail detail) {
		EMPTY_ACCOUNT
			.andThen(VALID_ACCOUNT)
			.andThen(EMPTY_CONCEPT)
			.andThen(VALID_BALANCING_ACCOUNT)
			.accept(detail, ctx);
	}

}
