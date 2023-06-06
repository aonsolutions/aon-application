package net.aonsolutions.occam.api.config;

import java.util.Optional;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.metadata.ConfigurationMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Configuration extends OccamEntity<ConfigurationMetadata> {

	private static final long serialVersionUID = 7723888010939038114L;

	private Integer uuid;
	private AccountingConfiguration accountingConfiguration;

	public Optional<AccountingConfiguration> accounting() {
		return Optional.ofNullable(accountingConfiguration);
	}
	public Configuration setAccounting(AccountingConfiguration accountingConfiguration) {
		AonObjectUtils.ifTrue(mustMarkaAsDirty(this.accountingConfiguration, accountingConfiguration), () -> markAsDirty(ConfigurationMetadata.ID));
		this.accountingConfiguration = accountingConfiguration; 
		return this;
	}
	
	@Override
	protected Object getUuid() {
		return uuid;
	}
	public void setUuid(Integer uuid) {
		this.uuid = uuid;
	}
}
