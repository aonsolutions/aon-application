package com.esferalia.aon.occam.test.ddff.json;


import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.ddff.json.AeatFiscalDataJSON;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;


public class AeatFiscalDataJSONTest extends AbstractOccamTest {

	@Test
	@Repeat( 10 )
	public void test() {
		AeatFiscalData expected = AonFaker.getAeatFiscalData();
		JSONObject json = AeatFiscalDataJSON.to(expected);
		AeatFiscalData actual = AeatFiscalDataJSON.from(json);
		Asserts.assertEqualsAeatFiscalData(expected, actual);
		if (json != null) {
			actual = AeatFiscalDataJSON.from(json.toString());
			Asserts.assertEqualsAeatFiscalData(expected, actual);
		}
	}
	
}
