package com.esferalia.aon.occam.test.fiscal.mod421;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.test.Asserts;

public class Mod421DeleteTest extends Mod421AbstractTest {
	
	@Test
	public void testDelete() {
		for (Mod421 mod421 : MODEL421.getMod421s(getOccam())) {
			MODEL421.delete(getOccam(), mod421);
		};
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL421.getMod421s(getOccam()));
	}
		
}
