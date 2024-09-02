package com.esferalia.aon.occam.test.registry.task_holder;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ValidationSaveEmptyActive.class,
	ValidationSaveEmptyType.class,
	CRUDETest.class,
	CRUDEExistingRegistryTest.class,
	TaskHolderTest.class
})
public class TaskHolderTestSuite {

	
}
