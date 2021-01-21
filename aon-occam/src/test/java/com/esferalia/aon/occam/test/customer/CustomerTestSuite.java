package com.esferalia.aon.occam.test.customer;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyTransaction.class,
	ValidationSaveEmptyStatus.class,
	CRUDETest.class,
})
public class CustomerTestSuite {

	
}
