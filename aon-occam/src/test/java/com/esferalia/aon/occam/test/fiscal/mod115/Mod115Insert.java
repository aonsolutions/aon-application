package com.esferalia.aon.occam.test.fiscal.mod115;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod115Insert extends AbstractOccamTest {
	
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
				Mod115 mod115 = new Mod115();
				mod115.setDomain(DOMAIN_ID);
				mod115.setYear(AonDateUtils.getYear(now));
				mod115.setPeriod( p );
				mod115 = MODEL115.initializeMod115(getOccam(), mod115);
				mod115.setAdministration(admon);
				mod115 = MODEL115.createMod115(getOccam(), mod115);
				MODEL115.save(getOccam(), mod115);
			}
		}
	}
}
