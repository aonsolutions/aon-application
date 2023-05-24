package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.AonLanguage;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AonLanguageTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(AonLanguage.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(AonLanguage.safeValueOf((byte) AonLanguage.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		AonLanguage dt = AonRandom.getAonLanguage();
		assertEquals(dt, AonLanguage.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(AonLanguage.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(AonLanguage.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(AonLanguage.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		AonLanguage dt = AonRandom.getAonLanguage();
		assertEquals(dt, AonLanguage.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(AonLanguage.safeValueOf( (String) null ).isEmpty());
	}
	
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(AonLanguage.safeValueOf( "" ).isEmpty());
	}
	
	@Test()
	void rightNameSafeValueTest() {
		AonLanguage dt = AonRandom.getAonLanguage();
		assertEquals(dt,AonLanguage.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(AonLanguage.values()).forEach(s -> assertNotNull(s.getLanguage() ));
	}
	
}
