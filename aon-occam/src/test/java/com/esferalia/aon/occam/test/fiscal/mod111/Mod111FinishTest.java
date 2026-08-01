package com.esferalia.aon.occam.test.fiscal.mod111;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

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
			assertNotNull(mod111.getDeclarationResultType(), "Mod111. Tipo resultado NULL");
			boolean finance = mod111.getDeclarationResultType().mustCreateFinance(); 
			MODEL111.markAsFinished(getOccam(), mod111);
			Mod111 mod111Bis = MODEL111.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod111Bis.getStatus(), "Status not FINISHED");
			Asserts.assertEqualsDouble("Mod111. Resultado no coincide."
					, result0
					, mod111Bis.getDeclarationResult());
			assertNotNull(mod111Bis.getDeclarationResultType(), "Mod111. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod111Bis.getFinance(), "Mod111. Finance NULL");	
			} else {
				assertNull(mod111Bis.getFinance(), "Mod111. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId), "Mod111. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
