package com.esferalia.aon.occam.test.registry.company;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	GetByDomainTest.class,
	ValidationDuplicateRowTest.class,
	CompanyUpdateTest.class,
})
public class CompanyTestSuite {

	
}
