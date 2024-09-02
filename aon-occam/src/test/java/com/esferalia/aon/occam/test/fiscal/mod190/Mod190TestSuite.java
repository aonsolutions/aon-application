package com.esferalia.aon.occam.test.fiscal.mod190;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	// Modelos mensuale
	Mod190ReopenTest.class,					// Pendiente
	Mod190DeleteTest.class,
	Mod190InsertTest.class,
	
})
public class Mod190TestSuite {

}
