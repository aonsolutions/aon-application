package com.esferalia.aon.occam.test.delivery;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class DeliveryCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		crudeDeliveryDAO();
		crudeDeliveryDAOwithSave();
		crudeWarehouseDAO();
	}
	
	private void crudeDeliveryDAO() {
		Delivery delivery = AonFaker.getDelivery(ctx); 
		delivery = DeliveryDAO.save(ctx, delivery);
		Integer deliveryId = delivery.getId();
		Delivery inserted = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(deliveryId));
		Asserts.assertEqualsDelivery(delivery, inserted);
		
		delivery = DeliveryDAO.update(ctx, delivery);
		Delivery updated = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(deliveryId));
		Asserts.assertEqualsDelivery(delivery, updated);
		
		DeliveryDAO.deleteDelivery(ctx, f -> f.getIdProperty().eq(deliveryId));
		Delivery deleted = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(deliveryId));
		assertNull(deleted.getId());
	}
	
	private void crudeDeliveryDAOwithSave() {
		Delivery delivery = AonFaker.getDelivery(ctx); 
		delivery = DeliveryDAO.save(ctx, delivery);
		Integer deliveryId = delivery.getId();
		Delivery inserted = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(deliveryId));
		Asserts.assertEqualsDelivery(delivery, inserted);
		
		delivery = DeliveryDAO.save(ctx, delivery);
		Delivery updated = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(deliveryId));
		Asserts.assertEqualsDelivery(delivery, updated);
		
		DeliveryDAO.deleteDelivery(ctx, f -> f.getIdProperty().eq(deliveryId));
		Delivery deleted = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(deliveryId));
		assertNull(deleted.getId());
	}
	
	private void crudeWarehouseDAO() {
		Delivery delivery = AonFaker.getDelivery(ctx); 
		Integer deliveryId = WarehouseDAO.insertDelivery(ctx, delivery);
		delivery.setId(deliveryId);
		Delivery inserted = WarehouseDAO.getDelivery(ctx, deliveryId);
		Asserts.assertEqualsDelivery(delivery, inserted);
			
		WarehouseDAO.updateDelivery(ctx, delivery);
		Delivery updated = WarehouseDAO.getDelivery(ctx,deliveryId);
		Asserts.assertEqualsDelivery(delivery, updated);
			
		WarehouseDAO.deleteDelivery(ctx, deliveryId);
		Delivery deleted = WarehouseDAO.getDelivery(ctx, deliveryId);
		assertNull(deleted.getId());
	}
}
