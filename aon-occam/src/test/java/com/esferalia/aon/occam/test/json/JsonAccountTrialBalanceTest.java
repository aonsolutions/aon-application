package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.json.AccountTrialBalanceJSON;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AccountingFaker;


public class JsonAccountTrialBalanceTest extends AbstractOccamTest {

	@Test
	@RepeatedTest( 20 )
	public void test() {
		AccountTrialBalance expected = AccountingFaker.getAccountTrialBalance( ctx );
		JSONObject json = AccountTrialBalanceJSON.toJSON(expected);
		AccountTrialBalance actual = AccountTrialBalanceJSON.fromJSON(json);
		Asserts.assertEqualsAccountTrialBalance(expected, actual);
		
		actual = AccountTrialBalanceJSON.fromString(json.toString());
		Asserts.assertEqualsAccountTrialBalance(expected, actual);
	}
	
}
