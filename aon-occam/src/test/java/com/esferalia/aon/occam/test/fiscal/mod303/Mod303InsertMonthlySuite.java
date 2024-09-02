package com.esferalia.aon.occam.test.fiscal.mod303;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	Mod303ReopenTest.class,					// Pendiente
	Mod303DeleteTest.class,
	InsertRandomInvoicesTest.class,
	Mod303InsertMonthlyTest.class,
})
public class Mod303InsertMonthlySuite {

}
