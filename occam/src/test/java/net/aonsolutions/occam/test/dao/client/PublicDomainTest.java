package net.aonsolutions.occam.test.dao.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class PublicDomainTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<Domain> domain = AON.getDomain(getOccam(), p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(domain.isPresent());
	}

	@Test()
	void selectFullTest() {
		Optional<Domain> domain = AON.getDomain(getOccam()
			, p -> p.withName().eq( DOMAIN_NAME )
			, b -> b.full()
			);
		assertTrue(domain.isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<Domain> domain = AON.getDomains(getOccam()
			,p -> p.withName().eq( DOMAIN_NAME )
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test()
	void selectFullStreamTest() {
		Stream<Domain> domain = AON.getDomains(getOccam()
			,p -> p.withName().eq( DOMAIN_NAME )
			,b -> b.full()
		);
		assertTrue(domain.findAny().isPresent());
	}
	
}
