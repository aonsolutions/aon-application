package com.esferalia.aon.occam.test.fiscal.mod390hf;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

class Mod390HFMarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	void testMarkAsCustomerCheck() {
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod = MODEL390HF.get(getOccam(), model.getId());
			mod = MODEL390HF.initializeForFinish(getOccam(), mod);
			double result0 = mod.getDeclarationResult();
			assertNotNull(mod.getDeclarationResultType(), "Mod390HF. Tipo resultado NULL");
			boolean finance = mod.getDeclarationResultType().mustCreateFinance(); 
			MODEL390HF.markAsCustomerCheck(getOccam(), mod);
			Mod390HF modBis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(modBis);			
			assertEquals(FiscalStatus.CUSTOMER_CHECK, modBis.getStatus(), "Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod390HF. Resultado no coincide."
					, result0
					, modBis.getDeclarationResult());
			assertNotNull(modBis.getDeclarationResultType(), "Mod390HF. Tipo resultado NULL");
			if (finance) {
				assertNotNull(modBis.getFinance(), "Mod390HF. Finance NULL");	
			} else {
				assertNull(modBis.getFinance(), "Mod390HF. Finance NOT NULL");
			}
		}
	}
	
}
