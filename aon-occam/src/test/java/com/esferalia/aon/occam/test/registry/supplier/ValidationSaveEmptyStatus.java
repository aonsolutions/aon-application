package com.esferalia.aon.occam.test.registry.supplier;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyStatus extends AbstractOccamTest {

	@Test
	public void test() {
		Supplier registry = AonFaker.getSupplier( ctx );
		registry.setStatus(null);
		registry = SupplierDAO.save(ctx, registry);
		assertEquals(registry.getStatus(), RegistryStatus.ACTIVE );
	}

}
