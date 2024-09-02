package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

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

public class Mod111InsertQuarterlyReplacementTest extends AbstractOccamTest {
	
	@Test
	public void mod111InsertMonthlyReplacementTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isQuarterPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod111InsertMonthlyReplacement(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod111InsertMonthlyReplacement(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod111 aeat  = insertModel( Administration.COMMON_TERRITORY,date);
		insertModel( Administration.ALAVA,date);
		
		Map<Administration, Double> results = MODEL111.getMod111s(getOccam())
			.stream()
			.filter(mod -> mod.getYear() == aeat.getYear())
			.filter(mod -> mod.getPeriod() == aeat.getPeriod())
			.collect(Collectors.groupingBy(Mod111::getAdministration , Collectors.summingDouble(Mod111::getDeclarationResult)));
			;
		Asserts.assertEqualsDouble("Sumatorios no coinciden."
				, results.get(Administration.COMMON_TERRITORY)
				, results.get(Administration.ALAVA));
			
	}

	private Mod111 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(false)
			.setAdministration(admon)
			.setReplacement(admon == Administration.ALAVA)
			.setComplementary(admon == Administration.COMMON_TERRITORY)
			.setGenerateFromYearStart(true)
			;
		Mod111 mod111 = FiscalFaker.createMod111(params);
		MODEL111.save(getOccam(), mod111);
		Mod111 actual = MODEL111.get(getOccam(), mod111.getId());  
		Asserts.assertMod111(mod111, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
