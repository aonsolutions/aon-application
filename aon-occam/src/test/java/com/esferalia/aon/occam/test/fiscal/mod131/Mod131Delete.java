package com.esferalia.aon.occam.test.fiscal.mod131;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod131Delete extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod131 mod131 : MODEL131.getMod131s(getOccam())) {
			MODEL131.delete(getOccam(), mod131);
		};
	}
		
}
