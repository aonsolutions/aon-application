package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonDateUtils;

public class Mod303ExpressionsTest extends Mod303AbstractTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		test( Administration.COMMON_TERRITORY );
	}

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
	}
	@Test
	public void testNavarraExpression() {
		test( Administration.NAVARRA);
	}
	
	private void test( Administration admon) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(AonDateUtils.getFirstDayOfYear( getTestDate()))
			.setMonthly(true)
			.setAdministration(admon);
		Mod303 mod303 = FiscalFaker.getMod303(params);
		MODEL303.calculate(getOccam(), mod303);
	}
}
