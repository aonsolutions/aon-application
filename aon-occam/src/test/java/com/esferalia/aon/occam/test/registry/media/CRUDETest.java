package com.esferalia.aon.occam.test.registry.media;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryMedia media = AonFaker.getRegistryMedia( ctx ); 
		media = RegistryMediaDAO.insert(ctx, media);
		RegistryMedia inserted = RegistryMediaDAO.get(ctx, media.getId());
		Asserts.assertEqualsRegistryMedia(media, inserted);
		
		media = RegistryMediaDAO.update(ctx, media);
		RegistryMedia updated = RegistryMediaDAO.get(ctx, media.getId());
		Asserts.assertEqualsRegistryMedia(media, updated);
		
		RegistryMediaDAO.delete(ctx, media.getId());
		RegistryMedia deleted = RegistryMediaDAO.get(ctx, media.getId());
		assertNull(deleted);
	}

}
