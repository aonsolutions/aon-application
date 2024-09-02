package com.esferalia.aon.occam.test.fiscal.mod123;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

class Mod123FinishTest extends AbstractOccamTest {
	
	@Test
	void testFinalize() {
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
			assertNotNull(mod123.getDeclarationResultType(), "Mod123. Tipo resultado NULL");
			boolean finance = mod123.getDeclarationResultType().mustCreateFinance(); 
			MODEL123.markAsFinished(getOccam(), mod123);
			Mod123 mod123Bis = MODEL123.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod123Bis.getStatus(), "Status not FINISHED");
			Asserts.assertEqualsDouble("Mod123. Resultado no coincide."
					, result0
					, mod123Bis.getDeclarationResult());
			assertNotNull(mod123Bis.getDeclarationResultType(), "Mod123. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod123Bis.getFinance(), "Mod123. Finance NULL");	
			} else {
				assertNull(mod123Bis.getFinance(), "Mod123. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId), "Mod123. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
