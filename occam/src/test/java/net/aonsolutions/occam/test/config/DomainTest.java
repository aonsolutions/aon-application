package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.Occam;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class DomainTest extends AbstractOccamTest {

	@Test()
	void emptyFilterTest() {
		Occam occam = getOccam();
		assertThrows(IllegalArgumentException.class, () -> AON.getDomain(occam, null));
	}
	
}
