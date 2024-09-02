package com.esferalia.aon.occam.test.product;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	TariffValidationSaveEmptyDomain.class,
	TariffValidationSaveEmptyName.class,
	TariffValidationSaveOverflowCode.class,
	TariffValidationSaveOverflowName.class,
	TariffCRUDETest.class,
})
public class TariffTestSuite {

	
}
