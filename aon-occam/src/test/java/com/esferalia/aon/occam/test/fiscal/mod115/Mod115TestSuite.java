package com.esferalia.aon.occam.test.fiscal.mod115;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.watson.util.AonStringUtils;

@RunWith(Suite.class)
@SuiteClasses({
	// MVEL expresions syntax check en las declaraciones
	Mod115ExpressionsTest.class,
	// Modelos mensuale
	Mod115DeleteTest.class,
	Mod115InsertMonthlyTest.class,
	Mod115InsertMonthlyComplementaryTest.class,
	Mod115InsertMonthlyReplacementTest.class,
	
	// Modelos trimestrales
	Mod115DeleteTest.class,
	Mod115InsertQuarterlyTest.class,
	Mod115InsertQuarterlyComplementaryTest.class,
	Mod115InsertQuarterlyReplacementTest.class,
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

	private static NumberFormat FMT = DecimalFormat.getInstance();
	
	public static void printModel( Mod115 mod ) {
		System.out.println( "\t" 
			+ AonStringUtils.leftPad(mod.getAdministration().getDescription(), 20)
			+ " Modelo "
			+ AonStringUtils.rightPad(mod.getModelFullName(), 20)
			+ AonStringUtils.leftPad(FMT.format(mod.getDeclarationResult()),15)
			);
	}
}
