package com.code.aon.report.test;

import static org.junit.jupiter.api.Assertions.*;

import com.code.aon.report.config.ReportConfig;
import com.code.aon.report.config.ReportConfigurationManager;
import com.code.aon.report.config.ReportConfigurationParser;

import org.junit.jupiter.api.Test;

/**
 * @author Consulting & Development. ecastellano - 14-nov-2005
 *
 */
public class ReportConfigLoaderTest {

	/**
	 *
	 */
	@Test
	public void testReadConfiguration() {
        try {
			ReportConfigurationParser parser = ReportConfigurationParser.getInstance();
			ReportConfigurationManager rc = parser.getConfigurationManager();
			System.out.println( rc );
			ReportConfig rcg= rc.getReport( "salesInvoiceList" );
			System.out.println( rcg );
			assertEquals("/com/code/aon/ui/finance/report/salesInvoiceList.jasper", rcg.getTemplate() );
			assertEquals("com.code.aon.finance.Invoice", rcg.getBeanKey()  );
			assertEquals("#{feeInvoicing}", rcg.getCriteriaProvider() );
			assertNotNull( rcg.getFetchMode() );
			assertEquals(true, rcg.getFetchMode().isPaginated() );
			assertEquals(100, rcg.getFetchMode().getPageCount() );
			assertEquals(50, rcg.getFetchMode().getVirtualizerPageMax() );
			assertNotNull( rcg.getParams());
			assertEquals("#{company.obtainCompany}", rcg.getParams().get("company") );
			ReportConfig rcg1 = rc.getReport( "customer" );
			assertTrue(rcg1.isForceRefresh());
			ReportConfig rcg2 = rc.getReport( "payform" );
			assertFalse(rcg2.isForceRefresh());
			ReportConfig rcg3 = rc.getReport( "item" );
			assertTrue(rcg3.isForceRefresh());
		} catch (Exception e) {
			e.printStackTrace();
			fail( e.getMessage() );
		}
	}
}
