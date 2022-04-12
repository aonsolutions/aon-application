package com.esferalia.aon.occam.test.fiscal.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDomainTest extends AbstractOccamTest {

	@Test()
	public void test() {
		FiscalFakerParams params = FiscalFaker.getRandomParams( ctx, getOccam());
		FiscalModel model = FiscalFaker.getFiscalModel(params, FiscalModel::new);
		model.setDomain(0);
		Exception e = assertThrows(AonCoreException.class, () -> {
			FiscalModelDAO.save(ctx, model);
	    });
		assertEquals("Wrong Exception",AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
	}
	
}
