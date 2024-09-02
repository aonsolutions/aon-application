package com.esferalia.aon.occam.test.registry.dirstaff;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.impl.jooq.dao.RDirStaffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyNameTest extends AbstractOccamTest {

	@Test
	public void test() {
		RDirStaff rdirStaff = AonFaker.getRDirStaff( ctx ); 
		rdirStaff.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RDirStaffDAO.save(ctx, rdirStaff) );
		assertEquals(AonError.EMPTY_DATA.format( RDirStaffDAO.RDIRSTAFF_NAME_LABEL),e.getMessage());
	}

}
