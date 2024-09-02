package com.esferalia.aon.occam.test.fiscal.mod115;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

@Nested
class Mod115FinishTest extends AbstractOccamTest {
	
	@Test
	void testFinalize() {
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
			assertNotNull(mod115.getDeclarationResultType(), "Mod115. Tipo resultado NULL");
			boolean finance = mod115.getDeclarationResultType().mustCreateFinance(); 
			MODEL115.markAsFinished(getOccam(), mod115);
			Mod115 mod115Bis = MODEL115.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod115Bis.getStatus(), "Status not FINISHED");
			Asserts.assertEqualsDouble("Mod115. Resultado no coincide."
					, result0
					, mod115Bis.getDeclarationResult());
			assertNotNull(mod115Bis.getDeclarationResultType(), "Mod115. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod115Bis.getFinance(), "Mod115. Finance NULL");	
			} else {
				assertNull(mod115Bis.getFinance(),"Mod115. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId), "Mod115. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
