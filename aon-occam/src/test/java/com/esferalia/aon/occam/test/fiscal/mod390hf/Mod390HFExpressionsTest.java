package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod390HFExpressionsTest extends AbstractOccamTest {
	
	@Test
	public void testArabaExpressions() {
		test( Administration.ALAVA);
	}
	
	@Test
	public void testBizkaiaExpression() {
		test( Administration.BIZKAIA);
	}
	@Test
	public void testGipuzkoaExpression() {
		test( Administration.GIPUZKOA);
		testPastYear( Administration.GIPUZKOA);
	}
	
	private void test( Administration admon) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(new Date())
			.setMonthly(true)
			.setAdministration(admon);
		Mod390HF mod = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod);
	}
	
	private void testPastYear( Administration admon) {
		
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate( AonRandom.getRandomYearDay( AonDateUtils.getYear( new Date() ) )) 
			.setMonthly(true)
			.setAdministration(admon);
		Mod390HF mod = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod);
	}
	
}
