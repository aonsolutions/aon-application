package com.esferalia.aon.occam.test.fiscal.mod115;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
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

public class Mod115InsertMonthlyComplementaryTest extends AbstractOccamTest {
	
	@Test
	public void mod115InsertMonthlyComplementaryTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod115InsertMonthlyComplementary(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod115InsertMonthlyComplementary(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod115 araba = insertModel( Administration.ALAVA,date);
		Mod115 bizkaia = insertModel( Administration.BIZKAIA,date);
		Mod115 gipuzkoa = insertModel( Administration.GIPUZKOA,date);
		Mod115 navarra = insertModel( Administration.NAVARRA,date);
		
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), navarra.getDeclarationResult());
	}
	
	private Mod115 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setComplementary(true)
			.setGenerateFromYearStart(true)
			;
		Mod115 mod115 = FiscalFaker.createMod115(params);
		MODEL115.save(getOccam(), mod115);
		Mod115 actual = MODEL115.get(getOccam(), mod115.getId());  
		Asserts.assertMod115(mod115, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
