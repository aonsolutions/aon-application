package net.aonsolutions.occam.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.occam.test.config.ConfigTestSuite;
import net.aonsolutions.occam.test.constants.ConstantsTestSuite;
import net.aonsolutions.occam.test.core.CoreTestSuite;
import net.aonsolutions.occam.test.dao.DAOTestSuite;
import net.aonsolutions.occam.test.dao.client.PublicDomainTestSuite;
import net.aonsolutions.occam.test.invoice.InvoiceTestSuite;

@Suite
@SelectClasses({
	 CoreTestSuite.class
	,ConstantsTestSuite.class
	,ConfigTestSuite.class
	,InvoiceTestSuite.class
	,DAOTestSuite.class
	,PublicDomainTestSuite.class
})
public class OccamTestSuite {

	
}
