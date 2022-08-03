package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod303DeleteTest extends AbstractOccamTest {
	
	@Test
	public void testDelete() {
		for (Mod303 mod303 : MODEL303.getMod303s(getOccam())) {
			MODEL303.delete(getOccam(), mod303);
		};
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL303.getMod303s(getOccam()));
	}
		
}
