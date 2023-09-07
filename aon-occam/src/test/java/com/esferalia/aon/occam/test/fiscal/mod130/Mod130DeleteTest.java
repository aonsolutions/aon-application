package com.esferalia.aon.occam.test.fiscal.mod130;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod130DeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod130 mod130 : MODEL130.getMod130s(getOccam())) {
			MODEL130.delete(getOccam(), mod130);
		};
	}
		
}
