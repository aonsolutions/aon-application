package com.esferalia.aon.occam.test.finance.paymethod;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	PayMethodValidationSaveEmptyDomain.class,
	PayMethodValidationSaveEmptyName.class,
	PayMethodValidationSaveOverflowName.class,
	PayMethodCRUDETest.class,
})
public class PayMethodTestSuite {

	
}
