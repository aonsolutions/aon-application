package com.esferalia.aon.occam.test.registry.address;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveMainAddress extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry = RegistryDAO.save(ctx, registry);
		Integer registryId = registry.getId();
		
		RegistryAddress toDeleteAddress = AonFaker.getRegistryAddress( ctx , registryId);
		toDeleteAddress.setMain(true);
		toDeleteAddress= RegistryAddressDAO.save(ctx, toDeleteAddress);
		// Primera dirección. Es main por lo que debe conservar el main.
		assertEquals(toDeleteAddress.isMain(), true);
		RegistryAddressDAO.delete(ctx, toDeleteAddress.getId());
		
		
		RegistryAddress firstAddress = AonFaker.getRegistryAddress( ctx , registryId);
		firstAddress.setMain(false);
		firstAddress = RegistryAddressDAO.save(ctx, firstAddress);
		// Primera dirección. NO es main por lo que debe ser main.
		assertEquals(firstAddress.isMain(), true);
		
		RegistryAddress secondAddress = AonFaker.getRegistryAddress( ctx , registryId);
		secondAddress.setMain(false);
		secondAddress = RegistryAddressDAO.save(ctx, secondAddress);
		// Segunda dirección. NO es main por lo que debe conservar NO main.
		assertEquals(secondAddress.isMain(), false);
		
		RegistryAddress thirdAddress = AonFaker.getRegistryAddress( ctx , registryId);
		thirdAddress.setMain(true);
		thirdAddress = RegistryAddressDAO.save(ctx, thirdAddress);
		firstAddress = RegistryAddressDAO.get( ctx, firstAddress.getId());
		
		// Tercera dirección. Es main por lo que debe conservar main. 
		assertEquals(thirdAddress.isMain(), true);
		// y ha debido modificar el main de la primera a false.
		assertEquals(firstAddress.isMain(), false);		
		
	}

}
