package com.esferalia.aon.occam.test.registry;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowName extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setName( AonStringUtils.repeat('X', REGISTRY.NAME.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryDAO.save(ctx, registry) );
		assertEquals(AonError.INVALID_LENGTH.format( "Nombre o raz\u00F3n social", REGISTRY.NAME.getDataType().length()),e.getMessage());
	}

}
