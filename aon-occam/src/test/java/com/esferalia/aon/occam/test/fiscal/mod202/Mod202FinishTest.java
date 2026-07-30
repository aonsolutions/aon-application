package com.esferalia.aon.occam.test.fiscal.mod202;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

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
			assertNotNull(mod202.getDeclarationResultType(), "Mod202. Tipo resultado NULL");
			boolean finance = mod202.getDeclarationResultType().mustCreateFinance(); 
			MODEL202.markAsFinished(getOccam(), mod202);
			Mod202 mod202Bis = MODEL202.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod202Bis.getStatus(), "Status not FINISHED");
			Asserts.assertEqualsDouble("Mod202. Resultado no coincide."
					, result0
					, mod202Bis.getDeclarationResult());
			assertNotNull(mod202Bis.getDeclarationResultType(), "Mod202. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod202Bis.getFinance(), "Mod202. Finance NULL");	
			} else {
				assertNull(mod202Bis.getFinance(), "Mod202. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId), "Mod202. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
