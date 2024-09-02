package com.esferalia.aon.occam.test.registry.company;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationDuplicateRowTest extends AbstractOccamTest {

	@Test
	public void test() {
		Company company = CompanyDAO.getByDomain(ctx, DOMAIN_ID);
		if (company == null) {
			company = AonFaker.getCompany(ctx, AonFaker.getRegistry(ctx));
			company = CompanyDAO.save(ctx, company);	
		}
		company.setId(null);
		Company comp = company; 
		AonCoreException e = assertThrows(AonCoreException.class, () -> CompanyDAO.save(ctx, comp) );
		assertEquals(AonError.DUPLICATED_COMPANY_ROW.getMessage(),e.getMessage());
	}
	
}
