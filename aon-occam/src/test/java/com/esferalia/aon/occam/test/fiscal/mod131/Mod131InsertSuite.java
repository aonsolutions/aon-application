package com.esferalia.aon.occam.test.fiscal.mod131;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	Mod131ReopenTest.class,
	Mod131DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod131InsertTest.class,
	Mod131FinishTest.class,
	Mod131RoundedAmountsTest.class,
})
public class Mod131InsertSuite {

}
