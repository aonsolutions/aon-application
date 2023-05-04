package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.AonStatus.AonStatusVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AonStatusTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(AonStatus.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(AonStatus.safeValueOf((byte) AonStatus.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		AonStatus dt = AonRandom.getAonStatus().get();
		assertEquals(dt, AonStatus.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(AonStatus.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(AonStatus.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(AonStatus.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		AonStatus dt = AonRandom.getAonStatus().get();
		assertEquals(dt, AonStatus.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(AonStatus.safeValueOf( (String) null ).isEmpty());
	}
	
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(AonStatus.safeValueOf( "" ).isEmpty());
	}
	
	@Test()
	void rightNameSafeValueTest() {
		AonStatus dt = AonRandom.getAonStatus().get();
		assertEquals(dt,AonStatus.safeValueOf( dt.toString() ).get());
	}
	
	@Test()
	void visitorTest() {
		AonStatusVisitor<Boolean,AonStatus> visitor = new AonStatusVisitor<Boolean,AonStatus>() {
			@Override public Boolean visitNonBillable(AonStatus t) {return t == AonStatus.NOT_BILLABLE;}
			@Override public Boolean visitBillable(AonStatus t) {return t == AonStatus.BILLABLE;}
		};
		Arrays.stream(AonStatus.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
