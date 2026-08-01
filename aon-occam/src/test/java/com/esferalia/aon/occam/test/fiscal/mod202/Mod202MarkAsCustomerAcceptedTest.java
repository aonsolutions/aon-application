package com.esferalia.aon.occam.test.fiscal.mod202;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod202MarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	public void markAsCustomerAcceptedTest() {
		for (Mod202 model : MODEL202.getMod202s(getOccam()) ) {
			Mod202 mod202 = MODEL202.get(getOccam(), model.getId());
			MODEL202.markAsCustomerAccepted(getOccam(), mod202);
			Mod202 mod202Bis = MODEL202.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, mod202Bis.getStatus(), "Status not CUSTOMER_ACCEPTED");
			assertNotNull(mod202Bis.getDeclarationResultType(), "Mod202. Tipo resultado NULL");
		}
	}
	
}
