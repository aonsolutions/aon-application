package com.esferalia.aon.occam.test.registry;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowAlias extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry.setAlias( AonStringUtils.repeat('X', REGISTRY.ALIAS.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryDAO.save(ctx, registry) );
		assertEquals(AonError.INVALID_LENGTH.format( "Alias", REGISTRY.ALIAS.getDataType().length() ),e.getMessage());
	}

}
