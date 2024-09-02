package com.esferalia.aon.occam.test.fiscal.mod131;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.test.AbstractOccamTest;

class Mod131MarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	void markAsCustomerAcceptedTest() {
		for (Mod131 model : MODEL131.getMod131s(getOccam()) ) {
			Mod131 mod131 = MODEL131.get(getOccam(), model.getId());
			MODEL131.markAsCustomerAccepted(getOccam(), mod131);
			Mod131 mod131Bis = MODEL131.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, mod131Bis.getStatus(),"Status not CUSTOMER_ACCEPTED");
			assertNotNull(mod131Bis.getDeclarationResultType(),"Mod131. Tipo resultado NULL");
		}
	}
	
}
