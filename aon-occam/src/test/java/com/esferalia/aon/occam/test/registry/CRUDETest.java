package com.esferalia.aon.occam.test.registry;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx ); 
		registry = RegistryDAO.save(ctx, registry);
		Registry inserted = RegistryDAO.get(ctx, registry.getId());
		Asserts.assertEqualsRegistry (registry, inserted);
		
		registry = RegistryDAO.save(ctx, registry);
		Registry updated = RegistryDAO.get(ctx, registry.getId());
		Asserts.assertEqualsRegistry (registry, updated);
		
		RegistryDAO.delete(ctx, registry.getId());
		Registry deleted = RegistryDAO.get(ctx, registry.getId());
		assertNull(deleted);
	}

}
