package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod111ExpressionsTest.class,
	Mod111ScriptTest.class,
	
	// Modelos mensuales
	Mod111ReopenTest.class,
	Mod111DeleteTest.class,
	InsertRandomInvoicesTest.class,
	
	Mod111SimulateMonthlyTest.class,
	Mod111SimulateQuarterlyTest.class,
	
	Mod111AEATTestSuite.class,
	Mod111ARABATestSuite.class,
	Mod111BIZKAIATestSuite.class,
	Mod111GIPUZKOATestSuite.class,
	Mod111NAVARRATestSuite.class,
})
public class Mod111TestSuite {

}
