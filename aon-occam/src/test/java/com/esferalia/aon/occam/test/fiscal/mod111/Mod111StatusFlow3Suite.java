package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod111ReopenTest.class,					// Pendiente
	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod111MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod111MarkAsCustomerReCheckTest.class,	// Enviado a cliente
	Mod111MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod111SentTest.class,					// Presentado
})
public class Mod111StatusFlow3Suite {

}
