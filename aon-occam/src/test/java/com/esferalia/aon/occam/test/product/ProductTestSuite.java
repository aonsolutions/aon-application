package com.esferalia.aon.occam.test.product;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ProductCRUDETest.class,
	ProductCategoryCRUDETest.class,
	BrandTest.class,
	ItemCRUDETest.class,
})
public class ProductTestSuite {

	
}
