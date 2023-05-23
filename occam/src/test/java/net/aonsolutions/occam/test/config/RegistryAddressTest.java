package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.RegistryAddress;
import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class RegistryAddressTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		RegistryAddress d = new RegistryAddress();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		RegistryAddress d = new RegistryAddress();
		d.setDomain(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyRegistryTest() {
		RegistryAddress d = new RegistryAddress();
		d.setRegistry(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMainTest() {
		RegistryAddress d = new RegistryAddress().setMain(true);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyRecipientTest() {
		RegistryAddress d = new RegistryAddress();
		d.setRecipient(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyStreetTypeTest() {
		RegistryAddress d = new RegistryAddress();
		d.setStreetType(StreetType.CIRCU);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyAddressTest() {
		RegistryAddress d = new RegistryAddress();
		d.setAddress(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyNumberTest() {
		RegistryAddress d = new RegistryAddress();
		d.setNumber(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyAddress2Test() {
		RegistryAddress d = new RegistryAddress();
		d.setAddress2(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyAddress3Test() {
		RegistryAddress d = new RegistryAddress();
		d.setAddress3(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyGeozoneTest() {
		RegistryAddress d = new RegistryAddress();
		d.setGeozone(AonFaker.getGeoZone());
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyParentGeozoneTest() {
		RegistryAddress d = new RegistryAddress();
		d.setParentGeozone(AonFaker.getGeoZone());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyZipTest() {
		RegistryAddress d = new RegistryAddress();
		d.setZip(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyCityTest() {
		RegistryAddress d = new RegistryAddress();
		d.setCity(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyAliasTest() {
		RegistryAddress d = new RegistryAddress();
		d.setAlias(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyMunicipalityCodeTest() {
		RegistryAddress d = new RegistryAddress();
		d.setMunicipalityCode(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		RegistryAddress d = new RegistryAddress();
		d.setStreetType( StreetType.ATZUC );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		RegistryAddress d = new RegistryAddress();
		d.setId( null );
		d.setAddress( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		RegistryAddress d = new RegistryAddress();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		RegistryAddress d1 = new RegistryAddress();
		RegistryAddress d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new RegistryAddress();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}