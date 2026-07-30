package com.esferalia.aon.occam.test.registry.company;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class GetByDomainTest extends AbstractOccamTest {

	@Test
	public void testGetByDomain() {
		Company company = CompanyDAO.getByDomain(ctx, DOMAIN_ID);
		assertNotNull(company);
	}
	
	@Test
	public void getCompany() {
		Company company1 = CompanyDAO.getCompany(ctx, DOMAIN_ID);
		assertNotNull(company1);
	}
	
}
