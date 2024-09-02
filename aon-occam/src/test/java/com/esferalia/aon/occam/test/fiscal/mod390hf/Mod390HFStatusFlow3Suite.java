package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Mod390HFReopenTest.class,					// Pendiente
	Mod390HFMarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod390HFMarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod390HFMarkAsCustomerReCheckTest.class,	// Enviado a cliente
	Mod390HFMarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod390HFSentTest.class,					// Presentado
})
public class Mod390HFStatusFlow3Suite {

}
