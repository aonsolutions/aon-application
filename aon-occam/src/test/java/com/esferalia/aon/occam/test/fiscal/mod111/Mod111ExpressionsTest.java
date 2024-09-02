package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;

public class Mod111ExpressionsTest extends AbstractOccamTest {
	
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
		Mod111 mod111 = FiscalFaker.getMod111(params);
		MODEL111.calculate(getOccam(), mod111);
	}
}
