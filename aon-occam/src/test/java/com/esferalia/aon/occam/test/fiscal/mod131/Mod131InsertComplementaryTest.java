package com.esferalia.aon.occam.test.fiscal.mod131;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod131InsertComplementaryTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		Date today = new Date();
		Arrays.stream(Period.values())
			.filter(p -> p.isQuarterPeriod())
			.map(p -> AonRandom.getRangeDate(today, p))
			.forEach(  d -> mod131Insert(d));
	}
	public void mod131Insert(Date date) {
		System.out.println( "\t ---------------------");
		insertModel( Administration.COMMON_TERRITORY, date);
	}

	public Mod131 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(false)
			.setComplementary(true)
			.setAdministration(admon);
		Mod131 mod131 = FiscalFaker.createMod131(params);
		MODEL131.save(getOccam(), mod131);
		Mod131 actual = MODEL131.get(getOccam(), mod131.getId());  
		Asserts.assertMod131(mod131, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
