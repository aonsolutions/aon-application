package com.esferalia.aon.occam.test.fiscal.mod130;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

class Mod130MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	void testMarkAsCustomerCheck() {
		for (Mod130 model : MODEL130.getMod130s(getOccam()) ) {
			Mod130 mod130 = MODEL130.get(getOccam(), model.getId());
			mod130 = MODEL130.initializeForFinish(getOccam(), mod130);
			double result0 = mod130.getDeclarationResult();
			assertNotNull(mod130.getDeclarationResultType(),"Mod130. Tipo resultado NULL");
			boolean finance = mod130.getDeclarationResultType().mustCreateFinance(); 
			MODEL130.markAsCustomerCheck(getOccam(), mod130);
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_CHECK, mod130Bis.getStatus(),"Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod130. Resultado no coincide."
					, result0
					, mod130Bis.getDeclarationResult());
			assertNotNull(mod130Bis.getDeclarationResultType(),"Mod130. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod130Bis.getFinance(),"Mod130. Finance NULL");	
			} else {
				assertNull(mod130Bis.getFinance(),"Mod130. Finance NOT NULL");
			}
		}
	}
	
}
