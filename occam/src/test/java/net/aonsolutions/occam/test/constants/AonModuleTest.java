package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.AonModule;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AonModuleTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(AonModule.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(AonModule.safeValueOf((byte) AonModule.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		AonModule dt = AonRandom.getAonModule();
		assertEquals(dt, AonModule.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(AonModule.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(AonModule.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(AonModule.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		AonModule dt = AonRandom.getAonModule();
		assertEquals(dt, AonModule.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(AonModule.safeValueOf( (String) null ).isEmpty());
	}
	
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(AonModule.safeValueOf( "" ).isEmpty());
	}
	
	@Test()
	void rightNameSafeValueTest() {
		AonModule dt = AonRandom.getAonModule();
		assertEquals(dt,AonModule.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(AonModule.values()).forEach(s -> assertNotNull(s.getName() ));
	}
	
}
