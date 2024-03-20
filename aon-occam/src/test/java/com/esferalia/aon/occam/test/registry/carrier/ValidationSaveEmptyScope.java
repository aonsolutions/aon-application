package com.esferalia.aon.occam.test.registry.carrier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyScope extends AbstractOccamTest {

	@Test @Ignore
	public void test() {
		Carrier carrier = AonFaker.getCarrier( ctx );
		carrier.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CarrierDAO.save(ctx, carrier) );
		assertEquals(AonError.EMPTY_SCOPE.getMessage(),e.getMessage());
	}

}
