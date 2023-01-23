package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303InsertMonthlyComplementaryTest extends Mod303AbstractTest {

	@Test
	public void mod303InsertMonthlyComplementaryTest() {
		Date today = getTestDate();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setMonthly(true)
			.setProrratePercent( getProratePercent(today) )
			.setSpecialProrrate( isSpecialProrrate(today) )
			.setGenerateFromYearStart(true)
			.setComplementary(true);
		
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod303InsertMonthlyComplementary( params.setIssueDate(AonRandom.getRangeDate(start,end)) );
			}
		}
	}
	
	public void mod303InsertMonthlyComplementary(FiscalFakerParams params) {
		Period period = Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()));
		if ( !period.isLastPeriod() ) {
			if ( AonMathUtils.isNotZero(params.getProrratePercent()) ) {
				String pr1 = " (" + (params.isSpecialProrrate()?"E":"G") + ") ";
				System.out.println( "\t --------------------- [Prorrate: " + params.getProrratePercent() + pr1 + "]");
			} else {
				System.out.println( "\t --------------------- [NO prorrate]");
			}
			
			Mod303 gipuzkoa = insertModel( params.setAdministration(Administration.GIPUZKOA));
			Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", 
					getMod303SuitableResult(gipuzkoa), getMod303SuitableResult(gipuzkoa));
		}
	}
	
}
