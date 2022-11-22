package com.esferalia.aon.occam.test.fiscal.mod390hf;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Objects;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;

public class Mod390HFFinishTest extends AbstractOccamTest {
	
	@Test
	public void testFinalize() {
		Integer creditorId = null;
		Creditor ar = getConfiguration().fiscal().getAdmonCreditor();
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
	
		for (Mod390HF model : MODEL390HF.getMod390HFs(getOccam()) ) {
			Mod390HF mod = MODEL390HF.get(getOccam(), model.getId());
			Integer oldFinanceId =  ctx.getDslContext()
					.select( FS_MODEL.FINANCE)
					.from(FS_MODEL)
					.where( FS_MODEL.ID.eq(mod.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue(FS_MODEL.FINANCE))
					.filter( Objects::nonNull)
					.findFirst()
					.orElse(null);
			mod = MODEL390HF.initializeForFinish(getOccam(), mod);
			double result0 = mod.getDeclarationResult();
			assertNotNull("Mod390HF. Tipo resultado NULL",mod.getDeclarationResultType());
			boolean finance = mod.getDeclarationResultType().mustCreateFinance(); 
			MODEL390HF.markAsFinished(getOccam(), mod);
			Mod390HF modBis = MODEL390HF.get(getOccam(), model.getId());
			FiscalTestSuite.printModel(modBis);
			assertEquals("Status not FINISHED", FiscalStatus.FINISHED, modBis.getStatus());
			Asserts.assertEqualsDouble("Mod303. Resultado no coincide."
					, result0
					, modBis.getDeclarationResult());
			assertNotNull("Mod303. Tipo resultado NULL",modBis.getDeclarationResultType());
			if (finance) {
				assertNotNull("Mod303. Finance NULL",modBis.getFinance());	
			} else {
				assertNull("Mod303. Finance NOT NULL",modBis.getFinance());
			}
			if (oldFinanceId != null) {
				assertNull("Mod303. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
			}
		}
	}
	
}
