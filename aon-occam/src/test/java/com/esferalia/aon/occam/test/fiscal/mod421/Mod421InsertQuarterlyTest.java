package com.esferalia.aon.occam.test.fiscal.mod421;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod421InsertQuarterlyTest extends Mod421AbstractTest {

	@Test
	public void test() {
		Date today = getTestDate();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setMonthly(false);
		for (Period period : Period.values()) {
			Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
			Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
			if (period.isQuarterPeriod()) {
				mod421InsertQuarterly( params.setIssueDate(AonRandom.getRangeDate(start,end)) );
			}
		}
	}
	
	public void mod421InsertQuarterly(FiscalFakerParams params) {
		
		Mod421 canarias1 = insertModel(params.setAdministration(Administration.CANARIAS));
		String modelFullName = canarias1.getModelFullName();
		double result1 = getMod421SuitableResult(canarias1);
		MODEL421.delete(getOccam(), canarias1);
		Mod421 canarias2 = insertModel(params.setAdministration(Administration.CANARIAS));
		double result2 = getMod421SuitableResult(canarias2);
		
		Asserts.assertEqualsDouble("Canarias " + modelFullName + ". Resultado no coincide.", result1, result2);
			
	}

}
