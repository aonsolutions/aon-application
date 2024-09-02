package com.esferalia.aon.occam.test.fiscal.mod123;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;

public class Mod123ExpressionsTest extends AbstractOccamTest {
	
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
		test(admon, true);
		test(admon, false);
	}
	
	private void test( Administration admon, boolean monthly) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(new Date())
			.setMonthly(monthly)
			.setAdministration(admon);
		Mod123 mod123 = FiscalFaker.getMod123(params);
		MODEL123.calculate(getOccam(), mod123);
	}
}
