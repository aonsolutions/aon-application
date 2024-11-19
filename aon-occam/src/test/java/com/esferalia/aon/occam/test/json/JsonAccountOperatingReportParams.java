package com.esferalia.aon.occam.test.json;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountOperatingReportJSON;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AccountingFaker;

public class JsonAccountOperatingReportParams extends AbstractOccamTest {

	@Test
	@Repeat( 20 )
	public void test() {
		AccountOperatingReport expected = AccountingFaker.getAccountOperatingReport(ctx);
		JSONObject json = AccountOperatingReportJSON.toJSON(expected);
		AccountOperatingReport actual = AccountOperatingReportJSON.fromJSON(json);
		Asserts.assertEqualsAccountOperatingReport(expected, actual);
		
	}

}
