package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod303AbstractTest extends AbstractOccamTest {
	
	private static Integer creditorId = null;
	private static boolean prorrateSet;
	private static double prorratePercent;
	private static boolean specialProrrate;
	
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
	
	
	private static void ensureProratePercent(Date today) {
		if ( !prorrateSet ) {
			int year = AonDateUtils.getYear(today);
			Mod303 mod = Mod303DAO.getMod303s(ctx, getOccam().getDomain(), p -> p.getYearProperty().eq(year))
				.findFirst()
				.orElse(null);
			if (mod == null) {
				prorratePercent = AonRandom.gt(10)? 0 : AonRandom.getPercent();
				specialProrrate = AonMathUtils.isNotZero(prorratePercent) && AonRandom.gt(60);
				prorrateSet = true;
			} else {
				prorratePercent = mod.getProratePercent();
				specialProrrate = AonMathUtils.isNotZero(prorratePercent) && mod.isSpecialProrate();
				prorrateSet = true;
			}
			if ( AonMathUtils.isNotZero(prorratePercent) ) {
				String pr1 = " (" + (specialProrrate?"E":"G") + ") ";
				System.out.println( "\t ---- [Prorrate SET: " + prorratePercent + pr1 + "]");
			} else {
				System.out.println( "\t ---- [Prorrate SET: NO prorrate]");
			}
			
		}
	}
	static double getProratePercent(Date today) {
		ensureProratePercent(today);
		return prorratePercent;
	}
	static boolean isSpecialProrrate(Date today) {
		ensureProratePercent(today);
		return specialProrrate;
	}

	
	protected void testFinalize(Mod303 mod303) {
		ensureCreditor();
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
		assertNotNull(mod303.getDeclarationResultType(), "Mod303. Tipo resultado NULL");
		boolean finance = mod303.getDeclarationResultType().mustCreateFinance() || mod303.isAeatRectification(); 
		MODEL303.markAsFinished(getOccam(), mod303);
		Mod303 mod303Bis = MODEL303.get(getOccam(), mod303.getId());
		assertEquals(FiscalStatus.FINISHED, mod303Bis.getStatus(), "Status not FINISHED");
		Asserts.assertEqualsDouble("Mod303. Resultado no coincide."
				, result0
				, mod303Bis.getDeclarationResult());
		assertNotNull(mod303Bis.getDeclarationResultType(), "Mod303. Tipo resultado NULL");
		if (finance) {
			assertNotNull(mod303Bis.getFinance(), "Mod303. Finance NULL");	
		} else {
			assertNull(mod303Bis.getFinance(), "Mod303. Finance NOT NULL");
		}
		if (oldFinanceId != null) {
			assertNull(FinanceDAO.getFinance(ctx, oldFinanceId), "Mod303. PREVIOUS Finance ["+ oldFinanceId +"] NOT DELETED!");	
		}
	}
	
	protected void testRoundedAmounts(Mod303 mod303) {
		mod303.getMap().values()
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

	protected Mod303 insertModel( FiscalFakerParams params) {
		Mod303 mod303 = FiscalFaker.createMod303(params);
		MODEL303.save(getOccam(), mod303);
		Mod303 actual = MODEL303.get(getOccam(), mod303.getId());  
		Asserts.assertMod303(mod303, actual);
		
		testRoundedAmounts(mod303);
		
		testFinalize(mod303);
		
		testRoundedAmounts(mod303);
		
		FiscalTestSuite.printModel(actual);		
		return actual;
	}


	protected double getMod303SuitableResult(Mod303 mod) {
		return AonMathUtils.round(mod.getAdministration().visit( 
			new  IAdministrationVisitor<Double>() {
				@Override
				public Double visitAlava() {
					return mod.getDeclarationResult()
						+ mod.getAmount(Mod303Key.AR_C063);
				}
	
				@Override
				public Double visitBizkaia() {
					return mod.getDeclarationResult()
						+ mod.getAmount(Mod303Key.BZ_C041)
						- mod.getAmount(Mod303Key.BZ_C042);
				}
	
				@Override
				public Double visitGipuzkoa() {
					return mod.getDeclarationResult();
				}
	
				@Override
				public Double visitNavarra() {
					return mod.getDeclarationResult();
				}
	
				@Override
				public Double visitCommonTerritory() {
					return mod.getDeclarationResult() 
						- mod.getAmount(Mod303Key.CT_C87)
						+ mod.getAmount(Mod303Key.CT_C70);
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
