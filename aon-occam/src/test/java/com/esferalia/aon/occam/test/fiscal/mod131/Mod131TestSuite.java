package com.esferalia.aon.occam.test.fiscal.mod131;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod131KeyTest.class,
	Mod131ExpressionsTest.class,
	
	Mod131InsertSuite.class,
	Mod131InsertComplementarySuite.class,
	Mod131StatusFlow1Suite.class,
	Mod131StatusFlow2Suite.class,
	Mod131StatusFlow3Suite.class,
})
public class Mod131TestSuite {

	
}
