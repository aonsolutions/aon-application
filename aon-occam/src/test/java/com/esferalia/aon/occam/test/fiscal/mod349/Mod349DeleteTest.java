package com.esferalia.aon.occam.test.fiscal.mod349;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod349DeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod349 mod349 : MODEL349.getMod349s(getOccam())) {
			MODEL349.delete(getOccam(), mod349);
		}
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL349.getMod349s(getOccam()));
	}
		
}
