package com.esferalia.aon.occam.test.sales;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	SalesAllTests.class,
	SalesDetailAllTests.class
})
public class SalesTest {

}
