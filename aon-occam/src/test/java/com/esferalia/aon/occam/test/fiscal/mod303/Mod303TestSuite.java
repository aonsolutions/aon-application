package com.esferalia.aon.occam.test.fiscal.mod303;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.watson.util.AonStringUtils;
 
@RunWith(Suite.class)
@SuiteClasses({
	
	Mod303ExpressionsTest.class,
	Mod303ScriptTest.class,

	VatContextJSONTest.class,
	
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
	
	
	
//	Mod303CheckInsertedTest.class,
	
	// Flujo de estados
	Mod303ReopenTest.class,					// Pendiente
	Mod303FinishTest.class,					// Finalizado
	Mod303SentTest.class,					// Presentado
	// Flujo de estados
	Mod303ReopenTest.class,					// Pendiente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod303SentTest.class,					// Presentado
	// Flujo de estados 
	Mod303ReopenTest.class,					// Pendiente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerRejectedTest.class, // Rechazado por cliente
	Mod303MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod303MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
	Mod303SentTest.class,					// Presentado
	
})

public class Mod303TestSuite {

	private static NumberFormat FMT = DecimalFormat.getInstance();

	public static void printModel( Mod303 mod ) {
		System.out.println( "\t" 
			+ AonStringUtils.leftPad(mod.getAdministration().getDescription(), 20)
			+ " Modelo "
			+ AonStringUtils.rightPad(mod.getModelFullName(), 30)
			+ AonStringUtils.leftPad(FMT.format(mod.getDeclarationResult()),25)
			+ AonStringUtils.leftPad(mod.getStatus().getName(),35)
			);
	}
	
}
