package com.esferalia.aon.occam.test.fiscal.mod123;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	InsertRandomInvoicesTest.class,
	Mod123InsertQuarterlyComplementaryTest.class,
	Mod123FinishTest.class,
	Mod123RoundedAmountsTest.class,
})
public class Mod123InsertQuarterlyComplementarySuite {

}
