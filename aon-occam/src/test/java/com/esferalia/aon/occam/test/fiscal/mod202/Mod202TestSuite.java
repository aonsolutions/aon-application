package com.esferalia.aon.occam.test.fiscal.mod202;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.mod111.Mod111RoundedAmountsTest;

@RunWith(Suite.class)
@SuiteClasses({
	Mod202ExpressionsTest.class,
	Mod202ScriptTest.class,
	
	Mod202Delete.class,
	Mod202Insert.class,
	Mod111RoundedAmountsTest.class,
	
//	// Flujo de estados
	Mod202ReopenTest.class,					// Pendiente
	Mod202FinishTest.class,					// Finalizado
	Mod202SentTest.class,					// Presentado
//	// Flujo de estados
	Mod202ReopenTest.class,					// Pendiente
	Mod202MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod202MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod202SentTest.class,					// Presentado
//	// Flujo de estados 
	Mod202ReopenTest.class,					// Pendiente
	Mod202MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod202MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod202MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod202MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod202SentTest.class,					// Presentado
	
})
public class Mod202TestSuite {

	
}
