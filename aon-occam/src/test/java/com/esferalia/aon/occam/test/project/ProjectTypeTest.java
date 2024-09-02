package com.esferalia.aon.occam.test.project;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ProjectTypeTest extends AbstractOccamTest {

	@Test
	public void crudeTest() {
		ProjectType projectType = AonFaker.getProjectType(ctx);
		
		projectType = ProjectTypeDAO.insert(ctx, projectType);
		Integer projectTypeId = projectType.getId();
		ProjectType inserted = ProjectTypeDAO.get(ctx, f -> f.getIdProperty().eq(projectTypeId));
		Asserts.assertEqualsProjectType(projectType, inserted);
		
		projectType.setDescription("Updated Description");
		projectType = ProjectTypeDAO.update(ctx, projectType);
		ProjectType updated = ProjectTypeDAO.get(ctx, f -> f.getIdProperty().eq(projectTypeId));
		Asserts.assertEqualsProjectType(projectType, updated);
		
		ProjectTypeDAO.delete(ctx, projectType.getId());
		ProjectType deleted = ProjectTypeDAO.get(ctx, f -> f.getIdProperty().eq(projectTypeId));
		
		assertNull(deleted.getId());
	}

	@Test
	public void dirtyTest() {
		ProjectType projectType = AonFaker.getProjectType(ctx);
		projectType.setDirty(false);
		projectType = ProjectTypeDAO.save(ctx, projectType);
		assertNull(projectType.getId());
		
		projectType.setDirty(true);
		projectType = ProjectTypeDAO.save(ctx, projectType);
		assertNotNull(projectType.getId());
		Integer projectTypeId = projectType.getId();
		
		ProjectType auxId = projectType.setId(null);
		projectType = ProjectTypeDAO.save(ctx, auxId);
		assertNull(projectType.getId());
		projectType.setId(projectTypeId);
		
		ProjectType auxDomain = projectType.setDomain(1231215);
		projectType = ProjectTypeDAO.save(ctx, auxDomain);
		ProjectType auxDomain2 = ProjectTypeDAO.get(ctx, f -> f.getIdProperty().eq(projectTypeId));
		assertEquals(ctx.getDomainId(), auxDomain2.getDomain().intValue());
		projectType.setDomain(ctx.getDomainId());	
		
		ProjectType auxDescription = projectType.setDescription("Updated Description");
		projectType = ProjectTypeDAO.save(ctx, auxDescription);
		ProjectType auxDescription2 = ProjectTypeDAO.get(ctx, f -> f.getIdProperty().eq(projectTypeId));
		assertEquals("Updated Description", auxDescription2.getDescription());
		
		ProjectType auxActive = projectType.setActive(!projectType.isActive());
		projectType = ProjectTypeDAO.save(ctx, auxActive);
		ProjectType auxActive2 = ProjectTypeDAO.get(ctx, f -> f.getIdProperty().eq(projectTypeId));
		assertEquals(projectType.isActive(), auxActive2.isActive());
		
		
	}
}
