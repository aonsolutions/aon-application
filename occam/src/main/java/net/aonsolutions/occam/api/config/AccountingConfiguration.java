package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Optional;

import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.constants.AppParam;

public class AccountingConfiguration implements Serializable {

	private static final long serialVersionUID = -4608981705550453419L;
	
	private EnumMap<AppParam, Account> accounts = new EnumMap<>(AppParam.class); 
	
	public Optional<Account> getAccount( AppParam param ) {
		return Optional.ofNullable(accounts.get(param));
	}
	public AccountingConfiguration setAccount( AppParam param, Account account ) {
		accounts.put(param, account);
		return this;
	}
	
}
