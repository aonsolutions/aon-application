package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class SupplierTest {
	
	@Test
	void testSupplier() {
		Supplier expected = AonMocker.mock(Supplier.class);
		Supplier actual = new Supplier()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setDocument(expected.getDocument())
			.setDocumentType(expected.getDocumentType())
			.setDocumentCountry(expected.getDocumentCountry())
			.setName(expected.getName())
			.setAlias(expected.getAlias())
			.setLegalPerson(expected.isLegalPerson())
			.setNationality(expected.getNationality())
			.setConfidential(expected.isConfidential())
			.setTariff(expected.getTariff())
			.setWithholding(expected.isWithholding())
			.setWithholdingFarmer(expected.isWithholdingFarmer())
			.setVatAccrualPayment(expected.isVatAccrualPayment())
			.setTransaction(expected.getTransaction())
			.setStatus(expected.getStatus())
			.setScope(expected.getScope())
			.setPurchaseValuated(expected.isPurchaseValuated())
			.setAccount(expected.getAccount().orElse(null))
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
