package com.esferalia.aon.occam.test.registry;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.RegistryFaker;

public class InsertTest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		Registry registry = RegistryFaker.get( ctx ); 
		registry = RegistryDAO.insert(ctx, registry);		
	}
}
