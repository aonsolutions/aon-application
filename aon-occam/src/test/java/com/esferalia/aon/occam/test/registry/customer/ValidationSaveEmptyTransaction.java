package com.esferalia.aon.occam.test.registry.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyTransaction extends AbstractOccamTest {

	@Test
	public void test() {
		Customer registry = AonFaker.getCustomer( ctx );
		registry.setTransaction(null);
		registry = CustomerDAO.save(ctx, registry);
		assertEquals(registry.getTransaction(), InvoiceTransactionType.NATIONAL);
	}

}
