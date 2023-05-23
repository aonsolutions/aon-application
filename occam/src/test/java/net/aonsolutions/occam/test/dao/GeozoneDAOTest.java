package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.AonLogger;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.GeozoneDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.server.AonEnumUtils;


@ExtendWith(TimingExtension.class)	
class GeozoneDAOTest extends AbstractOccamTest {
	
	private static final String GEOZONE_NAME = "Lugo";

	@Test()
	void selectOneTest() {
		Optional<Geozone> geozone = GeozoneDAO.get(ctx,p -> p.withName().eq( GEOZONE_NAME ), b -> b);
		assertTrue(geozone.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<Geozone> geozone = GeozoneDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ), b -> b);
		assertFalse(geozone.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<Geozone> geozone = GeozoneDAO.get(ctx,p -> p.withName().eq( GEOZONE_NAME ), b -> b);
		assertTrue(geozone.isPresent());
		assertFalse(geozone.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> GeozoneDAO.get(ctx, null, b -> b));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> GeozoneDAO.getStream(ctx,p -> p.withName().eq( GEOZONE_NAME ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectStreamTest() {
		Stream<Geozone> domain = GeozoneDAO.getStream(ctx
			,p -> p.withName().eq( GEOZONE_NAME )
			,b -> b
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = GeozoneDAO.getStream(ctx, p -> p.withName().like("%a%"), b -> b)
			.limit(10)
			.count();
		int rows = AonRandom.getInt(0, (int) max);
		long count = GeozoneDAO.getStream(ctx, p -> p.withName().like("%a%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<Geozone> optGeoZone = GeozoneDAO.get(ctx
			,p -> p.withName().eq( GEOZONE_NAME )
			,b -> b);
		assertTrue(optGeoZone.isPresent());
		Geozone expected = optGeoZone.get();
		Optional<Geozone> optActual = GeozoneDAO.get(ctx
			,p -> p.withId().eq( expected.getId() )
				.and(p.withDomain().eq( expected.getDomain() ))	
				.and(p.withName().eq( expected.getName() )) 
				.and(p.withCode().eq( expected.getCode() ))
				.and(p.withSystem().eq( AonEnumUtils.getByte(expected.isSystem())))
			,b -> b
		);
		assertTrue(optActual.isPresent(),"Domain not found!");
		Asserts.assertEqualsGeoZone(expected, optActual.get());
	}
	
	@Test()
	void denyReadOneTest() {
		try {
			ctx.denyRead();
			SecurityException e = assertThrows(SecurityException.class, () -> GeozoneDAO.get(ctx,p -> p.withName().eq( GEOZONE_NAME ), b -> b));
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
		} finally {
			ctx.allowRead();
		}
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
			Geozone geozone = new Geozone();
			GeozoneDAO.save(ctx, geozone);
			assertEquals(AonError.NOT_DIRTY.format("GeoZone",geozone.getId()), debugMessage.toString());
		} finally {
			ctx.setLoggerForTests( originalLogger );
			setLoggerLevel(originalLevel);
		}
	}
	
//	@Test
//	void saveTest() {
//		Domain insertDomain = AonFaker.getDomain(true);
//		assertDoesNotThrow(() -> GeoZoneDAO.save(ctx, insertDomain));
//
//		Domain updateDomain = AonFaker.getDomain(true);
//		updateDomain.setId(1);
//		assertDoesNotThrow(() -> GeoZoneDAO.save(ctx, updateDomain));
//	}
	
}
