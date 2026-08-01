package com.esferalia.aon.occam.test.registry.carrier;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyStatus extends AbstractOccamTest {

	@Test
	public void test() {
		Carrier carrier = AonFaker.getCarrier(ctx);
		carrier.setStatus(null);
		carrier = CarrierDAO.save(ctx, carrier);
		assertEquals(carrier.getStatus(), CarrierStatus.ACTIVE );
	}

}
