package com.esferalia.aon.occam.test.registry.address;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
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
