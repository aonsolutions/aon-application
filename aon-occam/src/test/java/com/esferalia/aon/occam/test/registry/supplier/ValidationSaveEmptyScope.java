package com.esferalia.aon.occam.test.registry.supplier;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyScope extends AbstractOccamTest {

	@Test 
	@Disabled
	void test() {
		Supplier registry = AonFaker.getSupplier( ctx );
		registry.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> SupplierDAO.save(ctx, registry) );
		assertEquals(AonError.EMPTY_SCOPE.getMessage(),e.getMessage());
	}

}
