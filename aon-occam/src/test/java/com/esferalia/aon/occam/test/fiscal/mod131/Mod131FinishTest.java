package com.esferalia.aon.occam.test.fiscal.mod131;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

class Mod131FinishTest extends AbstractOccamTest {
	
	@Test
	void testFinalize() {
		for (Mod131 model : MODEL131.getMod131s(getOccam()) ) {
			Mod131 mod131 = MODEL131.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod131.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod131 = MODEL131.initializeForFinish(getOccam(), mod131);
			double result0 = mod131.getDeclarationResult();
			assertNotNull(mod131.getDeclarationResultType(), "Mod131. Tipo resultado NULL");
			boolean finance = mod131.getDeclarationResultType().mustCreateFinance(); 
			MODEL131.markAsFinished(getOccam(), mod131);
			Mod131 mod131Bis = MODEL131.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod131Bis.getStatus(),"Status not FINISHED");
			Asserts.assertEqualsDouble("Mod131. Resultado no coincide."
					, result0
					, mod131Bis.getDeclarationResult());
			assertNotNull(mod131Bis.getDeclarationResultType(),"Mod131. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod131Bis.getFinance(),"Mod131. Finance NULL");	
			} else {
				assertNull(mod131Bis.getFinance(),"Mod131. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId),"Mod131. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
