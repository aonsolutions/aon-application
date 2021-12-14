package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod303Insert extends AbstractOccamTest {
	
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
			if (p.isQuarterPeriod() || p.isMonthPeriod()) {
				Mod303 mod303 = new Mod303();
				mod303.setDomain(DOMAIN_ID);
				mod303.setYear(AonDateUtils.getYear(now));
				mod303.setPeriod( p );
				mod303 = MODEL303.initializeMod303(getOccam(), mod303);
				mod303.setAdministration(admon);
				mod303 = MODEL303.createMod303(getOccam(), mod303);
				MODEL303.save(getOccam(), mod303);
			}
		}
	}
}
