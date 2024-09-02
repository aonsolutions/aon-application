package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod123ReopenTest.class,					// Pendiente
	Mod123FinishTest.class,					// Finalizado
	Mod123SentTest.class,					// Presentado
})
public class Mod123StatusFlow1Suite {

}
