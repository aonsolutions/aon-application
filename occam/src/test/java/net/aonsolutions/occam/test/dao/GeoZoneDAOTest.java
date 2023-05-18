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
import net.aonsolutions.occam.api.config.GeoZone;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.GeoZoneDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.server.AonEnumUtils;


@ExtendWith(TimingExtension.class)	
class GeoZoneDAOTest extends AbstractOccamTest {
	
	private static final String GEOZONE_NAME = "Lugo";

	@Test()
	void selectOneTest() {
		Optional<GeoZone> geozone = GeoZoneDAO.get(ctx,p -> p.withName().eq( GEOZONE_NAME ), b -> b);
		assertTrue(geozone.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<GeoZone> geozone = GeoZoneDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ), b -> b);
		assertFalse(geozone.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<GeoZone> geozone = GeoZoneDAO.get(ctx,p -> p.withName().eq( GEOZONE_NAME ), b -> b);
		assertTrue(geozone.isPresent());
		assertFalse(geozone.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> GeoZoneDAO.get(ctx, null, b -> b));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> GeoZoneDAO.getStream(ctx,p -> p.withName().eq( GEOZONE_NAME ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectStreamTest() {
		Stream<GeoZone> domain = GeoZoneDAO.getStream(ctx
			,p -> p.withName().eq( GEOZONE_NAME )
			,b -> b
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = GeoZoneDAO.getStream(ctx, p -> p.withName().like("%a%"), b -> b)
			.limit(10)
			.count();
		int rows = AonRandom.getInt(0, (int) max);
		long count = GeoZoneDAO.getStream(ctx, p -> p.withName().like("%a%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<GeoZone> optGeoZone = GeoZoneDAO.get(ctx
			,p -> p.withName().eq( GEOZONE_NAME )
			,b -> b);
		assertTrue(optGeoZone.isPresent());
		GeoZone expected = optGeoZone.get();
		Optional<GeoZone> optActual = GeoZoneDAO.get(ctx
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
			SecurityException e = assertThrows(SecurityException.class, () -> GeoZoneDAO.get(ctx,p -> p.withName().eq( GEOZONE_NAME ), b -> b));
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
			GeoZone geozone = new GeoZone();
			GeoZoneDAO.save(ctx, geozone);
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
