package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.occam.api.constants.TransactionType.TransactionTypeVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class TransactionTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(TransactionType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(TransactionType.safeValueOf((byte) TransactionType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		TransactionType dt = AonRandom.getTransactionType();
		assertEquals(dt, TransactionType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(TransactionType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(TransactionType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(TransactionType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		TransactionType dt = AonRandom.getTransactionType();
		assertEquals(dt, TransactionType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(TransactionType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(TransactionType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		TransactionType dt = AonRandom.getTransactionType();
		assertEquals(dt,TransactionType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(TransactionType.values()).forEach(s -> assertNotNull(s.getDescription() ));
	}
	
	@Test()
	void visitorTest() {
		TransactionTypeVisitor<Boolean,TransactionType> visitor = new TransactionTypeVisitor<Boolean,TransactionType>() {
			@Override public Boolean visitNational(TransactionType t) {return t == TransactionType.NATIONAL;}
			@Override public Boolean visitIntracommunity(TransactionType t) {return t == TransactionType.INTRACOMMUNITY;}
			@Override public Boolean visitExtracommunity(TransactionType t) {return t == TransactionType.EXTRACOMMUNITY;}
			@Override public Boolean visitCanCeuMel(TransactionType t) {return t == TransactionType.CAN_CEU_MEL;}
			@Override public Boolean visitOtherISP(TransactionType t) {return t == TransactionType.OTHER_ISP;}
		};

		Arrays.stream(TransactionType.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
