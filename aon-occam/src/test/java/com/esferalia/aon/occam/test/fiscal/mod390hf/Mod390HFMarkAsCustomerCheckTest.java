package com.esferalia.aon.occam.test.fiscal.mod390hf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod390HFMarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod = MODEL390HF.get(getOccam(), model.getId());
			mod = MODEL390HF.initializeForFinish(getOccam(), mod);
			double result0 = mod.getDeclarationResult();
			assertNotNull("Mod390HF. Tipo resultado NULL",mod.getDeclarationResultType());
			boolean finance = mod.getDeclarationResultType().mustCreateFinance(); 
			MODEL390HF.markAsCustomerCheck(getOccam(), mod);
			Mod390HF modBis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(modBis);			
			assertEquals("Status not CUSTOMER_CHECK", FiscalStatus.CUSTOMER_CHECK, modBis.getStatus());
			Asserts.assertEqualsDouble("Mod390HF. Resultado no coincide."
					, result0
					, modBis.getDeclarationResult());
			assertNotNull("Mod390HF. Tipo resultado NULL",modBis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod390HF. Finance NULL",modBis.getFinance());	
			} else {
				assertNull("Mod390HF. Finance NOT NULL",modBis.getFinance());
			}
		}
	}
	
}
