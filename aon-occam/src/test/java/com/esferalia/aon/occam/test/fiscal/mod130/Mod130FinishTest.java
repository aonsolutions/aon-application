package com.esferalia.aon.occam.test.fiscal.mod130;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;

class Mod130FinishTest extends AbstractOccamTest {
	
	@Test
	void testFinalize() {
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
			assertNotNull(mod130.getDeclarationResultType(), "Mod130. Tipo resultado NULL");
			boolean finance = mod130.getDeclarationResultType().mustCreateFinance(); 
			MODEL130.markAsFinished(getOccam(), mod130);
			Mod130 mod130Bis = MODEL130.get(getOccam(), model.getId());
			assertEquals(FiscalStatus.FINISHED, mod130Bis.getStatus(),"Status not FINISHED");
			Asserts.assertEqualsDouble("Mod130. Resultado no coincide."
					, result0
					, mod130Bis.getDeclarationResult());
			assertNotNull(mod130Bis.getDeclarationResultType(),"Mod130. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod130Bis.getFinance(),"Mod130. Finance NULL");	
			} else {
				assertNull(mod130Bis.getFinance(),"Mod130. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId),"Mod130. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
