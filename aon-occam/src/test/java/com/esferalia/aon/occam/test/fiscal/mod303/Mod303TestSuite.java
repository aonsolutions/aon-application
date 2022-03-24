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
	Mod303DeleteTest.class,
	Mod303InsertQuarterlyTest.class,
//	Mod303InsertQuarterlyComplementaryTest.class,
//	Mod303InsertQuarterlyReplacementTest.class,

	// VatContext JSON 
	VatContextJSONTest.class,
})
public class Mod303TestSuite {

	private static NumberFormat FMT = DecimalFormat.getInstance();
	
	public static void printModel( Mod303 mod ) {
		System.out.println( "\t" 
			+ AonStringUtils.leftPad(mod.getAdministration().getDescription(), 20)
			+ " Modelo "
			+ AonStringUtils.rightPad(mod.getModelFullName(), 20)
			+ AonStringUtils.leftPad(FMT.format(mod.getDeclarationResult()),15)
			);
	}
	
}
