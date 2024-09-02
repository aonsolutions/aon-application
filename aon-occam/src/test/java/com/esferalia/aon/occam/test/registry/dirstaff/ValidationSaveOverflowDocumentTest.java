package com.esferalia.aon.occam.test.registry.dirstaff;

import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.impl.jooq.dao.RDirStaffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowDocumentTest extends AbstractOccamTest {

	@Test
	public void test() {
		RDirStaff rDirStaff = AonFaker.getRDirStaff(ctx);
		rDirStaff.setDocument( AonStringUtils.repeat('X', RDIR_STAFF.DOCUMENT.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RDirStaffDAO.save(ctx, rDirStaff) );
		assertEquals(AonError.INVALID_LENGTH.format( RDirStaffDAO.RDIRSTAFF_DOCUMENT_LABEL, RDIR_STAFF.DOCUMENT.getDataType().length() ),e.getMessage());
	}

}
