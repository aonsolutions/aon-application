package com.esferalia.aon.occam.test.delivery;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDetailDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class DeliveryDetailCRUDETest extends AbstractOccamTest {

	/**
	 * Tests insert, update and delete
	 */
	@Test
	public void test() {
		// insert
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery = DeliveryDAO.save(ctx, delivery);
		
		DeliveryDetail deliveryDetail = AonFaker.getDeliveryDetail(ctx, delivery);
		
		deliveryDetail = DeliveryDetailDAO.save(ctx, deliveryDetail);
		
		Integer id = deliveryDetail.getId();
		DeliveryDetail inserted = DeliveryDetailDAO.get(ctx,  f -> f.getIdProperty().eq(id));
		
		Asserts.assertEqualsDeliveryDetail(deliveryDetail, inserted);

		// update
		deliveryDetail.setPrice(deliveryDetail.getPrice() * 2);
		
		DeliveryDetail updated = DeliveryDetailDAO.save(ctx, deliveryDetail);
		deliveryDetail = DeliveryDetailDAO.get(ctx, f -> f.getIdProperty().eq(id));
		
		Asserts.assertEqualsDeliveryDetail(updated, deliveryDetail);
		
		// delete
		DeliveryDetailDAO.delete(ctx, id);
		Asserts.assertEqualsDeliveryDetail(DeliveryDetailDAO.get(ctx, f -> f.getIdProperty().eq(id)), new DeliveryDetail());
	}
	
	/**
	 * Tests the method getAmounth
	 */
	public void getAmounthTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery = DeliveryDAO.save(ctx, delivery);
		
		DeliveryDetail deliveryDetail = AonFaker.getDeliveryDetail(ctx, delivery);
				
		double DELTA = 1e-8;
		Double amounth = (deliveryDetail.getQuantity() * deliveryDetail.getPrice()) - (deliveryDetail.getQuantity() * deliveryDetail.getPrice() * Double.parseDouble(deliveryDetail.getDiscountExpression()) / 100);
	
		assertEquals(deliveryDetail.getAmount(), amounth, DELTA);
	}

}
