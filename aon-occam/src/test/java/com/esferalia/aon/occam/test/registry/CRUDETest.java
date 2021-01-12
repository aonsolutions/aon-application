package com.esferalia.aon.occam.test.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.RegistryFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = RegistryFaker.get( ctx ); 
		registry = RegistryDAO.insert(ctx, registry);
		Registry inserted = RegistryDAO.get(ctx, registry.getId());
		assertEqualsRegistry (registry, inserted);
		
		registry = RegistryDAO.update(ctx, registry);
		Registry updated = RegistryDAO.get(ctx, registry.getId());
		assertEqualsRegistry (registry, updated);
		
		RegistryDAO.delete(ctx, registry.getId());
		Registry deleted = RegistryDAO.get(ctx, registry.getId());
		assertNull(deleted);
	}

	private void assertEqualsRegistry (Registry registry, Registry inserted) {
		assertEquals(registry.getId(), inserted.getId());
		assertEquals(registry.getDomain().getId(), inserted.getDomain().getId());
		assertEquals(registry.getDocument(), inserted.getDocument());
		assertEquals(registry.getDocumentType(), inserted.getDocumentType());
		assertEquals(registry.getDocumentCountry(), inserted.getDocumentCountry());
		assertEquals(registry.getName(), inserted.getName());
		assertEquals(registry.getAlias(), inserted.getAlias());
		assertEquals(registry.isLegalPerson(), inserted.isLegalPerson());
		assertEquals(registry.getNationality(), inserted.getNationality());
		assertEquals(registry.getSecurityLevel() , inserted.getSecurityLevel());
	}
}
