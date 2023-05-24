package net.aonsolutions.occam.test.dao;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 SecurityDAOTest.class
	,AccountDAOTest.class
	,GeozoneDAOTest.class
	,ConfigurationDAOTest.class
	,DomainDAOTest.class
})
public class DAOTestSuite {

	
}
