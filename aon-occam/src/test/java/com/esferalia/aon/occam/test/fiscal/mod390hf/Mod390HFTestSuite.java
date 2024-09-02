package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod390HFKeyTest.class,
	Mod390HFExpressionsTest.class,
	Mod390HFScriptTest.class,

	Mod390HFValidationSuite.class,
	Mod390HFValidationInsertSuite.class,
	Mod390HFValidationInsertComplementarySuite.class,
	Mod390HFValidationInsertReplacementSuite.class,

	Mod390HFStatusFlow1Suite.class,
	Mod390HFStatusFlow2Suite.class,
	Mod390HFStatusFlow3Suite.class,
})
public class Mod390HFTestSuite {

	
}
