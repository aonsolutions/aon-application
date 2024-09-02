package com.esferalia.aon.occam.test.fiscal.mod349;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod349InsertNoDiffQuaterlyTest extends Mod349AbstractTest {
	
	@Test
	public void mod349InsertNoDiffQuaterlyTest() { 	
		
		// Crear modelos periodo trimestral
		Date today = getTestDate();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setMonthly(false)
			.setDiffEnabled(false);			
		for (Period period : Period.values()) {
			Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
			Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
			if (period.isQuarterPeriod()) {
				mod349InsertAll(params.setIssueDate(AonRandom.getRangeDate(start,end)));				
			}
		}
		
	}

}
