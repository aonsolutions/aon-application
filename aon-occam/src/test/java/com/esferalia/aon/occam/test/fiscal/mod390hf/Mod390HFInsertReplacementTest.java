package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod390HFInsertReplacementTest extends AbstractOccamTest {
	
	@Test
	public void modInsertReplacementTest() {
		Date today = new Date();
		Period period = Period.YEAR;
		Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
		Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
		modInsertReplacement(AonRandom.getRangeDate(start,end));
	}
	
	public void modInsertReplacement(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod390HF araba = insertModel( Administration.ALAVA,date);
		insertModel( Administration.BIZKAIA,date);
		
		Map<Administration, Double> results = Mod390HFDAO.getMod390HFs(ctx, getOccam().getDomain())
			.filter(mod -> mod.getYear() == araba.getYear())
			.filter(mod -> mod.getPeriod() == araba.getPeriod())
			.collect(Collectors.groupingBy(Mod390HF::getAdministration , Collectors.summingDouble(Mod390HF::getDeclarationResult)));
			;
		
		
		Asserts.assertEqualsDouble("Sumatorios no coinciden."
			, results.get(Administration.ALAVA)
			, results.get(Administration.BIZKAIA));
	}
		
	

	private Mod390HF insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setMonthly(true)
			.setAdministration(admon)
			.setReplacement(admon == Administration.ALAVA || admon == Administration.NAVARRA)
			.setComplementary(admon == Administration.COMMON_TERRITORY || admon == Administration.BIZKAIA)
			.setGenerateFromYearStart(true)
			;
		Mod390HF mod = FiscalFaker.createMod390HF(params);
		MODEL390HF.save(getOccam(), mod);
		Mod390HF actual = MODEL390HF.get(getOccam(), mod.getId());  
		Asserts.assertMod390HF(mod, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
}
