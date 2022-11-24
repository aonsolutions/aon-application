package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod115ExpressionsTest.class,
	Mod115ScriptTest.class,
	
	// Modelos mensuale
	Mod115ReopenTest.class,
	Mod115DeleteTest.class,
	Mod115InsertInvoicesTest.class,
	Mod115InsertMonthlyTest.class,
	Mod115FinishTest.class,
	
	Mod115InsertInvoicesTest.class,
	Mod115InsertMonthlyComplementaryTest.class,
	Mod115FinishTest.class,
	
	Mod115InsertInvoicesTest.class,
	Mod115InsertMonthlyReplacementTest.class,
	Mod115RoundedAmountsTest.class,
	
	// Modelos trimestrales
	Mod115ReopenTest.class,
	Mod115DeleteTest.class,
	Mod115InsertInvoicesTest.class,
	Mod115InsertQuarterlyTest.class,
	Mod115FinishTest.class,
	
	Mod115InsertInvoicesTest.class,
	Mod115InsertQuarterlyComplementaryTest.class,
	Mod115FinishTest.class,

	Mod115InsertInvoicesTest.class,
	Mod115InsertQuarterlyReplacementTest.class,
	Mod115RoundedAmountsTest.class,
	
//	// Flujo de estados
	Mod115ReopenTest.class,					// Pendiente
	Mod115FinishTest.class,					// Finalizado
	Mod115SentTest.class,					// Presentado
//	// Flujo de estados
	Mod115ReopenTest.class,					// Pendiente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod115SentTest.class,					// Presentado
//	// Flujo de estados 
	Mod115ReopenTest.class,					// Pendiente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod115MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod115MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod115SentTest.class,					// Presentado
	
})
public class Mod115TestSuite {

}
