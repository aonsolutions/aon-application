package net.aonsolutions.occam.test.config;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.api.metadata.GeozoneMetadata;
import net.aonsolutions.occam.api.metadata.GeozoneMetadata.GeozoneMetadataVisitor;
import net.aonsolutions.occam.dao.GeozoneDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.client.util.AonStringUtils;


@ExtendWith(TimingExtension.class)	
class GeozoneTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Geozone u = new Geozone();
		u.setId(1);
		assertTrue(u.isDirty());
		assertTrue(u.getDirtySet().contains( GeozoneMetadata.ID ));
	}
	
	@Test()
	void dirtyDomainTest() {
		Geozone u = new Geozone();
		u.setDomain(1);
		assertTrue(u.isDirty());
		assertTrue(u.getDirtySet().contains( GeozoneMetadata.DOMAIN));
	}

	@Test()
	void dirtyCodeTest() {
		Geozone u = new Geozone();
		u.setCode(AonRandom.string(10));
		assertTrue(u.isDirty());
		assertTrue(u.getDirtySet().contains( GeozoneMetadata.CODE));
	}

	@Test()
	void dirtyNameTest() {
		Geozone u = new Geozone();
		u.setName(AonRandom.string(10));
		assertTrue(u.isDirty());
		assertTrue(u.getDirtySet().contains( GeozoneMetadata.NAME));
	}

	@Test()
	void dirtySystemTest() {
		Geozone u = new Geozone();
		u.setSystem( true );
		assertTrue(u.isDirty());
		assertTrue(u.getDirtySet().contains( GeozoneMetadata.SYSTEM));
	}

	@Test()
	void dirtyMarkTrueTest() {
		Geozone d = new Geozone();
		d.setName( "dddddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Geozone d = new Geozone();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void metadataVisitorTest() {
		GeozoneMetadataVisitor<Boolean,GeozoneMetadata> visitor = new GeozoneMetadataVisitor<Boolean, GeozoneMetadata>() {
			@Override public Boolean visitId(GeozoneMetadata t) {return t == GeozoneMetadata.ID;}
			@Override public Boolean visitDomain(GeozoneMetadata t) {return t == GeozoneMetadata.DOMAIN;}
			@Override public Boolean visitName(GeozoneMetadata t) {return t == GeozoneMetadata.NAME;}
			@Override public Boolean visitCode(GeozoneMetadata t) {return t == GeozoneMetadata.CODE;}
			@Override public Boolean visitSystem(GeozoneMetadata t) {return t == GeozoneMetadata.SYSTEM;}
		}; 
		Arrays.stream(GeozoneMetadata.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));		
	}

	@Test()
	void selectedMarkTest() {
		Geozone d = new Geozone();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		Geozone d1 = new Geozone();
		Geozone d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Geozone();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}
	
	@Test
	void saveValidationEmptyTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> GeozoneDAO.save(ctx, (Geozone) null));
		assertEquals(AonError.SAVE_EMPTY.getMessage(), e.getMessage());
	}
	
	@Test
	void saveValidationEmptyDomainTest() {
		Geozone geozone = new Geozone();
		geozone.setName("dddd");
		AonCoreException e = assertThrows(AonCoreException.class, () -> GeozoneDAO.save(ctx, geozone));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}

	@Test
	void saveValidationNameTest() {
		Geozone geozone = AonFaker.getGeozone ();
		
		geozone.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> GeozoneDAO.save(ctx, geozone));
		assertEquals(AonError.EMPTY_NAME.getMessage(), e.getMessage());
		
		geozone.setName( AonStringUtils.repeat("A",GEOZONE.NAME.getDataType().length() + 1));
		e = assertThrows(AonCoreException.class, () -> GeozoneDAO.save(ctx, geozone));
		assertEquals(AonError.INVALID_LENGTH.format( "Nombre", GEOZONE.NAME.getDataType().length() ), e.getMessage());
	}
	
	@Test
	void saveValidationCodeTest() {
		Geozone geozone = AonFaker.getGeozone().setName("Pontevedra");
		
		geozone.setCode(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> GeozoneDAO.save(ctx, geozone));
		assertEquals(AonError.EMPTY_CODE.getMessage(), e.getMessage());
		
		geozone.setCode( AonStringUtils.repeat("A",GEOZONE.CODE.getDataType().length() + 1));
		e = assertThrows(AonCoreException.class, () -> GeozoneDAO.save(ctx, geozone));
		assertEquals(AonError.INVALID_LENGTH.format( "C\u00F3digo", GEOZONE.CODE.getDataType().length() ), e.getMessage());
	}
}
