package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class TariffValidationSaveEmptyName extends AbstractOccamTest {

	@Test
	public void test() {
		Tariff tariff = AonFaker.getTariff( ctx );
		tariff.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> TariffDAO.insert(ctx, tariff) );
		assertEquals(AonError.EMPTY_NAME.getMessage(),e.getMessage());
	}

}
