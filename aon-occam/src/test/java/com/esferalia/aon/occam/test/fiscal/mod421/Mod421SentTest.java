package com.esferalia.aon.occam.test.fiscal.mod421;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod421SentTest extends Mod421AbstractTest {
	
	@Test
	public void testSent() {
		for (Mod421 model : MODEL421.getMod421s(getOccam()) ) {
			Mod421 mod421 = MODEL421.get(getOccam(), model.getId());
			MODEL421.markAsSent(getOccam(), mod421);
			Mod421 mod421Bis = MODEL421.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod421);
			assertEquals("Status not SENT", FiscalStatus.SENT, mod421Bis.getStatus());
			assertNotNull("Mod421. Tipo resultado NULL",mod421Bis.getDeclarationResultType());
		}
	}
	
}
