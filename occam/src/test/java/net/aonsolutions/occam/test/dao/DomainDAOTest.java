package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class DomainDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<Domain> domain = DomainDAO.getDomain(ctx,p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(domain.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<Domain> domain = DomainDAO.getDomain(ctx,p -> p.withId().eq( Integer.MIN_VALUE ));
		assertFalse(domain.isPresent());
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> DomainDAO.getDomain(ctx, null));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> DomainDAO.getDomains(ctx,p -> p.withName().eq( DOMAIN_NAME ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderStreamTest() {
		Stream<Domain> domain = DomainDAO.getDomains(ctx,p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(domain.findAny().isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<Domain> domain = DomainDAO.getDomains(ctx
			,p -> p.withName().eq( DOMAIN_NAME )
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		int rows = 2;
		long count = DomainDAO.getDomains(ctx, 
			p -> p.withName().like("a%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<Domain> optDomain = DomainDAO.getDomain(ctx
			,p -> p.withName().eq( DOMAIN_NAME )
			,b -> b.full());
		assertTrue(optDomain.isPresent());
		Domain expected = optDomain.get();
		
		Optional<Domain> optActual = DomainDAO.getDomain(ctx
			,p -> p.withId().eq( expected.getId() )
				.and(p.withName().eq( expected.getName() )) 
				.and(p.withDescription().eq( expected.getDescription() ))
				.and(p.withParent().eq( expected.getParentId() ))
				.and(p.withType().eq( AonEnumUtils.getByte(expected.getType())))
				.and(p.withScope().eq( expected.getScope() ))
				.and(p.withSubDomainSuffix().eq( expected.getSubDomainSuffix() ))
				.and(p.withEnableHeredity().eq( AonEnumUtils.getByte(expected.isEnableHeredity())))
				.and(p.withDomainManagement().eq( AonEnumUtils.getByte(expected.isDomainManagement())))
				.and(p.withDisableDomainManagement().eq( AonEnumUtils.getByte(expected.isDisableDomainManagement())))
				.and(p.withMaxDocumentSize().eq( expected.getMaxDocumentSize()))
				.and(p.withMaxTotalDocumentSize().eq( expected.getMaxTotalDocumentSize()))
				.and(p.withMaxDefinedUsers().eq( expected.getMaxDefinedUsers() ))
				.and(p.withActive().eq( AonEnumUtils.getByte(expected.isActive())))
				.and(p.withOwner().eq( expected.getOwner() ))
				.and(p.withExpirationDate().eq( AonDateUtils.toSql(expected.getExpirationDate())))
				.and(p.withLastAccessDate().eq( AonDateUtils.toTimestamp(expected.getLastAccessDate())))
				.and(p.withLastAccessUser().eq( expected.getLastAccessUser() ))
				.and(p.withAonCustomer().eq( expected.getAonCustomer() ))
				.and(p.withAonStatus().eq( AonEnumUtils.getByte(expected.getAonStatus())))
				.and(p.withCreationUser().eq( expected.getCreationUser() ))
				.and(p.withCreationDate().eq( AonDateUtils.toTimestamp(expected.getCreationDate())))
				.and(p.withModificationUser().eq( expected.getModificationUser() ))
				.and(p.withModificationDate().eq( AonDateUtils.toTimestamp(expected.getModificationDate())))
			,b -> b.full()
		);
		assertTrue(optActual.isPresent(),"Domain not found!");
		Asserts.assertEqualsDomain(expected, optActual.get());
	}
	
}
