package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod115Delete extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod115 mod115 : MODEL115.getMod115s(getOccam())) {
			MODEL115.deleteMod115(getOccam(), mod115);
		};
	}
		
}
