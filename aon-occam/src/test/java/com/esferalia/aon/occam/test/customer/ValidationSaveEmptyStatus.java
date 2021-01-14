package com.esferalia.aon.occam.test.customer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyStatus extends AbstractOccamTest {

	@Test
	public void test() {
		Customer registry = AonFaker.getCustomer( ctx );
		registry.setStatus(null);
		registry = CustomerDAO.insert(ctx, registry);
		assertEquals(registry.getStatus(), RegistryStatus.ACTIVE );
	}

}
