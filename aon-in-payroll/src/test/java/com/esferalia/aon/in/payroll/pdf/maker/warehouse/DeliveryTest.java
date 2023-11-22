package com.esferalia.aon.in.payroll.pdf.maker.warehouse;


import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class DeliveryTest extends AbstractOccamTest {

	@Test
	public void test() {
		
		Delivery delivery = AonFaker.getDelivery();
		Date date = new Date();
		delivery.setDate(date);
		
		assertEquals(date, delivery.getDate());
	}
}
