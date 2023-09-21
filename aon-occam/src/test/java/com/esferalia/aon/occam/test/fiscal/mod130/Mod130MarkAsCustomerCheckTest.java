package com.esferalia.aon.occam.test.fiscal.mod130;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod130MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod130 model : MODEL130.getMod130s(getOccam()) ) {
			Mod130 mod130 = MODEL130.get(getOccam(), model.getId());
			mod130 = MODEL130.initializeForFinish(getOccam(), mod130);
			double result0 = mod130.getDeclarationResult();
			assertNotNull("Mod130. Tipo resultado NULL",mod130.getDeclarationResultType());
			boolean finance = mod130.getDeclarationResultType().mustCreateFinance(); 
			MODEL130.markAsCustomerCheck(getOccam(), mod130);
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals("Status not CUSTOMER_CHECK", FiscalStatus.CUSTOMER_CHECK, mod130Bis.getStatus());
			Asserts.assertEqualsDouble("Mod130. Resultado no coincide."
					, result0
					, mod130Bis.getDeclarationResult());
			assertNotNull("Mod130. Tipo resultado NULL",mod130Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod130. Finance NULL",mod130Bis.getFinance());	
			} else {
				assertNull("Mod130. Finance NOT NULL",mod130Bis.getFinance());
			}
		}
	}
	
}
