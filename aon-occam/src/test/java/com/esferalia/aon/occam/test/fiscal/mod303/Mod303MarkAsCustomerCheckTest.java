package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod303MarkAsCustomerCheckTest extends Mod303AbstractTest {
	
	@Test
	public void testMarkAsCustomerCheck() {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			mod303 = MODEL303.initializeForFinish(getOccam(), mod303);
			double result0 = mod303.getDeclarationResult();
			assertNotNull(mod303.getDeclarationResultType(), "Mod303. Tipo resultado NULL");
			boolean finance = mod303.getDeclarationResultType().mustCreateFinance() || mod303.isAeatRectification(); 
			MODEL303.markAsCustomerCheck(getOccam(), mod303);
			Mod303 mod303Bis = MODEL303.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303Bis);			
			assertEquals(FiscalStatus.CUSTOMER_CHECK, mod303Bis.getStatus(), "Status not CUSTOMER_CHECK");
			Asserts.assertEqualsDouble("Mod303. Resultado no coincide."
					, result0
					, mod303Bis.getDeclarationResult());
			assertNotNull(mod303Bis.getDeclarationResultType(), "Mod303. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod303Bis.getFinance(), "Mod303. Finance NULL");	
			} else {
				assertNull(mod303Bis.getFinance(), "Mod303. Finance NOT NULL");
			}
		}
	}
	
}
