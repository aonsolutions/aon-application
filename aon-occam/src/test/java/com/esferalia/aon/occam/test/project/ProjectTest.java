package com.esferalia.aon.occam.test.project;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ProjectTest extends AbstractOccamTest {

	@Test
	public void crudeTest() {
		Project project = AonFaker.getProject(ctx);
		
		project = ProjectDAO.insert(ctx, project);
		Integer projectId = project.getId();
		Project inserted = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		Asserts.assertEqualsProject(project, inserted);
		
		project.setName("Updated Name");
		project = ProjectDAO.update(ctx, project);
		Project updated = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		Asserts.assertEqualsProject(project, updated);
		
		ProjectDAO.delete(ctx, project.getId());
		Project deleted = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		
		assertNull(deleted.getId());
	}
	
	@Test
	public void dirtyTest() {
		Project project = AonFaker.getProject(ctx);
		project.setDirty(false);
		project = ProjectDAO.save(ctx, project);
		assertNull(project.getId());
		
		project.setDirty(true);
		project = ProjectDAO.save(ctx, project);
		assertNotNull(project.getId());
		Integer projectId = project.getId();
		
		Project auxId = project.setId(null);
		project = ProjectDAO.save(ctx, auxId);
		assertNull(project.getId());
		project.setId(projectId);
		
		Project auxDomain = project.setDomain(new Domain().setId(1231215));
		project = ProjectDAO.save(ctx, auxDomain);
		Project auxDomain2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(ctx.getDomainId(), auxDomain2.getDomain().getId().intValue());
		project.setDomain(new Domain().setId(ctx.getDomainId()));	
				
		Project auxName = project.setName("Updated Name");
		project = ProjectDAO.save(ctx, auxName);
		Project auxName2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(auxName2.getName(), "Updated Name");
		
		Project auxAlias = project.setAlias("Updated Alias");
		project = ProjectDAO.save(ctx, auxAlias);
		Project auxAlias2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(auxAlias2.getAlias(), "Updated Alias");
		
		Date date = AonDateUtils.getDate(2021, 10, 12);
		Project auxDate = project.setDate(date);
		project = ProjectDAO.save(ctx, auxDate);
		Project auxDate2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(date, auxDate2.getDate());
		
		ProjectType type = ProjectTypeDAO.save(ctx, AonFaker.getProjectType(ctx)); 
		Project auxType = project.setType(type);
		project = ProjectDAO.save(ctx, auxType);
		Project auxType2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(type.getId(), auxType2.getType().getId());
	
		Project auxActive = project.setActive(!project.isActive());
		project = ProjectDAO.save(ctx, auxActive);
		Project auxActive2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(project.isActive(), auxActive2.isActive());
		
		Project auxTas = project.setTas(!project.isTas());
		project = ProjectDAO.save(ctx, auxTas);
		Project auxTas2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(project.isTas(), auxTas2.isTas());
		
		Project auxCommercial = project.setCommercial(!project.isCommercial());
		project = ProjectDAO.save(ctx, auxCommercial);
		Project auxCommercial2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(project.isCommercial(), auxCommercial2.isCommercial());
		
		Project auxReservation = project.setReservation(!project.isReservation());
		project = ProjectDAO.save(ctx, auxReservation);
		Project auxReservation2 = ProjectDAO.get(ctx, f -> f.getIdProperty().eq(projectId));
		assertEquals(project.isReservation(), auxReservation2.isReservation());
	}

}
