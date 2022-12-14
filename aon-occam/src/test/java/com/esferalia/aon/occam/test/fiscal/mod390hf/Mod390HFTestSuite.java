package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	Mod390HFKeyTest.class,
	Mod390HFExpressionsTest.class,
	Mod390HFScriptTest.class,

	Mod390HFReopenTest.class,
	Mod390HFDeleteTest.class,
	Mod390HFValidationTest.class,
	
	Mod390HFPreapare390InsertTest.class,	
	Mod390HFInsertTest.class,
	Mod390HFRoundedAmountsTest.class,
	Mod390HFFinishTest.class,
	
	Mod390HFInsertInvoicesTest.class,
	Mod390HFInsertComplementaryTest.class,
	Mod390HFRoundedAmountsTest.class,
	Mod390HFFinishTest.class,
	
	Mod390HFInsertInvoicesTest.class,
	Mod390HFInsertReplacementTest.class,
	Mod390HFRoundedAmountsTest.class,
	Mod390HFFinishTest.class,

	// Flujo de estados
	Mod390HFReopenTest.class,		// Pendiente
	Mod390HFFinishTest.class,		// Finalizado
	Mod390HFSentTest.class,			// Presentado
	
	// Flujo de estados
	Mod390HFReopenTest.class,					// Pendiente
	Mod390HFMarkAsCustomerCheckTest.class,		// Enviado a cliente
	Mod390HFMarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod390HFSentTest.class,						// Presentado

	// Flujo de estados 
	Mod390HFReopenTest.class,					// Pendiente
	Mod390HFMarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod390HFMarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod390HFMarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod390HFMarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod390HFSentTest.class,					// Presentado
})
public class Mod390HFTestSuite {

	
}
