package com.esferalia.aon.occam.test.fiscal.mod190;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod190DeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod190 mod190 : MODEL190.getMod190s(getOccam())) {
			MODEL190.delete(getOccam(), mod190);
		}
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL190.getMod190s(getOccam()));
	}
		
}
