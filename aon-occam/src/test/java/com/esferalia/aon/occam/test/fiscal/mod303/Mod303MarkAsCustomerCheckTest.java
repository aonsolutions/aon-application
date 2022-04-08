package com.esferalia.aon.occam.test.fiscal.mod303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod303MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			mod303 = MODEL303.initializeForFinish(getOccam(), mod303);
			double result0 = mod303.getDeclarationResult();
			assertNotNull("Mod303. Tipo resultado NULL",mod303.getDeclarationResultType());
			boolean finance = mod303.getDeclarationResultType().mustCreateFinance(); 
			MODEL303.markAsCustomerCheck(getOccam(), mod303);
			Mod303 mod303Bis = MODEL303.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303Bis);			
			assertEquals("Status not CUSTOMER_CHECK", FiscalStatus.CUSTOMER_CHECK, mod303Bis.getStatus());
			Asserts.assertEqualsDouble("Mod303. Resultado no coincide."
					, result0
					, mod303Bis.getDeclarationResult());
			assertNotNull("Mod303. Tipo resultado NULL",mod303Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod303. Finance NULL",mod303Bis.getFinance());	
			} else {
				assertNull("Mod303. Finance NOT NULL",mod303Bis.getFinance());
			}
		}
	}
	
}
