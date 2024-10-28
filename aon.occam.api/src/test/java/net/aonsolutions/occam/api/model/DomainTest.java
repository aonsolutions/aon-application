package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class DomainTest {
	
	@Test
	void testDomain() {
		Domain expected = AonMocker.mock(Domain.class);
		Domain actual = new Domain()
			.setId(expected.getId())
			.setName(expected.getName())
			.setDescription(expected.getDescription())
			.setOwner(expected.getOwner())
			.setParentId(expected.getParentId())
			.setDomainType(expected.getDomainType())
			.setHeredityEnabled(expected.isHeredityEnabled())
			.setDomainManagement(expected.isDomainManagement())
			.setDisableDomainManagement(expected.isDisableDomainManagement())
			.setActive(expected.isActive())
			.setScope(expected.getScope())
			.setMaxDefinedUsers(expected.getMaxDefinedUsers())
			.setDefinedUsers(expected.getDefinedUsers())
			.setMaxDocumentSize(expected.getMaxDocumentSize())
			.setMaxTotalDocumentSize(expected.getMaxTotalDocumentSize())
			.setLastAccessUser(expected.getLastAccessUser())
			.setLastAccessDate(expected.getLastAccessDate())
			.setExpirationDate(expected.getExpirationDate())
			.setAonCustomer(expected.getAonCustomer())
			.setAonStatus(expected.getAonStatus())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())			
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
