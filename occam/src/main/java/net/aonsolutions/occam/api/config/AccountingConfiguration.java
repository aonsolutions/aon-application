package net.aonsolutions.occam.api.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.watson.server.AonObjectUtils;

public class AccountingConfiguration extends OccamEntity {

	private static final long serialVersionUID = -4608981705550453419L;

	private Integer uuid;
	private EnumMap<AppParam, Account> accounts = new EnumMap<>(AppParam.class);

	@Override
	protected Object getUuid() {
		return uuid;
	}
	public void setUuid(Integer uuid) {
		this.uuid = uuid;
	}

	public Map<AppParam, Account> getAccounts() {
		return accounts;
	}

	public Optional<Account> getAccount(AppParam param) {
		return Optional.ofNullable(accounts.get(param));
	}

	public AccountingConfiguration setAccount(AppParam param, Account account) {
		AonObjectUtils.ifTrue(mustMarkaAsDirty(accounts.get(param), account), () -> markAsDirty(AonNames.ACCOUNTS));
		accounts.put(param, account);
		return this;
	}

}
