package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017BIZKAIAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017BIZKAIAScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017BIZKAIAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017GIPUZKOASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017PrintARABAScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017PrintBIZKAIAScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902017PrintGIPUZKOAScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902021ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902021GIPUZKOASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902021PrintGIPUZKOAScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022ARABAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022ARABARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022ARABAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022ARABAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIAScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIAScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022BIZKAIASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022GIPUZKOAAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022GIPUZKOARScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022GIPUZKOAResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022GIPUZKOASpecificOperationsScript;
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model3902022PrintBIZKAIAScript;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDeclaration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFScriptTest extends AbstractOccamTest {
	
	@Test
	public void testArabaExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.ALAVA);
		Mod390HF mod = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod);
		test( mod, Model3902017ARABAAdditionalDataScript.values() );
		test( mod, Model3902017ARABAResultScript.values() );
		test( mod, Model3902017ARABARScript1.values() );
		test( mod, Model3902017ARABAScript2.values() );
		test( mod, Model3902021ARABAAdditionalDataScript.values() );
		test( mod, Model3902022ARABAAdditionalDataScript.values() );
		test( mod, Model3902022ARABAResultScript.values() );
		test( mod, Model3902022ARABARScript1.values() );
		test( mod, Model3902022ARABAScript2.values() );
		test( mod, Model3902017PrintARABAScript.values() );
	}
	
	@Test
	public void testBizkaiaExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.BIZKAIA);
		Mod390HF mod = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod);
		test( mod, Model3902017BIZKAIAAdditionalDataScript.values() );
		test( mod, Model3902017BIZKAIAScript1.values() );
		test( mod, Model3902017BIZKAIAScript2.values() );
		test( mod, Model3902017BIZKAIASpecificOperationsScript.values() );
		test( mod, Model3902022BIZKAIAAdditionalDataScript.values() );
		test( mod, Model3902022BIZKAIAScript1.values() );
		test( mod, Model3902022BIZKAIAScript2.values() );
		test( mod, Model3902022BIZKAIASpecificOperationsScript.values() );
		test( mod, Model3902017PrintBIZKAIAScript.values() );
	}
	
	@Test
	public void testGipuzkoaExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.GIPUZKOA);
		Mod390HF mod = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod);
		test( mod, Model3902017GIPUZKOAAdditionalDataScript.values() );
		test( mod, Model3902017GIPUZKOAResultScript.values() );
		test( mod, Model3902017GIPUZKOARScript1.values() );
		test( mod, Model3902017GIPUZKOASpecificOperationsScript.values() );
		test( mod, Model3902021GIPUZKOASpecificOperationsScript.values() );
		test( mod, Model3902022GIPUZKOAAdditionalDataScript.values() );
		test( mod, Model3902022GIPUZKOAResultScript.values() );
		test( mod, Model3902022GIPUZKOARScript1.values() );
		test( mod, Model3902022GIPUZKOASpecificOperationsScript.values() );
		test( mod, Model3902022PrintBIZKAIAScript.values() ); 
		test( mod, Model3902017PrintGIPUZKOAScript.values() );
		test( mod, Model3902021PrintGIPUZKOAScript.values() );
	}

	private void test( Mod390HF mod, IModelScript<Mod390Key>[] scripts) {
		for (IModelScript<Mod390Key> script : scripts ) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							for (Mod390Key key : script.getKeys() ) {
								String info = MODEL390HF.getInfo(getOccam(), mod, script, infoKey);
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
										Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
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

