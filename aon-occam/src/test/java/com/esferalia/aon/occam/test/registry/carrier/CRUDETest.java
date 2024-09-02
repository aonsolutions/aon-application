package com.esferalia.aon.occam.test.registry.carrier;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Carrier carrier = AonFaker.getCarrier( ctx ); 
		carrier = CarrierDAO.save(ctx, carrier);
		Carrier inserted = CarrierDAO.get(ctx, carrier.getId());
		Asserts.assertEqualsCarrier(carrier, inserted);
		
		carrier = CarrierDAO.save(ctx, carrier);
		Carrier updated = CarrierDAO.get(ctx, carrier.getId());
		Asserts.assertEqualsCarrier(carrier, updated);
		
		CarrierDAO.delete(ctx, carrier.getId());
		Carrier deleted = CarrierDAO.get(ctx, carrier.getId());
		assertNull(deleted.getId());
	}
}
