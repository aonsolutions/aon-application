package com.esferalia.aon.occam.test.registry.customer;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ValidationSaveEmptyScope.class,
	ValidationSaveEmptyTransaction.class,
	ValidationSaveEmptyStatus.class,
	CRUDETest.class,
	CRUDEExistingRegistryTest.class,
	CRUDEFullTest.class,
})
public class CustomerTestSuite {

	
}
