package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.occam.test.OccamAssertions.fail;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod303UnrecordTest extends Mod303AbstractTest {
	
	@Test
	public void testRecord() {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			System.out.println();
			System.out.println();
			FiscalTestSuite.printModel( model );
			Integer accountEntryId = model.getAccountEntry();
			model =  Mod303DAO.unrecord(ctx, model);
			if ( model.isRecorded()) {
				fail("Modelo no descontabilizado");
			}
			AccountEntry ae = AccountEntryDAO.getAccountEntry(ctx, accountEntryId);
			if ( ae != null) {
				fail("Apunte no borrado");
			}
		}
	}
	
	
	
}
