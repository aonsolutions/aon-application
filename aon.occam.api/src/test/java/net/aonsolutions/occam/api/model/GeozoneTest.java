package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class GeozoneTest {
	
	@Test
	void testGeozone() {
		Geozone expected = AonMocker.mock(Geozone.class);
		Geozone actual = new Geozone()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setCode(expected.getCode())
			.setSystem(expected.isSystem())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
