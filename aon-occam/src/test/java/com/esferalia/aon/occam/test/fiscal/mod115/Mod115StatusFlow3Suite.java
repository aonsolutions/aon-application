package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod115ReopenTest.class,					// Pendiente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod115MarkAsCustomerReCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod115SentTest.class,					// Presentado
})
public class Mod115StatusFlow3Suite {

}
