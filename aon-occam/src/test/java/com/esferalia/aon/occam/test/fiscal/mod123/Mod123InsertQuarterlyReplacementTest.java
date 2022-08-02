package com.esferalia.aon.occam.test.fiscal.mod123;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123.Mod123DAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod123InsertQuarterlyReplacementTest extends AbstractOccamTest {
	
	@Test
	public void mod123InsertMonthlyReplacementTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isQuarterPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod123InsertMonthlyReplacement(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod123InsertMonthlyReplacement(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod123 aeat  = insertModel( Administration.COMMON_TERRITORY,date);
		insertModel( Administration.ALAVA,date);
		
		Map<Administration, Double> results = Mod123DAO.getMod123s(ctx, getOccam().getDomain())
			.filter(mod -> mod.getYear() == aeat.getYear())
			.filter(mod -> mod.getPeriod() == aeat.getPeriod())
			.collect(Collectors.groupingBy(Mod123::getAdministration , Collectors.summingDouble(Mod123::getDeclarationResult)));
			;
		
		Asserts.assertEqualsDouble("Sumatorios no coinciden."
				, results.get(Administration.COMMON_TERRITORY)
				, results.get(Administration.ALAVA));
			
	}

	private Mod123 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(false)
			.setAdministration(admon)
			.setReplacement(admon == Administration.ALAVA)
			.setComplementary(admon == Administration.COMMON_TERRITORY)
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
