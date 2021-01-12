package com.esferalia.aon.occam.test.registry;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.RegistryFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowDocument extends AbstractOccamTest {

	@Test
	public void testOverflowDocument() {
		Registry registry = RegistryFaker.get( ctx );
		registry.setDocument( AonStringUtils.repeat('X', REGISTRY.DOCUMENT.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryDAO.insert(ctx, registry) );
		assertEquals(
			 AonStringUtils.substring(AonError.REGISTRY_OVERFLOW_DOCUMENT.getMessage(), 0,15)
			,AonStringUtils.substring(e.getMessage(), 0,15)
			);
	}

}
