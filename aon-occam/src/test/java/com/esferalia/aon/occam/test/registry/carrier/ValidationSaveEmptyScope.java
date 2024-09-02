package com.esferalia.aon.occam.test.registry.carrier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

class ValidationSaveEmptyScope extends AbstractOccamTest {

	@Test 
	@Disabled
	void test() {
		Carrier carrier = AonFaker.getCarrier( ctx );
		carrier.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CarrierDAO.save(ctx, carrier) );
		assertEquals(AonError.EMPTY_SCOPE.getMessage(),e.getMessage());
	}

}
