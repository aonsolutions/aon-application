package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.constants.WithholdingType.WithholdingTypeVisitor;
import net.aonsolutions.occam.api.constants.WithholdingTypeGroup;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class WithholdingTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(WithholdingType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(WithholdingType.safeValueOf((byte) WithholdingType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		WithholdingType dt = AonRandom.getWithholdingType();
		assertEquals(dt, WithholdingType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(WithholdingType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(WithholdingType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(WithholdingType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		WithholdingType dt = AonRandom.getWithholdingType();
		assertEquals(dt, WithholdingType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(WithholdingType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(WithholdingType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		WithholdingType dt = AonRandom.getWithholdingType();
		assertEquals(dt,WithholdingType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(WithholdingType.values()).forEach(s ->  {
			assertNotNull(s.getDescription() );	
			assertNotNull(s.getAbbreviatedDescription() );
		});
	}
	
	@Test()
	void groupNotNullTest() {
		Arrays.stream(WithholdingType.values()).forEach(s ->  {
			assertNotNull(s.getGroup() );	
		});
	}

	@Test()
	void visitorTest() {
		WithholdingTypeVisitor<Boolean,WithholdingType> visitor = new WithholdingTypeVisitor<Boolean,WithholdingType>() {
			@Override public Boolean visitProfessional(WithholdingType t) {return t == WithholdingType.PROFESSIONAL;}
			@Override public Boolean visitRenting(WithholdingType t) {return t == WithholdingType.RENTING;}
			@Override public Boolean visitMovableCapital(WithholdingType t) {return t == WithholdingType.MOVABLE_CAPITAL;}
			@Override public Boolean visitFarmer(WithholdingType t) {return t == WithholdingType.FARMER;}
			@Override public Boolean visitTransportOperator(WithholdingType t) {return t == WithholdingType.TRANSPORT_OPERATOR;}
			@Override public Boolean visitM190G02(WithholdingType t) {return t == WithholdingType.M190_G_02;}
			@Override public Boolean visitM190G03(WithholdingType t) {return t == WithholdingType.M190_G_03;}
			@Override public Boolean visitM190H02(WithholdingType t) {return t == WithholdingType.M190_H_02;}
			@Override public Boolean visitM190H03(WithholdingType t) {return t == WithholdingType.M190_H_03;}
			@Override public Boolean visitM190I01(WithholdingType t) {return t == WithholdingType.M190_I_01;}
			@Override public Boolean visitM190I02(WithholdingType t) {return t == WithholdingType.M190_I_02;}
			@Override public Boolean visitM190J(WithholdingType t) {return t == WithholdingType.M190_J;}
			@Override public Boolean visitM190K01(WithholdingType t) {return t == WithholdingType.M190_K_01;}
			@Override public Boolean visitM190K03(WithholdingType t) {return t == WithholdingType.M190_K_03;}
			@Override public Boolean visitM190K02(WithholdingType t) {return t == WithholdingType.M190_K_02;}
			@Override public Boolean visitM193C1(WithholdingType t) {return t == WithholdingType.M193_C1;}
			@Override public Boolean visitM193C2(WithholdingType t) {return t == WithholdingType.M193_C2;}
			@Override public Boolean visitM193C3(WithholdingType t) {return t == WithholdingType.M193_C3;}
			@Override public Boolean visitM190F01(WithholdingType t) {return t == WithholdingType.M190_F_01;}
			@Override public Boolean visitM190F021(WithholdingType t) {return t == WithholdingType.M190_F_02_1;}
			@Override public Boolean visitM190F022(WithholdingType t) {return t == WithholdingType.M190_F_02_2;}

		};
		Arrays.stream(WithholdingType.values())
			.forEach( dt -> assertTrue(dt.visit(visitor, dt), () -> dt.getDescription()));
	}

	@Test()
	void typesNotNullTest() {
		Arrays.stream(WithholdingTypeGroup.values())
			.forEach( wtg -> assertNotNull(WithholdingType.getTypes(wtg) )); 
	}

	@Test()
	void valueTypesNotNullTest() {
		Arrays.stream(WithholdingTypeGroup.values())
			.forEach( wtg -> assertNotNull(WithholdingType.getValueTypes(wtg) )); 
	}
}
