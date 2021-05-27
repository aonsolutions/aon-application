package com.esferalia.aon.occam.test.accounting.entry;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	InsertTest.class,
	ValidationSaveEmptyDateTest.class,
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyPeriodTest.class,
	ValidationSaveEmptyTypeTest.class,
	ValidationSaveWrongDomainTest.class
})
public class AccountEntryTestSuite {

	
}
