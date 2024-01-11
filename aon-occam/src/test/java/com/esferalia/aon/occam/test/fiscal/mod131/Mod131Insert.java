package com.esferalia.aon.occam.test.fiscal.mod131;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod131Insert extends AbstractOccamTest {
	
	@Test
	public void test() {
		test( Administration.COMMON_TERRITORY);
	}

	public void test( Administration admon) {
		Date now = new Date();
		for (Period p : Period.values()) {
			if (p.isQuarterPeriod()) {
				Mod131 mod131 = new Mod131();
				mod131.setDomain(DOMAIN_ID);
				mod131.setYear(AonDateUtils.getYear(now));
				mod131.setPeriod( p );
				mod131 = MODEL131.initialize(getOccam(), mod131);
				mod131.setAdministration(admon);
				mod131 = MODEL131.create(getOccam(), mod131);
				MODEL131.save(getOccam(), mod131);
			}
		}
	}
}
