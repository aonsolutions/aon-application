package com.esferalia.aon.occam.test.fiscal.mod202;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod202Insert extends AbstractOccamTest {
	
	@Test
	public void test() {
		test( Administration.COMMON_TERRITORY);
	}

	public void test( Administration admon) {
		Date now = new Date();
		for (Period p : Period.values()) {
			if (p.isQuarterPeriod() && p != Period.T4) {
				Mod202 mod202 = new Mod202();
				mod202.setDomain(DOMAIN_ID);
				mod202.setYear(AonDateUtils.getYear(now));
				mod202.setPeriod( p );
				mod202 = MODEL202.initializeMod202(getOccam(), mod202);
				mod202.setAdministration(admon);
				mod202 = MODEL202.createMod202(getOccam(), mod202);
				MODEL202.save(getOccam(), mod202);
			}
		}
	}
}
