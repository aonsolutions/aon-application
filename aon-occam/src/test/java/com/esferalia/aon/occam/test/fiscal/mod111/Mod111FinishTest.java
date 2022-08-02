package com.esferalia.aon.occam.test.fiscal.mod111;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

public class Mod111FinishTest extends AbstractOccamTest {
	
	@Test
	public void testFinalize() {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod111.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod111 = MODEL111.initializeForFinish(getOccam(), mod111);
			double result0 = mod111.getDeclarationResult();
			assertNotNull("Mod111. Tipo resultado NULL",mod111.getDeclarationResultType());
			boolean finance = mod111.getDeclarationResultType().mustCreateFinance(); 
			MODEL111.markAsFinished(getOccam(), mod111);
			Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
			assertEquals("Status not FINISHED", FiscalStatus.FINISHED, mod111Bis.getStatus());
			Asserts.assertEqualsDouble("Mod111. Resultado no coincide."
					, result0
					, mod111Bis.getDeclarationResult());
			assertNotNull("Mod111. Tipo resultado NULL",mod111Bis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod111. Finance NULL",mod111Bis.getFinance());	
			} else {
				assertNull("Mod111. Finance NOT NULL",mod111Bis.getFinance());
			}
			if (oldFinanceId != null) {
				assertNull("Mod111. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
			}
		}
	}
	
}
