package com.esferalia.aon.occam.test.registry.carrier;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyScope.class,
	ValidationSaveEmptyStatus.class,
	CRUDETest.class,
	CRUDEExistingRegistryTest.class
})
public class CarrierTestSuite {

	
}
