package com.esferalia.aon.occam.test.fiscal.mod131;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod131ReopenTest.class,					// Pendiente
	Mod131MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod131MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod131MarkAsCustomerReCheckTest.class,	// Enviado a cliente
	Mod131MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod131SentTest.class,					// Presentado
})
public class Mod131StatusFlow3Suite {

}
