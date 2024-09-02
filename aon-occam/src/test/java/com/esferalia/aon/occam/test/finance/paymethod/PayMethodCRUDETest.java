package com.esferalia.aon.occam.test.finance.paymethod;


import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class PayMethodCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));
		PayMethodDAO.save(ctx, AonFaker.getPayMethod( ctx ));

		PayMethod payMethod = AonFaker.getPayMethod( ctx ); 
		payMethod = PayMethodDAO.save(ctx, payMethod);
		PayMethod inserted = PayMethodDAO.get(ctx, payMethod.getId());
		Asserts.assertEqualsPayMethod(payMethod, inserted);
		
		payMethod = PayMethodDAO.save(ctx, payMethod);
		PayMethod updated = PayMethodDAO.get(ctx, payMethod.getId());
		Asserts.assertEqualsPayMethod(payMethod, updated);
		
		PayMethodDAO.delete(ctx, payMethod.getId());
		PayMethod deleted = PayMethodDAO.get(ctx, payMethod.getId());
		assertNull(deleted);
	}

}
