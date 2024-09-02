package com.esferalia.aon.occam.test.registry.carrier;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ValidationSaveEmptyScope.class,
	ValidationSaveEmptyStatus.class,
	CRUDETest.class,
	CRUDEExistingRegistryTest.class
})
public class CarrierTestSuite {

	
}
