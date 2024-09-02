package com.esferalia.aon.occam.test.fiscal.mod349;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.fiscal.invoice.InsertRandomInvoicesTest;

@Suite
@SelectClasses({
	InsertRandomInvoicesTest.class,
	Mod349DeleteTest.class,	
	Mod349InsertDiffQuaterlyTest.class,	
})
public class Mod349InsertDiffQuaterlySuite {

}
