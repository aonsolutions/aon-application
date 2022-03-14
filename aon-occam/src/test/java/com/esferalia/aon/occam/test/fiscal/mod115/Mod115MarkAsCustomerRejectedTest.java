package com.esferalia.aon.occam.test.fiscal.mod115;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class Mod115MarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	public void testReopen() {
		for (Mod115 model : MODEL115.getMod115s(getOccam()) ) {
			Mod115 mod115 = MODEL115.get(getOccam(), model.getId());
			MODEL115.markAsCustomerRejected(getOccam(), mod115, AonRandom.string(50, 20));
			Mod115 mod115Bis = MODEL115.get(getOccam(), model.getId());
			assertEquals("Status not CUSTOMER_REJECTED", FiscalStatus.CUSTOMER_REJECTED, mod115Bis.getStatus());
			assertNotNull("Mod115. Tipo resultado NULL",mod115Bis.getDeclarationResultType());
		}
	}
	
}
