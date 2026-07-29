package com.esferalia.aon.occam.test.registry.dirstaff;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.impl.jooq.dao.RDirStaffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyDocumentTest extends AbstractOccamTest {

	@Test
	public void test() {
		RDirStaff rdirStaff = AonFaker.getRDirStaff( ctx ); 
		rdirStaff.setDocument(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> RDirStaffDAO.save(ctx, rdirStaff) );
		assertEquals(AonError.EMPTY_DATA.format( RDirStaffDAO.RDIRSTAFF_DOCUMENT_LABEL),e.getMessage());
	}

}
