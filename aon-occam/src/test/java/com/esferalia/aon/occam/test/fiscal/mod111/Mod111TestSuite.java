package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod111ExpressionsTest.class,
	Mod111ScriptTest.class,
	
	// Modelos mensuale
	Mod111ReopenTest.class,					// Pendiente
	Mod111DeleteTest.class,
//	InsertRandomInvoicesTest.class,
//	Mod111InsertMonthlyTest.class,
//	Mod111FinishTest.class,
//	
//	InsertRandomInvoicesTest.class,
//	Mod111InsertMonthlyComplementaryTest.class,
//	Mod111FinishTest.class,
//	
//	InsertRandomInvoicesTest.class,
//	Mod111InsertMonthlyReplacementTest.class,
//	Mod111RoundedAmountsTest.class,
//	
//	// Modelos trimestrales
//	Mod111ReopenTest.class,					// Pendiente
//	Mod111DeleteTest.class,
//	InsertRandomInvoicesTest.class,
//	Mod111InsertQuarterlyTest.class,
//	Mod111FinishTest.class,
//	
//	InsertRandomInvoicesTest.class,
//	Mod111InsertQuarterlyComplementaryTest.class,
//	Mod111FinishTest.class,
//	
//	InsertRandomInvoicesTest.class,
//	Mod111InsertQuarterlyReplacementTest.class,
//	Mod111RoundedAmountsTest.class,
//	
//	// IRPF JSON 
//	IrpfBreakdownJSONTest.class,
//	
//	// Flujo de estados
//	Mod111ReopenTest.class,					// Pendiente
//	Mod111FinishTest.class,					// Finalizado
//	Mod111SentTest.class,					// Presentado
//	// Flujo de estados
//	Mod111ReopenTest.class,					// Pendiente
//	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
//	Mod111MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
//	Mod111SentTest.class,					// Presentado
//	// Flujo de estados 
//	Mod111ReopenTest.class,					// Pendiente
//	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
//	Mod111MarkAsCustomerRejectedTest.class, // Rechazado por cliente
//	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
//	Mod111MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
//	Mod111SentTest.class,					// Presentado
	
})
public class Mod111TestSuite {

}
