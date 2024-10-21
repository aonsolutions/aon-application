package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class RegistryAddressTest {
	
	@Test
	void testRegistryAdddress() {
		RegistryAddress expected = AonMocker.mock(RegistryAddress.class);
		RegistryAddress actual = new RegistryAddress()
			.setDeleted(expected.isDeleted())
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setRegistry(expected.getRegistry())
			.setMain(expected.isMain())
			.setRecipient(expected.getRecipient())
			.setStreetType(expected.getStreetType())
			.setAddress(expected.getAddress())
			.setNumber(expected.getNumber())
			.setAddress2(expected.getAddress2())
			.setAddress3(expected.getAddress3())
			.setZip(expected.getZip())
			.setCity(expected.getCity())
			.setAlias(expected.getAlias())
			.setMunicipalityCode(expected.getMunicipalityCode())
			.setGeozone(expected.getGeozone().orElse(null))
			.setParent(expected.getParent().orElse(null))
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
