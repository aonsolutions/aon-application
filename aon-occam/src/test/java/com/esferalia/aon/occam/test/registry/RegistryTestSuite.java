package com.esferalia.aon.occam.test.registry;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.registry.bank.RegistryBankAONTest;
import com.esferalia.aon.occam.test.registry.bank.RegistryBankDAOTest;

@Suite
@SelectClasses({
	ValidationSaveEmptyDomain.class,
	ValidationSaveEmptyDomainId.class,
	ValidationSaveOverflowDocument.class,
	ValidationSaveDocumentType.class,
	ValidationSaveOverflowName.class,
	ValidationSaveOverflowAlias.class,
	ValidationSaveEmptyDocumentCountry.class,
	ValidationSaveEmptyNationality.class,
	ValidationSaveLegalEntityTest.class,
	CRUDETest.class,
	
	// bank
	RegistryBankDAOTest.class,
	RegistryBankAONTest.class
})
public class RegistryTestSuite {
	
}
