package com.esferalia.aon.occam.test.fiscal.mod421;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;
 
@RunWith(Suite.class)
@SuiteClasses({
	
	Mod421KeyTest.class,
	Mod421ExpressionsTest.class,
	Mod421ScriptTest.class,

	VatContextJSONTest.class,
	
	Mod421ReopenTest.class,					// Pendiente
	Mod421DeleteTest.class,
	Mod421ValidationTest.class,
	
	Mod421ReopenTest.class,					// Pendiente
	Mod421DeleteTest.class,
	
	InsertRandomInvoicesTest.class,
	Mod421InsertQuarterlyTest.class,

	InsertRandomInvoicesTest.class,
	Mod421InsertQuarterlyComplementaryTest.class,
	
	// Flujo de estados
	Mod421ReopenTest.class,					// Pendiente
	Mod421FinishTest.class,					// Finalizado
	Mod421SentTest.class,					// Presentado
	
	// Flujo de estados
	Mod421ReopenTest.class,					// Pendiente
	Mod421MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod421MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod421SentTest.class,					// Presentado
	
	// Flujo de estados 
	Mod421ReopenTest.class,					// Pendiente
	Mod421MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod421MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod421MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod421MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod421SentTest.class,					// Presentado

})

public class Mod421TestSuite {
	
}
