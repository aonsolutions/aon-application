package net.aonsolutions.watson.test.client;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 PairTest.class
	 ,MutableBooleanTest.class
	 ,MutableObjectTest.class
})
public class ClientTestSuite {

}
