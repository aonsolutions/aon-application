package com.esferalia.aon.occam.test.registry.creditor;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyScope.class,
	ValidationSaveEmptyTransaction.class,
	ValidationSaveEmptyStatus.class,
	CRUDETest.class,
	CRUDEExistingRegistryTest.class,
})
public class CreditorTestSuite {

	
}
