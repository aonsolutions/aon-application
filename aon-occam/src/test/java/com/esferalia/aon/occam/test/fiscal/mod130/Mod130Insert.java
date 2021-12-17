package com.esferalia.aon.occam.test.fiscal.mod130;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod130Insert extends AbstractOccamTest {
	
	@Test
	public void test() {
		test( Administration.COMMON_TERRITORY);
		test( Administration.BIZKAIA);
	}

	public void test( Administration admon) {
		Date now = new Date();
		for (Period p : Period.values()) {
			if (p.isQuarterPeriod()) {
				Mod130 mod130 = new Mod130();
				mod130.setDomain(DOMAIN_ID);
				mod130.setYear(AonDateUtils.getYear(now));
				mod130.setPeriod( p );
				mod130 = MODEL130.initializeMod130(getOccam(), mod130);
				mod130.setAdministration(admon);
				mod130 = MODEL130.createMod130(getOccam(), mod130);
				MODEL130.save(getOccam(), mod130);
			}
		}
	}
}
