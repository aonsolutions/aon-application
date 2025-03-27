package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod123ExpressionsTest.class,
	Mod123ScriptTest.class,
	
	// Modelos mensuale
	Mod123ReopenTest.class,
	Mod123DeleteTest.class,
	InsertRandomInvoicesTest.class,

	Mod123SimulateMonthlyTest.class,
	Mod123SimulateQuarterlyTest.class,
	
	Mod123AEATTestSuite.class,
	Mod123ARABATestSuite.class,
	Mod123BIZKAIATestSuite.class,
	Mod123GIPUZKOATestSuite.class,
	Mod123NAVARRATestSuite.class,

})
public class Mod123TestSuite {

}
