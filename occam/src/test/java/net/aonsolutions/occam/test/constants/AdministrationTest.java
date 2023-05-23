package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.Administration;
import net.aonsolutions.occam.api.constants.Administration.AdministrationVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AdministrationTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(Administration.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(Administration.safeValueOf((byte) Administration.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		Administration dt = AonRandom.getAdministration().get();
		assertEquals(dt, Administration.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(Administration.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(Administration.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(Administration.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		Administration dt = AonRandom.getAdministration().get();
		assertEquals(dt, Administration.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(Administration.safeValueOf( (String) null ).isEmpty());
	}
	
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(Administration.safeValueOf( "" ).isEmpty());
	}
	
	@Test()
	void rightNameSafeValueTest() {
		Administration dt = AonRandom.getAdministration().get();
		assertEquals(dt,Administration.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(Administration.values()).forEach(s -> assertNotNull(s.getDescription() ));
	}
	
	@Test()
	void visitorTest() {
		AdministrationVisitor<Boolean,Administration> visitor = new AdministrationVisitor<Boolean,Administration>() {
			@Override public Boolean visitAlava(Administration t) {return t == Administration.ALAVA;}
			@Override public Boolean visitBizkaia(Administration t) {return t == Administration.BIZKAIA;}
			@Override public Boolean visitGipuzkoa(Administration t) {return t == Administration.GIPUZKOA;}
			@Override public Boolean visitNavarra(Administration t) {return t == Administration.NAVARRA;}
			@Override public Boolean visitCommonTerritory(Administration t) {return t == Administration.COMMON_TERRITORY;}
			@Override public Boolean visitUnknown(Administration t) {return t == Administration.UNKNOWN;}
		};
		Arrays.stream(Administration.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
