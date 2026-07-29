package com.esferalia.aon.occam.test.project;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectHolderTest extends AbstractOccamTest {

	@Test
	public void crudeTest() {
		ProjectHolder projectHolder = AonFaker.getProjectHolder(ctx);
		
		projectHolder = ProjectHolderDAO.insert(ctx, projectHolder);
		Integer projectHolderId = projectHolder.getId();
		ProjectHolder inserted = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		Asserts.assertEqualsProjectHolder(projectHolder, inserted);
		
		projectHolder.setEndDate(new Date());
		projectHolder = ProjectHolderDAO.update(ctx, projectHolder);
		ProjectHolder updated = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		Asserts.assertEqualsProjectHolder(projectHolder, updated);
		
		ProjectHolderDAO.delete(ctx, projectHolder.getId());
		ProjectHolder deleted = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		
		assertNull(deleted.getId());
	}

	@Test
	public void dirtyTest() {
		ProjectHolder projectHolder = AonFaker.getProjectHolder(ctx);
		projectHolder.setDirty(false);
		projectHolder = ProjectHolderDAO.save(ctx, projectHolder);
		assertNull(projectHolder.getId());
		
		projectHolder.setDirty(true);
		projectHolder = ProjectHolderDAO.save(ctx, projectHolder);
		assertNotNull(projectHolder.getId());
		Integer projectHolderId = projectHolder.getId();
		
		ProjectHolder auxId = projectHolder.setId(null);
		projectHolder = ProjectHolderDAO.save(ctx, auxId);
		assertNull(projectHolder.getId());
		projectHolder.setId(projectHolderId);
		
		ProjectHolder auxDomain = projectHolder.setDomain(1231215);
		projectHolder = ProjectHolderDAO.save(ctx, auxDomain);
		ProjectHolder auxDomain2 = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		assertEquals(ctx.getDomainId(), auxDomain2.getDomain().intValue());
		projectHolder.setDomain(ctx.getDomainId());	
		
		Integer projectId = projectHolder.getProject(); 
		ProjectHolder auxProject = projectHolder.setProject(32312);
		projectHolder = ProjectHolderDAO.save(ctx, auxProject);
		ProjectHolder auxProject2 = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		assertEquals(projectId, auxProject2.getProject());
		projectHolder.setProject(projectId);	

		Date startDate = AonDateUtils.getDate(2021, 05, 12);
		ProjectHolder auxStartDate = projectHolder.setStartDate(startDate);
		projectHolder = ProjectHolderDAO.save(ctx, auxStartDate);
		ProjectHolder auxStartDate2 = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		assertEquals(startDate, auxStartDate2.getStartDate());
		
		Date endDate = AonDateUtils.getDate(2021, 10, 12);
		ProjectHolder auxEndDate = projectHolder.setEndDate(endDate);
		projectHolder = ProjectHolderDAO.save(ctx, auxEndDate);
		ProjectHolder auxEndDate2 = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		assertEquals(endDate, auxEndDate2.getEndDate());
		
		Workgroup workgroup = WorkgroupDAO.save(ctx, AonFaker.getWorkgroup(ctx)); 
		ProjectHolder auxWorkgroup = projectHolder.setWorkgroup(workgroup);
		projectHolder = ProjectHolderDAO.save(ctx, auxWorkgroup);
		ProjectHolder auxWorkgroup2 = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		assertEquals(workgroup.getId(), auxWorkgroup2.getWorkgroup().getId());
		
		TaskHolder taskHolder = TaskHolderDAO.save(ctx, AonFaker.getTaskHolder(ctx)); 
		ProjectHolder auxTaskHolder = projectHolder.setTaskHolder(taskHolder);
		projectHolder = ProjectHolderDAO.save(ctx, auxTaskHolder);
		ProjectHolder auxTaskHolder2 = ProjectHolderDAO.get(ctx, f -> f.getIdProperty().eq(projectHolderId));
		assertEquals(taskHolder.getId(), auxTaskHolder2.getTaskHolder().getId());
	}
}
