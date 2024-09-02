package com.esferalia.aon.occam.test.registry.supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class CRUDEFullTest extends AbstractOccamTest {

	@RepeatedTest( 20 )
	@Test
	public void test() {
		SupplierFull full = new SupplierFull();
		Supplier registry = AonFaker.getSupplier(ctx);
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
		
		SupplierFull inserted = SupplierDAO.save(ctx, full);
		full = SupplierDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsSupplierFull(full, inserted);
		
		if (inserted.hasMedias()) {
			for (RegistryMedia media : inserted.getMedias()) {
				assertFalse(media.isDirty() ,"Registry Media must be not dirty");
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
				assertFalse(address.isDirty(),"Registry Address must be not dirty");
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

		SupplierFull updated = SupplierDAO.save(ctx, full);
		full = SupplierDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsSupplierFull(full, updated);
		
//		SupplierDAO.delete(ctx, full);
//		SupplierFull deleted = SupplierDAO.getFull(ctx, registry.getId());
//		assertNull(deleted);
	}

}
