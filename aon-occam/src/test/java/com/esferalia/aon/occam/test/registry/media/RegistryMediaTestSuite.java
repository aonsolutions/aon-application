package com.esferalia.aon.occam.test.registry.media;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomain.class,
	ValidationSaveEmptyRegistry.class,
	ValidationSaveEmptyMedia.class,
	ValidationSaveOverflowValue.class,
	ValidationSaveInvalidMail.class,
//	CRUDETest.class,
})
public class RegistryMediaTestSuite {
	
}
