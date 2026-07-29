package com.esferalia.aon.occam.test.fiscal.mod111;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class Mod111MarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	public void testReopen() {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			MODEL111.markAsCustomerRejected(getOccam(), mod111, AonRandom.string(50, 20));
			Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod111Bis.getStatus(), "Status not CUSTOMER_REJECTED");
			assertNotNull(mod111Bis.getDeclarationResultType(), "Mod111. Tipo resultado NULL");
		}	
	}
	
}
