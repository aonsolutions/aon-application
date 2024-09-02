package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod390HFReopenTest.class,
	Mod390HFDeleteTest.class,
	Mod390HFValidationTest.class,
})
public class Mod390HFValidationSuite {

}
