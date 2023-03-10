package com.esferalia.aon.occam.test.fiscal.mod130;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class Mod130MarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	public void testReopen() {
		for (Mod130 model : MODEL130.getMod130s(getOccam()) ) {
			Mod130 mod130 = MODEL130.get(getOccam(), model.getId());
			MODEL130.markAsCustomerRejected(getOccam(), mod130, AonRandom.string(50, 20));
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals("Status not CUSTOMER_REJECTED", FiscalStatus.CUSTOMER_REJECTED, mod130Bis.getStatus());
			assertNotNull("Mod130. Tipo resultado NULL",mod130Bis.getDeclarationResultType());
		}	
	}
	
}
