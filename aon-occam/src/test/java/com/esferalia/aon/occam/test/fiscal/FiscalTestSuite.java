package com.esferalia.aon.occam.test.fiscal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.mod111.Mod111TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod115.Mod115TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod123.Mod123TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod130.Mod130TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod131.Mod131TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod202.Mod202TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod303.Mod303TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod390hf.Mod390HFTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	Mod111TestSuite.class,
//	Mod115TestSuite.class,
//	Mod123TestSuite.class,
//	Mod130TestSuite.class,
//	Mod131TestSuite.class,
//	Mod202TestSuite.class,
//	Mod303TestSuite.class,
//	Mod390HFTestSuite.class,
})
public class FiscalTestSuite {

	
}
