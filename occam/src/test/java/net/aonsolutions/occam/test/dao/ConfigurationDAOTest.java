package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.dao.ConfigurationDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class ConfigurationDAOTest extends AbstractOccamTest {

	@Test()
	void noneTest() {
		Configuration config = ConfigurationDAO.getConfiguration(ctx, b -> b);
		assertNotNull(config);
		assertFalse(config.accounting().isPresent());
	}
	
	@Test()
	void accountingTest() {
		Configuration config = ConfigurationDAO.getConfiguration(ctx, b -> b.withAccounting());
		assertNotNull(config);
		assertTrue(config.accounting().isPresent());
		assertTrue(config.accounting().get().getAccount(AppParam.ACC_DEFAULT_CASH_ACC).isPresent());
	}
}
