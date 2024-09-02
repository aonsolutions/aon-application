package com.esferalia.aon.occam.test.registry.media;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ValidationSaveEmptyDomain.class,
	ValidationSaveEmptyRegistry.class,
	ValidationSaveEmptyMedia.class,
	ValidationSaveOverflowValue.class,
	ValidationSaveInvalidMail.class,
	CRUDETest.class,
})
public class RegistryMediaTestSuite {
	
}
