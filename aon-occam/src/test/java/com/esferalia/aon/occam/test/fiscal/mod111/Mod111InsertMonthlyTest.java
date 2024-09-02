package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;

import org.junit.jupiter.api.Test;

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

public class Mod111InsertMonthlyTest extends AbstractOccamTest {
	
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
		
		Mod111 aeat = insertModel( Administration.COMMON_TERRITORY, date);
		Mod111 araba = insertModel( Administration.ALAVA, date);
		Mod111 bizkaia = insertModel( Administration.BIZKAIA, date);
		Mod111 gipuzkoa = insertModel( Administration.GIPUZKOA, date);
		Mod111 navarra = insertModel( Administration.NAVARRA, date);
		
		Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Navarra " + navarra.getModelFullName() + ". Resultado no coincide.", aeat.getDeclarationResult(), navarra.getDeclarationResult());
	}

	private Mod111 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon);
		Mod111 mod111 = FiscalFaker.createMod111(params);
		MODEL111.save(getOccam(), mod111);
		Mod111 actual = MODEL111.get(getOccam(), mod111.getId());  
		Asserts.assertMod111(mod111, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
