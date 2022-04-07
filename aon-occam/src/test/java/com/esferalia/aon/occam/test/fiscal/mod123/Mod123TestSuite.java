package com.esferalia.aon.occam.test.fiscal.mod123;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.watson.util.AonStringUtils;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod123ExpressionsTest.class,
	// Modelos mensuale
	Mod123DeleteTest.class,
	Mod123InsertInvoicesTest.class,
	Mod123InsertMonthlyTest.class,
	Mod123FinishTest.class,
	
	Mod123InsertInvoicesTest.class,
	Mod123InsertMonthlyComplementaryTest.class,
	Mod123FinishTest.class,
	
	Mod123InsertInvoicesTest.class,
	Mod123InsertMonthlyReplacementTest.class,
	Mod123RoundedAmountsTest.class,
	
	// Modelos trimestrales
	Mod123DeleteTest.class,
	Mod123InsertInvoicesTest.class,
	Mod123InsertQuarterlyTest.class,
	Mod123FinishTest.class,
	
	Mod123InsertInvoicesTest.class,
	Mod123InsertQuarterlyComplementaryTest.class,
	Mod123FinishTest.class,
	
	Mod123InsertInvoicesTest.class,
	Mod123InsertQuarterlyReplacementTest.class,
	Mod123RoundedAmountsTest.class,
	
	// Flujo de estados
	Mod123ReopenTest.class,					// Pendiente
	Mod123FinishTest.class,					// Finalizado
	Mod123SentTest.class,					// Presentado
//	// Flujo de estados
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
public class Mod123TestSuite {

	private static NumberFormat FMT = DecimalFormat.getInstance();
	
	public static String toString( Mod123 mod ) {
		return AonStringUtils.leftPad(mod.getAdministration().getDescription(), 20)
			+ " Modelo "
			+ AonStringUtils.rightPad(mod.getModelFullName(), 20)
			+ AonStringUtils.leftPad(FMT.format(mod.getDeclarationResult()),15);
	}
	public static void printModel( Mod123 mod ) {
		System.out.println( "\t" + toString(mod) );
	}
}
