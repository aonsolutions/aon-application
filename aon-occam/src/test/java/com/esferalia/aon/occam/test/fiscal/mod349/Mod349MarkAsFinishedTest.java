package com.esferalia.aon.occam.test.fiscal.mod349;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.test.AbstractOccamTest;

class Mod349MarkAsFinishedTest extends AbstractOccamTest {
	
	@Test
	void mod349MarkAsFinishedTest() {
		for (Mod349 model : MODEL349.getMod349s(getOccam()) ) {
			Mod349 mod349 = MODEL349.get(getOccam(), model.getId());
			MODEL349.changeStatus(getOccam(), mod349, FiscalStatus.FINISHED);
			Mod349 mod349Bis = MODEL349.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod349Bis.getStatus(), "Status not FINISHED");
		}
	}
	
}
