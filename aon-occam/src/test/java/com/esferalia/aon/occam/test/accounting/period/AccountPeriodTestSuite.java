package com.esferalia.aon.occam.test.accounting.period;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	InsertTest.class,
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyInitialDateTest.class,
	ValidationSaveEmptyDeadlineTest.class,
	ValidationSaveWrongRangeTest.class,
	ValidationSaveInitialDateOverlapTest.class,
	ValidationSaveDeadlineOverlapTest.class,
	ValidationAutoCompleteStatusTest.class,
	UpdateTest.class,
	DeleteTest.class,
	CheckMinDateTest.class,
	CheckMaxDateTest.class
})
public class AccountPeriodTestSuite {

	
}
