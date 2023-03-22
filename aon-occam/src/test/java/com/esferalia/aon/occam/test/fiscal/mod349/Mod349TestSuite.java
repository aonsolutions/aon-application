package com.esferalia.aon.occam.test.fiscal.mod349;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@RunWith(Suite.class)
@SuiteClasses({

	Mod349DeleteTest.class,
	Mod349InsertManualQuaterlyTest.class,
	Mod349InsertManualMonthlyTest.class,
	
	Mod349MarkAsPendingTest.class,
	Mod349MarkAsFinishedTest.class,
	Mod349MarkAsSentTest.class,
	
	InsertRandomInvoicesTest.class,
	
	Mod349DeleteTest.class,	
	Mod349InsertDiffQuaterlyTest.class,	

	Mod349DeleteTest.class,	
	Mod349InsertDiffMonthlyTest.class,
	
	Mod349DeleteTest.class,	
	Mod349InsertNoDiffQuaterlyTest.class,	
	
	Mod349DeleteTest.class,	
	Mod349InsertNoDiffMonthlyTest.class,
	
	Mod349MarkAsPendingTest.class,
	Mod349MarkAsFinishedTest.class,
	Mod349MarkAsSentTest.class,
	
})
public class Mod349TestSuite {

}

