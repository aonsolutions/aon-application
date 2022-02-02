package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod111Insert extends AbstractOccamTest {
	
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
				Mod111 mod111 = new Mod111();
				mod111.setDomain(DOMAIN_ID);
				mod111.setYear(AonDateUtils.getYear(now));
				mod111.setPeriod( p );
				mod111 = MODEL111.initializeMod111(getOccam(), mod111);
				mod111.setAdministration(admon);
				mod111 = MODEL111.createMod111(getOccam(), mod111);
				MODEL111.save(getOccam(), mod111);
			}
		}
	}
}
