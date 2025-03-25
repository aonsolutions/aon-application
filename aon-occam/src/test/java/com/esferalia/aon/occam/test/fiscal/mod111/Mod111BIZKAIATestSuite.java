package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// Modelos mensuales
	Mod111ReopenTest.class,
	Mod111DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod111BIZKAIATest.class,
	// Flujo de estados
	Mod111ReopenTest.class,					// Pendiente
	Mod111FinishTest.class,					// Finalizado
	Mod111SentTest.class,					// Presentado
	// Flujo de estados
	Mod111ReopenTest.class,					// Pendiente
	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod111MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod111SentTest.class,					// Presentado
	// Flujo de estados 
	Mod111ReopenTest.class,					// Pendiente
	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod111MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod111MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod111SentTest.class,					// Presentado
})
public class Mod111BIZKAIATestSuite {

}
