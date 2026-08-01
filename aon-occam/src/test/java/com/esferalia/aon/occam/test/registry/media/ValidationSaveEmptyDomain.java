package com.esferalia.aon.occam.test.registry.media;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyDomain extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryMedia media = AonFaker.getRegistryMedia( ctx );
		media.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryMediaDAO.insert(ctx, media) );
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
	}

}
