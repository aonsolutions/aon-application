package net.aonsolutions.occam.test.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.RectificationType;
import net.aonsolutions.occam.api.constants.RectificationType.RectificationTypeVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class RectificationTypeTest extends AbstractOccamTest {

	@Test()
	void nullIndexSafeByteValueTest() {
		assertTrue(RectificationType.safeValueOf((Byte) null).isEmpty());
	}
	@Test()
	void wrongIndexSafeByteValueTest() {
		assertTrue(RectificationType.safeValueOf((byte) RectificationType.values().length).isEmpty());
	}
	@Test()
	void rightIndexSafeByteValueTest() {
		RectificationType dt = AonRandom.getRectificationType();
		assertEquals(dt, RectificationType.safeValueOf( dt.value() ).get());
	}

	@Test()
	void nullIndexSafeValueTest() {
		assertTrue(RectificationType.safeValueOf( (Integer) null).isEmpty());
	}
	@Test()
	void wrongMinIndexSafeValueTest() {
		assertTrue(RectificationType.safeValueOf(Integer.MIN_VALUE).isEmpty());
	}
	@Test()
	void wrongMaxIndexSafeValueTest() {
		assertTrue(RectificationType.safeValueOf(Integer.MAX_VALUE).isEmpty());
	}
	@Test()
	void rightIndexSafeValueTest() {
		RectificationType dt = AonRandom.getRectificationType();
		assertEquals(dt, RectificationType.safeValueOf( dt.ordinal() ).get());
	}
	
	@Test()
	void nullNameSafeValueTest() {
		assertTrue(RectificationType.safeValueOf( (String) null ).isEmpty());
	}
	@Test()
	void wrongNameSafeValueTest() {
		assertTrue(RectificationType.safeValueOf( "" ).isEmpty());
	}
	@Test()
	void rightNameSafeValueTest() {
		RectificationType dt = AonRandom.getRectificationType();
		assertEquals(dt,RectificationType.safeValueOf( dt.toString() ).get());
	}
	@Test()
	void descriptionNotNullTest() {
		Arrays.stream(RectificationType.values()).forEach(s -> assertNotNull(s.getDescription() ));
	}
	
	@Test()
	void visitorTest() {
		RectificationTypeVisitor<Boolean,RectificationType> visitor = new RectificationTypeVisitor<Boolean,RectificationType>() {
			@Override public Boolean visitNone(RectificationType t) {return t == RectificationType.NONE;}
			@Override public Boolean visitNormalRectifier(RectificationType t) {return t == RectificationType.NORMAL_RECTIFIER;}
			@Override public Boolean visitSpecialRectifier(RectificationType t) {return t == RectificationType.SPECIAL_RECTIFIER;}
			@Override public Boolean visitRectified(RectificationType t) {return t == RectificationType.RECTIFIED;}
		};

		Arrays.stream(RectificationType.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));
	}
	
}
