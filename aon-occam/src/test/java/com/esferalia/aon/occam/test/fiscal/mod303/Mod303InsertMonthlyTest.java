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

public class Mod303InsertMonthlyTest extends Mod303AbstractTest {

	@Test
	public void test() {
		Date today = getTestDate();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setMonthly(true)
			.setProrratePercent( getProratePercent(today) )
			.setSpecialProrrate( isSpecialProrrate(today) );
		for (Period period : Period.values()) {
			Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
			Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
			if (period.isMonthPeriod()) {
				mod303InsertMonthly( params.setIssueDate(AonRandom.getRangeDate(start,end)) );
			}
		}
	}
	
	public void mod303InsertMonthly(FiscalFakerParams params) {
		if ( AonMathUtils.isNotZero(params.getProrratePercent()) ) {
			String pr1 = " (" + (params.isSpecialProrrate()?"E":"G") + ") ";
			System.out.println( "\t --------------------- [Prorrate: " + params.getProrratePercent() + pr1 + "]");
		} else {
			System.out.println( "\t --------------------- [NO prorrate]");
		}
		Mod303 aeat = insertModel( params.setAdministration(Administration.COMMON_TERRITORY) );
		if ( !aeat.isLastPeriod() ) {
			Mod303 araba = insertModel( params.setAdministration(Administration.ALAVA));
			Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", 
					getMod303SuitableResult(aeat), getMod303SuitableResult(araba));
			
			Mod303 bizkaia = insertModel( params.setAdministration(Administration.BIZKAIA));
			Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", 
					getMod303SuitableResult(aeat), getMod303SuitableResult(bizkaia));
			
			Mod303 gipuzkoa = insertModel( params.setAdministration(Administration.GIPUZKOA));
			Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", 
					getMod303SuitableResult(aeat), getMod303SuitableResult(gipuzkoa));
		}
		
//		Mod303 navarra = insertModel( params.setAdministration(Administration.NAVARRA));
//		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", 
//				getMod303SuitableResult(aeat), getMod303SuitableResult(navarra));
	}

}
