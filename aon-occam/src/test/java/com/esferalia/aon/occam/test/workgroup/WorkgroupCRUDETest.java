package com.esferalia.aon.occam.test.workgroup;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class WorkgroupCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Workgroup workgroup = AonFaker.getWorkgroup( ctx ); 
		workgroup = WorkgroupDAO.insert(ctx, workgroup);
		Integer workgroupId = workgroup.getId();
		Workgroup inserted = WorkgroupDAO.get(ctx, f -> f.getIdProperty().eq(workgroupId));
		Asserts.assertEqualsWorkgroup(workgroup, inserted);
		
		workgroup = WorkgroupDAO.update(ctx, workgroup);
		Workgroup updated = WorkgroupDAO.get(ctx, f -> f.getIdProperty().eq(workgroupId));
		Asserts.assertEqualsWorkgroup(workgroup, updated);
		
		WorkgroupDAO.delete(ctx, workgroup.getId());
		Workgroup deleted = WorkgroupDAO.get(ctx, f -> f.getIdProperty().eq(workgroupId));
		
		assertNull(deleted.getId());
	}

}
