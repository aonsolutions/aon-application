package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class CreditorTest {
	
	@Test
	void testCreditor() {
		Creditor expected = AonMocker.mock(Creditor.class);
		Creditor actual = new Creditor()
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
			.setWithholding(expected.isWithholding())
			.setVatAccrualPayment(expected.isVatAccrualPayment())
			.setTransaction(expected.getTransaction())
			.setStatus(expected.getStatus())
			.setScope(expected.getScope())
			.setAccount(expected.getAccount().orElse(null))
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
