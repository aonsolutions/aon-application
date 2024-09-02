package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303InsertQuarterlyReplacementTest extends Mod303AbstractTest {
	
	@Test
	public void mod303InsertQuarterlyReplacementTest() {
		Date today = getTestDate();
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setMonthly(false)
			.setProrratePercent( getProratePercent(today) )
			.setSpecialProrrate( isSpecialProrrate(today) )
			.setGenerateFromYearStart(true);
		for (Period period : Period.values()) {
			if (period.isQuarterPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod303InsertQuarterlyReplacement( params.setIssueDate(AonRandom.getRangeDate(start,end)) );
			}
		}
	}
	
	public void mod303InsertQuarterlyReplacement(FiscalFakerParams params) {
		if ( AonMathUtils.isNotZero(params.getProrratePercent()) ) {
			String pr1 = " (" + (params.isSpecialProrrate()?"E":"G") + ") ";
			System.out.println( "\t --------------------- [Prorrate: " + params.getProrratePercent() + pr1 + "]");
		} else {
			System.out.println( "\t --------------------- [NO prorrate]");
		}
		Mod303 aeat  = insertModel( Administration.COMMON_TERRITORY,params);
		
		if ( !aeat.isLastPeriod() ) {
		
			Mod303 araba = insertModel( Administration.ALAVA,params);
			Mod303 bizkaia = insertModel( Administration.BIZKAIA,params);
			insertModel( Administration.NAVARRA,params);
		
			Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide."
					, getMod303SuitableResult(aeat)
					, getMod303SuitableResult(araba));
			Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide."
					, getMod303SuitableResult(aeat)
					, getMod303SuitableResult(bizkaia));
//			Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide."
//			, getMod303SuitableResult(aeat)
//			, getMod303SuitableResult(navarra));
		}
	}

	private Mod303 insertModel( Administration admon, FiscalFakerParams params) {
		params.setAdministration(admon)
			.setReplacement(admon == Administration.ALAVA || admon == Administration.NAVARRA)
			.setComplementary(admon == Administration.COMMON_TERRITORY || admon == Administration.BIZKAIA)
			;
		return insertModel(params);
	}
}
