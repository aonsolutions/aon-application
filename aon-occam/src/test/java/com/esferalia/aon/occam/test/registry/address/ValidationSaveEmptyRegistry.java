package com.esferalia.aon.occam.test.registry.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyRegistry extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryAddress address = AonFaker.getRegistryAddress( ctx );
		address.setRegistry(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryAddressDAO.save(ctx, address) );
		assertEquals(AonError.EMPTY_DATA.format( RegistryAddressDAO.ADDRESS_REGISTRY_LABEL),e.getMessage());
	}

}
