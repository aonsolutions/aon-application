package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.Collator;
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
	
	@Test()
	void selectByCodeTest() {
		Stream<Geozone> domain = GeozoneDAO.getStream(ctx
			,p -> p.withDomain().eq( DOMAIN_ID )
			,b -> b.orderByCode().limit(0, 25)
		);
		String[] codes = domain.map(g -> g.getCode()).toArray(String[]::new);
		assertsSorted(codes);
	}
	
	@Test()
	void selectByNameTest() {
		Stream<Geozone> domain = GeozoneDAO.getStream(ctx
			,p -> p.withDomain().eq( DOMAIN_ID )
			,b -> b.orderByName().limit(0, 5)
		);
		String[] names = domain.map(g -> g.getName()).toArray(String[]::new);
		assertsSorted(names);
	}

	@Test()
	void selectRandomTest() {
		Stream<Geozone> domain = GeozoneDAO.getStream(ctx
			,p -> p.withDomain().eq( DOMAIN_ID )
			,b -> b.orderByRandom().limit(0, 5)
		);
		assertTrue(domain.findAny().isPresent());
	}

	private void assertsSorted(String[] array) {
		final Collator instance = Collator.getInstance();
	    instance.setStrength(Collator.PRIMARY);
	    for (int i = 0; i < array.length - 1; ++i) {
	    	String current = array[i];
	    	String next = array[i + 1];
	    	String msg = "\"" + current + "\" > \"" + next +"\"";
	    	assertFalse(instance.compare(current, next) > 0, msg);
	    }
	}

	@Test
	void streamLimitTest() {
		long max = GeozoneDAO.getStream(ctx, p -> p.withName().like("%a%"), b -> b)
			.limit(10)
			.count();
		int rows = AonRandom.number(0, (int) max);
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
		Asserts.assertEqualsGeozone(expected, optActual.get());
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
