package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// Modelos mensuales
	Mod123ReopenTest.class,
	Mod123DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod123BIZKAIATest.class,
	// Flujo de estados
	Mod123ReopenTest.class,					// Pendiente
	Mod123FinishTest.class,					// Finalizado
	Mod123SentTest.class,					// Presentado
	// Flujo de estados
	Mod123ReopenTest.class,					// Pendiente
	Mod123MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod123MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod123SentTest.class,					// Presentado
	// Flujo de estados 
	Mod123ReopenTest.class,					// Pendiente
	Mod123MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod123MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod123MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod123MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod123SentTest.class,					// Presentado
})
public class Mod123BIZKAIATestSuite {

}
