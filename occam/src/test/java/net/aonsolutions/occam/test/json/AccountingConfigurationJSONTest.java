package net.aonsolutions.occam.test.json;


import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.AccountingConfiguration;
import net.aonsolutions.occam.json.AccountingConfigurationJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;


@ExtendWith(TimingExtension.class)
class AccountingConfigurationJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		AccountingConfiguration expected = AccountingConfigurationJSON.from( json );
		assertNull(expected);
		expected = null;
		json = AccountingConfigurationJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		AccountingConfiguration expected = AonFaker.getAccountingConfiguration( );
		JSONObject json = AccountingConfigurationJSON.to(expected);
		AccountingConfiguration actual = AccountingConfigurationJSON.from(json);
		Asserts.assertEqualsAccountingConfiguration(expected, actual);
	}
	
}
