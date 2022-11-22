package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.time.LocalDate;
import java.util.Date;

import org.junit.Test;

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

public class Mod390HFInsertTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = LocalDate.now().getYear();
		Period period =  Period.YEAR;
		Date start =  FiscalUtils.getPeriodStart(year,period);
		Date end =  FiscalUtils.getPeriodEnd(year,period);
		modInsert(AonRandom.getRangeDate(start,end));
	}
	
	public void modInsert(Date date) {
		System.out.println( "\t ---------------------");
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(date)
				.setProrratePercent( AonRandom.gt(10)? 0 : AonRandom.getPercent() );

		Mod390HF araba = insertModel( params.setAdministration(Administration.ALAVA));
		Mod390HF bizkaia = insertModel( params.setAdministration(Administration.BIZKAIA));
		Mod390HF gipuzkoa = insertModel( params.setAdministration(Administration.GIPUZKOA));
		
		Asserts.assertEqualsDouble("Araba " + araba.getModelFullName() + ". Resultado no coincide.", bizkaia.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Bizkaia " + bizkaia.getModelFullName() + ". Resultado no coincide.", araba.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Gipuzkoa " + gipuzkoa.getModelFullName() + ". Resultado no coincide.", bizkaia.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		
	}

	private Mod390HF insertModel( FiscalFakerParams params) {
		Mod390HF mod = FiscalFaker.createMod390HF(params);
		MODEL390HF.save(getOccam(), mod);
		Mod390HF actual = MODEL390HF.get(getOccam(), mod.getId());  
		Asserts.assertMod390HF(mod, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
	
}
