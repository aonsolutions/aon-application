package com.esferalia.aon.occam.test.registry.creditor;


import static com.esferalia.aon.occam.test.OccamAssertions.assertFalse;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class CRUDEFullTest extends AbstractOccamTest {

	@Repeat( 20 )
	@Test
	public void test() {
		CreditorFull full = new CreditorFull();
		Creditor registry = AonFaker.getCreditor(ctx);
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
		
		CreditorFull inserted = CreditorDAO.save(ctx, full);
		full = CreditorDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsCreditorFull(full, inserted);
		
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

		CreditorFull updated = CreditorDAO.save(ctx, full);
		full = CreditorDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsCreditorFull(full, updated);
		
//		CreditorDAO.delete(ctx, full);
//		CreditorFull deleted = CreditorDAO.getFull(ctx, registry.getId());
//		assertNull(deleted);
	}

}
