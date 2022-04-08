package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod303InsertQuarterlyComplementaryTest extends AbstractOccamTest {

	@Test
	public void mod303InsertQuarterlyComplementaryTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isQuarterPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod303InsertQuarterlyComplementary(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod303InsertQuarterlyComplementary(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod303 araba = insertModel( Administration.ALAVA,date);
//		Mod303 bizkaia = insertModel( Administration.BIZKAIA,date);
		Mod303 gipuzkoa = insertModel( Administration.GIPUZKOA,date);
//		Mod303 navarra = insertModel( Administration.NAVARRA,date);
		
//		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), gipuzkoa.getDeclarationResult());
//		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), navarra.getDeclarationResult());
	}
	
	private Mod303 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(false)
			.setAdministration(admon)
			.setComplementary(true)
			.setGenerateFromYearStart(true)
			;
		Mod303 mod303 = FiscalFaker.createMod303(params);
		MODEL303.save(getOccam(), mod303);
		Mod303 actual = MODEL303.get(getOccam(), mod303.getId());  
		Asserts.assertMod303(mod303, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
