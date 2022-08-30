package com.esferalia.aon.occam.test.fiscal.mod123;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
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

public class Mod123InsertMonthlyComplementaryTest extends AbstractOccamTest {
	
	@Test
	public void mod123InsertMonthlyComplementaryTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod123InsertMonthlyComplementary(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod123InsertMonthlyComplementary(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod123 araba = insertModel( Administration.ALAVA,date);
		Mod123 bizkaia = insertModel( Administration.BIZKAIA,date);
		Mod123 gipuzkoa = insertModel( Administration.GIPUZKOA,date);
		Mod123 navarra = insertModel( Administration.NAVARRA,date);
		
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), navarra.getDeclarationResult());
	}
	
	private Mod123 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setComplementary(true)
			.setGenerateFromYearStart(true)
			;
		Mod123 mod123 = FiscalFaker.createMod123(params);
		MODEL123.save(getOccam(), mod123);
		Mod123 actual = MODEL123.get(getOccam(), mod123.getId());  
		Asserts.assertMod123(mod123, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
