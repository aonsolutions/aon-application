package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class RegistryTest {
	
	@Test
	void testRegistry() {
		Registry expected = AonMocker.mock(Registry.class);
		Registry actual = new Registry()
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
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
