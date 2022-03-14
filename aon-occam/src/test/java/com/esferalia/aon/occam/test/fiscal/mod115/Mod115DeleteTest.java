package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod115DeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod115 mod115 : MODEL115.getMod115s(getOccam())) {
			MODEL115.delete(getOccam(), mod115);
		};
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL115.getMod115s(getOccam()));
	}
		
}
