package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;

public class Configuration implements Serializable, HasDirtyFlag<Configuration> {

	private static final long serialVersionUID = 7723888010939038114L;

	private AccountingConfiguration accountingConfiguration;

	private boolean dirty;

	public Optional<AccountingConfiguration> accounting() {
		return Optional.ofNullable(accountingConfiguration);
	}
	public Configuration setAccounting(AccountingConfiguration accountingConfiguration) {
		this.dirtyMark( this.accountingConfiguration, accountingConfiguration );
		this.accountingConfiguration = accountingConfiguration; 
		return this;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Configuration setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
}
