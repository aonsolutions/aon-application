package com.esferalia.aon.occam.test.registry.supplier;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDEExistingRegistryTest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry = RegistryDAO.save(ctx, registry);
		
		Supplier supplier = AonFaker.getSupplier( ctx,  registry);
		supplier = SupplierDAO.save(ctx, supplier);
		Supplier inserted = SupplierDAO.get(ctx, supplier.getId());
		Asserts.assertEqualsSupplier(supplier, inserted);
		
	}
}
