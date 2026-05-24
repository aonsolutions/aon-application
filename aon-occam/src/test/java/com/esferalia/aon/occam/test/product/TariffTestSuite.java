package com.esferalia.aon.occam.test.product;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TariffValidationSaveEmptyDomain.class,
	TariffValidationSaveEmptyName.class,
	TariffValidationSaveOverflowCode.class,
	TariffValidationSaveOverflowName.class,
	TariffCRUDETest.class
})
public class TariffTestSuite {

	
}
