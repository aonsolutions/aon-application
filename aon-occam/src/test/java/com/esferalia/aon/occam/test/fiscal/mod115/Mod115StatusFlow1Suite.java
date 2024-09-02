package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod115ReopenTest.class,					// Pendiente
	Mod115FinishTest.class,					// Finalizado
	Mod115SentTest.class,					// Presentado
})
public class Mod115StatusFlow1Suite {

}
