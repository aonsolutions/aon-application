package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod390HFPreapare390InsertTest.class,	
	Mod390HFInsertTest.class,
	Mod390HFRoundedAmountsTest.class,
	Mod390HFFinishTest.class,
})
public class Mod390HFValidationInsertSuite {

}
