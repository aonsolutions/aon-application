package com.esferalia.aon.occam.test.fiscal.mod130;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.test.AbstractOccamTest;

class Mod130MarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	void markAsCustomerAcceptedTest() {
		for (Mod130 model : MODEL130.getMod130s(getOccam()) ) {
			Mod130 mod130 = MODEL130.get(getOccam(), model.getId());
			MODEL130.markAsCustomerAccepted(getOccam(), mod130);
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, mod130Bis.getStatus(),"Status not CUSTOMER_ACCEPTED");
			assertNotNull(mod130Bis.getDeclarationResultType(),"Mod130. Tipo resultado NULL");
		}
	}
	
}
