package com.esferalia.aon.occam.test.product;


import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class TariffCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Tariff tariff = AonFaker.getTariff( ctx ); 
		tariff = TariffDAO.insert(ctx, tariff);
		Tariff inserted = TariffDAO.get(ctx, tariff.getId());
		Asserts.assertEqualsTariff(tariff, inserted);
		
		tariff = TariffDAO.update(ctx, tariff);
		Tariff updated = TariffDAO.get(ctx, tariff.getId());
		Asserts.assertEqualsTariff(tariff, updated);
		
		TariffDAO.delete(ctx, tariff.getId());
		Tariff deleted = TariffDAO.get(ctx, tariff.getId());
		assertNull(deleted);
	}

}
