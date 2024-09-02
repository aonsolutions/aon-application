package com.esferalia.aon.occam.test.registry.creditor;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDEExistingRegistryTest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry = RegistryDAO.save(ctx, registry);
		
		Creditor creditor = AonFaker.getCreditor( ctx,  registry);
		creditor = CreditorDAO.save(ctx, creditor);
		Creditor inserted = CreditorDAO.get(ctx, creditor.getId());
		Asserts.assertEqualsCreditor(creditor, inserted);
		
	}
}
