package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod390HFDeleteTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		for (Mod390HF mod390HF : MODEL390HF.getMod390HFs(getOccam())) {
			MODEL390HF.delete(getOccam(), mod390HF);
		};
	}
		
}
