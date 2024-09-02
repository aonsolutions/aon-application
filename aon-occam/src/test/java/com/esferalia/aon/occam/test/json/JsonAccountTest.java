package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;


public class JsonAccountTest extends AbstractOccamTest {

	@Test
	@RepeatedTest( 20 )
	public void test() {
		Account expected = AonRandom.getAccount( ctx );
		JSONObject json = AccountJSON.toJSON(expected);
		Account actual = AccountJSON.fromJSON(json);
		Asserts.assertEqualsAccount(expected, actual);
		
		if (json != null) {
			actual = AccountJSON.fromString(json.toString());
			Asserts.assertEqualsAccount(expected, actual);
		}
	}
	
}
