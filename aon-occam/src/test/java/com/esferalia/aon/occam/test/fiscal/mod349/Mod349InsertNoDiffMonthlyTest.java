package com.esferalia.aon.occam.test.fiscal.mod349;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod349InsertNoDiffMonthlyTest extends Mod349AbstractTest {
	
	@Test
	public void mod349InsertNoDiffMonthlyTest() { 	
		
		// Crear modelos periodo mensual
		Date today = getTestDate();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setMonthly(true)
			.setDiffEnabled(false);			
		for (Period period : Period.values()) {
			Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
			Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
			if (period.isMonthPeriod()) {
				mod349InsertAll(params.setIssueDate(AonRandom.getRangeDate(start,end)));				
			}
		}
		
	}
	
}
