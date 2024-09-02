package com.esferalia.aon.occam.test.fiscal.mod131;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod131ReopenTest.class,					// Pendiente
	Mod131FinishTest.class,					// Finalizado
	Mod131SentTest.class,					// Presentado
})
public class Mod131StatusFlow1Suite {

}
