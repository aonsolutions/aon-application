package com.esferalia.aon.occam.test.fiscal.mod421;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.MessageFormat;
import java.util.Objects;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod421AbstractTest extends AbstractOccamTest {
	
	private static Integer creditorId = null;
	
	private void ensureCreditor() {
		if (creditorId == null) {
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
		}
	}
	
	protected void testFinalize(Mod421 mod421) {
		ensureCreditor();
		Integer oldFinanceId =  ctx.getDslContext()
				.select( FS_MODEL.FINANCE)
				.from(FS_MODEL)
				.where( FS_MODEL.ID.eq(mod421.getId()))
				.fetch()
				.stream()
				.map( r -> r.getValue(FS_MODEL.FINANCE))
				.filter( Objects::nonNull)
				.findFirst()
				.orElse(null);
		mod421 = MODEL421.initializeForFinish(getOccam(), mod421);
		double result0 = mod421.getDeclarationResult();
		assertNotNull("Mod421. Tipo resultado NULL",mod421.getDeclarationResultType());
		boolean finance = mod421.getDeclarationResultType().mustCreateFinance() || mod421.isAeatRectification(); 
		MODEL421.markAsFinished(getOccam(), mod421);
		Mod421 mod421Bis = MODEL421.get(getOccam(), mod421.getId());
		assertEquals("Status not FINISHED", FiscalStatus.FINISHED, mod421Bis.getStatus());
		Asserts.assertEqualsDouble("Mod421. Resultado no coincide."
				, result0
				, mod421Bis.getDeclarationResult());
		assertNotNull("Mod421. Tipo resultado NULL",mod421Bis.getDeclarationResultType());
		if (finance) {
			assertNotNull("Mod421. Finance NULL",mod421Bis.getFinance());	
		} else {
			assertNull("Mod421. Finance NOT NULL",mod421Bis.getFinance());
		}
		if (oldFinanceId != null) {
			assertNull("Mod421. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!", FinanceDAO.getFinance(ctx, oldFinanceId));	
		}
	}
	
	protected void testRoundedAmounts(Mod421 mod421) {
		mod421.getMap().values()
			.stream()
			.forEach( det -> {
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Declarado",det.getType())
					,AonMathUtils.round(det.getDeclaredAmount()) 
					,det.getDeclaredAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Resultado",det.getType())
					,AonMathUtils.round(det.getResultAmount()) 
					,det.getResultAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Ajuste",det.getType())
					,AonMathUtils.round(det.getAdjustAmount()) 
					,det.getAdjustAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Amount",det.getType())
					,AonMathUtils.round(det.getAmount()) 
					,det.getAmount());
		});
	}

	protected Mod421 insertModel( FiscalFakerParams params) {
		Mod421 mod421 = FiscalFaker.createMod421(params);
		MODEL421.save(getOccam(), mod421);
		Mod421 actual = MODEL421.get(getOccam(), mod421.getId());  
		Asserts.assertMod421(mod421, actual);
		
		testRoundedAmounts(mod421);
		
		testFinalize(mod421);
		
		testRoundedAmounts(mod421);
		
		FiscalTestSuite.printModel(actual);		
		return actual;
	}


	protected double getMod421SuitableResult(Mod421 mod) {
		return AonMathUtils.round(mod.getAdministration().visit( 
			new  IAdministrationVisitor<Double>() {
				@Override
				public Double visitAlava() {
					return null;					
				}
	
				@Override
				public Double visitBizkaia() {
					return null;
				}
	
				@Override
				public Double visitGipuzkoa() {
					return null;
				}
	
				@Override
				public Double visitNavarra() {
					return null;
				}
	
				@Override
				public Double visitCommonTerritory() {
					return null;
				}
	
				@Override
				public Double visitUnknown() {
					return mod.getDeclarationResult();
				}
				
				@Override
				public Double visitCanarias() {
					return mod.getDeclarationResult();
				}
				
			}
		)); 
	}
}
