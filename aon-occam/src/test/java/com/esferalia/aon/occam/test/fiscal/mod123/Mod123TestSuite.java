package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod123ExpressionsTest.class,
	Mod123ScriptTest.class,
	
	Mod123InsertMonthlySuite.class,
	Mod123InsertMonthlyComplementarySuite.class,
	Mod123InsertMonthlyReplacementSuite.class,
	
	Mod123InsertQuarterlySuite.class,
	Mod123InsertQuarterlyComplementarySuite.class,
	Mod123InsertQuarterlyReplacementSuite.class,
	
	Mod123StatusFlow1Suite.class,
	Mod123StatusFlow2Suite.class,
	Mod123StatusFlow3Suite.class,
})
public class Mod123TestSuite {

}
