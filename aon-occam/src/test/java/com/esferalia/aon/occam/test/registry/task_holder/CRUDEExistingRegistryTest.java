package com.esferalia.aon.occam.test.registry.task_holder;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDEExistingRegistryTest extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		registry = RegistryDAO.save(ctx, registry);
		
		TaskHolder taskHolder = AonFaker.getTaskHolder(ctx,  registry);
		taskHolder = TaskHolderDAO.save(ctx, taskHolder);
		TaskHolder inserted = TaskHolderDAO.get(ctx, taskHolder.getId());
		Asserts.assertEqualsTaskHolder(taskHolder, inserted);
	}
}
