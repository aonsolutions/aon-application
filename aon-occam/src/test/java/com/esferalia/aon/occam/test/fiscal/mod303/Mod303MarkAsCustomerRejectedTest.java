package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod303MarkAsCustomerRejectedTest extends Mod303AbstractTest {
	
	@Test
	public void testCustomerRejected() {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			MODEL303.markAsCustomerRejected(getOccam(), mod303, AonRandom.string(50, 20));
			Mod303 mod303Bis = MODEL303.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303Bis);			
			assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod303Bis.getStatus(), "Status not CUSTOMER_REJECTED");
			assertNotNull(mod303Bis.getDeclarationResultType(), "Mod303. Tipo resultado NULL");
		}
	}
	
}
