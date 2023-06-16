package net.aonsolutions.occam.api.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.metadata.AccountingConfigurationMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class AccountingConfiguration extends OccamEntity<AccountingConfigurationMetadata> {

	private static final long serialVersionUID = -4608981705550453419L;

	private String uuid;
	private EnumMap<AppParam, Account> accounts = new EnumMap<>(AppParam.class);

	@Override
	public Object getUuid() {
		return uuid;
	}
	public AccountingConfiguration setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public Map<AppParam, Account> getAccounts() {
		return accounts;
	}

	public Optional<Account> getAccount(AppParam param) {
		return Optional.ofNullable(accounts.get(param));
	}

	public AccountingConfiguration setAccount(AppParam param, Account account) {
		AonObjectUtils.ifTrue(mustMarkaAsDirty(accounts.get(param), account), () -> markAsDirty(AccountingConfigurationMetadata.ACCOUNTS));
		accounts.put(param, account);
		return this;
	}

}
