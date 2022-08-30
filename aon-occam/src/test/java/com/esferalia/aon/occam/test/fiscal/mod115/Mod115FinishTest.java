package com.esferalia.aon.occam.test.fiscal.mod115;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod115FinishTest extends AbstractOccamTest {
	
	@Test
	public void testFinalize() {
		for (Mod115 model : MODEL115.getMod115s(getOccam()) ) {
			Mod115 mod115 = MODEL115.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod115.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod115 = MODEL115.initializeForFinish(getOccam(), mod115);
			double result0 = mod115.getDeclarationResult();
			assertNotNull("Mod115. Tipo resultado NULL",mod115.getDeclarationResultType());
			boolean finance = mod115.getDeclarationResultType().mustCreateFinance(); 
			MODEL115.markAsFinished(getOccam(), mod115);
			Mod115 mod115Bis = MODEL115.get(getOccam(), model.getId());
			assertEquals("Status not FINISHED", FiscalStatus.FINISHED, mod115Bis.getStatus());
			Asserts.assertEqualsDouble("Mod115. Resultado no coincide."
					, result0
					, mod115Bis.getDeclarationResult());
			assertNotNull("Mod115. Tipo resultado NULL",mod115Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod115. Finance NULL",mod115Bis.getFinance());	
			} else {
				assertNull("Mod115. Finance NOT NULL",mod115Bis.getFinance());
			}
			if (oldFinanceId != null) {
				assertNull("Mod115. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
			}
		}
	}
	
}
