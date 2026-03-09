package com.esferalia.aon.occam.test.registry;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.registry.bank.RegistryBankAONTest;
import com.esferalia.aon.occam.test.registry.bank.RegistryBankDAOTest;
import com.esferalia.aon.occam.test.registry.recordData.RecordDataTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomain.class,
	ValidationSaveEmptyDomainId.class,
	ValidationSaveOverflowDocument.class,
	ValidationSaveDocumentType.class,
	ValidationSaveOverflowName.class,
	ValidationSaveOverflowAlias.class,
	ValidationSaveEmptyDocumentCountry.class,
	ValidationSaveEmptyNationality.class,
	ValidationSaveLegalEntity.class,
	CRUDETest.class,
	
	// bank
	RegistryBankDAOTest.class,
	RegistryBankAONTest.class,

	RecordDataTestSuite.class,
})
public class RegistryTestSuite {
	
}
