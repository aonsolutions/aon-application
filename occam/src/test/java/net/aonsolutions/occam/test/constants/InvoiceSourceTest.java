package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.constants.InvoiceSource.InvoiceSourceVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonEnumRandom;


@ExtendWith(TimingExtension.class)	
class InvoiceSourceTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(InvoiceSource.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(InvoiceSource.safeValueOf((byte) InvoiceSource.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		InvoiceSource dt = AonEnumRandom.getInvoiceSource();
		assertEquals(dt, InvoiceSource.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(InvoiceSource.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(InvoiceSource.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(InvoiceSource.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		InvoiceSource dt = AonEnumRandom.getInvoiceSource();
		assertEquals(dt, InvoiceSource.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(InvoiceSource.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(InvoiceSource.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		InvoiceSource dt = AonEnumRandom.getInvoiceSource();
		assertEquals(dt,InvoiceSource.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(InvoiceSource.values()).forEach(s ->  {
			assertNotNull(s.getDescription() );	
		});
	}
	
	@Test()
	void visitorTest() {
		InvoiceSourceVisitor<Boolean,InvoiceSource> visitor = new InvoiceSourceVisitor<Boolean,InvoiceSource>() {
			@Override public Boolean visitDirectExpense(InvoiceSource t) {return t == InvoiceSource.DIRECT_EXPENSE;}
			@Override public Boolean visitPurchase(InvoiceSource t) {return t == InvoiceSource.PURCHASE;}
			@Override public Boolean visitSales(InvoiceSource t) {return t == InvoiceSource.SALES;}
			@Override public Boolean visitDelivery(InvoiceSource t) {return t == InvoiceSource.DELIVERY;}
			@Override public Boolean visitIncome(InvoiceSource t) {return t == InvoiceSource.INCOME;}
			@Override public Boolean visitFee(InvoiceSource t) {return t == InvoiceSource.FEE;}
			@Override public Boolean visitAccount(InvoiceSource t) {return t == InvoiceSource.ACCOUNT;}
			@Override public Boolean visitDirectInvoice(InvoiceSource t) {return t == InvoiceSource.DIRECT_INVOICE;}
			@Override public Boolean visitOffer(InvoiceSource t) {return t == InvoiceSource.OFFER;}
			@Override public Boolean visitReservation(InvoiceSource t) {return t == InvoiceSource.RESERVATION;}
			@Override public Boolean visitTedi(InvoiceSource t) {return t == InvoiceSource.TEDI;}

		};
		Arrays.stream(InvoiceSource.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
