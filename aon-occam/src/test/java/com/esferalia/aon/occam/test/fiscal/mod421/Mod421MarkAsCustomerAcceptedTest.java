package com.esferalia.aon.occam.test.fiscal.mod421;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod421MarkAsCustomerAcceptedTest extends Mod421AbstractTest {
	
	@Test
	public void markAsCustomerAcceptedTest() {
		for (Mod421 model : MODEL421.getMod421s(getOccam()) ) {
			Mod421 mod421 = MODEL421.get(getOccam(), model.getId());
			MODEL421.markAsCustomerAccepted(getOccam(), mod421);
			Mod421 mod421Bis = MODEL421.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod421Bis);
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, mod421Bis.getStatus(), "Status not CUSTOMER_ACCEPTED");
			assertNotNull(mod421Bis.getDeclarationResultType(), "Mod421. Tipo resultado NULL");
		}
	}
	
}
