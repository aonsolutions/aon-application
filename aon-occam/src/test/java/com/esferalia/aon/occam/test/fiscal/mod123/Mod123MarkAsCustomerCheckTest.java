package com.esferalia.aon.occam.test.fiscal.mod123;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod123MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod123 model : MODEL123.getMod123s(getOccam()) ) {
			Mod123 mod123 = MODEL123.get(getOccam(), model.getId());
			mod123 = MODEL123.initializeForFinish(getOccam(), mod123);
			double result0 = mod123.getDeclarationResult();
			assertNotNull(mod123.getDeclarationResultType(), "Mod123. Tipo resultado NULL");
			boolean finance = mod123.getDeclarationResultType().mustCreateFinance(); 
			MODEL123.markAsCustomerCheck(getOccam(), mod123);
			Mod123 mod123Bis = MODEL123.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_CHECK, mod123Bis.getStatus(), "Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod123. Resultado no coincide."
					, result0
					, mod123Bis.getDeclarationResult());
			assertNotNull(mod123Bis.getDeclarationResultType(), "Mod123. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod123Bis.getFinance(), "Mod123. Finance NULL");	
			} else {
				assertNull(mod123Bis.getFinance(), "Mod123. Finance NOT NULL");
			}
		}
	}
	
}
