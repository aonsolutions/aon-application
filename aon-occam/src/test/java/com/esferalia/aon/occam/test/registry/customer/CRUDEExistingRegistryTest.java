package com.esferalia.aon.occam.test.registry.customer;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDEExistingRegistryTest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry = RegistryDAO.save(ctx, registry);
		
		Customer customer = AonFaker.getCustomer( ctx,  registry);
		customer = CustomerDAO.save(ctx, customer);
		Customer inserted = CustomerDAO.get(ctx, customer.getId());
		Asserts.assertEqualsCustomer(customer, inserted);
		
	}
}
