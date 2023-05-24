package net.aonsolutions.occam.test.json;


import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.json.ConfigurationJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;


@ExtendWith(TimingExtension.class)
class ConfigurationJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Configuration expected = ConfigurationJSON.from( json );
		assertNull(expected);
		expected = null;
		json = ConfigurationJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Configuration expected = AonFaker.getConfiguration( );
		JSONObject json = ConfigurationJSON.to(expected);
		System.out.println( json.toString(1) );
		Configuration actual = ConfigurationJSON.from(json);
		Asserts.assertEqualsConfiguration(expected, actual);
	}
	
}
