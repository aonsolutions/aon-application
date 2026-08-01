package com.esferalia.aon.occam.test.registry.task_holder;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ValidationSaveEmptyActive extends AbstractOccamTest {

	@Test
	public void test() {
		TaskHolder taskHolder = AonFaker.getTaskHolder( ctx );
		taskHolder.setActive(null);
		taskHolder = TaskHolderDAO.save(ctx, taskHolder);
		assertEquals(taskHolder.isActive(), false);
	}

}
