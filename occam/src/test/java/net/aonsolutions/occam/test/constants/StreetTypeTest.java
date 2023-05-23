package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class StreetTypeTest extends AbstractOccamTest {

	@Test()
	void rightIndexSafeByteValueTest() {
		StreetType dt = AonRandom.getStreetType().get();
		assertEquals(dt, StreetType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(StreetType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(StreetType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(StreetType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		StreetType dt = AonRandom.getStreetType().get();
		assertEquals(dt, StreetType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(StreetType.safeValueOf( (String) null ).isEmpty());
	}
	
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(StreetType.safeValueOf( "" ).isEmpty());
	}
	
	@Test()
	void rightNameSafeValueTest() {
		StreetType dt = AonRandom.getStreetType().get();
		assertEquals(dt,StreetType.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(StreetType.values()).forEach(s -> assertNotNull( s.name() ));
	}
	
}
