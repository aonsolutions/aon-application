package com.esferalia.aon.occam.test.fiscal.mod190;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	// Modelos mensuale
	Mod190ReopenTest.class,					// Pendiente
	Mod190DeleteTest.class,
	Mod190InsertTest.class,
	
})
public class Mod190TestSuite {

}
