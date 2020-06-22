package com.esferalia.aon.occam.test.accounting.analytical;

import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.ACCOUNTS;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.COST_CENTERS;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.NAME;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.PERCENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccount;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.occam.impl.jooq.dao.AnalyticalAccountingDAO;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;


public class AnalyticalAccountingGetTest extends AbstractOccamTest {

	@Test
	public void testGet() {
		Analytical analytical = AnalyticalAccountingDAO.get(ctx);
		assertEquals(analytical.getName(), NAME);
		assertNotNull(analytical.getCostCenters());
		assertEquals(analytical.getCostCenters().size(), COST_CENTERS.length);
		for (int i = 0; i < COST_CENTERS.length; i++) {
			AnalyticalCostCenter cc = analytical.getCostCenters().get(COST_CENTERS[i]); 
			assertEquals(cc.getName(), COST_CENTERS[i]);	
			assertEquals(cc.getPercent(), PERCENTS[i],0);
			assertNotNull(cc.getAccounts());
			assertEquals(cc.getAccounts().size(), ACCOUNTS.length);
			for (int x = 0; x < ACCOUNTS.length; x++) {
				AnalyticalAccount account = cc.getAccounts().get(ACCOUNTS[x].getCode());
				assertEquals(account.getCode(), ACCOUNTS[x].getCode());	
				assertEquals(account.getPercent(), PERCENTS[i],0);
			}
		}
	}
	
}
