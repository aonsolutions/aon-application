package com.esferalia.aon.occam.test.accounting.analytical;

import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.NAME;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.ACCOUNTS;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.CC1;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.COST_CENTERS;
import static com.esferalia.aon.occam.test.accounting.analytical.AnalyticalAccountingData.PERCENTS;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccount;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.occam.impl.jooq.dao.AnalyticalAccountingDAO;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;


public class AnalyticalAccountingSaveTest extends AbstractOccamTest {

	@Test
	public void testSave() {
		Analytical analytical = new Analytical();
		analytical.setName(NAME);
		if (COST_CENTERS.length != PERCENTS.length) {
			 throw new IllegalStateException("Longitudes de los arrays!!");
		}
		for (int i = 0; i < COST_CENTERS.length; i++) {
			AnalyticalCostCenter costCenter = new AnalyticalCostCenter().setName( COST_CENTERS[i]).setPercent(PERCENTS[i]).setMain(COST_CENTERS[i] == CC1); 
			analytical.add(costCenter);
			for (int x = 0; x < ACCOUNTS.length; x++) {
				costCenter.addAccount( new AnalyticalAccount().setCode(ACCOUNTS[x].getCode()).setPercent(PERCENTS[i]) );
			}
		}
		AnalyticalAccountingDAO.save(ctx, analytical);
	}
	
}
