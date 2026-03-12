package com.esferalia.aon.occam.test.fiscal.mod421;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonDateUtils;

public class Mod421ExpressionsTest extends Mod421AbstractTest {
	
	@Test
	public void testCanariasExpression() {
		test(Administration.CANARIAS);
	}
	
	private void test( Administration admon) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(AonDateUtils.getFirstDayOfYear(getTestDate()))
			.setMonthly(false)
			.setAdministration(admon);
		Mod421 mod421 = FiscalFaker.getMod421(params);
		MODEL421.calculate(getOccam(), mod421);
	}
}
