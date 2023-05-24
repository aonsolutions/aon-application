package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.constants.AppParam;

public class AccountingConfiguration implements Serializable, HasDirtyFlag<AccountingConfiguration> {

	private static final long serialVersionUID = -4608981705550453419L;

	private EnumMap<AppParam, Account> accounts = new EnumMap<>(AppParam.class);

	private boolean dirty;

	public Map<AppParam, Account> getAccounts() {
		return accounts;
	}

	public Optional<Account> getAccount(AppParam param) {
		return Optional.ofNullable(accounts.get(param));
	}

	public AccountingConfiguration setAccount(AppParam param, Account account) {
		this.dirtyMark(accounts.get(param), account);
		accounts.put(param, account);
		return this;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public AccountingConfiguration setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
}
