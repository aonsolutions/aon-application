package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
 
@Suite
@SelectClasses({
	
	Mod303KeyTest.class,
	Mod303ExpressionsTest.class,
	Mod303ScriptTest.class,
	VatContextJSONTest.class,
	
	Mod303ValidationSuite.class,
	Mod303InsertMonthlySuite.class,
	Mod303InsertMonthlyComplementarySuite.class,
	Mod303InsertMonthlyReplacementSuite.class,
	Mod303InsertQuarterlySuite.class,
	Mod303InsertQuarterlyComplementarySuite.class,
	Mod303InsertQuarterlyReplacementSuite.class,

	Mod303StatusFlow1Suite.class,
	Mod303StatusFlow2Suite.class,
	Mod303StatusFlow3Suite.class,

	//  PENDIENTE!!	
	//	Mod303RecordTest.class,					// Contabilizar
	//	Mod303UnrecordTest.class,				// Descontabilizar
})

public class Mod303TestSuite {
	
}
