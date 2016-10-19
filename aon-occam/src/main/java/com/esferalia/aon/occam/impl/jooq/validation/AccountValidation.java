package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.util.function.BiConsumer;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountValidation {

	/**
	 * El dominio de la cuenta no puede estar vacio.
	 */
	public static BiConsumer<Account,AONContext> EMPTY_DOMAIN = (account,ctx) -> {
		if (account.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El código de cuenta contable es un dato obligatorio.
	 */
	public static BiConsumer<Account,AONContext> EMPTY_CODE = (account,ctx) -> {
		if (AonStringUtils.isEmpty(account.getCode()))
			throw new AonCoreException(AonError.ACCOUNT_EMPTY_CODE.getMessage());
	};
	
	/**
	 * La descripcion de cuenta contable es un dato obligatorio.
	 */
	public static BiConsumer<Account,AONContext> EMPTY_DESCRIPTION = (account,ctx) -> {
		if (AonStringUtils.isEmpty(account.getCode()))
			throw new AonCoreException(AonError.ACCOUNT_EMPTY_DESCRIPTION.getMessage());
	};

	/**
	 * La longitd de la cuenta debe ser 1,2,3,4, ó 9
	 */
	public static BiConsumer<Account,AONContext> VALID_LENGTH = (account,ctx) -> {
		if (AonStringUtils.length(account.getCode()) != 1
		 && AonStringUtils.length(account.getCode()) != 2
		 && AonStringUtils.length(account.getCode()) != 3
		 && AonStringUtils.length(account.getCode()) != 4
		 && AonStringUtils.length(account.getCode()) != 9)
			throw new AonCoreException(AonError.ACCOUNT_INVALID_LENGTH.format(account.getCode()));
		
	};
	/**
	 * La cuenta contable debe ser numerica
	 */
	public static BiConsumer<Account,AONContext> NUMERIC_CODE = (account,ctx) -> {
		if (!AonStringUtils.isNumeric(account.getCode()))
			throw new AonCoreException(AonError.ACCOUNT_NO_NUMERIC.getMessage());
	};
	
	/**
	 * Los niveles inferiores de la cuenta deben existir.
	 */
	public static BiConsumer<Account,AONContext> LOW_LEVEL_EXISTS = (account,ctx) -> {
		int level = (byte) ((account.getCode().length() > 4)? 5: account.getCode().length());
		if (level>1) {
			int parentLevel = level - 1;
			String parentCode = AonStringUtils.substring(account.getCode(),0, parentLevel);
			Account a = AccountDAO.get(ctx, ACCOUNT.CODE.equal(parentCode));
			if (a == null)
				throw new AonCoreException(AonError.ACCOUNT_LOW_LEVEL_NOT_PRESENT.getMessage());
		}
	};
	
	/**
	 * La cuenta contable no puede estar duplicada.
	 */
	public static BiConsumer<Account,AONContext> DUPLICATED_CODE = (account,ctx) -> {
			Account duplicated = AccountDAO.get(ctx, 
					ACCOUNT.CODE.equal(account.getCode())
					.and(account.getId() == null? DSL.trueCondition() : ACCOUNT.ID.ne(account.getId()) )
					);
			if (duplicated != null) {
				throw new AonCoreException(AonError.ACCOUNT_DUPLICATED_CODE.format(account.getFullName(),duplicated.getFullName()));
			}
	};
	
	public static void validate(AONContext ctx, Account account)
			throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_CODE)
			.andThen(EMPTY_DESCRIPTION)
			.andThen(VALID_LENGTH)
			.andThen(NUMERIC_CODE)
			.andThen(LOW_LEVEL_EXISTS)
			.andThen(DUPLICATED_CODE)
			.accept(account, ctx);

	}

}
