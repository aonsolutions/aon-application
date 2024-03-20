package com.esferalia.aon.occam.test.registry.carrier;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDEExistingRegistryTest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry = RegistryDAO.save(ctx, registry);
		
		Carrier carrier = AonFaker.getCarrier( ctx,  registry);
		carrier = CarrierDAO.save(ctx, carrier);
		Carrier inserted = CarrierDAO.get(ctx, carrier.getId());
		Asserts.assertEqualsCarrier(carrier, inserted);
	}
}
