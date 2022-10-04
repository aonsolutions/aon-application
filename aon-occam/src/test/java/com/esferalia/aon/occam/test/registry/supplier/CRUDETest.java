package com.esferalia.aon.occam.test.registry.supplier;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Supplier supplier = AonFaker.getSupplier( ctx ); 
		supplier = SupplierDAO.save(ctx, supplier);
		Supplier inserted = SupplierDAO.get(ctx, supplier.getId());
		Asserts.assertEqualsSupplier(supplier, inserted);
		
		supplier = SupplierDAO.save(ctx, supplier);
		Supplier updated = SupplierDAO.get(ctx, supplier.getId());
		Asserts.assertEqualsSupplier(supplier, updated);
		
		SupplierDAO.delete(ctx, supplier.getId());
		Supplier deleted = SupplierDAO.get(ctx, supplier.getId());
		assertNull(deleted.getId());
	}
}
