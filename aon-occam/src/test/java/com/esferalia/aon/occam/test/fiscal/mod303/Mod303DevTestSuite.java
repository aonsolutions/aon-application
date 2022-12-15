package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.mod390hf.Mod390HFDeleteTest;
import com.esferalia.aon.occam.test.fiscal.mod390hf.Mod390HFReopenTest;
 
@RunWith(Suite.class)
@SuiteClasses({
	
	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	Mod390HFReopenTest.class,
	Mod390HFDeleteTest.class,
})

public class Mod303DevTestSuite {
	
}
