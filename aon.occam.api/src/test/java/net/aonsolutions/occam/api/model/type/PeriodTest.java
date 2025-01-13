package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class PeriodTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		Period m = AonRandom.getEnum(Period.class);
		assertSame(m , Period.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		Period m = AonRandom.getEnum(Period.class);
		assertTrue(Period.value( (Byte) null).isEmpty());
		assertTrue(Period.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(Period.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<Period> om = Period.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		Period m = AonRandom.getEnum(Period.class);
		assertTrue(Period.value( (Integer) null).isEmpty());
		assertTrue(Period.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(Period.value( Integer.MAX_VALUE).isEmpty());
		Optional<Period> om = Period.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@Test
	void testPeriodFlag() {
		for (Period p : Period.values()) {
			if (p.getDueMonth() - p.getStartMonth() == 0) {
				assertTrue(p.isMonthPeriod());
			} else if (p.getDueMonth() - p.getStartMonth() == 2) {
				assertTrue(p.isQuarterPeriod());
			} else {
				assertSame(p, Period.YEAR );
			}
		}
		
		assertTrue( Period.M01.isFirstPeriod() );
		assertTrue( Period.T1.isFirstPeriod() );
		assertFalse( Period.M02.isFirstPeriod() );
		assertFalse( Period.T2.isFirstPeriod() );
		
		assertTrue( Period.M12.isLastPeriod() );
		assertTrue( Period.T4.isLastPeriod() );
		assertFalse( Period.M02.isFirstPeriod() );
		assertFalse( Period.T2.isFirstPeriod() );
		
	}
	
	@Test
	void testGetByMonth() {
		assertSame(Period.M01, Period.getMonthlyPeriod(0).get());
		assertSame(Period.M03, Period.getMonthlyPeriod(2).get());
		assertSame(Period.M05, Period.getMonthlyPeriod(4).get());
		assertSame(Period.M08, Period.getMonthlyPeriod(7).get());
		assertSame(Period.M11, Period.getMonthlyPeriod(10).get());
		
		assertTrue(Period.getMonthlyPeriod(-1).isEmpty());
		assertTrue(Period.getMonthlyPeriod(12).isEmpty());
		
		assertSame(Period.T1, Period.getQuarterlyPeriod(0).get());
		assertSame(Period.T3, Period.getQuarterlyPeriod(6).get());
		
		assertTrue(Period.getQuarterlyPeriod(-1).isEmpty());
		assertTrue(Period.getQuarterlyPeriod(12).isEmpty());
		
	}

	@Test
	void testSemester() {
		for (Period p : Period.values()) {
			if (p.getDueMonth() <= 5) {
				assertTrue(p.isFirstSemester());
				assertFalse(p.isLastSemester());
			} else {
				assertFalse(p.isFirstSemester());
				assertTrue(p.isLastSemester());
			}

			if (p.getDueMonth() == 11 && p != Period.YEAR) {
				 assertTrue(p.isLastPeriod());
			} else {
				 assertFalse(p.isLastPeriod());
			}
		}
		
	}

}