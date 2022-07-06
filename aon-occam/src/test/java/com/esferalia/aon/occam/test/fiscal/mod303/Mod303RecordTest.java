package com.esferalia.aon.occam.test.fiscal.mod303;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303RecordTest extends AbstractOccamTest {
	
	@Test
	public void testRecord() {
		ctx.getDslContext().transaction( config -> {
			for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
				System.out.println();
				System.out.println();
				FiscalTestSuite.printModel( model );
				model =  Mod303DAO.doRecord(ctx, model);
				if ( model.isRecorded()) {
					AccountEntry ae = AccountEntryDAO.getAccountEntry(ctx, model.getAccountEntry());
					assertNotNull("Mod303. Sin apunte", ae);
					FiscalTestSuite.print( ae );
					if ( AonMathUtils.isNotZero( model.getDeclarationResult() )) {
						assertNotEquals("Mod303. Sin detalles en apuntes", 0, ae.getDetailsSize());
					}
					Asserts.assertEqualsDouble("Mod303. Apunte descuadrado", 
						ae.getDetails().stream().map(aed -> aed.getDebit() ).reduce(0.0, Double::sum),
						ae.getDetails().stream().map(aed -> aed.getCredit() ).reduce(0.0, Double::sum));
				} else {
					fail("WARNING! No script found!!");
				}
			}
			
		});
	}
	
	
	
}
