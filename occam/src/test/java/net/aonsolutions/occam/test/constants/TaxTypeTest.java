package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.TaxType.TaxTypeVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class TaxTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(TaxType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(TaxType.safeValueOf((byte) TaxType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		TaxType dt = AonRandom.getTaxType();
		assertEquals(dt, TaxType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(TaxType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(TaxType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(TaxType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		TaxType dt = AonRandom.getTaxType();
		assertEquals(dt, TaxType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(TaxType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(TaxType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		TaxType dt = AonRandom.getTaxType();
		assertEquals(dt,TaxType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void nameNotNullTest() {
		Arrays.stream(TaxType.values()).forEach(s ->  {
			assertNotNull(s.getName() );	
		});
	}
	
	@Test()
	void visitorTest() {
		TaxTypeVisitor<Boolean,TaxType> visitor = new TaxTypeVisitor<Boolean,TaxType>() {
			@Override public Boolean visitUnknown(TaxType t) {return t == TaxType.UNKNOWN;}
			@Override public Boolean visitVat(TaxType t) {return t == TaxType.VAT;}
			@Override public Boolean visitRetention(TaxType t) {return t == TaxType.RETENTION;}
		};
		Arrays.stream(TaxType.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
