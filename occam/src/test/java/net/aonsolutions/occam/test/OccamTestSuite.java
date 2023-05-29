package net.aonsolutions.occam.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.occam.test.accounting.AccountingTestSuite;
import net.aonsolutions.occam.test.config.ConfigTestSuite;
import net.aonsolutions.occam.test.constants.ConstantsTestSuite;
import net.aonsolutions.occam.test.core.CoreTestSuite;
import net.aonsolutions.occam.test.dao.DAOTestSuite;
import net.aonsolutions.occam.test.dao.client.PublicTestSuite;
import net.aonsolutions.occam.test.invoicing.InvoicingTestSuite;
import net.aonsolutions.occam.test.json.JSONTestSuite;

@Suite
@SelectClasses({
	 CoreTestSuite.class
	,ConstantsTestSuite.class
	,ConfigTestSuite.class
	,AccountingTestSuite.class
	,InvoicingTestSuite.class
	,JSONTestSuite.class
	,DAOTestSuite.class
	,PublicTestSuite.class
})
public class OccamTestSuite {

	
}
