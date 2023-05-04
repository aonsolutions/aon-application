package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class DomainDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(domain.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ));
		assertFalse(domain.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(domain.isPresent());
		assertFalse(domain.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> DomainDAO.get(ctx, null));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> DomainDAO.getStream(ctx,p -> p.withName().eq( DOMAIN_NAME ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderStreamTest() {
		Stream<Domain> domain = DomainDAO.getStream(ctx,p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(domain.findAny().isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<Domain> domain = DomainDAO.getStream(ctx
			,p -> p.withName().eq( DOMAIN_NAME )
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = DomainDAO.getStream(ctx, p -> p.withName().like("a%"))
			.limit(10)
			.count();
		int rows = AonRandom.getInt(0, (int) max);
		long count = DomainDAO.getStream(ctx, p -> p.withName().like("a%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx
			,p -> p.withName().eq( DOMAIN_NAME )
			,b -> b.full());
		assertTrue(optDomain.isPresent());
		Domain expected = optDomain.get();
		
		Optional<Domain> optActual = DomainDAO.get(ctx
			,p -> p.withId().eq( expected.getId() )
				.and(p.withName().eq( expected.getName() )) 
				.and(p.withDescription().eq( expected.getDescription() ))
				.and(p.withParent().eq( AonObjectUtils.ifOptionalPresent(expected.getParent(), o -> o.getId()) ))
				.and(p.withType().eq( AonEnumUtils.getOptEnumByte(expected.getType())))
				.and(p.withScope().eq( expected.getScope().orElse(null) ))
				.and(p.withSubDomainSuffix().eq( expected.getSubDomainSuffix().orElse(null) ))
				.and(p.withEnableHeredity().eq( AonEnumUtils.getByte(expected.isEnableHeredity())))
				.and(p.withDomainManagement().eq( AonEnumUtils.getByte(expected.isDomainManagement())))
				.and(p.withDisableDomainManagement().eq( AonEnumUtils.getByte(expected.isDisableDomainManagement())))
				.and(p.withMaxDocumentSize().eq( expected.getMaxDocumentSize().orElse(null)))
				.and(p.withMaxTotalDocumentSize().eq( expected.getMaxTotalDocumentSize().orElse(null)))
				.and(p.withMaxDefinedUsers().eq( expected.getMaxDefinedUsers().orElse(null)))
				.and(p.withActive().eq( AonEnumUtils.getByte(expected.isActive())))
				.and(p.withOwner().eq( expected.getOwner().orElse(null)))
				.and(p.withExpirationDate().eq( AonDateUtils.toSql(expected.getExpirationDate().orElse(null))))
				.and(p.withLastAccessDate().eq( AonDateUtils.toTimestamp(expected.getLastAccessDate().orElse(null))))
				.and(p.withLastAccessUser().eq( expected.getLastAccessUser().orElse(null)))
				.and(p.withAonCustomer().eq( expected.getAonCustomer().orElse(null)))
				.and(p.withAonStatus().eq( AonEnumUtils.getByte(expected.getAonStatus().orElse(null))))
				.and(p.withCreationUser().eq( expected.getCreationUser().orElse(null)))
				.and(p.withCreationDate().eq( AonDateUtils.toTimestamp(expected.getCreationDate().orElse(null))))
				.and(p.withModificationUser().eq( expected.getModificationUser().orElse(null)))
				.and(p.withModificationDate().eq( AonDateUtils.toTimestamp(expected.getModificationDate().orElse(null))))
			,b -> b.full()
		);
		assertTrue(optActual.isPresent(),"Domain not found!");
		Asserts.assertEqualsDomain(expected, optActual.get());
	}
	
	@Test()
	void denyReadOneTest() {
		try {
			ctx.denyRead();
			SecurityException e = assertThrows(SecurityException.class, () -> DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME )));
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
		} finally {
			ctx.allowRead();
		}
	}
	
	@Test
	void basicGetTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx, p -> p.withName().eq( DOMAIN_NAME ));
		assertTrue(optDomain.isPresent());
		
		assertTrue(optDomain.get().getOwner().isEmpty());
		assertTrue(optDomain.get().getParent().isEmpty());
		assertTrue(optDomain.get().getType().isEmpty());
		assertTrue(optDomain.get().getSubDomainSuffix().isEmpty());
		assertTrue(optDomain.get().isEnableHeredity().isEmpty());
		assertTrue(optDomain.get().isDomainManagement().isEmpty());
		assertTrue(optDomain.get().isDisableDomainManagement().isEmpty());
		assertTrue(optDomain.get().isActive().isEmpty());
		assertTrue(optDomain.get().getScope().isEmpty());
		assertTrue(optDomain.get().getMaxDefinedUsers().isEmpty());
		assertTrue(optDomain.get().getMaxDocumentSize().isEmpty());
		assertTrue(optDomain.get().getMaxTotalDocumentSize().isEmpty());
		assertTrue(optDomain.get().getLastAccessUser().isEmpty());
		assertTrue(optDomain.get().getLastAccessDate().isEmpty());
		assertTrue(optDomain.get().getExpirationDate().isEmpty());
		assertTrue(optDomain.get().getAonCustomer().isEmpty());
		assertTrue(optDomain.get().getAonStatus().isEmpty());
		assertTrue(optDomain.get().getCreationUser().isEmpty());
		assertTrue(optDomain.get().getCreationDate().isEmpty());
		assertTrue(optDomain.get().getModificationUser().isEmpty());
		assertTrue(optDomain.get().getModificationDate().isEmpty());
	}
	
	@Test
	void usersGetTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx
			, p -> p.withName().eq( DOMAIN_NAME )
			, b -> b.withUsers()
			);
		assertTrue(optDomain.isPresent());
		assertTrue(optDomain.get().getUsers().isPresent());
		Asserts.assertNotEmpty(optDomain.get().getUsers().get(), "Users");
		assertTrue(optDomain.get().getUsers().get().stream().anyMatch(u -> AonStringUtils.equals( u.getLogin(), USER)));
	}
}
