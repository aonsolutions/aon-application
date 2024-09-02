package com.esferalia.aon.occam.test.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class DeliveryCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		crudeDeliveryDAO();
		crudeDeliveryDAOwithSave();
		crudeWarehouseDAO();
		emptyDomainTest();
		emptyCustomerTest();
		emptyDateTest();
		emptyTotalPackagesTest();
		emptyTotalWeightTest();
		getEmptyRegistryAddressTest();
		//getFullEmptyRegistryAddressTest();
	}
	
	/**
	 * Tests insert, update and delete
	 */
	private void crudeDeliveryDAO() {
		Delivery delivery = AonFaker.getDelivery(ctx); 
		delivery = DeliveryDAO.insertDelivery(ctx, delivery);
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
	
	/**
	 * Tests that it throws an exception if the domain is null
	 */
	private void emptyDomainTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery.setDomain(null);
		
		AonCoreException e = assertThrows(AonCoreException.class, () -> DeliveryDAO.save(ctx, delivery));
		assertEquals(AonError.EMPTY_DATA.format("domain"), e.getMessage());
	}
	
	/**
	 * Tests that it throws an exception if the customer is null
	 */
	private void emptyCustomerTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery.setCustomer(null);
		
		AonCoreException e = assertThrows(AonCoreException.class, () -> DeliveryDAO.save(ctx, delivery));
		assertEquals(AonError.EMPTY_DATA.format("customer"), e.getMessage());
	}
	
	/**
	 * Tests that it throws an exception if the date is null
	 */
	private void emptyDateTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery.setDate(null);
		
		DeliveryDAO.save(ctx, delivery);
		
		delivery = DeliveryDAO.get(ctx, delivery.getId());
		assertNotNull(delivery.getDate());
	}
	
	/**
	 * Tests that if the totalPackages is null, it sets a default value
	 */
	private void emptyTotalPackagesTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery.setTotalPackages(null);
		
		DeliveryDAO.save(ctx, delivery);
		
		Double totalPackages = 0.0;
		
		delivery = DeliveryDAO.get(ctx, delivery.getId());
		assertEquals(totalPackages, delivery.getTotalPackages());
	}
	
	/**
	 * Tests that if the totalWeight is null, it sets a default value
	 */
	private void emptyTotalWeightTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery.setTotalWeight(null);
		
		DeliveryDAO.save(ctx, delivery);
		
		Double totalWeight = 0.0;
		
		delivery = DeliveryDAO.get(ctx, delivery.getId());
		assertEquals(totalWeight, delivery.getTotalWeight());
	}
	
	/**
	 * Tests that if the registryAddress is null, it sets the main address using get
	 */
	private void getEmptyRegistryAddressTest() {
		Delivery delivery = AonFaker.getDelivery(ctx);
		Customer customer = AonRandom.getCustomer(ctx);
		delivery.setCustomer(customer);
		delivery.setAddress(null);
		
		delivery = DeliveryDAO.save(ctx, delivery);
		
		RegistryAddress mainAddress = RegistryAddressDAO.getMain(ctx, delivery.getCustomer().getId());
		
		if (mainAddress != null) {
			Integer id = delivery.getId();
			
			delivery = DeliveryDAO.get(ctx, f -> f.getIdProperty().eq(id));
			assertEquals(mainAddress.getId(), delivery.getAddress().getId());
		}
	}
	
	/**
	 * Tests that if the registryAddress is null, it sets the main address using getFull
	 */
	private void getFullEmptyRegistryAddressTest() {
        Delivery delivery = AonFaker.getDelivery(ctx);
        Customer customer = AonRandom.getCustomer(ctx);
        delivery.setCustomer(customer);
        delivery.setAddress(null);
        
        delivery = DeliveryDAO.save(ctx, delivery);
        
		RegistryAddress mainAddress = RegistryAddressDAO.getMain(ctx, delivery.getCustomer().getId());
		
		if (mainAddress != null) {
	        Integer id = delivery.getId();
	        
	        delivery = DeliveryDAO.getFull(ctx, f -> f.getIdProperty().eq(id));
	        Asserts.assertEqualsRegistryAddress(mainAddress, delivery.getAddress());
		}
    }
}


