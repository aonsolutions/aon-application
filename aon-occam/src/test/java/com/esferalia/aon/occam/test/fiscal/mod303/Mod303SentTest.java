package com.esferalia.aon.occam.test.fiscal.mod303;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

class Mod303SentTest extends Mod303AbstractTest {
	
	@Test
	void testSent() {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			MODEL303.markAsSent(getOccam(), mod303);
			Mod303 mod303Bis = MODEL303.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303);
			assertEquals(FiscalStatus.SENT, mod303Bis.getStatus(), "Status not SENT");
			assertNotNull(mod303Bis.getDeclarationResultType(), "Mod303. Tipo resultado NULL");
		}
	}
	
}
