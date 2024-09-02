package com.esferalia.aon.occam.test.fiscal.mod349;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod349MarkAsPendingTest.class,
	Mod349MarkAsFinishedTest.class,
	Mod349MarkAsSentTest.class,
})
public class Mod349FlowStatusSuite {

}
