package com.esferalia.aon.occam.test.fiscal.mod390hf;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

class Mod390HFSentTest extends AbstractOccamTest {
	
	@Test
	void testSent() {
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod = MODEL390HF.get(getOccam(), model.getId());
			MODEL390HF.markAsSent(getOccam(), mod);
			Mod390HF modBis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod);
			assertEquals(FiscalStatus.SENT, modBis.getStatus(), "Status not SENT");
			assertNotNull(modBis.getDeclarationResultType(), "Mod390HF. Tipo resultado NULL");
		}
	}
	
}
