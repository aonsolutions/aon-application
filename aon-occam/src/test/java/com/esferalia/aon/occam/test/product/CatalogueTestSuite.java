package com.esferalia.aon.occam.test.product;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CatalogueValidationSaveEmptyDomain.class,
	CatalogueValidationSaveEmptyName.class,
	CatalogueValidationSaveEmptyStartDate.class,
	CatalogueCRUDETest.class,
	CatalogueItemCRUDETest.class,
	CatalogueCategoryCRUDETest.class,
})
public class CatalogueTestSuite {

}
