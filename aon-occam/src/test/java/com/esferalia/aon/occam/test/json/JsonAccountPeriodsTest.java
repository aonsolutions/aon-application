package com.esferalia.aon.occam.test.json;

import java.util.Collection;

import org.json.JSONArray;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountPeriodsJSON;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class JsonAccountPeriodsTest extends AbstractOccamTest{

	@Test
	@Repeat ( 100 )
	public void test() {
		Collection<AccountPeriod> expected = AccountingFaker.getAccountPeriods(ctx);
		JSONArray json = AccountPeriodsJSON.toJSON(expected);
		Collection<AccountPeriod> actual = AccountPeriodsJSON.fromJSON(json);

		int i = 0;
		
		for (AccountPeriod expectedPeriod : expected) {
				AccountPeriod actualPeriod = null;
				
				int j=0;
				
				for (AccountPeriod aPeriod : actual) {
					if (j++<=i) {
						actualPeriod = aPeriod;
					}	
				}
				Asserts.assertEqualsAccountPeriod(expectedPeriod, actualPeriod);
				i++;
		}
		
	}

}
