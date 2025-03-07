package com.esferalia.aon.occam.test.accounting.income;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AccountingIncomeJSONTest.class,
	ValidationSaveTest.class,
	SaveTest.class,
	CRUDETest.class,
})
public class AccountingIncomeTestSuite {

}
