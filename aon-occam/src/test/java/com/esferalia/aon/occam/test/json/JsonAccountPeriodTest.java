package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.json.AccountPeriodJSON;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;


public class JsonAccountPeriodTest extends AbstractOccamTest {

	@Test
	@RepeatedTest( 10 )
	public void test() {
		AccountPeriod expected = AonRandom.getAccountPeriod( ctx );
		JSONObject json = AccountPeriodJSON.toJSON(expected);
		AccountPeriod actual = AccountPeriodJSON.fromJSON(json);
		Asserts.assertEqualsAccountPeriod(expected, actual);
		if (json != null) {
			actual = AccountPeriodJSON.fromString(json.toString());
			Asserts.assertEqualsAccountPeriod(expected, actual);
		}
	}
	
}
