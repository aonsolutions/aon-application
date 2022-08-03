package com.esferalia.aon.occam.test.fiscal.mod303;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod303SentTest extends AbstractOccamTest {
	
	@Test
	public void testSent() {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			MODEL303.markAsSent(getOccam(), mod303);
			Mod303 mod303Bis = MODEL303.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303);
			assertEquals("Status not SENT", FiscalStatus.SENT, mod303Bis.getStatus());
			assertNotNull("Mod303. Tipo resultado NULL",mod303Bis.getDeclarationResultType());
		}
	}
	
}
