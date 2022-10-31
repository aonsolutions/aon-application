package es.aonsolutions.aio.test.product;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@SelectClasses({
	ProductTest.class,
	ItemTest.class,
})
@Suite
public class ProductTestSuite {
	
}
