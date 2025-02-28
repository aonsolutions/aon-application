package com.esferalia.aon.occam.test.fiscal.mod202;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod202SentTest extends AbstractOccamTest {
	
	@Test
	public void testSent() {
		for (Mod202 model : MODEL202.getMod202s(getOccam()) ) {
			Mod202 mod202 = MODEL202.get(getOccam(), model.getId());
			MODEL202.markAsSent(getOccam(), mod202);
			Mod202 mod202Bis = MODEL202.get(getOccam(), model.getId());
			assertEquals("Status not SENT", FiscalStatus.SENT, mod202Bis.getStatus());
			assertNotNull("Mod202. Tipo resultado NULL",mod202Bis.getDeclarationResultType());
		}
	}
	
}
