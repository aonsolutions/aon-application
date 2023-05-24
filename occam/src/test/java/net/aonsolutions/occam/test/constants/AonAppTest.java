package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.AonApp;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AonAppTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(AonApp.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(AonApp.safeValueOf((byte) AonApp.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		AonApp dt = AonRandom.getAonApp();
		assertEquals(dt, AonApp.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(AonApp.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(AonApp.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(AonApp.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		AonApp dt = AonRandom.getAonApp();
		assertEquals(dt, AonApp.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(AonApp.safeValueOf( (String) null ).isEmpty());
	}
	
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(AonApp.safeValueOf( "" ).isEmpty());
	}
	
	@Test()
	void rightNameSafeValueTest() {
		AonApp dt = AonRandom.getAonApp();
		assertEquals(dt,AonApp.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(AonApp.values()).forEach(s -> assertNotNull(s.getDescription() ));
	}
	
}
