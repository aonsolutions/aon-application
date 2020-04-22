package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;


import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryValidation {

	/**
	 * El dominio del apunte no puede estar vacio.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_DOMAIN = (ae,ctx) -> {
		if (ae.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La fecha del asiento es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_DATE = (ae,ctx) -> {
		if (ae.getEntryDate() == null)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_DATE.getMessage());
	};
	
	/**
	 * El periodod del asiento es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_PERIOD = (ae,ctx) -> {
		if (ae.getPeriod() == null)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage());
	};
	
	/**
	 * El Tipo de asiento no puede ser null.
	 */
	public static BiConsumer<AccountEntry,AONContext> EMPTY_ENTRY_TYPE = (ae,ctx) -> {
		if (ae.getEntryType() == null) {
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
	public static BiConsumer<AccountEntry,AONContext> ENTRY_PERIOD_CHECK = (ae,ctx) -> {
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx,ae.getPeriod());
		// El periodo debe existir y tener el mismo dominio que el asiento.
		if (period == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_WRONG_DOMAIN.format(ae.getDomain()));
		}
		// Se chequea que la fecha del apunte esté comprendida
		// entre la fecha inicial y la fecha final del ejercicio.
		Date periodFrom = period.getInitiationDate();
		Date periodTo = period.getDeadline();
		if (!AonDateUtils.isSameDay(ae.getEntryDate(), periodFrom)
				&& !AonDateUtils.isSameDay(ae.getEntryDate(), periodTo)
				&& (ae.getEntryDate().before(periodFrom) || ae.getEntryDate().after(periodTo))) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_DATE_IN_PERIOD.format(period.getName()));
		}
		// Se chequea que el ejericio no esté inactivo.
		if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE.format(period.getName()));
		}

		// Si el ejercicio está en explotación, solo se permite la introducción
		// de apuntes de explotación o cierre.
		if (period.getStatus() == AccountPeriodStatus.OPERATING
				&& ae.getEntryType() != AccountEntryType.OPERATING
				&& ae.getEntryType() != AccountEntryType.CLOSING) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING.format(period.getName()));
		}
		// Si el ejercicio está cerrado, solo se permite la introducción de
		// apuntes de cierre.
		if (period.getStatus() == AccountPeriodStatus.CLOSED
			&& ae.getEntryType() != AccountEntryType.CLOSING) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING.format(period.getName()));
		}
	};

	/**
	 * El apunte debe estar cuadrado.
	 */
	public static BiConsumer<AccountEntry,AONContext> ENTRY_SETTLED = (ae,ctx) -> {
		double sumD = 0.0;
		double sumC = 0.0;
		boolean empty = true;
		for (AccountEntryDetail aed : ae.getDetails()) {
			if (!aed.isDeleted()) {
				sumD = AonMathUtils.sum(sumD, aed.getDebit());	
				sumC = AonMathUtils.sum(sumC, aed.getCredit());
				empty = false;
			}
		}
		if (empty) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_DETAILS.getMessage());
		}
		
		if (!AonMathUtils.isZero( AonMathUtils.round(sumD - sumC))) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_SETTLED.getMessage());
		}
	};

	/**
	 * El concepto del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> EMPTY_CONCEPT = (detail,ctx) -> {
		if (AonStringUtils.isEmpty(detail.getConcept() ))
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_CONCEPT.getMessage());
	};
	
	/**
	 * El concepto no debe superar la longitud de la columan.
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> OVERFLOW_CONCEPT = (detail,ctx) -> {
		if (AonStringUtils.length(detail.getConcept()) > ACCOUNT_ENTRY_DETAIL.CONCEPT.getDataType().length() )
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_OVERFLOW_CONCEPT.getMessage());
	};

	/**
	 * La cuenta contable del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntryDetail,AONContext> EMPTY_ACCOUNT = (detail,ctx) -> {
		if (detail.getAccount() == null )
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_ACCOUNT.format(detail.getLine()
				,detail.getConcept(),detail.getDebit(),detail.getCredit()));
	};
	

	private static void validateAccount(AccountEntryDetail detail,
			Integer accountId, AONContext ctx) {
		Account account = AccountDAO.get(ctx, detail.getAccount());
		if (account == null)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_NOT_FOUND
					.format(Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription()));
		if (!account.isActive())
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_INACTIVE
					.format(Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription()));
		if (account.getCode().length() != 9)
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_ACCOUNT_INVALID_LENGTH
					.format(Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription()));
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
			.andThen(ENTRY_SETTLED)
			.accept(ae, ctx);

	}


	public static void validateDetail(AONContext ctx, AccountEntryDetail detail) {
		EMPTY_ACCOUNT
			.andThen(VALID_ACCOUNT)
			.andThen(EMPTY_CONCEPT)
			.andThen(OVERFLOW_CONCEPT)
			.andThen(VALID_BALANCING_ACCOUNT)
			.accept(detail, ctx);
	}


	/**
	 * La cuenta contable del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,AONContext> PERIOD_DELETION_ENABLED = (entry,ctx) -> {
		if (entry.getPeriodStatus() == null || !entry.getPeriodStatus().isActive()) {
			if (entry.getPeriodStatus() == AccountPeriodStatus.INACTIVE)
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE.format(entry.getPeriodName()));
			if (entry.getPeriodStatus() == AccountPeriodStatus.OPERATING)
				if (entry.getEntryType() != AccountEntryType.OPERATING) {
					throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING.format(entry.getPeriodName()));
				}
			if (entry.getPeriodStatus() == AccountPeriodStatus.CLOSED)
				if (entry.getEntryType() != AccountEntryType.CLOSING) {
					throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING.format(entry.getPeriodName()));
				}
		}
	};

	public static void validateRemove(AONContext ctx, AccountEntry entry) {
		PERIOD_DELETION_ENABLED
			.accept(entry, ctx);
		
	}

}
