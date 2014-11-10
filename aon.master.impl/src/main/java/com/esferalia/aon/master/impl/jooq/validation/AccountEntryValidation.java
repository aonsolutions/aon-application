package com.esferalia.aon.master.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;

import java.util.Date;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.esferalia.aon.master.impl.client.Account;
import com.esferalia.aon.master.impl.client.AccountEntry;
import com.esferalia.aon.master.impl.client.AccountEntryDetail;
import com.esferalia.aon.master.impl.client.AccountPeriod;
import com.esferalia.aon.master.impl.server.jooq.AccountDAO;
import com.esferalia.aon.master.impl.server.jooq.AccountPeriodDAO;
import com.esferalia.aon.master.impl.server.jooq.DAOContext;
import com.esferalia.aon.master.impl.server.sql.AonDAOError;
import com.esferalia.aon.master.impl.server.sql.AonDAOException;
import com.esferalia.aon.shared.commons.AonDateUtils;
import com.esferalia.aon.shared.commons.AonStringUtils;

public class AccountEntryValidation {

	public static final Logger LOGGER = LoggerFactory
			.getLogger(AccountEntryValidation.class);
	
	/**
	 * El dominio del apunte no puede estar vacio.
	 */
	public static BiConsumer<AccountEntry,DAOContext> EMPTY_DOMAIN = (ae,ctx) -> {
		if (ae.getDomain() == null) 
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_DOMAIN);
	};
	
	/**
	 * La fecha del asiento es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,DAOContext> EMPTY_DATE = (ae,ctx) -> {
		if (ae.getEntryDate() == null)
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_EMPTY_DATE);
	};
	
	/**
	 * El periodod del asiento es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntry,DAOContext> EMPTY_PERIOD = (ae,ctx) -> {
		if (ae.getAccountPeriod() == null)
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_EMPTY_PERIOD);
	};
	
	/**
	 * El Tipo de asiento no puede ser null.
	 */
	public static BiConsumer<AccountEntry,DAOContext> EMPTY_ENTRY_TYPE = (ae,ctx) -> {
		if (ae.getEntryType() == null) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_EMPTY_TYPE);
		}
	};
	
	
	/**
	 * El periodo es una dato obligatorio, debe existir en el mismo dominio,
	 * La fecha de asiento debe estar comprendida entre las fechas de inicio 
	 * y fin del asiento y el estado del periodo debe permitir la entrada del 
	 * tipo de asiento. Tal que:
	 * 		
	 */
	public static BiConsumer<AccountEntry,DAOContext> ENTRY_PERIOD_CHECK = (ae,ctx) -> {
		AccountPeriod period = AccountPeriodDAO.fetchOne(ctx,
				ACCOUNT_PERIOD.ID.equal(ae.getAccountPeriod())
				.and(ACCOUNT_PERIOD.DOMAIN.equal(ae.getDomain())));
		// El periodo debe existir y tener el mismo dominio que el asiento.
		if (period == null) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_WRONG_DOMAIN,ae.getDomain());
		}
		// Se chequea que la fecha del apunte esté comprendida
		// entre la fecha inicial y la fecha final del ejercicio.
		Date periodFrom = period.getInitiationDate();
		Date periodTo = period.getDeadline();
		if (!AonDateUtils.isSameDay(ae.getEntryDate(), periodFrom)
				&& !AonDateUtils.isSameDay(ae.getEntryDate(), periodTo)
				&& (ae.getEntryDate().before(periodFrom) || ae.getEntryDate().after(periodTo))) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_DATE_IN_PERIOD,period.getName());
		}
		// Se chequea que el ejericio no esté inactivo.
		if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_PERIOD_INACTIVE,period.getName());
		}

		// Si el ejercicio está en explotación, solo se permite la introducción
		// de apuntes de explotación o cierre.
		if (period.getStatus() == AccountPeriodStatus.OPERATING
				&& ae.getEntryType() != AccountEntryType.OPERATING
				&& ae.getEntryType() != AccountEntryType.CLOSING) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_PERIOD_OPERATING,period.getName());
		}
		// Si el ejercicio está cerrado, solo se permite la introducción de
		// apuntes de cierre.
		if (period.getStatus() == AccountPeriodStatus.CLOSED
			&& ae.getEntryType() != AccountEntryType.CLOSING) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_PERIOD_CLOSING,period.getName());
		}
	};

	/**
	 * El concepto del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntryDetail,DAOContext> EMPTY_CONCEPT = (detail,ctx) -> {
		if (AonStringUtils.isEmpty(detail.getConcept() ))
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_EMPTY_CONCEPT);
	};
	
	/**
	 * La cuenta contable del apunte es un dato obligatorio.
	 */
	public static BiConsumer<AccountEntryDetail,DAOContext> EMPTY_ACCOUNT = (detail,ctx) -> {
		if (detail.getAccount() == null )
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_EMPTY_ACCOUNT);
	};
	

	private static void validateAccount(AccountEntryDetail detail,
			Integer accountId, DAOContext ctx) {
		Account account = AccountDAO.fetchOne(ctx, detail.getAccount());
		if (account == null)
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_ACCOUNT_NOT_FOUND
					,Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription());
		if (!account.isActive())
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_ACCOUNT_INACTIVE
					,Objects.toString(detail.getAccount())
					,detail.getAccountCode(),detail.getAccountDescription());
		if (account.getCode().length() != 9)
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_ACCOUNT_INVALID_LENGTH
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
	public static BiConsumer<AccountEntryDetail,DAOContext> VALID_ACCOUNT = (detail,ctx) -> {
		validateAccount(detail,detail.getAccount(),ctx);
	};

	/**
	 * La contrapartida debe ser una cuenta válida.
	 * La contrapartida debe pertenecer al dominio del apunte.
	 * La contrapartida debe estar activa.
	 * La contrapartida debe ser de último nivel.
	 * 
	 */
	public static BiConsumer<AccountEntryDetail,DAOContext> VALID_BALANCING_ACCOUNT = (detail,ctx) -> {
		if (detail.getBalancingAccount() != null) {
			validateAccount(detail,detail.getBalancingAccount(),ctx);
		}
	};

	public static void validateEntry(DAOContext ctx, AccountEntry ae)
			throws AonDAOException {
		EMPTY_DOMAIN
			.andThen(EMPTY_DATE)
			.andThen(EMPTY_PERIOD)
			.andThen(EMPTY_ENTRY_TYPE)
			.andThen(ENTRY_PERIOD_CHECK)
			.accept(ae, ctx);

	}


	public static void validateDetail(DAOContext ctx, AccountEntryDetail detail) {
		EMPTY_ACCOUNT
			.andThen(VALID_ACCOUNT)
			.andThen(EMPTY_CONCEPT)
			.andThen(VALID_BALANCING_ACCOUNT)
			.accept(detail, ctx);
	}

}
