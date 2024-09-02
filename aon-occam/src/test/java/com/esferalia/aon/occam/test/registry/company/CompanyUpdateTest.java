package com.esferalia.aon.occam.test.registry.company;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CompanyUpdateTest extends AbstractOccamTest {

	@Test
	public void test() {
		ctx.transaction(configuration -> {
			Company ori = CompanyDAO.getByDomain(ctx, DOMAIN_ID);
			Company company = AonFaker.getCompany(ctx, AonFaker.getRegistry(ctx));
			company.setId(ori.getId());
			CompanyDAO.save(ctx, company);
			CompanyDAO.save(ctx, ori);
			Company actual = CompanyDAO.getByDomain(ctx, DOMAIN_ID);
			Asserts.assertEqualsCompany(ori, actual);
		});
	}

}
