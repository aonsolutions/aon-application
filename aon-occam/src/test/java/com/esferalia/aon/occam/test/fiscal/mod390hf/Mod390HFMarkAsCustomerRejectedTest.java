package com.esferalia.aon.occam.test.fiscal.mod390hf;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

class Mod390HFMarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	void testCustomerRejected() {
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod303 = MODEL390HF.get(getOccam(), model.getId());
			MODEL390HF.markAsCustomerRejected(getOccam(), mod303, AonRandom.string(50, 20));
			Mod390HF mod303Bis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303Bis);			
			assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod303Bis.getStatus(), "Status not CUSTOMER_REJECTED");
			assertNotNull(mod303Bis.getDeclarationResultType(), "Mod390HF. Tipo resultado NULL");
		}
	}
	
}
