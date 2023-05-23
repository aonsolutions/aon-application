package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Optional;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 7723888010939038114L;

	private AccountingConfiguration accountingConfiguration;

	public Optional<AccountingConfiguration> accounting() {
		return Optional.ofNullable(accountingConfiguration);
	}
	public Configuration setAccounting(AccountingConfiguration accountingConfiguration) {
		this.accountingConfiguration = accountingConfiguration; 
		return this;
	}
	
}
