package com.esferalia.aon.occam.test.fiscal.mod130;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod130InsertTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		Date today = new Date();
		Arrays.stream(Period.values())
			.filter(p -> p.isQuarterPeriod())
			.map(p -> AonRandom.getRangeDate(today, p))
			.forEach(  d -> mod130Insert(d));
	}
	public void mod130Insert(Date date) {
		System.out.println( "\t ---------------------");
		insertModel( Administration.COMMON_TERRITORY, date);
		insertModel( Administration.BIZKAIA, date);
	}

	public Mod130 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(false)
			.setAdministration(admon);
		Mod130 mod130 = FiscalFaker.createMod130(params);
		MODEL130.save(getOccam(), mod130);
		Mod130 actual = MODEL130.get(getOccam(), mod130.getId());  
		Asserts.assertMod130(mod130, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
