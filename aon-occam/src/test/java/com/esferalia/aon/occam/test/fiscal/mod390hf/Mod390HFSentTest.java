package com.esferalia.aon.occam.test.fiscal.mod390hf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod390HFSentTest extends AbstractOccamTest {
	
	@Test
	public void testSent() {
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod = MODEL390HF.get(getOccam(), model.getId());
			MODEL390HF.markAsSent(getOccam(), mod);
			Mod390HF modBis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod);
			assertEquals("Status not SENT", FiscalStatus.SENT, modBis.getStatus());
			assertNotNull("Mod390HF. Tipo resultado NULL",modBis.getDeclarationResultType());
		}
	}
	
}
