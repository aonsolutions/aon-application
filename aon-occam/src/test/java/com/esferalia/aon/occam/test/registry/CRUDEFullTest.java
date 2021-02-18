package com.esferalia.aon.occam.test.registry;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class CRUDEFullTest extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryFull full = new RegistryFull();
		Registry registry = AonFaker.getRegistry( ctx );
		full.setRegistry(registry);
		
		int times = AonRandom.number(0, 10);
		for (int i = 0; i < times; i++) {
			RegistryMedia media = AonFaker.getRegistryMedia(ctx, registry);
			full.addMedia( media );
		}
		RegistryFull inserted = RegistryDAO.save(ctx, full);
		full = RegistryDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsRegistryFull(full, inserted);
		
		if (inserted.hasMedias()) {
			for (RegistryMedia media : inserted.getMedias()) {
				assertFalse("Registry Media must be not dirty", media.isDirty());
			}
			int idx = AonRandom.number(0, times - 1);
			RegistryMedia media = inserted.getMedias().get(idx);
			media.setComment("MODIFICADO");
			// Nuevo contacto
			full.addMedia( AonFaker.getRegistryMedia(ctx, registry) );
		}
		
		RegistryFull updated = RegistryDAO.save(ctx, full);
		full = RegistryDAO.getFull(ctx, full.getId());
		Asserts.assertEqualsRegistryFull(full, updated);
		
		RegistryDAO.delete(ctx, full);
		RegistryFull deleted = RegistryDAO.getFull(ctx, registry.getId());
		assertNull(deleted);
	}

}
