package com.esferalia.aon.occam.test.fiscal.mod130;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod130ReopenTest.class,					// Pendiente
	Mod130FinishTest.class,					// Finalizado
	Mod130SentTest.class,					// Presentado
})
public class Mod130StatusFlow1Suite {

}
