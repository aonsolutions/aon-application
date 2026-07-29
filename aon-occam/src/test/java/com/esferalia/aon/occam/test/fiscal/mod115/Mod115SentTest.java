package com.esferalia.aon.occam.test.fiscal.mod115;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod115SentTest extends AbstractOccamTest {
	
	@Test
	public void testSent() {
		for (Mod115 model : MODEL115.getMod115s(getOccam()) ) {
			Mod115 mod115 = MODEL115.get(getOccam(), model.getId());
			MODEL115.markAsSent(getOccam(), mod115);
			Mod115 mod115Bis = MODEL115.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.SENT, mod115Bis.getStatus(), "Status not SENT");
			assertNotNull(mod115Bis.getDeclarationResultType(), "Mod115. Tipo resultado NULL");
		}
	}
	
}
