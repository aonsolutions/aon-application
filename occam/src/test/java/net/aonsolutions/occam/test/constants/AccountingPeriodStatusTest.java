package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.occam.api.constants.AccountingPeriodStatus.AccountingPeriodStatusVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AccountingPeriodStatusTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf((byte) AccountingPeriodStatus.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		AccountingPeriodStatus dt = AonRandom.getAccountingPeriodStatus();
		assertEquals(dt, AccountingPeriodStatus.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		AccountingPeriodStatus dt = AonRandom.getAccountingPeriodStatus();
		assertEquals(dt, AccountingPeriodStatus.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(AccountingPeriodStatus.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		AccountingPeriodStatus dt = AonRandom.getAccountingPeriodStatus();
		assertEquals(dt,AccountingPeriodStatus.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(AccountingPeriodStatus.values()).forEach(s ->  {
			assertNotNull(s.getDescription() );	
		});
	}
	
	@Test()
	void visitorTest() {
		AccountingPeriodStatusVisitor<Boolean,AccountingPeriodStatus> visitor = new AccountingPeriodStatusVisitor<Boolean,AccountingPeriodStatus>() {
			@Override public Boolean visitActive(AccountingPeriodStatus t) {return t == AccountingPeriodStatus.ACTIVE;}
			@Override public Boolean visitInactive(AccountingPeriodStatus t) {return t == AccountingPeriodStatus.INACTIVE;}
			@Override public Boolean visitOpening(AccountingPeriodStatus t) {return t == AccountingPeriodStatus.OPENING;}
			@Override public Boolean visitOperating(AccountingPeriodStatus t) {return t == AccountingPeriodStatus.OPERATING;}
			@Override public Boolean visitClosed(AccountingPeriodStatus t) {return t == AccountingPeriodStatus.CLOSED;}
		};
		Arrays.stream(AccountingPeriodStatus.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
