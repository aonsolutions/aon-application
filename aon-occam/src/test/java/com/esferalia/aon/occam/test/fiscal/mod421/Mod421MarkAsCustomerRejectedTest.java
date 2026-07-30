package com.esferalia.aon.occam.test.fiscal.mod421;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod421MarkAsCustomerRejectedTest extends Mod421AbstractTest {
	
	@Test
	public void testCustomerRejected() {
		for (Mod421 model : MODEL421.getMod421s(getOccam()) ) {
			Mod421 mod421 = MODEL421.get(getOccam(), model.getId());
			MODEL421.markAsCustomerRejected(getOccam(), mod421, AonRandom.string(50, 20));
			Mod421 mod421Bis = MODEL421.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod421Bis);			
			assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod421Bis.getStatus(), "Status not CUSTOMER_REJECTED");
			assertNotNull(mod421Bis.getDeclarationResultType(), "Mod421. Tipo resultado NULL");
		}
	}
	
}
