package net.aonsolutions.occam.test.invoice;



import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.Occam;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class GetInvoicesTest extends AbstractOccamTest {

	@Test()
	void emptyFilterTest() {
		Occam occam = getOccam();
		assertThrows(IllegalArgumentException.class, () -> AON.getInvoices(occam, null));
	}

	@Test
	void emptyStreamTest() {
		Occam occam = getOccam();
		assertFalse(AON.getInvoices(occam, p ->p.getIdProperty().eq(1).and(p.getIdProperty().eq(2)) ).findAny().isPresent());
	}
	
	@Test
	void notEmptyStreamTest() {
		Occam occam = getOccam();
		assertTrue(AON.getInvoices(occam, p -> p.getIdProperty().gt(1)).findAny().isPresent());
	}
	
//	@Test
//	void streamLimitTest() {
//		Occam occam = getOccam();
//		int rows = 6;
//		long count = AON.getInvoices(occam, 
//			p -> p.getDomainProperty().eq(occam.getDomain())
//				.limit(0, rows))
//		.count();
//		assertEquals(count, rows, "Limit not working" );
//	}
	
}
