package com.esferalia.aon.occam.test.fiscal.mod111;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod111SentTest extends AbstractOccamTest {
	
	@Test
	public void testSent() {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			MODEL111.markAsSent(getOccam(), mod111);
			Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.SENT, mod111Bis.getStatus(), "Status not SENT");
			assertNotNull(mod111Bis.getDeclarationResultType(), "Mod111. Tipo resultado NULL");
		}
	}
	
}
