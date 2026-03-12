package com.esferalia.aon.occam.test.fiscal.mod421;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod421MarkAsCustomerCheckTest extends Mod421AbstractTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod421 model : MODEL421.getMod421s(getOccam()) ) {
			Mod421 mod421 = MODEL421.get(getOccam(), model.getId());
			mod421 = MODEL421.initializeForFinish(getOccam(), mod421);
			double result0 = mod421.getDeclarationResult();
			assertNotNull("Mod421. Tipo resultado NULL",mod421.getDeclarationResultType());
			boolean finance = mod421.getDeclarationResultType().mustCreateFinance() || mod421.isAeatRectification(); 
			MODEL421.markAsCustomerCheck(getOccam(), mod421);
			Mod421 mod421Bis = MODEL421.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod421Bis);			
			assertEquals("Status not CUSTOMER_CHECK", FiscalStatus.CUSTOMER_CHECK, mod421Bis.getStatus());
			Asserts.assertEqualsDouble("Mod421. Resultado no coincide."
					, result0
					, mod421Bis.getDeclarationResult());
			assertNotNull("Mod421. Tipo resultado NULL",mod421Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod421. Finance NULL",mod421Bis.getFinance());	
			} else {
				assertNull("Mod421. Finance NOT NULL",mod421Bis.getFinance());
			}
		}
	}
	
}
