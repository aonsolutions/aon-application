package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class CompanyTest {
	
	@Test
	void testCompany() {
		Company expected = AonMocker.mock(Company.class);
		Company actual = new Company()
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
			.setActive(expected.isActive())
			.setSurcharge(expected.isSurcharge())
			.setWithholding(expected.isWithholding())
			.setVatAccrualPayment(expected.isVatAccrualPayment())
			.seteInvoice(expected.iseInvoice())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
