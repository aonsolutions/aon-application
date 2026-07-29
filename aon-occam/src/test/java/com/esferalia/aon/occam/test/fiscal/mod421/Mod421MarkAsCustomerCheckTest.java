package com.esferalia.aon.occam.test.fiscal.mod421;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

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
			assertNotNull(mod421.getDeclarationResultType(), "Mod421. Tipo resultado NULL");
			boolean finance = mod421.getDeclarationResultType().mustCreateFinance() || mod421.isAeatRectification(); 
			MODEL421.markAsCustomerCheck(getOccam(), mod421);
			Mod421 mod421Bis = MODEL421.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod421Bis);			
			assertEquals(FiscalStatus.CUSTOMER_CHECK, mod421Bis.getStatus(), "Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod421. Resultado no coincide."
					, result0
					, mod421Bis.getDeclarationResult());
			assertNotNull(mod421Bis.getDeclarationResultType(), "Mod421. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod421Bis.getFinance(), "Mod421. Finance NULL");	
			} else {
				assertNull(mod421Bis.getFinance(), "Mod421. Finance NOT NULL");
			}
		}
	}
	
}
