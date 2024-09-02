package com.esferalia.aon.occam.test.fiscal.mod349;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({

	Mod349InsertManualSuite.class,
	Mod349InsertDiffQuaterlySuite.class,
	Mod349InsertDiffMonthlySuite.class,
	Mod349InsertNoDiffQuaterlySuite.class,
	Mod349InsertNoDiffMonthlySuite.class,
	Mod349FlowStatusSuite.class,

})
public class Mod349TestSuite {

}
