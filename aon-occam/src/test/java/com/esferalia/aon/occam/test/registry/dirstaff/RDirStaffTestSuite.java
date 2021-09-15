package com.esferalia.aon.occam.test.registry.dirstaff;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyRegistryTest.class,
	ValidationSaveEmptyDocumentTest.class,
	ValidationSaveEmptyNameTest.class,
	ValidationSaveOverflowDocumentTest.class,
	CRUDETest.class,
})
public class RDirStaffTestSuite {
	
}
