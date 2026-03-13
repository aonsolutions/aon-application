package com.esferalia.aon.occam.test.fiscal.mod421;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
			assertEquals("Status not CUSTOMER_REJECTED", FiscalStatus.CUSTOMER_REJECTED, mod421Bis.getStatus());
			assertNotNull("Mod421. Tipo resultado NULL",mod421Bis.getDeclarationResultType());
		}
	}
	
}
