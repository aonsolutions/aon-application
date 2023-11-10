package com.esferalia.aon.occam.test.registry.address;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomain.class,
	ValidationSaveEmptyRegistry.class,
	ValidationSaveOverflowNumber.class,
	ValidationSaveOverflowZip.class,
	ValidationSaveMainAddress.class,
	CRUDETest.class,
	ValidationDeleteForeignKey.class,
})
public class RegistryAddressTestSuite {
	
}
