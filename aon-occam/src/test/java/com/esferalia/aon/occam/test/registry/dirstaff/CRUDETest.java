package com.esferalia.aon.occam.test.registry.dirstaff;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.impl.jooq.dao.RDirStaffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CRUDETest extends AbstractOccamTest {

	@Test
	@Repeat( 50 )
	public void test() {
		RDirStaff rDirStaff = AonFaker.getRDirStaff( ctx ); 
		rDirStaff = RDirStaffDAO.save(ctx, rDirStaff);
		RDirStaff inserted = RDirStaffDAO.get(ctx, rDirStaff.getId());
		Asserts.assertEqualsRDirStaff(rDirStaff, inserted);
		
		rDirStaff = RDirStaffDAO.save(ctx, rDirStaff);
		RDirStaff updated = RDirStaffDAO.get(ctx, rDirStaff.getId());
		Asserts.assertEqualsRDirStaff(rDirStaff, updated);
		
		RDirStaffDAO.delete(ctx, rDirStaff.getId());
		RDirStaff deleted = RDirStaffDAO.get(ctx, rDirStaff.getId());
		assertNull(deleted);
	}
	
}
