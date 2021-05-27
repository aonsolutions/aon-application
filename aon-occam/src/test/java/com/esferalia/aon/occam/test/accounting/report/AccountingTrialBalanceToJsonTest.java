package com.esferalia.aon.occam.test.accounting.report;


import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.AccountTrialBalanceReportJSON;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonDateUtils;


public class AccountingTrialBalanceToJsonTest extends AbstractOccamTest {

	@Test
	public void test() {
		int[] levels = {1,2,4,5,9};
		Date now = new Date();
		int year = AonDateUtils.getYear(now);
		
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, AonRandom.getRandomYearDay(year)); 
		AccountingReportParams params = new AccountingReportParams()
			.setDomain( DOMAIN_ID )
			.setDomainName( DOMAIN_NAME )
			.setUser(USER)
			.setLevel(levels[ AonRandom.getInt(0, levels.length) ])
			.setPeriod( period == null? null : period.getId() )
			.setFromDate( period == null? null : period.getInitiationDate() )
			.setToDate( period == null? null : period.getDeadline() )
			;
		AccountTrialBalanceReport report = ACCOUNTING.getAccountTrialBalance(DOMAIN_NAME, DOMAIN_ID, USER, params);
		System.out.println( AccountTrialBalanceReportJSON.toJSON(report).toString(2) );
	}

}
