package com.esferalia.aon.occam.test.fiscal.mod130;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod130ExpressionsTest.class,
	Mod130ScriptTest.class,
	
	Mod130InsertSuite.class,
	Mod130InsertComplementarySuite.class,
	Mod130StatusFlow1Suite.class,
	Mod130StatusFlow2Suite.class,
	Mod130StatusFlow3Suite.class,
	
})
public class Mod130TestSuite {

}
