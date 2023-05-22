package net.aonsolutions.occam.test.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;

@ExtendWith(TimingExtension.class)	
class AonCoreExceptionTest extends AbstractOccamTest {
	
	@Test()
	void instanceTest() {
		AonCoreException a = new AonCoreException();
		assertNull(a.getMessage());
		
		AonCoreException b = new AonCoreException(a);
		assertEquals(a, b.getCause());
	}
}
