package com.esferalia.aon.occam.test.fiscal.mod202;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class Mod202MarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	public void testReopen() {
		for (Mod202 model : MODEL202.getMod202s(getOccam()) ) {
			Mod202 mod202 = MODEL202.get(getOccam(), model.getId());
			MODEL202.markAsCustomerRejected(getOccam(), mod202, AonRandom.string(50, 20));
			Mod202 mod202Bis = MODEL202.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod202Bis.getStatus(), "Status not CUSTOMER_REJECTED");
			assertNotNull(mod202Bis.getDeclarationResultType(), "Mod202. Tipo resultado NULL");
		}	
	}
	
}
