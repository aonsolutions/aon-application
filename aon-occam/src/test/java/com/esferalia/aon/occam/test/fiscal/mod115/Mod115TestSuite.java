package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod115ExpressionsTest.class,
	Mod115ScriptTest.class,
	
	Mod115InsertMonthlySuite.class,
	Mod115InsertMonthlyComplementarySuite.class,
	Mod115InsertMonthlyReplacementSuite.class,
	
	Mod115InsertQuarterlySuite.class,
	Mod115InsertQuarterlyComplementarySuite.class,
	Mod115InsertQuarterlyReplacementSuite.class,
	
	Mod115StatusFlow1Suite.class,
	Mod115StatusFlow2Suite.class,
	Mod115StatusFlow3Suite.class,
})
public class Mod115TestSuite {

}
