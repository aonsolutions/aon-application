package com.esferalia.aon.occam.test.fiscal.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyYearTest.class,
	ValidationSaveEmptyInvalidYearTest.class,
	ValidationSaveEmptyPeriodTest.class,
	ValidationDocumentLengthTest.class,
	ValidationNameLengthTest.class,
	ValidationSurnameLengthTest.class,
	ValidationStreetNumberLengthTest.class,
	ValidationZipLengthTest.class,
	ValidationContactCellularLengthTest.class,	
	ValidationContactPhoneLengthTest.class,
})

public class FiscalModelTestSuite {
	
}
