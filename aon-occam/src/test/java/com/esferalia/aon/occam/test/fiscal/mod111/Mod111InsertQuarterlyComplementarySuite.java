package com.esferalia.aon.occam.test.fiscal.mod111;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	InsertRandomInvoicesTest.class,
	Mod111InsertQuarterlyComplementaryTest.class,
	Mod111FinishTest.class,
	Mod111RoundedAmountsTest.class,
})
public class Mod111InsertQuarterlyComplementarySuite {

}
