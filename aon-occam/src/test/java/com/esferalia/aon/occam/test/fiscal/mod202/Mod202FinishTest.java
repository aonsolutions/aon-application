package com.esferalia.aon.occam.test.fiscal.mod202;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod202FinishTest extends AbstractOccamTest {
	
	@Test
	public void testFinalize() {
		for (Mod202 model : MODEL202.getMod202s(getOccam()) ) {
			Mod202 mod202 = MODEL202.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod202.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod202 = MODEL202.initializeForFinish(getOccam(), mod202);
			double result0 = mod202.getDeclarationResult();
			assertNotNull("Mod202. Tipo resultado NULL",mod202.getDeclarationResultType());
			boolean finance = mod202.getDeclarationResultType().mustCreateFinance(); 
			MODEL202.markAsFinished(getOccam(), mod202);
			Mod202 mod202Bis = MODEL202.get(getOccam(), model.getId());
			assertEquals("Status not FINISHED", FiscalStatus.FINISHED, mod202Bis.getStatus());
			Asserts.assertEqualsDouble("Mod202. Resultado no coincide."
					, result0
					, mod202Bis.getDeclarationResult());
			assertNotNull("Mod202. Tipo resultado NULL",mod202Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod202. Finance NULL",mod202Bis.getFinance());	
			} else {
				assertNull("Mod202. Finance NOT NULL",mod202Bis.getFinance());
			}
			if (oldFinanceId != null) {
				assertNull("Mod202. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
			}
		}
	}
	
}
