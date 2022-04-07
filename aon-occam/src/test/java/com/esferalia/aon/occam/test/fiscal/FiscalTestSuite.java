package com.esferalia.aon.occam.test.fiscal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.mod111.Mod111TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod115.Mod115TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod123.Mod123TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod303.Mod303TestSuite;
import com.esferalia.aon.occam.test.fiscal.model.FiscalModelTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	FiscalModelTestSuite.class,
	Mod111TestSuite.class,
	Mod115TestSuite.class,
	Mod123TestSuite.class,
	Mod303TestSuite.class,
//	Mod130TestSuite.class,
//	Mod131TestSuite.class,
//	Mod202TestSuite.class,
//	Mod390HFTestSuite.class,
})
public class FiscalTestSuite {

	
}
