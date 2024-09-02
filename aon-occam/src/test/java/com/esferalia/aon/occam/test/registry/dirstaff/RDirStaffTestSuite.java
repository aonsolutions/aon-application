package com.esferalia.aon.occam.test.registry.dirstaff;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyRegistryTest.class,
	ValidationSaveEmptyDocumentTest.class,
	ValidationSaveEmptyNameTest.class,
	ValidationSaveOverflowDocumentTest.class,
	CRUDETest.class,
})
public class RDirStaffTestSuite {
	
}
