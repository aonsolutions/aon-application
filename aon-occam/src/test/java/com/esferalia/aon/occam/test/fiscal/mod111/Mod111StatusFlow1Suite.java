package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod111ReopenTest.class,					// Pendiente
	Mod111FinishTest.class,					// Finalizado
	Mod111SentTest.class,					// Presentado
})
public class Mod111StatusFlow1Suite {

}
