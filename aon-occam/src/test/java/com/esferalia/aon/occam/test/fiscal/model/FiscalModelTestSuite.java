package com.esferalia.aon.occam.test.fiscal.model;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
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
