package com.esferalia.aon.occam.test.registry.customer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyScope extends AbstractOccamTest {

	@Test 
	@Disabled
	void test() {
		Customer registry = AonFaker.getCustomer( ctx );
		registry.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CustomerDAO.save(ctx, registry) );
		assertEquals(AonError.EMPTY_SCOPE.getMessage(),e.getMessage());
	}

}
