package com.esferalia.aon.occam.test.accounting.entry;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	InsertTest.class,
	ValidationSaveEmptyDateTest.class,
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyPeriodTest.class,
	ValidationSaveEmptyTypeTest.class,
	ValidationSaveWrongDomainTest.class
})
public class AccountEntryTestSuite {

	
}
