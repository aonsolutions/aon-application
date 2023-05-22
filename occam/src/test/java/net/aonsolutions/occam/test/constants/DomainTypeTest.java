package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.constants.DomainType.DomainTypeVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class DomainTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(DomainType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(DomainType.safeValueOf((byte) DomainType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		DomainType dt = AonRandom.getDomainType().get();
		assertEquals(dt, DomainType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(DomainType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(DomainType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(DomainType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		DomainType dt = AonRandom.getDomainType().get();
		assertEquals(dt, DomainType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(DomainType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(DomainType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		DomainType dt = AonRandom.getDomainType().get();
		assertEquals(dt,DomainType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(DomainType.values()).forEach(s -> assertNotNull(s.getName() ));
	}
	
	@Test()
	void visitorTest() {
		DomainTypeVisitor<Boolean,DomainType> visitor = new DomainTypeVisitor<Boolean,DomainType>() {
			@Override public Boolean visitEnterprise(DomainType t) {return t == DomainType.ENTERPRISE;}
			@Override public Boolean visitConsultancy(DomainType t) {return t == DomainType.CONSULTANCY;}
			@Override public Boolean visitGarage(DomainType t) {return t == DomainType.GARAGE;}
			@Override public Boolean visitAcademy(DomainType t) {return t == DomainType.ACADEMY;}
			@Override public Boolean visitHotel(DomainType t) {return t == DomainType.HOTEL;}
			@Override public Boolean visitAdmin(DomainType t) {return t == DomainType.ADMIN;}
			@Override public Boolean visitOffice(DomainType t) {return t == DomainType.OFFICE;}
			@Override public Boolean visitGeneric(DomainType t) {return t == DomainType.GENERIC;}
			@Override public Boolean visitCommerce(DomainType t) {return t == DomainType.COMMERCE;}
			@Override public Boolean visitKitDigital(DomainType t) {return t == DomainType.KIT_DIGITAL;}
		};
		Arrays.stream(DomainType.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
