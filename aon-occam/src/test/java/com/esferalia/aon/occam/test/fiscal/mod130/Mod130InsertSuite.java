package com.esferalia.aon.occam.test.fiscal.mod130;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	Mod130ReopenTest.class,
	Mod130DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod130InsertTest.class,
	Mod130FinishTest.class,
	Mod130RoundedAmountsTest.class,
})
public class Mod130InsertSuite {

}
