package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.SeriesJSON;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;


public class JsonSeriesTest extends AbstractOccamTest {

	@Test
	@Repeat( 20 )
	public void test() {
		Series expected = AonFaker.getSeries();
		JSONObject json = SeriesJSON.to(expected);
		Series actual = SeriesJSON.from(json);
		Asserts.assertEqualsSeries(expected, actual);
		
		if (json != null) {
			actual = SeriesJSON.from(json.toString());
			Asserts.assertEqualsSeries(expected, actual);
		}
	}
	
}
