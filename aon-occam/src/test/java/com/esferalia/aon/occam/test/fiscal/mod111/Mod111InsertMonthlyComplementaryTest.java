package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
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

public class Mod111InsertMonthlyComplementaryTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod111InsertMonthlyComplementary(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod111InsertMonthlyComplementary(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod111 sbizkaia = simulateModel( Administration.BIZKAIA,date);
		Mod111 sgipuzkoa = simulateModel( Administration.GIPUZKOA,date);
		Mod111 snavarra = simulateModel( Administration.NAVARRA,date);

		Mod111 araba = insertModel( Administration.ALAVA,date);
		Mod111 bizkaia = insertModel( Administration.BIZKAIA,date);
		Mod111 gipuzkoa = insertModel( Administration.GIPUZKOA,date);
		Mod111 navarra = insertModel( Administration.NAVARRA,date);
		
		Asserts.assertEqualsDouble("Gipuzkoa " + sgipuzkoa.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), sgipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia" + sbizkaia.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), sbizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + snavarra.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), snavarra.getDeclarationResult());
		
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + "debe ser cero", 0.0, bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + "debe ser cero", 0.0, gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + "debe ser cero", 0.0, navarra.getDeclarationResult());
		
	}
	
	private Mod111 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setComplementary(true)
			.setGenerateFromYearStart(true)
			;
		Mod111 mod111 = FiscalFaker.createMod111(params);
		MODEL111.save(getOccam(), mod111);
		Mod111 actual = MODEL111.get(getOccam(), mod111.getId());  
		Asserts.assertMod111(mod111, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}

	private Mod111 simulateModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setComplementary(true)
			.setGenerateFromYearStart(true)
			;
		Mod111 mod111 = FiscalFaker.simulateMod111(params);
		mod111 = MODEL111.calculate(getOccam(), mod111);
		FiscalTestSuite.printModel(mod111, true);
		return mod111;
	}
}
