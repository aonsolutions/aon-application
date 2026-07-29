package com.esferalia.aon.occam.test.fiscal.model;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ValidationContactPhoneLengthTest extends AbstractOccamTest {

	@Test()
	public void test() {
		FiscalFakerParams params = FiscalFaker.getRandomParams( ctx, getOccam());
		FiscalModel model = FiscalFaker.getFiscalModel(params, FiscalModel::new);
		int length = FS_MODEL.CONTACT_PHONE.getDataType().length();
		model.setContactPhone( AonStringUtils.repeat("X", length + 2));
		Exception e = assertThrows(AonCoreException.class, () -> {
			FiscalModelDAO.save(ctx, model);
	    });
		String expected = AonError.INVALID_LENGTH.format( FiscalModelValidation.CONTACT_PHONE, length );
		assertEquals(expected, e.getMessage(), "Wrong Exception");
	}
	
}
