package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	Mod303ValidationTest.class,
})
public class Mod303ValidationSuite {

}
