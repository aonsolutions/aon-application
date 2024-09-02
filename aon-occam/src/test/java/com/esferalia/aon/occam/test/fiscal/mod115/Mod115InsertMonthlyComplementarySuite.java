package com.esferalia.aon.occam.test.fiscal.mod115;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	InsertRandomInvoicesTest.class,
	Mod115InsertMonthlyComplementaryTest.class,
	Mod115FinishTest.class,
	Mod115RoundedAmountsTest.class,
})
public class Mod115InsertMonthlyComplementarySuite {

}
