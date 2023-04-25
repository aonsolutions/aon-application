package net.aonsolutions.occam.test.dao;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	DomainDAOTest.class
	,InvoiceDAOTest.class
})
public class DAOTestSuite {

	
}
