package com.esferalia.aon.occam.test.finance.paymethod;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	PayMethodValidationSaveEmptyDomain.class,
	PayMethodValidationSaveEmptyName.class,
	PayMethodValidationSaveOverflowName.class,
	PayMethodCRUDETest.class,
})
public class PayMethodTestSuite {

	
}
