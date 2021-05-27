package com.esferalia.aon.occam.test.registry.task_holder;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyActive.class,
	ValidationSaveEmptyType.class,
	CRUDETest.class,
	CRUDEExistingRegistryTest.class,
})
public class TaskHolderTestSuite {

	
}
