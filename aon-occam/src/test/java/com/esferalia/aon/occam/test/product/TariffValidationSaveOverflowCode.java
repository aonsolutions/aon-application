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

public class TariffValidationSaveOverflowCode extends AbstractOccamTest {

	@Test
	public void test() {
		Tariff tariff = AonFaker.getTariff( ctx );
		tariff.setCode( AonStringUtils.repeat('X', TARIFF.CODE.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> TariffDAO.insert(ctx, tariff) );
		assertEquals(AonError.INVALID_LENGTH.format( "C\u00F3digo", TARIFF.CODE.getDataType().length() ),e.getMessage());
	}

}
