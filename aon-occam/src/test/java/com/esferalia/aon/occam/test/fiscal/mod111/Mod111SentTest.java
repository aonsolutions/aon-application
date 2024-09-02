package com.esferalia.aon.occam.test.fiscal.mod111;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod111SentTest extends AbstractOccamTest {
	
	@Test
	public void testSent() {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			if (mod111.canBeSent()) {
				MODEL111.markAsSent(getOccam(), mod111);
				Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
				assertEquals( FiscalStatus.SENT, mod111Bis.getStatus(),"Status not SENT");
				assertNotNull(mod111Bis.getDeclarationResultType(),"Mod115. Tipo resultado NULL");
			} else {
				assertThrows(AonCoreException.class, () -> MODEL111.markAsSent(getOccam(), mod111));
			}
		}
	}
	
}
