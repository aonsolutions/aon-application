package com.esferalia.aon.occam.test.accounting.period;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	InsertTest.class,
	ValidationSaveDeadlineOverlapTest.class,
	ValidationSaveEmptyDeadlineTest.class,
	ValidationSaveEmptyInitialDateTest.class,
	ValidationSaveInitialDateOverlapTest.class,
	ValidationSaveWrongRangeTest.class,
	ValidationSaveEmptyStatusTest.class,
	UpdateTest.class,
	DeleteTest.class,
	CheckMinDateTest.class,
	CheckMaxDateTest.class
})
public class AccountPeriodTestSuite {

	
}
