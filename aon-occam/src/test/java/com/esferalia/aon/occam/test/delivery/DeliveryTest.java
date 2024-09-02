package com.esferalia.aon.occam.test.delivery;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	DeliveryCRUDETest.class,
	DeliveryDetailCRUDETest.class
})
public class DeliveryTest {

}
