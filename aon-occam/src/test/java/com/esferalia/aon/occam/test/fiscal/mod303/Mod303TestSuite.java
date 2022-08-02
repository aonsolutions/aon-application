package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
 
@RunWith(Suite.class)
@SuiteClasses({
	
	Mod303KeyTest.class,
	Mod303ExpressionsTest.class,
	Mod303ScriptTest.class,

	VatContextJSONTest.class,
	
	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	Mod303ValidationTest.class,
	
	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	Mod303InsertInvoicesTest.class,
	Mod303InsertMonthlyTest.class,
	Mod303RoundedAmountsTest.class,
	Mod303FinishTest.class,
	
	Mod303InsertInvoicesTest.class,
	Mod303InsertMonthlyComplementaryTest.class,
	Mod303RoundedAmountsTest.class,
	Mod303FinishTest.class,
	
	Mod303InsertInvoicesTest.class,
	Mod303InsertMonthlyReplacementTest.class,
	Mod303RoundedAmountsTest.class,

	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	
	Mod303InsertInvoicesTest.class,
	Mod303InsertQuarterlyTest.class,
	Mod303RoundedAmountsTest.class,
	Mod303FinishTest.class,
	
	Mod303InsertInvoicesTest.class,
	Mod303InsertQuarterlyComplementaryTest.class,
	Mod303RoundedAmountsTest.class,
	Mod303FinishTest.class,
	
	Mod303InsertInvoicesTest.class,
	Mod303InsertQuarterlyReplacementTest.class,
	Mod303RoundedAmountsTest.class,
//
//	// Flujo de estados
	Mod303ReopenTest.class,					// Pendiente
	Mod303FinishTest.class,					// Finalizado
	Mod303SentTest.class,					// Presentado
//	// Flujo de estados
	Mod303ReopenTest.class,					// Pendiente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod303SentTest.class,					// Presentado
//	// Flujo de estados 
	Mod303ReopenTest.class,					// Pendiente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod303SentTest.class,					// Presentado
	
//	Mod303RecordTest.class,					// Contabilizar
//	Mod303UnrecordTest.class,				// Descontabilizar
})

public class Mod303TestSuite {

	
}
