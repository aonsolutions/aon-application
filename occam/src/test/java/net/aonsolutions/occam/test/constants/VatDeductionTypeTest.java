package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonEnumRandom;


@ExtendWith(TimingExtension.class)	
class VatDeductionTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(VatDeductionType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(VatDeductionType.safeValueOf((byte) VatDeductionType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		VatDeductionType dt = AonEnumRandom.getVatDeductionType();
		assertEquals(dt, VatDeductionType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(VatDeductionType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(VatDeductionType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(VatDeductionType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		VatDeductionType dt = AonEnumRandom.getVatDeductionType();
		assertEquals(dt, VatDeductionType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(VatDeductionType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(VatDeductionType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		VatDeductionType dt = AonEnumRandom.getVatDeductionType();
		assertEquals(dt,VatDeductionType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void nameNotNullTest() {
		Arrays.stream(VatDeductionType.values()).forEach(s ->  {
			assertNotNull(s.getName() );	
			assertNotNull(s.getAbbr() );
		});
	}
	
}
