package com.esferalia.aon.occam.test.registry.address;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	@Repeat( 50 )
	public void test() {
		RegistryAddress address = AonFaker.getRegistryAddress( ctx ); 
		address = RegistryAddressDAO.save(ctx, address);
		RegistryAddress inserted = RegistryAddressDAO.get(ctx, address.getId());
		Asserts.assertEqualsRegistryAddress(address, inserted);
		
		address = RegistryAddressDAO.save(ctx, address);
		RegistryAddress updated = RegistryAddressDAO.get(ctx, address.getId());
		Asserts.assertEqualsRegistryAddress(address, updated);
		
		RegistryAddressDAO.delete(ctx, address.getId());
		RegistryAddress deleted = RegistryAddressDAO.get(ctx, address.getId());
		assertNull(deleted);
	}
	
}
