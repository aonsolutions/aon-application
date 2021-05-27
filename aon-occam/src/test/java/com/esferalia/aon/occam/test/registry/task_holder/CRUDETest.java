package com.esferalia.aon.occam.test.registry.task_holder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		TaskHolder taskHolder = AonFaker.getTaskHolder( ctx ); 
		taskHolder = TaskHolderDAO.save(ctx, taskHolder);
		TaskHolder inserted = TaskHolderDAO.get(ctx, taskHolder.getId());
		Asserts.assertEqualsTaskHolder(taskHolder, inserted);
		
		taskHolder = TaskHolderDAO.save(ctx, taskHolder);
		TaskHolder updated = TaskHolderDAO.get(ctx,taskHolder.getId());
		Asserts.assertEqualsTaskHolder(taskHolder, updated);
		
		TaskHolderDAO.delete(ctx, taskHolder);
		TaskHolder deleted = TaskHolderDAO.get(ctx, taskHolder.getId());
		assertEquals(true, deleted.isEmpty());
	}
}
