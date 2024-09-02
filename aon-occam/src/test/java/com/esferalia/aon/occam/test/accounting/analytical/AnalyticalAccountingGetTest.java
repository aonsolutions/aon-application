package com.esferalia.aon.occam.test.accounting.analytical;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.impl.jooq.dao.AnalyticalAccountingDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;


public class AnalyticalAccountingGetTest extends AbstractOccamTest {

	@Test
	public void testGet() {
		Analytical analytical = AnalyticalAccountingDAO.get(ctx);
		assertNotNull(analytical);
		assertNotNull(analytical.getCostCenters());
	}
	
}
