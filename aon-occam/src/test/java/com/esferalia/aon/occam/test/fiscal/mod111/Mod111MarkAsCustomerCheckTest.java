package com.esferalia.aon.occam.test.fiscal.mod111;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod111MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			mod111 = MODEL111.initializeForFinish(getOccam(), mod111);
			double result0 = mod111.getDeclarationResult();
			assertNotNull("Mod111. Tipo resultado NULL",mod111.getDeclarationResultType());
			boolean finance = mod111.getDeclarationResultType().mustCreateFinance(); 
			MODEL111.markAsCustomerCheck(getOccam(), mod111);
			Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
			assertEquals("Status not CUSTOMER_CHECK", FiscalStatus.CUSTOMER_CHECK, mod111Bis.getStatus());
			Asserts.assertEqualsDouble("Mod111. Resultado no coincide."
					, result0
					, mod111Bis.getDeclarationResult());
			assertNotNull("Mod111. Tipo resultado NULL",mod111Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod111. Finance NULL",mod111Bis.getFinance());	
			} else {
				assertNull("Mod111. Finance NOT NULL",mod111Bis.getFinance());
			}
		}
	}
	
}
