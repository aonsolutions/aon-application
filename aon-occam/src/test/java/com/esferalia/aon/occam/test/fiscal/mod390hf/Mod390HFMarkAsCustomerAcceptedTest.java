package com.esferalia.aon.occam.test.fiscal.mod390hf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

class Mod390HFMarkAsCustomerAcceptedTest extends AbstractOccamTest {
	
	@Test
	void markAsCustomerAcceptedTest() {
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod = MODEL390HF.get(getOccam(), model.getId());
			MODEL390HF.markAsCustomerAccepted(getOccam(), mod);
			Mod390HF modBis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(modBis);
			assertEquals(FiscalStatus.CUSTOMER_ACCEPTED, modBis.getStatus(), "Status not CUSTOMER_ACCEPTED");
			assertNotNull(modBis.getDeclarationResultType(), "Mod. Tipo resultado NULL");
		}
	}
	
}
