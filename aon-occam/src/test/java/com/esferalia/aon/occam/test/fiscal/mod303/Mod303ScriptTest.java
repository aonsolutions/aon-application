package com.esferalia.aon.occam.test.fiscal.mod303;

import static org.junit.Assert.assertThrows;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEAT390nfoScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATPrintScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegime4TScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAPrintScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017GIPUZKOAPrintScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017GIPUZKOARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017GIPUZKOAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032019ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model30320212AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model30320212AEATPrintScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021AEATPrintScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATSimplifiedRegime4TScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022BIZKAIAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022BIZKAIAScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022BIZKAIAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022GIPUZKOARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022GIPUZKOAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022NAVARRARGScript;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303ScriptTest extends Mod303AbstractTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(getTestDate())
				.setMonthly(true)
				.setAdministration(Administration.COMMON_TERRITORY);
		Mod303 mod303 = FiscalFaker.getMod303(params);
		MODEL303.calculate(getOccam(), mod303);
		
		test( mod303, Model3032017AEAT390nfoScript.values() );
		test( mod303, Model3032017AEATAdditionalDataScript.values() );
		test( mod303, Model3032017AEATGeneralRegimeScript1.values() );
		test( mod303, Model3032017AEATGeneralRegimeScript2.values() );
		test( mod303, Model3032017AEATResultScript.values() );
		test( mod303, Model3032017AEATSimplifiedRegime4TScript.values() );
		test( mod303, Model3032017AEATSimplifiedRegimeScript.values() );
		test( mod303, Model3032017AEATPrintScript.values() );
		test( mod303, Model30320212AEATAdditionalDataScript.values() );
		test( mod303, Model30320212AEATPrintScript.values() );
		test( mod303, Model3032021AEATResultScript.values() );
		test( mod303, Model3032021AEATPrintScript.values() );
		test( mod303, Model3032022AEATAdditionalDataScript.values() );
		test( mod303, Model3032022AEATGeneralRegimeScript1.values() );
		test( mod303, Model3032022AEATGeneralRegimeScript2.values() );
		test( mod303, Model3032022AEATResultScript.values() );
		test( mod303, Model3032022AEATSimplifiedRegime4TScript.values() );
		test( mod303, Model3032022AEATSimplifiedRegimeScript.values() );
	}
	
	@Test
	public void testGipuzkoaExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(getTestDate())
				.setMonthly(true)
				.setAdministration(Administration.GIPUZKOA);
		
		Period p = Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()));
		if (p.isLastPeriod()) {
			assertThrows(AonCoreException.class, () -> {
				FiscalFaker.getMod303(params);
			});
		} else {
			final Mod303 mod303 = FiscalFaker.getMod303(params);
			MODEL303.calculate(getOccam(), mod303);
			
			test( mod303,Model3032017GIPUZKOAAdditionalDataScript.values() );
			test( mod303,Model3032017GIPUZKOAResultScript.values() );
			test( mod303,Model3032017GIPUZKOARScript1.values() );
			test( mod303,Model3032017GIPUZKOAPrintScript.values() );
			test( mod303,Model3032021GIPUZKOAAdditionalDataScript.values() );
			test( mod303,Model3032022GIPUZKOAAdditionalDataScript.values() );
			test( mod303,Model3032022GIPUZKOAResultScript.values() );
			test( mod303,Model3032022GIPUZKOARScript1.values() );	
		}
	}
	
	@Test
	public void testBizkaiaExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(AonDateUtils.getYearFirstDay( getTestDate()))
				.setMonthly(true)
				.setAdministration(Administration.BIZKAIA);

		Period p = Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()));
		if (p.isLastPeriod()) {
			assertThrows(AonCoreException.class, () -> {
				FiscalFaker.getMod303(params);
			});
		} else {
			Mod303 mod303 = FiscalFaker.getMod303(params);
			MODEL303.calculate(getOccam(), mod303);
			 
			test( mod303,Model3032017BIZKAIAAdditionalDataScript.values() );
			test( mod303,Model3032017BIZKAIAPrintScript.values() );
			test( mod303,Model3032017BIZKAIAScript1.values() );
			test( mod303,Model3032017BIZKAIAScript2.values() );
			test( mod303,Model3032017BIZKAIASpecificOperationsScript.values() );
			test( mod303,Model3032022BIZKAIAAdditionalDataScript.values() );
			test( mod303,Model3032022BIZKAIAScript1.values() );
			test( mod303,Model3032022BIZKAIAScript2.values() );
			test( mod303,Model3032022BIZKAIASpecificOperationsScript.values() );
		}			
	}
	
	@Test
	public void testArabaExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(getTestDate())
				.setMonthly(true)
				.setAdministration(Administration.ALAVA);
		Period p = Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()));
		if (p.isLastPeriod()) {
			assertThrows(AonCoreException.class, () -> {
				FiscalFaker.getMod303(params);
			});
		} else {
			Mod303 mod303 = FiscalFaker.getMod303(params);
			mod303.setPeriod(
				mod303.isLastPeriod() 
						?Period.values()[(mod303.getPeriod().ordinal()-1)]
						:mod303.getPeriod()
			);
			MODEL303.calculate(getOccam(), mod303);
			
			test( mod303,Model3032017ARABAAdditionalDataScript.values() );
			test( mod303,Model3032017ARABAResultScript.values() );
			test( mod303,Model3032017ARABARScript1.values() );
			test( mod303,Model3032017ARABAScript2.values() );
			test( mod303,Model3032019ARABAScript2.values() );
			test( mod303,Model3032022ARABAAdditionalDataScript.values() );
			test( mod303,Model3032022ARABAResultScript.values() );
			test( mod303,Model3032022ARABARScript1.values() );
			test( mod303,Model3032022ARABAScript2.values() );
		}
	}
	
	@Test
	public void testNavarraExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(getTestDate())
				.setMonthly(true)
				.setAdministration(Administration.NAVARRA);
		Mod303 mod303 = FiscalFaker.getMod303(params);
		MODEL303.calculate(getOccam(), mod303);
		test( mod303, Model3032022NAVARRARGScript.values() );
	}
	
	
	private void test( Mod303 mod303, IModelScript<Mod303Key>[] scripts) {
		for (IModelScript<Mod303Key> script : scripts) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							for (Mod303Key key : script.getKeys() ) {
								String info = MODEL303.getInfo(getOccam(), mod303, script, infoKey);
								infoKey.visit( new IFiscalModelKeyInfoVisitor<String>(){
									
									private String arrayNotNull() {
										JSONArray array = new JSONArray(info);
										Assert.assertNotNull(array);
										return null;
									}
			
									@Override public String visitModelInvoiceVatBreakdown() { return arrayNotNull(); }
									@Override public String visitCompute() { return arrayNotNull(); }
									@Override public String visitModelInvoiceIrpfBreakdown() {return arrayNotNull(); }
									@Override public String visitModelSalaryIrpfBreakdown() {return arrayNotNull(); }
									@Override public String visitProrratedModelInvoiceVatBreakdown() {return arrayNotNull(); }
									@Override public String visitModelOutVatAccrualInvoice() {return arrayNotNull(); }
									@Override public String visitModelInVatAccrualInvoice() {return arrayNotNull(); }
									
									@Override 
									public String visitComputeKey() {
										Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
										if (key != null && dec.getRegularizationKey() != null && dec.getRegularizationKey() == key) {
											Assert.assertNotNull(info);
											Assert.assertNotEquals(info, "");
										} else  if (key != null && Arrays.stream(dec.getCompensationExplainKeys()).anyMatch(k -> k == key)) {
											Assert.assertNotNull(info);
											Assert.assertNotEquals(info, "");
										} else if (key != null && Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
											Assert.assertNotNull(info);
											Assert.assertNotEquals(info, "");
										} else if (key != null) {
											JSONObject json = new JSONObject(info);
											Assert.assertNotNull(json);
											JSONArray array = json.getJSONArray("messages");
											Assert.assertNotNull(array);
										}
			
										return null;
									}
									
									@Override 
									public String visitNone() { 
										Assert.assertEquals(AonStringUtils.EMPTY,info);
										return null;
									}
									
									@Override public String visitInvoice() {return null;}
									@Override public String visitInAccrualInvoice() {return null;}
									@Override public String visitOutAccrualInvoice() {return null;}
									@Override public String visitDiffInvoice() {return null;}
									@Override public String visitDiffInAccrualInvoice() {return null;}
									@Override public String visitDiffOutAccrualInvoice() {return null;}
									@Override public String visitSalary() {return null;}
									@Override public String visitDiffSalary() {return null;}
									@Override public String visitActAccount() {return null;}
									@Override public String visitTitle() {return null;}
									@Override public String visitIrpfActivity() {return null;}
									@Override public String visitCorporate() {return null;}
								});
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println( " \t [ERROR]" 
					+ " Class: " + scripts.getClass().getSimpleName() 
					+ " Script Key: " + script 
					+ " DAO Keys: " + Arrays.stream(script.getKeys())
						.filter( k -> k!= null)
						.map(k -> k.toString())
						.reduce(String::concat)
					);
				throw e;
			}
		}
	}
}

