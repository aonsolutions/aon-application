package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod111ExpressionsTest.class,
	Mod111ScriptTest.class,
	
	Mod111InsertMonthlySuite.class,
	Mod111InsertMonthlyComplementarySuite.class,
	Mod111InsertMonthlyReplacementSuite.class,
	
	Mod111InsertQuarterlySuite.class,
	Mod111InsertQuarterlyComplementarySuite.class,
	Mod111InsertQuarterlyReplacementSuite.class,
	
	Mod111StatusFlow1Suite.class,
	Mod111StatusFlow2Suite.class,
	Mod111StatusFlow3Suite.class,
})
public class Mod111TestSuite {

}
