package net.aonsolutions.occam.test.dao.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.json.DomainJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
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
		AON.getDomains(getOccam(), null ,b -> b.full())
			.forEach(d -> {
				assertTrue(d.getAudit().isPresent());
				assertTrue(d.getBooking().isPresent());
				assertTrue(d.getCompany().isPresent());
				assertTrue(d.getConfiguration().isPresent());
				assertTrue(d.getUsers().isPresent());
			});
	}
	
	@Test()
	void saveTest() {
		Optional<Domain> domain = AON.getDomain(getOccam()
			, p -> p.withName().eq( DOMAIN_NAME )
			, b -> b.withBooking());
		assertTrue(domain.isPresent());
		Domain toSave = domain.get();
		Domain saved = AON.save(getOccam(),toSave);
		Asserts.assertEqualsDomain(toSave, saved);
	}
	
	
	@Test()
	void selectStreamTest1() {
		AON.getDomains(getOccam(),
			f -> f.withName().eq( DOMAIN_NAME )
			,b -> b.withCompany()
				.withBooking()
				.limit(0, 2)
				)
		.forEach(d ->  System.out.println( DomainJSON.to(d).toString(1)));
	}
	
	
}
