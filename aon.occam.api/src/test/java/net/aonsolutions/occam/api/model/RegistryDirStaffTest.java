package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class RegistryDirStaffTest {
	
	@Test
	void testRegistryDirStaff() {
		RegistryDirStaff expected = AonMocker.mock(RegistryDirStaff.class);
		RegistryDirStaff actual = new RegistryDirStaff()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setRegistry(expected.getRegistry())
			.setDocument(expected.getDocument())
			.setName(expected.getName())
			.setShareHolder(expected.isShareHolder())
			.setRepresentative(expected.isRepresentative())
			.setDirector(expected.isDirector())
			.setRepresentativeLabor(expected.isRepresentativeLabor())
			.setDueDate(expected.getDueDate())
			.setPercentShare(expected.getPercentShare())
			.setShareNumber(expected.getShareNumber())
			.setNominalValue(expected.getNominalValue())
			.setChargeDescription(expected.getChargeDescription())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
