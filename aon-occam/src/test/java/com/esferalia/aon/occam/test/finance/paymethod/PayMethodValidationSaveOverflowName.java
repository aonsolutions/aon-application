package com.esferalia.aon.occam.test.finance.paymethod;


import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PayMethodValidationSaveOverflowName extends AbstractOccamTest {

	@Test
	public void test() {
		PayMethod payMethod = AonFaker.getPayMethod( ctx );
		payMethod.setName( AonStringUtils.repeat('X', PAY_METHOD.NAME.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> PayMethodDAO.save(ctx, payMethod) );
		assertEquals(AonError.INVALID_LENGTH.format( "Nombre", PAY_METHOD.NAME.getDataType().length() ),e.getMessage());
	}

}
