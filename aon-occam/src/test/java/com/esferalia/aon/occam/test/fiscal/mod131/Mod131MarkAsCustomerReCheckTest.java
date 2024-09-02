package com.esferalia.aon.occam.test.fiscal.mod131;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

class Mod131MarkAsCustomerReCheckTest extends AbstractOccamTest {
	
	@Test
	void testMarkAsCustomerCheck() {
		for (Mod131 model : MODEL131.getMod131s(getOccam()) ) {
			Mod131 mod131 = MODEL131.get(getOccam(), model.getId());
			mod131 = MODEL131.initializeForFinish(getOccam(), mod131);
			double result0 = mod131.getDeclarationResult();
			assertNotNull(mod131.getDeclarationResultType(),"Mod131. Tipo resultado NULL");
			boolean finance = mod131.getDeclarationResultType().mustCreateFinance(); 
			MODEL131.markAsCustomerCheck(getOccam(), mod131);
			Mod131 mod131Bis = MODEL131.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_CHECK, mod131Bis.getStatus(),"Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod131. Resultado no coincide."
					, result0
					, mod131Bis.getDeclarationResult());
			assertNotNull(mod131Bis.getDeclarationResultType(),"Mod131. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod131Bis.getFinance(),"Mod131. Finance NULL");	
			} else {
				assertNull(mod131Bis.getFinance(),"Mod131. Finance NOT NULL");
			}
		}
	}
	
}
