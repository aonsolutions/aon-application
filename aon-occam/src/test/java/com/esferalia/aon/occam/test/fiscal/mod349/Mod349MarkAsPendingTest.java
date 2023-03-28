package com.esferalia.aon.occam.test.fiscal.mod349;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod349MarkAsPendingTest extends AbstractOccamTest {
	
	@Test
	public void mod349MarkAsPendingTest() {
		for (Mod349 model : MODEL349.getMod349s(getOccam()) ) {
			Mod349 mod349 = MODEL349.get(getOccam(), model.getId());
			MODEL349.changeStatus(getOccam(), mod349, FiscalStatus.PENDING );
			Mod349 mod349Bis = MODEL349.get(getOccam(), model.getId());
			assertEquals("Status not PENDING", FiscalStatus.PENDING, mod349Bis.getStatus());
		}
	}
	
}
