package com.esferalia.aon.occam.test.fiscal.mod390hf;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	InsertRandomInvoicesTest.class,
	Mod390HFInsertReplacementTest.class,
	Mod390HFRoundedAmountsTest.class,
	Mod390HFFinishTest.class,
})
public class Mod390HFValidationInsertReplacementSuite {

}
