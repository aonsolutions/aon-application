package net.aonsolutions.watson.test.server;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 AonDateUtilsTests.class
	 ,AonEnumUtilsTests.class
	 ,AonObjectUtilsTests.class
})
public class ServerTestSuite {

}
