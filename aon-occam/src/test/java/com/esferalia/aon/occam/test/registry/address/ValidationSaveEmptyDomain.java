package com.esferalia.aon.occam.test.registry.address;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyDomain extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryAddress address = AonFaker.getRegistryAddress( ctx );
		address.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryAddressDAO.save(ctx, address) );
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
	}

}
