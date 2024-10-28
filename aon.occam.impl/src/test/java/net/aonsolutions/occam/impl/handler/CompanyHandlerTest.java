package net.aonsolutions.occam.impl.handler;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.json.CompanyJSON;
import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.Company;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;

class CompanyHandlerTest extends AbstractOccamImplTest {

	@Test
	void testGetByDomain() {
		Optional<Company> companyOpt = CompanyHandler.getByDomain(ctx, DOMAIN_ID);
		assertNotNull(companyOpt);
		assertTrue(companyOpt.isPresent());
	}
	
	@Test
	void testDBJSON() {
		Optional<Company> companyOpt = CompanyHandler.getByDomain(ctx, DOMAIN_ID);
		assertNotNull(companyOpt);
		assertTrue(companyOpt.isPresent());
		JSONObject json = CompanyJSON.toJSON(companyOpt.get());
		AonAsserts.assertNotEmptyKeys("CompanyJSON", json);
	}
	
}
