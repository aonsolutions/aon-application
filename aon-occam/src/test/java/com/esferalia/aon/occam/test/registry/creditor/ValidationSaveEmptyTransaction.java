package com.esferalia.aon.occam.test.registry.creditor;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyTransaction extends AbstractOccamTest {

	@Test
	public void test() {
		Creditor registry = AonFaker.getCreditor( ctx );
		registry.setTransaction(null);
		registry = CreditorDAO.save(ctx, registry);
		assertEquals(registry.getTransaction(), InvoiceTransactionType.NATIONAL);
	}

}
