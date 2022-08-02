package com.esferalia.aon.occam.test.fiscal.mod115;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115.Mod115DAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod115InsertMonthlyReplacementTest extends AbstractOccamTest {
	
	@Test
	public void mod115InsertMonthlyReplacementTest() {
		Date today = new Date();
		for (Period period : Period.values()) {
			if (period.isMonthPeriod()) {
				Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
				Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
				mod115InsertMonthlyReplacement(AonRandom.getRangeDate(start,end));
			}
		}
	}
	
	public void mod115InsertMonthlyReplacement(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod115 aeat  = insertModel( Administration.COMMON_TERRITORY,date);
		insertModel( Administration.ALAVA,date);
		
		Map<Administration, Double> results = Mod115DAO.getMod115s(ctx, getOccam().getDomain())
			.filter(mod -> mod.getYear() == aeat.getYear())
			.filter(mod -> mod.getPeriod() == aeat.getPeriod())
			.collect(Collectors.groupingBy(Mod115::getAdministration , Collectors.summingDouble(Mod115::getDeclarationResult)));
			;
		
		Asserts.assertEqualsDouble("Sumatorios no coinciden."
				, results.get(Administration.COMMON_TERRITORY)
				, results.get(Administration.ALAVA));
			
	}

	private Mod115 insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setReplacement(admon == Administration.ALAVA)
			.setComplementary(admon == Administration.COMMON_TERRITORY)
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
