package net.aonsolutions.occam.impl;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import net.aonsolutions.occam.impl.handler.OccamImplHandlersTestSuite;

@Suite
@SuiteDisplayName("OCCAM IMPL TEST SUITE")
@SelectPackages({
	 "net.aonsolutions.occam.impl.generic"
	,"net.aonsolutions.occam.impl.json"
})
@SelectClasses({
	 OccamImplHandlersTestSuite.class
})
public class OccamImplTestSuite {

}
