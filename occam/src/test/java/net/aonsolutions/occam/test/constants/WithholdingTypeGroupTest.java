package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.WithholdingTypeGroup;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonEnumRandom;


@ExtendWith(TimingExtension.class)	
class WithholdingTypeGroupTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf((byte) WithholdingTypeGroup.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		WithholdingTypeGroup dt = AonEnumRandom.getWithholdingTypeGroup();
		assertEquals(dt, WithholdingTypeGroup.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		WithholdingTypeGroup dt = AonEnumRandom.getWithholdingTypeGroup();
		assertEquals(dt, WithholdingTypeGroup.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(WithholdingTypeGroup.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		WithholdingTypeGroup dt = AonEnumRandom.getWithholdingTypeGroup();
		assertEquals(dt,WithholdingTypeGroup.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void nameNotNullTest() {
		Arrays.stream(WithholdingTypeGroup.values()).forEach(s ->  {
			assertNotNull(s.getDescription() );	
		});
	}
	
}
