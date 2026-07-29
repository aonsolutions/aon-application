package com.esferalia.aon.occam.test.fiscal.mod130;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod130MarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	public void markAsCustomerAcceptedTest() {
		for (Mod130 model : MODEL130.getMod130s(getOccam()) ) {
			Mod130 mod130 = MODEL130.get(getOccam(), model.getId());
			MODEL130.markAsCustomerAccepted(getOccam(), mod130);
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, mod130Bis.getStatus(), "Status not CUSTOMER_ACCEPTED");
			assertNotNull(mod130Bis.getDeclarationResultType(), "Mod130. Tipo resultado NULL");
		}
	}
	
}
