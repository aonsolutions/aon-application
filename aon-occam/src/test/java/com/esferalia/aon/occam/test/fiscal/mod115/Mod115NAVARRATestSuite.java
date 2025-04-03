package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// Modelos mensuales
	Mod115ReopenTest.class,
	Mod115DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod115NAVARRATest.class,
	// Flujo de estados
	Mod115ReopenTest.class,					// Pendiente
	Mod115FinishTest.class,					// Finalizado
	Mod115SentTest.class,					// Presentado
	// Flujo de estados
	Mod115ReopenTest.class,					// Pendiente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod115SentTest.class,					// Presentado
	// Flujo de estados 
	Mod115ReopenTest.class,					// Pendiente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod115SentTest.class,					// Presentado
})
public class Mod115NAVARRATestSuite {

}
