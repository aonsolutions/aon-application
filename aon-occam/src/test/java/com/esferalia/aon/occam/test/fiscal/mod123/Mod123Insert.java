package com.esferalia.aon.occam.test.fiscal.mod123;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod123Insert extends AbstractOccamTest {
	
	@Test
	public void test() {
		test( Administration.COMMON_TERRITORY);
		test( Administration.ALAVA);
		test( Administration.BIZKAIA);
		test( Administration.GIPUZKOA);
	}

	public void test( Administration admon) {
		Date now = new Date();
		for (Period p : Period.values()) {
			if (p.isMonthPeriod() || p.isQuarterPeriod()) {
				Mod123 mod123 = new Mod123();
				mod123.setDomain(DOMAIN_ID);
				mod123.setYear(AonDateUtils.getYear(now));
				mod123.setPeriod( p );
				mod123 = MODEL123.initializeMod123(getOccam(), mod123);
				mod123.setAdministration(admon);
				mod123 = MODEL123.createMod123(getOccam(), mod123);
				MODEL123.save(getOccam(), mod123);
			}
		}
	}
}
