package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountingReportParamsJSON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;


public class JsonAccountingReportParamsTest extends AbstractOccamTest {

	@Test
	@Repeat( 50 )
	public void test() {
		AccountingReportParams expected = AonFaker.getAccountingReportParams( ctx );
		JSONObject json = AccountingReportParamsJSON.toJSON(expected);
		System.out.println( json.toString(2) );
		AccountingReportParams actual = AccountingReportParamsJSON.fromJSON(json);
		Asserts.assertEqualsAccountingReportParams(expected, actual);
		
		actual = AccountingReportParamsJSON.fromString(json.toString());
		Asserts.assertEqualsAccountingReportParams(expected, actual);
	}
	
}
