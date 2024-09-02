package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod123DeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod123 mod123 : MODEL123.getMod123s(getOccam())) {
			MODEL123.delete(getOccam(), mod123);
		};
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL123.getMod123s(getOccam()));
	}
		
}
