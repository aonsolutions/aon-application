package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111AEATTestSuite;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111ARABATestSuite;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111BIZKAIATestSuite;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111GIPUZKOATestSuite;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111NAVARRATestSuite;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111SimulateMonthlyTest;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111SimulateQuarterlyTest;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod115ExpressionsTest.class,
	Mod115ScriptTest.class,
	
	// Modelos mensuale
	Mod115ReopenTest.class,
	Mod115DeleteTest.class,
	InsertRandomInvoicesTest.class,
	
	Mod115SimulateMonthlyTest.class,
	Mod115SimulateQuarterlyTest.class,
	
	Mod115AEATTestSuite.class,
	Mod115ARABATestSuite.class,
	Mod115BIZKAIATestSuite.class,
	Mod115GIPUZKOATestSuite.class,
	Mod115NAVARRATestSuite.class,
	
})
public class Mod115TestSuite {

}
