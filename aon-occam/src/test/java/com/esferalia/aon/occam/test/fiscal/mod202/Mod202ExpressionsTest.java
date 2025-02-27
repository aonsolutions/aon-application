package com.esferalia.aon.occam.test.fiscal.mod202;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;

public class Mod202ExpressionsTest extends AbstractOccamTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		test( Administration.COMMON_TERRITORY );
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
		Mod202 mod202 = FiscalFaker.getMod202(params);
		MODEL202.calculate(getOccam(), mod202);
	}
}
