package net.aonsolutions.watson.test.client;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 PairTest.class
	 ,MutableBooleanTest.class
})
public class ClientTestSuite {

}
