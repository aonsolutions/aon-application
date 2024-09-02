package com.esferalia.aon.occam.test.product;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class TariffValidationSaveEmptyDomain extends AbstractOccamTest {

	@Test
	public void test() {
		Tariff tariff = AonFaker.getTariff( ctx );
		tariff.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> TariffDAO.insert(ctx, tariff) );
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
	}

}
