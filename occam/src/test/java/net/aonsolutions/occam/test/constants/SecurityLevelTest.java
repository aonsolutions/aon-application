package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class SecurityLevelTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(SecurityLevel.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(SecurityLevel.safeValueOf((byte) SecurityLevel.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		SecurityLevel dt = AonRandom.getSecurityLevel().get();
		assertEquals(dt, SecurityLevel.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(SecurityLevel.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(SecurityLevel.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(SecurityLevel.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		SecurityLevel dt = AonRandom.getSecurityLevel().get();
		assertEquals(dt, SecurityLevel.safeValueOf( dt.ordinal() ).get());
	}
	@Test()
	void nameNotNullTest() {
		Arrays.stream(SecurityLevel.values()).forEach(s ->  assertNotNull(s.getName() ));
	}
	
}
