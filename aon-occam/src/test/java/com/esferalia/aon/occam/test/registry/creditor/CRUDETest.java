package com.esferalia.aon.occam.test.registry.creditor;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Creditor creditor = AonFaker.getCreditor( ctx ); 
		creditor = CreditorDAO.save(ctx, creditor);
		Creditor inserted = CreditorDAO.get(ctx, creditor.getId());
		Asserts.assertEqualsCreditor(creditor, inserted);
		
		creditor = CreditorDAO.save(ctx, creditor);
		Creditor updated = CreditorDAO.get(ctx, creditor.getId());
		Asserts.assertEqualsCreditor(creditor, updated);
		
		CreditorDAO.delete(ctx, creditor.getId());
		Creditor deleted = CreditorDAO.get(ctx, creditor.getId());
		assertNull(deleted.getId());
	}
}
