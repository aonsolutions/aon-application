package com.esferalia.aon.occam.test.fiscal.mod123;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.test.AbstractOccamTest;

class Mod123MarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	void markAsCustomerAcceptedTest() {
		for (Mod123 model : MODEL123.getMod123s(getOccam()) ) {
			Mod123 mod123 = MODEL123.get(getOccam(), model.getId());
			MODEL123.markAsCustomerAccepted(getOccam(), mod123);
			Mod123 mod123Bis = MODEL123.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, mod123Bis.getStatus(), "Status not CUSTOMER_ACCEPTED");
			assertNotNull(mod123Bis.getDeclarationResultType(), "Mod123. Tipo resultado NULL");
		}
	}
	
}
