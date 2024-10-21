package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class RegistryBankTest {
	
	@Test
	void testRegistryBank() {
		RegistryBank expected = AonMocker.mock(RegistryBank.class);
		RegistryBank actual = new RegistryBank()
			.setDeleted(expected.isDeleted())
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setRegistry(expected.getRegistry())
			.setBankAccount(expected.getBankAccount().orElse(null))
			.setBic(expected.getBic())
			.setSuffix(expected.getSuffix())
			.setAlias(expected.getAlias())
			.setActive(expected.isActive())
			.setAccount(expected.getAccount().orElse(null))
			.setRequisition(expected.getRequisition())
			.setSepaMandateRef(expected.getSepaMandateRef())
			.setBalance(expected.getBalance())
			.setAvailableBalance(expected.getAvailableBalance())
			.setBalanceDate(expected.getBalanceDate())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
