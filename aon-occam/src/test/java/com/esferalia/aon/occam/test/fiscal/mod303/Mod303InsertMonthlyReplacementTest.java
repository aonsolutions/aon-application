package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod303InsertMonthlyReplacementTest extends AbstractOccamTest {
	
	@Test
	public void mod303InsertQuarterlyReplacementTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod303InsertMonthlyReplacement(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod303InsertMonthlyReplacement(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod303 aeat  = insertModel( Administration.COMMON_TERRITORY,date);
		insertModel( Administration.ALAVA,date);
		insertModel( Administration.BIZKAIA,date);
		insertModel( Administration.NAVARRA,date);
		
		Map<Administration, Double> results = Mod303DAO.getMod303s(ctx, getOccam().getDomain())
			.filter(mod -> mod.getYear() == aeat.getYear())
			.filter(mod -> mod.getPeriod() == aeat.getPeriod())
			.collect(Collectors.groupingBy(Mod303::getAdministration , Collectors.summingDouble(Mod303::getDeclarationResult)));
			;
		
		if (!aeat.isLastPeriod()) {
			Asserts.assertEqualsDouble("Sumatorios no coinciden."
					, results.get(Administration.COMMON_TERRITORY)
					, results.get(Administration.ALAVA));
			Asserts.assertEqualsDouble("Sumatorios no coinciden."
					, results.get(Administration.COMMON_TERRITORY)
					, results.get(Administration.BIZKAIA));
//			Asserts.assertEqualsDouble("Sumatorios no coinciden."
//					, results.get(Administration.COMMON_TERRITORY)
//					, results.get(Administration.NAVARRA));
		}
			
	}

	private Mod303 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setReplacement(admon == Administration.ALAVA || admon == Administration.NAVARRA)
			.setComplementary(admon == Administration.COMMON_TERRITORY || admon == Administration.BIZKAIA)
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
