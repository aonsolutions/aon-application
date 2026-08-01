package com.esferalia.aon.occam.test.fiscal.mod202;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod202MarkAsCustomerCheckTest extends AbstractOccamTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod202 model : MODEL202.getMod202s(getOccam()) ) {
			Mod202 mod202 = MODEL202.get(getOccam(), model.getId());
			mod202 = MODEL202.initializeForFinish(getOccam(), mod202);
			double result0 = mod202.getDeclarationResult();
			assertNotNull(mod202.getDeclarationResultType(), "Mod202. Tipo resultado NULL");
			boolean finance = mod202.getDeclarationResultType().mustCreateFinance(); 
			MODEL202.markAsCustomerCheck(getOccam(), mod202);
			Mod202 mod202Bis = MODEL202.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.CUSTOMER_CHECK, mod202Bis.getStatus(), "Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod202. Resultado no coincide."
					, result0
					, mod202Bis.getDeclarationResult());
			assertNotNull(mod202Bis.getDeclarationResultType(), "Mod202. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod202Bis.getFinance(), "Mod202. Finance NULL");	
			} else {
				assertNull(mod202Bis.getFinance(), "Mod202. Finance NOT NULL");
			}
		}
	}
	
}
