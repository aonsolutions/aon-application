package com.code.aon.ui.report.test;

import java.net.URL;

import junit.framework.TestCase;

import com.code.aon.ui.report.config.ReportConfig;
import com.code.aon.ui.report.config.ReportConfigurationManager;
import com.code.aon.ui.report.config.ReportConfigurationParser;

/**
 * @author Consulting & Development. ecastellano - 14-nov-2005
 *
 */
public class ReportConfigLoaderTest extends TestCase {

	/**
	 * 
	 */
	public void testReadConfiguration() {
        try {
			ReportConfigurationParser parser = ReportConfigurationParser.getInstance();
			URL url = ReportConfigLoaderTest.class.getResource( "report-config.xml" );
			ReportConfigurationManager rc = parser.getConfiguration(url.openStream());
			System.out.println( rc );
			ReportConfig rcg= rc.getReport( "payform" );
			System.out.println( rcg );
		} catch (Exception e) {
			e.printStackTrace();
			fail( e.getMessage() );
		}
	}
}
