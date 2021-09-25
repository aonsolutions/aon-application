package com.esferalia.aon.occam.test.registry.company;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	GetByDomainTest.class,
	ValidationDuplicateRowTest.class,
	CompanyUpdateTest.class,
})
public class CompanyTestSuite {

	
}
