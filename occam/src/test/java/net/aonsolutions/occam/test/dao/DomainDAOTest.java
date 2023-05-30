package net.aonsolutions.occam.test.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.AonLogger;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonDateUtils;
import net.aonsolutions.watson.server.AonEnumUtils;
import net.aonsolutions.watson.server.AonObjectUtils;


@ExtendWith(TimingExtension.class)	
class DomainDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME ), b -> b);
		assertTrue(domain.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ), b -> b);
		assertFalse(domain.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<Domain> domain = DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME ), b -> b);
		assertTrue(domain.isPresent());
		assertFalse(domain.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class
			, () -> DomainDAO.getList(ctx,p -> p.withName().eq( DOMAIN_NAME ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectStreamTest() {
		List<Domain> domains = DomainDAO.getList(ctx
			,p -> p.withName().eq( DOMAIN_NAME )
			,b -> b
		);
		assertTrue(AonCollectionUtils.isNotEmpty(domains));
	}
	
	@Test
	void streamLimitTest() {
		long max = DomainDAO.getList(ctx, p -> p.withName().like("a%"), b -> b)
			.stream()
			.limit(10)
			.count();
		int rows = AonRandom.number(0, (int) max);
		long count = DomainDAO.getList(ctx, p -> p.withName().like("a%")
			,b -> b.limit(0, rows))
		.size();
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
				.and(p.withType().eq( AonEnumUtils.getByte(expected.getType())))
				.and(p.withScope().eq( AonObjectUtils.ifOptionalPresent(expected.getScope(), o -> o.getId()) ))
				.and(p.withEnableHeredity().eq( AonEnumUtils.getByte(expected.isEnableHeredity())))
				.and(p.withActive().eq( AonEnumUtils.getByte(expected.isActive())))
				// Booking
				.and(p.withOwner().eq( AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> a.getOwner())))
				.and(p.withExpirationDate().eq( AonDateUtils.toSql(AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> a.getExpirationDate().orElse(null)))))
				.and(p.withDomainManagement().eq(AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> AonEnumUtils.getByte(a.isDomainManagement()))))
				.and(p.withDisableDomainManagement().eq( AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> AonEnumUtils.getByte(a.isDisableDomainManagement()))))
				.and(p.withMaxDefinedUsers().eq( AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> a.getMaxDefinedUsers().orElse(null))))
				.and(p.withAonCustomer().eq( AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> a.getAonCustomer().orElse(null))))
				.and(p.withAonStatus().eq( AonObjectUtils.ifOptionalPresent(expected.getBooking(), a -> AonEnumUtils.getByte(a.getAonStatus()))))
				// Audit
				.and(p.withLastAccessDate().eq( AonDateUtils.toTimestamp(AonObjectUtils.ifOptionalPresent(expected.getAudit(), a -> a.getLastAccessDate().orElse(null)))))
				.and(p.withLastAccessUser().eq( AonObjectUtils.ifOptionalPresent(expected.getAudit(), a -> a.getLastAccessUser().orElse(null))))
				.and(p.withCreationUser().eq(AonObjectUtils.ifOptionalPresent(expected.getAudit(), a -> a.getCreationUser().orElse(null))))
				.and(p.withCreationDate().eq(AonDateUtils.toTimestamp(AonObjectUtils.ifOptionalPresent(expected.getAudit(), a -> a.getCreationDate().orElse(null)))))
				.and(p.withModificationUser().eq(AonObjectUtils.ifOptionalPresent(expected.getAudit(), a -> a.getModificationUser().orElse(null))))
				.and(p.withModificationDate().eq(AonDateUtils.toTimestamp(AonObjectUtils.ifOptionalPresent(expected.getAudit(), a -> a.getModificationDate().orElse(null)))))
				
			,b -> b.full()
		);
		assertTrue(optActual.isPresent(),"Domain not found!");
		Asserts.assertEqualsDomain(expected, optActual.get());
	}
	
	@Test()
	void denyReadOneTest() {
		try {
			ctx.denyRead();
			SecurityException e = assertThrows(SecurityException.class, () -> DomainDAO.get(ctx,p -> p.withName().eq( DOMAIN_NAME ), b -> b));
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
		} finally {
			ctx.allowRead();
		}
	}
	
	@Test
	void basicGetTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx, p -> p.withName().eq( DOMAIN_NAME ), b -> b);
		assertTrue(optDomain.isPresent());
		assertTrue(optDomain.get().getParent().isEmpty());
		assertTrue(optDomain.get().getBooking().isEmpty());
		assertTrue(optDomain.get().getAudit().isEmpty());
		assertTrue(optDomain.get().getCompany().isEmpty());
	}
	
	@Test
	void auditTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx, p -> p.withName().eq( DOMAIN_NAME ), b -> b.withAudit());
		assertTrue(optDomain.isPresent());
		assertTrue(optDomain.get().getAudit().isPresent());
		assertTrue(optDomain.get().getParent().isEmpty());
		assertTrue(optDomain.get().getBooking().isEmpty());
		assertTrue(optDomain.get().getCompany().isEmpty());
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

	@Test
	void companyGetTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx
			, p -> p.withName().eq( DOMAIN_NAME )
			, b -> b.withCompany().withBooking().withUsers()
			);
		assertTrue(optDomain.isPresent());
		assertTrue(optDomain.get().getCompany().isPresent());
	}

	@Test
	void configurationGetTest() {
		Optional<Domain> optDomain = DomainDAO.get(ctx
			, p -> p.withName().eq( DOMAIN_NAME )
			, b -> b.withCompany()
				.withBooking()
				.withUsers()
				.withConfiguration( c -> c.withAccountingConfiguration() )
			);
		assertTrue(optDomain.isPresent());
		assertTrue(optDomain.get().getConfiguration().isPresent());
		assertTrue(optDomain.get().getConfiguration().get().accounting().isPresent());
	}

	@Test
	void usersStreamTest() {
		DomainDAO.getList(ctx
			, p -> p.withId().gt( 0 )
			, b -> b.withUsers().withParentDomain().withBooking().withAudit()
			)
		.stream()
		.forEach(d -> {
			if (AonStringUtils.equals(d.getName(),DOMAIN_NAME)) {
				assertTrue(d.getUsers().isPresent());
				Asserts.assertNotEmpty(d.getUsers().get(), "Users");
				assertTrue(d.getUsers().get().stream().anyMatch(u -> AonStringUtils.equals( u.getLogin(), USER)));
			}
		});
	}
	
	@Test
	void saveDirtyFlagTes() {
		AonLogger originalLogger = ctx.getLoggerForTests();
		Level originalLevel = ctx.getLoggerLevelForTests();
		try {
			StringBuilder debugMessage = new StringBuilder(); 
			Logger thisLogger = Logger.getLogger(this.getClass().getName());
			setLoggerLevel(Level.FINEST);
			ctx.setLoggerForTests( new AonLogger(thisLogger, DOMAIN_NAME) {
				@Override
				public void debug(String msg) {
					super.debug(msg);
					debugMessage.append(msg);
				}
			});
			Domain domain = new Domain();
			DomainDAO.save(ctx, domain);
			assertEquals(AonError.NOT_DIRTY.format("Domain",domain.getId()), debugMessage.toString());
		} finally {
			ctx.setLoggerForTests( originalLogger );
			setLoggerLevel(originalLevel);
		}
	}
	
	@Test
	void saveValidationEmptyTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, (Domain) null));
		assertEquals(AonError.SAVE_EMPTY.getMessage(), e.getMessage());
	}
	
	@Test
	void saveValidationNameTest() {
		Domain domain = AonFaker.getDomain();
		
		domain.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.EMPTY_NAME.getMessage(), e.getMessage());
		
		domain.setName( AonStringUtils.repeat("A",DOMAIN.NAME.getDataType().length() + 1));
		e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.INVALID_LENGTH.format( "Nombre", DOMAIN.NAME.getDataType().length() ), e.getMessage());
	}
	
	@Test
	void saveValidationDescriptionTest() {
		Domain domain = AonFaker.getDomain();

		domain.setDescription(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.EMPTY_DESCRIPTION.getMessage(), e.getMessage());
		
		domain.setDescription( AonStringUtils.repeat("A",DOMAIN.DESCRIPTION.getDataType().length() + 1));
		e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.INVALID_LENGTH.format( "Descripci\u00F3n", DOMAIN.DESCRIPTION.getDataType().length() ), e.getMessage());
	}
	
	@Test
	void saveValidationBookingTest() {
		Domain domain = AonFaker.getDomain().setId(null);
		domain.setBooking(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.DOMAIN_NO_BOOKING_INFO.getMessage(), e.getMessage());
		
	}

	@Test
	void saveValidationOwnerTest() {
		Domain domain = AonFaker.getDomain().setId(null);
		if (!domain.getBooking().isPresent()) {
			domain.setBooking(AonFaker.getBooking());
		}
		Booking booking = domain.getBooking().get();
		booking.setOwner(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.DOMAIN_NO_OWNER.getMessage(), e.getMessage());
		
		booking.setOwner(AonStringUtils.repeat("A",DOMAIN.OWNER.getDataType().length() + 1));
		e = assertThrows(AonCoreException.class, () -> DomainDAO.save(ctx, domain));
		assertEquals(AonError.INVALID_LENGTH.format( "Creador", DOMAIN.OWNER.getDataType().length() ), e.getMessage());
	}
	
	@Test
	void saveLowerCaseNameTest() {
		String uuid = AonRandom.uuid(10);
		String lDomainName = uuid + "occam.aonsolutions.net";
		String domainName =  uuid + "OCCAM.AONSOLUTIONS.NET";
		Domain domain = AonFaker.getDomainStandalone()
			.setId(null)
			.setName(domainName);
		Domain saved = DomainDAO.save(ctx, domain);
		assertEquals(lDomainName, saved.getName());
	}

	@Test
	void saveTest() {
		Domain newDomain = AonFaker.getDomainStandalone().setId(null);
		assertDoesNotThrow(() -> DomainDAO.save(ctx, newDomain));
		Optional<Domain> savedDomain = DomainDAO.get(ctx, newDomain.getName() );
		assertTrue(savedDomain.isPresent());

		String description = "NEW DOMAIN DESCRIPTION";
		savedDomain.get().setDescription(description);
		assertDoesNotThrow(() -> DomainDAO.save(ctx, savedDomain.get()));
		Optional<Domain> updatedDomain = DomainDAO.get(ctx, newDomain.getName() );
		assertTrue(updatedDomain.isPresent());
		assertEquals(description, updatedDomain.get().getDescription());
		
		
	}
	
}
