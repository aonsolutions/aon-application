package com.esferalia.aon.occam.test.fiscal.mod123;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod123FinishTest extends AbstractOccamTest {
	
	@Test
	public void testFinalize() {
		for (Mod123 model : MODEL123.getMod123s(getOccam()) ) {
			Mod123 mod123 = MODEL123.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod123.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod123 = MODEL123.initializeForFinish(getOccam(), mod123);
			double result0 = mod123.getDeclarationResult();
			assertNotNull("Mod123. Tipo resultado NULL", mod123.getDeclarationResultType());
			boolean finance = mod123.getDeclarationResultType().mustCreateFinance(); 
			MODEL123.markAsFinished(getOccam(), mod123);
			Mod123 mod123Bis = MODEL123.get(getOccam(), model.getId());
			assertEquals("Status not FINISHED", FiscalStatus.FINISHED, mod123Bis.getStatus());
			Asserts.assertEqualsDouble("Mod123. Resultado no coincide."
					, result0
					, mod123Bis.getDeclarationResult());
			assertNotNull("Mod123. Tipo resultado NULL",mod123Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod123. Finance NULL",mod123Bis.getFinance());	
			} else {
				assertNull("Mod123. Finance NOT NULL",mod123Bis.getFinance());
			}
			if (oldFinanceId != null) {
				assertNull("Mod123. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
			}
		}
	}
	
}
