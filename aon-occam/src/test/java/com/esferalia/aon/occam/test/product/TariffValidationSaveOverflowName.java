package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TariffValidationSaveOverflowName extends AbstractOccamTest {

	@Test
	public void test() {
		Tariff tariff = AonFaker.getTariff( ctx );
		tariff.setName( AonStringUtils.repeat('X', TARIFF.NAME.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> TariffDAO.insert(ctx, tariff) );
		assertEquals(AonError.INVALID_LENGTH.format( "Nombre", TARIFF.NAME.getDataType().length() ),e.getMessage());
	}

}
