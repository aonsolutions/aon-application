package com.esferalia.aon.occam.test.fiscal.mod115;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod115MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod115 model : MODEL115.getMod115s(getOccam()) ) {
			Mod115 mod115 = MODEL115.get(getOccam(), model.getId());
			mod115 = MODEL115.initializeForFinish(getOccam(), mod115);
			double result0 = mod115.getDeclarationResult();
			assertNotNull("Mod115. Tipo resultado NULL",mod115.getDeclarationResultType());
			boolean finance = mod115.getDeclarationResultType().mustCreateFinance(); 
			MODEL115.markAsCustomerCheck(getOccam(), mod115);
			Mod115 mod115Bis = MODEL115.get(getOccam(), model.getId());
			assertEquals("Status not CUSTOMER_CHECK", FiscalStatus.CUSTOMER_CHECK, mod115Bis.getStatus());
			Asserts.assertEqualsDouble("Mod115. Resultado no coincide."
					, result0
					, mod115Bis.getDeclarationResult());
			assertNotNull("Mod115. Tipo resultado NULL",mod115Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod115. Finance NULL",mod115Bis.getFinance());	
			} else {
				assertNull("Mod115. Finance NOT NULL",mod115Bis.getFinance());
			}
		}
	}
	
}
