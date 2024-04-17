package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountAutoComplete {

	/**
	 * Se rellena el nivel de la cuenta.
	 */
	public static BiConsumer<Account,AONContext> COMPLETE_LEVEL = (account,ctx) -> {
		account.setLevel( (byte) ((account.getCode().length() > 4) 
					? 5 
					: account.getCode().length()));
	};

	/**
	 * Se rellena si permite apuntes o no.
	 */
	public static BiConsumer<Account,AONContext> COMPLETE_ENTRY_ENABLED  = (account,ctx) -> {
		account.setEntryEnabled(account.getLevel()==5);
	};

	/**
	 * Se rellena si permite apuntes o no.
	 */
	public static BiConsumer<Account,AONContext> ENSURE_COST_CENTER = (account,ctx) -> {
    	if (AonStringUtils.isBlank( account.getCostCenter())) {
    		account.setCostCenter(null);
    	}
	};
	
	public static void complete(AONContext ctx,Account account) throws AonCoreException {
		
		COMPLETE_LEVEL
			.andThen(COMPLETE_ENTRY_ENABLED)
			.andThen(ENSURE_COST_CENTER)
			.accept(account, ctx );
	}

}
