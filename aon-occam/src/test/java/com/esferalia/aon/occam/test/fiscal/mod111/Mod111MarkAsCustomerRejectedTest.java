package com.esferalia.aon.occam.test.fiscal.mod111;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod111MarkAsCustomerRejectedTest extends AbstractOccamTest {
	
	@Test
	public void testCustomerRejected() {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			if (mod111.isCustomerCheck() ) {
				MODEL111.markAsCustomerRejected(getOccam(), mod111, AonRandom.string(50, 20));
				Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
				assertEquals(FiscalStatus.CUSTOMER_REJECTED, mod111Bis.getStatus(),"Status not CUSTOMER_REJECTED");
				assertNotNull(mod111Bis.getDeclarationResultType(),"Mod111. Tipo resultado NULL");
			} else {
				Mod111 m111 = mod111;
				assertThrows(AonCoreException.class, () -> MODEL111.markAsCustomerRejected(getOccam(), m111, AonRandom.string(50, 20)));
			}
		}
	}
	
}
