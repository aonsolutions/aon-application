package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod111DeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod111 mod111 : MODEL111.getMod111s(getOccam())) {
			MODEL111.delete(getOccam(), mod111);
		}
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL111.getMod111s(getOccam()));
	}
		
}
