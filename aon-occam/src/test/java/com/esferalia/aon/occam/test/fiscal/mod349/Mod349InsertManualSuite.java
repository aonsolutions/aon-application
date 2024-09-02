package com.esferalia.aon.occam.test.fiscal.mod349;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	Mod349DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod349InsertManualQuaterlyTest.class,
	Mod349InsertManualMonthlyTest.class,
	Mod349MarkAsPendingTest.class,
	Mod349MarkAsFinishedTest.class,
	Mod349MarkAsSentTest.class,
})
public class Mod349InsertManualSuite {

}
