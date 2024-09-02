package com.esferalia.aon.occam.test.registry.task_holder;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class TaskHolderTest extends AbstractOccamTest {

	@Test
	public void test() {
		List<TaskHolder> list = TaskHolderDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())).toList();
		insertTaskHolders(1);
		
		long count1 = TaskHolderDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())).count();
		assertEquals(list.size() + 1, count1);
		
		long count2 = TaskHolderDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()), new Options()).count();
		assertEquals(list.size() + 1, count2);
		
		long count3 = TaskHolderDAO.getStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()), new Options().setPage(1).setPerPage(1)).count();
		assertEquals(1, count3);
	}
	
	private void insertTaskHolders(Integer count) {
		for(Integer i = 0; i < count; i++) {
			TaskHolder taskHolder = AonFaker.getTaskHolder( ctx ); 
			taskHolder = TaskHolderDAO.save(ctx, taskHolder);	
			
			TaskHolder inserted = TaskHolderDAO.get(ctx, taskHolder.getId());
			Asserts.assertEqualsTaskHolder(taskHolder, inserted);
		}
	}
}
