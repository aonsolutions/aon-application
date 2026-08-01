package com.esferalia.aon.occam.test.registry.customer;


import static com.esferalia.aon.occam.test.OccamAssertions.assertFalse;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class CRUDEFullTest extends AbstractOccamTest {

	@Repeat( 20 )
	@Test
	public void test() {
		CustomerFull full = new CustomerFull();
		Customer registry = AonFaker.getCustomer(ctx);
		full.setRegistry(registry);
		
		int times = AonRandom.number(0, 10);
		for (int i = 0; i < times; i++) {
			RegistryMedia media = AonFaker.getRegistryMedia(ctx, registry);
			full.addMedia( media );
		}
		times = AonRandom.number(0, 10);
		for (int i = 0; i < times; i++) {
			RegistryAddress address = AonFaker.getRegistryAddress(ctx, registry);
			full.addAddress( address );
		}
		
		CustomerFull inserted = CustomerDAO.save(ctx, full);
		full = CustomerDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsCustomerFull(full, inserted);
		
		if (inserted.hasMedias()) {
			for (RegistryMedia media : inserted.getMedias()) {
				assertFalse(media.isDirty(), "Registry Media must be not dirty");
			}
		}
		if (inserted.hasMedias()) {
			for (RegistryMedia media : inserted.getMedias()) {
				// Borrado
				media.setRemoved(AonRandom.gt(90));
				// Modificacion de un contacto contacto
				media.setComment(AonRandom.gt(90)?"MODIFICADO":media.getComment());	 	
			}
		}
		// Nuevo contacto
		if (AonRandom.gt(10)) {
			full.addMedia( AonFaker.getRegistryMedia(ctx, registry) );
		}
		
		if (inserted.hasAddresses()) {
			for (RegistryAddress address : inserted.getAddresses()) {
				assertFalse(address.isDirty(), "Registry Address must be not dirty");
			}
		}
		if (inserted.hasAddresses()) {
			for (RegistryAddress address : inserted.getAddresses()) {
				// Borrado
				address.setRemoved(AonRandom.gt(90));
				// Modificacion de un contacto contacto
				address.setAddress(AonRandom.gt(90)?"MODIFICADO":address.getAddress());	 	
			}
		}
		// Nuevo contacto
		if (AonRandom.gt(10)) {
			full.addAddress( AonFaker.getRegistryAddress(ctx, registry) );
		}

		CustomerFull updated = CustomerDAO.save(ctx, full);
		full = CustomerDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsCustomerFull(full, updated);
		
//		CustomerDAO.delete(ctx, full);
//		CustomerFull deleted = CustomerDAO.getFull(ctx, registry.getId());
//		assertNull(deleted);
	}

}
