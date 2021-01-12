package com.esferalia.aon.occam.test.registry;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomain.class,
	ValidationSaveEmptyDomainId.class,
	ValidationSaveOverflowDocument.class,
	ValidationSaveOverflowName.class,
	ValidationSaveOverflowAlias.class,
	ValidationSaveEmptyDocumentCountry.class,
	ValidationSaveEmptyNationality.class,
	ValidationSaveLegalEntity.class,
	InsertTest.class,
})
public class RegistryTestSuite {
	
}
