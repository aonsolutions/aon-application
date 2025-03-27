package com.esferalia.aon.occam.test.fiscal.mod115;

import java.util.Date;

import org.junit.Test;

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

public class Mod115SimulateMonthlyTest extends AbstractOccamTest {
	
	@Test
	public void mod115InsertMonthlyTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod115InsertMonthly(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod115InsertMonthly(Date date) {
		System.out.println( "\t ---------------------");

		Mod115 aeat = simulateModel( Administration.COMMON_TERRITORY, date);
		Mod115 araba = simulateModel( Administration.ALAVA, date);
		Mod115 bizkaia = simulateModel( Administration.BIZKAIA, date);
		Mod115 gipuzkoa = simulateModel( Administration.GIPUZKOA, date);
		Mod115 navarra = simulateModel( Administration.NAVARRA, date);
		
		Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), navarra.getDeclarationResult());
	}

	private Mod115 simulateModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			;
		Mod115 mod115 = FiscalFaker.simulateMod115(params);
		mod115 = MODEL115.calculate(getOccam(), mod115);
		FiscalTestSuite.printModel(mod115);
		return mod115;
	}
}
