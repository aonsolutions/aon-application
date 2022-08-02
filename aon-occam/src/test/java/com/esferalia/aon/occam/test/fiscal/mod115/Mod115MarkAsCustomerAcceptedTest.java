package com.esferalia.aon.occam.test.fiscal.mod115;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod115MarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	public void markAsCustomerAcceptedTest() {
		for (Mod115 model : MODEL115.getMod115s(getOccam()) ) {
			Mod115 mod115 = MODEL115.get(getOccam(), model.getId());
			MODEL115.markAsCustomerAccepted(getOccam(), mod115);
			Mod115 mod115Bis = MODEL115.get(getOccam(), model.getId());
			assertEquals("Status not CUSTOMER_ACCEPTED", FiscalStatus.CUSTOMER_ACCEPTED, mod115Bis.getStatus());
			assertNotNull("Mod115. Tipo resultado NULL",mod115Bis.getDeclarationResultType());
		}
	}
	
}
