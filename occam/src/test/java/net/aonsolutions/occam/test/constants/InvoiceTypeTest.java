package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonEnumRandom;


@ExtendWith(TimingExtension.class)	
class InvoiceTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(InvoiceType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(InvoiceType.safeValueOf((byte) InvoiceType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		InvoiceType dt = AonEnumRandom.getInvoiceType();
		assertEquals(dt, InvoiceType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(InvoiceType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(InvoiceType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(InvoiceType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		InvoiceType dt = AonEnumRandom.getInvoiceType();
		assertEquals(dt, InvoiceType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(InvoiceType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(InvoiceType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		InvoiceType dt = AonEnumRandom.getInvoiceType();
		assertEquals(dt,InvoiceType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(InvoiceType.values()).forEach(s ->  {
			assertNotNull(s.getDescription() );	
			assertNotNull(s.getAbbrDescription() );
		});
	}
	
	@Test()
	void visitorTest() {
		InvoiceTypeVisitor<Boolean,InvoiceType> visitor = new InvoiceTypeVisitor<Boolean,InvoiceType>() {
			@Override public Boolean visitPurchase(InvoiceType t) {return t == InvoiceType.PURCHASE;}
			@Override public Boolean visitSales(InvoiceType t) {return t == InvoiceType.SALES;}
			@Override public Boolean visitExpenses(InvoiceType t) {return t == InvoiceType.EXPENSES;}
			@Override public Boolean visitUndeductible(InvoiceType t) {return t == InvoiceType.UNDEDUCTIBLE;}
		};
		Arrays.stream(InvoiceType.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
