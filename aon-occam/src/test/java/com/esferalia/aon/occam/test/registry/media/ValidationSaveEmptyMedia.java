package com.esferalia.aon.occam.test.registry.media;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyMedia extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryMedia media = AonFaker.getRegistryMedia( ctx );
		media.setMedia(null);
		media = RegistryMediaDAO.insert(ctx, media);
		assertEquals(media.getMedia(),MediaType.UNKNOWN);
	}

}
