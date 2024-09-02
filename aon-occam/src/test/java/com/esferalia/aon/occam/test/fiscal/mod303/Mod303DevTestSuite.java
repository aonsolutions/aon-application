package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.mod390hf.Mod390HFDeleteTest;
import com.esferalia.aon.occam.test.fiscal.mod390hf.Mod390HFReopenTest;
 
@Suite
@SelectClasses({
	
	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	Mod390HFReopenTest.class,
	Mod390HFDeleteTest.class,
})

public class Mod303DevTestSuite {
	
}
