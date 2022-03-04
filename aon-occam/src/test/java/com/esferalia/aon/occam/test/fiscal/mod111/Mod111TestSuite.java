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
	Mod111ExpressionsTest.class,
//	Mod111DeleteTest.class,
//	Mod111InsertMonthlyTest.class,
//	Mod111InsertMonthlyComplementaryTest.class,
//	Mod111InsertMonthlyReplacementTest.class,
//	Mod111DeleteTest.class,
//	Mod111InsertQuarterlyTest.class,
//	Mod111InsertQuarterlyComplementaryTest.class,
//	Mod111InsertQuarterlyReplacementTest.class,
	Mod111ReopenTest.class,
	Mod111FinishTest.class,
	Mod111ReopenTest.class,
	Mod111FinishTest.class,
	Mod111MarkAsCustomerCheckTest.class,
	Mod111MarkAsCustomerAcceptedTest.class,
	Mod111ReopenTest.class,
	Mod111FinishTest.class,
	Mod111MarkAsCustomerCheckTest.class,
	Mod111MarkAsCustomerRejectedTest.class,
	Mod111ReopenTest.class,
	Mod111FinishTest.class,
	Mod111SentTest.class,
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
