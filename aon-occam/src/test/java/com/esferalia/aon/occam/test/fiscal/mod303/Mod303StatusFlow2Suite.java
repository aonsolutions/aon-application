package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod303ReopenTest.class,					// Pendiente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod303SentTest.class,					// Presentado
})
public class Mod303StatusFlow2Suite {

}
