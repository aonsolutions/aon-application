package com.esferalia.aon.occam.test.fiscal.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyInvalidYearTest extends AbstractOccamTest {

	@Test()
	public void test() {
		FiscalFakerParams params = FiscalFaker.getRandomParams( ctx, getOccam());
		FiscalModel model = FiscalFaker.getFiscalModel(params, FiscalModel::new);
		model.setYear(2004);
		Exception e = assertThrows(AonCoreException.class, () -> {
			FiscalModelDAO.save(ctx, model);
	    });
		assertEquals(AonError.INVALID_YEAR.getMessage(),e.getMessage(),"Wrong Exception");

		model.setYear(2044);
		e = assertThrows(AonCoreException.class, () -> {
			FiscalModelDAO.save(ctx, model);
	    });
		assertEquals(AonError.INVALID_YEAR.getMessage(),e.getMessage(),"Wrong Exception");
	}
	
}
