package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.json.AccountTrialBalanceReportJSON;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AccountingFaker;


public class JsonAccountTrialBalanceReportTest extends AbstractOccamTest {

	@Test
	@RepeatedTest( 20 )
	public void test() {
		AccountTrialBalanceReport expected = AccountingFaker.getAccountTrialBalanceReport( ctx );
		JSONObject json = AccountTrialBalanceReportJSON.toJSON(expected);
		AccountTrialBalanceReport actual = AccountTrialBalanceReportJSON.fromJSON(json);
		Asserts.assertEqualsAccountTrialBalanceReport(expected, actual);
		
		actual = AccountTrialBalanceReportJSON.fromString(json.toString());
		Asserts.assertEqualsAccountTrialBalanceReport(expected, actual);
	}
	
}
