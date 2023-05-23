package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class ApplicationParameterTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		ApplicationParameter d = new ApplicationParameter();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		ApplicationParameter d = new ApplicationParameter();
		d.setDomain(1);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyNameTest() {
		ApplicationParameter d = new ApplicationParameter();
		d.setName(AonRandom.getAppParam().get());
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyValueTest() {
		ApplicationParameter d = new ApplicationParameter();
		d.setValue(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		ApplicationParameter d = new ApplicationParameter();
		d.setName( AppParam.ACC_DEFAULT_PAID_RET_ACC );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		ApplicationParameter d = new ApplicationParameter();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void equalsTest() {
		ApplicationParameter d1 = new ApplicationParameter();
		ApplicationParameter d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new ApplicationParameter();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}