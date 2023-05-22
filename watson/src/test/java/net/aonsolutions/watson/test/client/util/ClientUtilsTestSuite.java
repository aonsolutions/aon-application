package net.aonsolutions.watson.test.client.util;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	AonArrayUtilsTests.class
	,AonCollectionUtilsTests.class
	,AonNumberUtilsTests.class
	,AonCharSequenceUtilsTests.class
	,AonStringUtilsTests.class
})
public class ClientUtilsTestSuite {

}
