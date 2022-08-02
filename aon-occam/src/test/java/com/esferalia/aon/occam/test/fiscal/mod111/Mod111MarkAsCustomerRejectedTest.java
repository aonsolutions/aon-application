package com.esferalia.aon.occam.test.fiscal.mod111;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
			assertEquals("Status not CUSTOMER_REJECTED", FiscalStatus.CUSTOMER_REJECTED, mod111Bis.getStatus());
			assertNotNull("Mod111. Tipo resultado NULL",mod111Bis.getDeclarationResultType());
		}	
	}
	
}
