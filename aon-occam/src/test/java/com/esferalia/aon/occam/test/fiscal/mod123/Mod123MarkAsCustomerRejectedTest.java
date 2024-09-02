package com.esferalia.aon.occam.test.fiscal.mod123;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

class Mod123MarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	void testReopen() {
		for (Mod123 model : MODEL123.getMod123s(getOccam()) ) {
			Mod123 mod123 = MODEL123.get(getOccam(), model.getId());
			MODEL123.markAsCustomerRejected(getOccam(), mod123, AonRandom.string(50, 20));
			Mod123 mod123Bis = MODEL123.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod123Bis.getStatus(), "Status not CUSTOMER_REJECTED");
			assertNotNull(mod123Bis.getDeclarationResultType(), "Mod123. Tipo resultado NULL");
		}
	}
	
}
