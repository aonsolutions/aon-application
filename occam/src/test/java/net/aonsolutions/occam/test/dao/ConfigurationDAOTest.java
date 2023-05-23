package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.dao.ConfigurationDAO;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class ConfigurationDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME ), b -> b);
		assertTrue(domain.isPresent());
		Configuration config = ConfigurationDAO.getConfiguration(ctx, domain.get().getId());
		assertNotNull(config);
	}
	
}
