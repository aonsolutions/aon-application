package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class TariffTest {
	
	@Test
	void testTariff() {
		Tariff expected = AonMocker.mock(Tariff.class);
		Tariff actual = new Tariff()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setCode(expected.getCode())
			.setName(expected.getName())
			.setPurchase(expected.isPurchase())
			.setDiscount(expected.getDiscount())
			.setActive(expected.isActive())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
