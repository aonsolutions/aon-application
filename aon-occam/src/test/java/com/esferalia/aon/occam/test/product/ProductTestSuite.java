package com.esferalia.aon.occam.test.product;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ProductCRUDETest.class,
	ProductCategoryCRUDETest.class,
	BrandTest.class,
	ItemCRUDETest.class,
})
public class ProductTestSuite {

	
}
