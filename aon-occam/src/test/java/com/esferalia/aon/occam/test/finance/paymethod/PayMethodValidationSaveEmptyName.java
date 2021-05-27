package com.esferalia.aon.occam.test.finance.paymethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class PayMethodValidationSaveEmptyName extends AbstractOccamTest {

	@Test
	public void test() {
		PayMethod payMethod = AonFaker.getPayMethod( ctx );
		payMethod.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> PayMethodDAO.save(ctx, payMethod) );
		assertEquals(AonError.EMPTY_NAME.getMessage(),e.getMessage());
	}

}
