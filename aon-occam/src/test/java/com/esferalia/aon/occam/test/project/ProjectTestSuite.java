package com.esferalia.aon.occam.test.project;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ProjectTest.class,
	ProjectTypeTest.class,
	ProjectHolderTest.class
})
public class ProjectTestSuite {

	
}
