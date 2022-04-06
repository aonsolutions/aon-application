package com.esferalia.aon.occam.test.accounting.account;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
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
