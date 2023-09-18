package com.esferalia.aon.occam.test.fiscal.mod130;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod130FinishTest extends AbstractOccamTest {
	
	@Test
	public void testFinalize() {
		for (Mod130 model : MODEL130.getMod130s(getOccam()) ) {
			Mod130 mod130 = MODEL130.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod130.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod130 = MODEL130.initializeForFinish(getOccam(), mod130);
			double result0 = mod130.getDeclarationResult();
			assertNotNull("Mod130. Tipo resultado NULL",mod130.getDeclarationResultType());
			boolean finance = mod130.getDeclarationResultType().mustCreateFinance(); 
			MODEL130.markAsFinished(getOccam(), mod130);
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals("Status not FINISHED", FiscalStatus.FINISHED, mod130Bis.getStatus());
			Asserts.assertEqualsDouble("Mod130. Resultado no coincide."
					, result0
					, mod130Bis.getDeclarationResult());
			assertNotNull("Mod130. Tipo resultado NULL",mod130Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod130. Finance NULL",mod130Bis.getFinance());	
			} else {
				assertNull("Mod130. Finance NOT NULL",mod130Bis.getFinance());
			}
			if (oldFinanceId != null) {
				assertNull("Mod130. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
			}
		}
	}
	
}
