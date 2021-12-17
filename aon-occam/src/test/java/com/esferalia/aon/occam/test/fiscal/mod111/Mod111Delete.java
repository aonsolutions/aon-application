package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod111Delete extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod111 mod111 : MODEL111.getMod111s(getOccam())) {
			MODEL111.deleteMod111(getOccam(), mod111);
		};
	}
		
}
