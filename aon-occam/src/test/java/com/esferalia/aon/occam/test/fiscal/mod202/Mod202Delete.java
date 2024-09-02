package com.esferalia.aon.occam.test.fiscal.mod202;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod202Delete extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod202 mod202 : MODEL202.getMod202s(getOccam())) {
			MODEL202.deleteMod202(getOccam(), mod202);
		};
	}
		
}
