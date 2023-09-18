package com.esferalia.aon.occam.test.fiscal.mod130;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	Mod130ExpressionsTest.class,
	Mod130ScriptTest.class,
	
	Mod130ReopenTest.class,
	Mod130DeleteTest.class,
	Mod130InsertTest.class,
	Mod130FinishTest.class,
	
	Mod130InsertComplementaryTest.class,
	Mod130FinishTest.class,
	
	Mod130RoundedAmountsTest.class,
	
//	// Flujo de estados
	Mod130ReopenTest.class,					// Pendiente
	Mod130FinishTest.class,					// Finalizado
	Mod130SentTest.class,					// Presentado
//	// Flujo de estados
	Mod130ReopenTest.class,					// Pendiente
	Mod130MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod130MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod130SentTest.class,					// Presentado
//	// Flujo de estados 
	Mod130ReopenTest.class,					// Pendiente
	Mod130MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod130MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod130MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod130MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod130SentTest.class,					// Presentado
	
})
public class Mod130TestSuite {

	
}
