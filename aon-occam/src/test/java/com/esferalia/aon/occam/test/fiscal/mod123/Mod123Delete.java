package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod123Delete extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod123 mod123 : MODEL123.getMod123s(getOccam())) {
			MODEL123.deleteMod123(getOccam(), mod123);
		};
	}
		
}
