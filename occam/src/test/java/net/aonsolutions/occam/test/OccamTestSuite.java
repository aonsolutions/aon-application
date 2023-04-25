package net.aonsolutions.occam.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.occam.test.config.ConfigTestSuite;
import net.aonsolutions.occam.test.constants.ConstantsTestSuite;
import net.aonsolutions.occam.test.dao.DAOTestSuite;
import net.aonsolutions.occam.test.invoice.InvoiceTestSuite;

@Suite
@SelectClasses({
	ConstantsTestSuite.class
	,ConfigTestSuite.class
	,InvoiceTestSuite.class
	,DAOTestSuite.class
})
public class OccamTestSuite {

	
}
