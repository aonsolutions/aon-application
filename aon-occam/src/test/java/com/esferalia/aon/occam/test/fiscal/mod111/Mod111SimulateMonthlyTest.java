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

public class Mod111SimulateMonthlyTest extends AbstractOccamTest {
	
	@Test
	public void mod111InsertMonthlyTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod111InsertMonthly(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod111InsertMonthly(Date date) {
		System.out.println( "\t ---------------------");

		Mod111 aeat = simulateModel( Administration.COMMON_TERRITORY, date);
		Mod111 araba = simulateModel( Administration.ALAVA, date);
		Mod111 bizkaia = simulateModel( Administration.BIZKAIA, date);
		Mod111 gipuzkoa = simulateModel( Administration.GIPUZKOA, date);
		Mod111 navarra = simulateModel( Administration.NAVARRA, date);
		
		Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), navarra.getDeclarationResult());
	}

	private Mod111 simulateModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			;
		Mod111 mod111 = FiscalFaker.simulateMod111(params);
		mod111 = MODEL111.calculate(getOccam(), mod111);
		FiscalTestSuite.printModel(mod111);
		return mod111;
	}
}
