package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod390HFReopenTest.class,					// Pendiente
	Mod390HFFinishTest.class,					// Finalizado
	Mod390HFSentTest.class,					// Presentado
})
public class Mod390HFStatusFlow1Suite {

}
