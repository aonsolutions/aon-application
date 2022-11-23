package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod380HFDeleteTest extends AbstractOccamTest {
	
	@Test
	public void testDelete() {
		for (Mod390HF mod : MODEL390HF.getMod390HFs(getOccam())) {
			MODEL390HF.delete(getOccam(), mod);
		};
		Asserts.assertEmptyCollection("Existen modelos después del borrado", MODEL303.getMod303s(getOccam()));
	}
		
}
