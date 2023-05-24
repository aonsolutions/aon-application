package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.dao.ConfigurationDAO;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class ConfigurationDAOTest extends AbstractOccamTest {

	@Test()
	void DomainneTest() {
		Configuration config = ConfigurationDAO.getConfiguration(ctx,b -> b);
		assertNotNull(config);
		assertFalse(config.accounting().isPresent());
	}
	
	@Test()
	void accountingTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx, DOMAIN_NAME);
		assertTrue(optDomain.isPresent());
		Configuration config = ConfigurationDAO.getConfiguration(ctx, b -> b.withAccountingConfiguration());
		assertNotNull(config);
		assertTrue(config.accounting().isPresent());
		assertTrue(config.accounting().get().getAccount(AppParam.ACC_DEFAULT_CASH_ACC).isPresent());
	}
}
