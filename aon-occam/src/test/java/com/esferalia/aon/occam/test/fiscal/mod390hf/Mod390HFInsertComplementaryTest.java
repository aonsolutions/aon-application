package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
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

public class Mod390HFInsertComplementaryTest extends AbstractOccamTest {

	@Test
	public void mod303InsertMonthlyComplementaryTest() {
		Date today = new Date();
		Period period =  Period.YEAR;
		Date start =  FiscalUtils.getPeriodStart(AonDateUtils.getYear(today),period);
		Date end =  FiscalUtils.getPeriodEnd(AonDateUtils.getYear(today),period);
		modInsertComplementary(AonRandom.getRangeDate(start,end));
	}
	
	public void modInsertComplementary(Date date) {
		System.out.println( "\t ---------------------");
		
		Mod390HF gipuzkoa = insertModel( Administration.GIPUZKOA,date);
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", gipuzkoa.getDeclarationResult(), gipuzkoa.getDeclarationResult());
	}
	
	private Mod390HF insertModel( Administration admon, Date date) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(date)
			.setAdministration(admon)
			.setComplementary(true)
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
