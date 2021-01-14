package com.esferalia.aon.occam.test.customer;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Customer customer = AonFaker.getCustomer( ctx ); 
		customer = CustomerDAO.insert(ctx, customer);
		Customer inserted = CustomerDAO.get(ctx, customer.getId());
		Asserts.assertEqualsCustomer(customer, inserted);
		
		customer = CustomerDAO.update(ctx, customer);
		Customer updated = CustomerDAO.get(ctx, customer.getId());
		Asserts.assertEqualsCustomer(customer, updated);
		
		CustomerDAO.delete(ctx, customer.getId());
		Customer deleted = CustomerDAO.get(ctx, customer.getId());
		assertNull(deleted);
	}
}
