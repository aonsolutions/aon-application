package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class RegistryMediaTest {
	
	@Test
	void testRegistryMedia() {
		RegistryMedia expected = AonMocker.mock(RegistryMedia.class);
		RegistryMedia actual = new RegistryMedia()
			.setDeleted(expected.isDeleted())
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setRegistry(expected.getRegistry())
			.setMedia(expected.getMedia())
			.setValue(expected.getValue())
			.setComment(expected.getComment())
			.setAdministrative(expected.isAdministrative())
			.setCommercial(expected.isCommercial())
			.setTechnical(expected.isTechnical())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
