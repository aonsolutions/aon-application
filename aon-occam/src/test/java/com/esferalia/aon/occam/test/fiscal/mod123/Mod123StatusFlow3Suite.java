package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod123ReopenTest.class,					// Pendiente
	Mod123MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod123MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod123MarkAsCustomerReCheckTest.class,	// Enviado a cliente
	Mod123MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod123SentTest.class,					// Presentado
})
public class Mod123StatusFlow3Suite {

}
