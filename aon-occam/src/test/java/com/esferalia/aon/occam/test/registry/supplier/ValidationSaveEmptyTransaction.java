package com.esferalia.aon.occam.test.registry.supplier;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyTransaction extends AbstractOccamTest {

	@Test
	public void test() {
		Supplier registry = AonFaker.getSupplier( ctx );
		registry.setTransaction(null);
		registry = SupplierDAO.save(ctx, registry);
		assertEquals(registry.getTransaction(), InvoiceTransactionType.NATIONAL);
	}

}
