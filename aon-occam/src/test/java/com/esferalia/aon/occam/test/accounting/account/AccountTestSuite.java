package com.esferalia.aon.occam.test.accounting.account;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	LoadTest.class,
	//INSERT TESTS
	AccountNoLowLevelInsertTest.class,
	AccountEmptyDescriptionInsertTest.class,
	AccountEmptyDomainInsertTest.class,
	AccountEmptyCodeInsertTest.class,
	AccountNoNumericCodeInsertTest.class,
	//LOW LEVEL GENERATOR
	AccountGenerateLowLevelTest.class,
	//CRUDE
	CRUDETest.class
})
public class AccountTestSuite {
	
}
