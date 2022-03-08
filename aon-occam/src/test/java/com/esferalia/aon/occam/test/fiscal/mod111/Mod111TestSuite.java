package com.esferalia.aon.occam.test.fiscal.mod111;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.util.AonStringUtils;

@RunWith(Suite.class)
@SuiteClasses({
//	// MVEL expresions syntax check en las declaraciones
//	Mod111ExpressionsTest.class,
//	// Modelos mensuale
//	Mod111DeleteTest.class,
//	Mod111InsertMonthlyTest.class,
//	Mod111InsertMonthlyComplementaryTest.class,
//	Mod111InsertMonthlyReplacementTest.class,
//	
//	// Modelos trimestrales
//	Mod111DeleteTest.class,
//	Mod111InsertQuarterlyTest.class,
//	Mod111InsertQuarterlyComplementaryTest.class,
//	Mod111InsertQuarterlyReplacementTest.class,
//	// Flujo de estados
//	Mod111ReopenTest.class,					// Pendiente
//	Mod111FinishTest.class,					// Finalizado
//	Mod111SentTest.class,					// Presentado
	// Flujo de estados
	Mod111ReopenTest.class,					// Pendiente
	Mod111MarkAsCustomerCheckTest.class,	// Enviado a cliente
	Mod111MarkAsCustomerAcceptedTest.class,	// Aceptado por cliente
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

	private static NumberFormat FMT = DecimalFormat.getInstance();
	
	public static void printModel( Mod111 mod ) {
		System.out.println( "\t" 
			+ AonStringUtils.leftPad(mod.getAdministration().getDescription(), 20)
			+ " Modelo "
			+ AonStringUtils.rightPad(mod.getModelFullName(), 20)
			+ AonStringUtils.leftPad(FMT.format(mod.getDeclarationResult()),15)
			);
	}
}
