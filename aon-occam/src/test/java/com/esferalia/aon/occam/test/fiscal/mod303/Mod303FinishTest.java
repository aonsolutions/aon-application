package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

class Mod303FinishTest extends Mod303AbstractTest {
	
	@Test
	void testFinalize() {
		Integer creditorId = null;
		Creditor ar = ctx.getConfiguration().fiscal().getAdmonCreditor();
		if ( ar != null) {
			creditorId = ar.getId();
		}
		if (creditorId == null) {
			Creditor creditor = AonRandom.getCreditor(ctx);
			if (creditor == null) {
				creditor = CreditorDAO.save(ctx, AonFaker.getCreditor(ctx));
			}
			creditorId = creditor.getId();
			ctx.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, ctx.getDomainId())
			.set(APP_PARAM.NAME, AppParam.FS_ADMON_CREDITOR.toString())
			.set(APP_PARAM.VALUE, creditorId.toString() )
			.onDuplicateKeyUpdate()
			.set(APP_PARAM.VALUE, creditorId.toString() )				
			.execute();
			ctx.log().info("App Param FS_ADMON_CREDITOR set to " + creditorId);
		}
	
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod303.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod303 = MODEL303.initializeForFinish(getOccam(), mod303);
			double result0 = mod303.getDeclarationResult();
			assertNotNull(mod303.getDeclarationResultType(),"Mod303. Tipo resultado NULL");
			boolean finance = mod303.getDeclarationResultType().mustCreateFinance(); 
			MODEL303.markAsFinished(getOccam(), mod303);
			Mod303 mod303Bis = MODEL303.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(mod303Bis);
			assertEquals(FiscalStatus.FINISHED, mod303Bis.getStatus(),"Status not FINISHED");
			Asserts.assertEqualsDouble("Mod303. Resultado no coincide."
					, result0
					, mod303Bis.getDeclarationResult());
			assertNotNull(mod303Bis.getDeclarationResultType(),"Mod303. Tipo resultado NULL");
			if (finance) {
				assertNotNull(mod303Bis.getFinance(),"Mod303. Finance NULL");	
			} else {
				assertNull(mod303Bis.getFinance(),"Mod303. Finance NOT NULL");
			}
			if (oldFinanceId != null) {
				assertNull(FinanceDAO.getFinance(ctx, oldFinanceId),"Mod303. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
			}
		}
	}
	
}
