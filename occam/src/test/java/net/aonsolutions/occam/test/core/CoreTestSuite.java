package net.aonsolutions.occam.test.core;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	AonContextTest.class
	,FilterTest.class
})
public class CoreTestSuite {

	
}
