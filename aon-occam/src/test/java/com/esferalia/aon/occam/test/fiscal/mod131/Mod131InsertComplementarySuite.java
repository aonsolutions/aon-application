package com.esferalia.aon.occam.test.fiscal.mod131;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	InsertRandomInvoicesTest.class,
	Mod131InsertComplementaryTest.class,
	Mod131FinishTest.class,
	Mod131RoundedAmountsTest.class,
})
public class Mod131InsertComplementarySuite {

}
