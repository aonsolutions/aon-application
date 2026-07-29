package com.esferalia.aon.occam.test.registry;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowDocument extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setDocument( AonStringUtils.repeat('X', REGISTRY.DOCUMENT.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryDAO.save(ctx, registry) );
		assertEquals(AonError.REGISTRY_OVERFLOW_DOCUMENT.getMessage(),e.getMessage());
	}

}
