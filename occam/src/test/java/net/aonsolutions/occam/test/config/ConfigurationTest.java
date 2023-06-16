package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.metadata.AccountingConfigurationMetadata;
import net.aonsolutions.occam.api.metadata.AccountingConfigurationMetadata.AccountingConfigurationMetadataVisitor;
import net.aonsolutions.occam.api.metadata.ConfigurationMetadata;
import net.aonsolutions.occam.api.metadata.ConfigurationMetadata.ConfigurationMetadataVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;

@ExtendWith(TimingExtension.class)
class ConfigurationTest extends AbstractOccamTest {

	@Test()
	void metadataVisitorTest() {
		ConfigurationMetadataVisitor<ConfigurationMetadata> visitor = new ConfigurationMetadataVisitor<>() {
			@Override public ConfigurationMetadata visitId() { return ConfigurationMetadata.ID; }
			@Override public ConfigurationMetadata visitAccountingConfiguration() { return  ConfigurationMetadata.ACCOUNTING_CONFIGURATION; }
		};
		
		Arrays.stream(ConfigurationMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}

	@Test()
	void metadataAccountingVisitorTest() {
		AccountingConfigurationMetadataVisitor<AccountingConfigurationMetadata> visitor = new AccountingConfigurationMetadataVisitor<>() {
			@Override public AccountingConfigurationMetadata visitId() { return AccountingConfigurationMetadata.ID; }
			@Override public AccountingConfigurationMetadata visitAccounts() { return  AccountingConfigurationMetadata.ACCOUNTS; }
		};
		
		Arrays.stream(AccountingConfigurationMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}
	
}
