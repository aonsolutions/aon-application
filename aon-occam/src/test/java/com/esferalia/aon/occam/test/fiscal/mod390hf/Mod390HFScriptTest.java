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
import com.esferalia.aon.occam.api.model.fiscal.mod390hf.Model390ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFScriptTest extends AbstractOccamTest {
	
	@Test
	public void testAraba() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.ALAVA);
		Mod390HF mod390HF = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod390HF);
		test( mod390HF, Model390ScriptProvider.obtainScript(mod390HF));
	}

	@Test
	public void testBizkaia() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.BIZKAIA);
		Mod390HF mod390HF = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod390HF);
		test( mod390HF, Model390ScriptProvider.obtainScript(mod390HF));
	}

	@Test
	public void testGipuzkoa() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.GIPUZKOA);
		Mod390HF mod390HF = FiscalFaker.getMod390HF(params);
		MODEL390HF.calculate(getOccam(), mod390HF);
		test( mod390HF, Model390ScriptProvider.obtainScript(mod390HF));
	}

	private void test( Mod390HF mod390HF, IModelScript<Mod390Key>[] scripts) {
		for (IModelScript<Mod390Key> script : scripts) {
			try {
				for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
					String info = MODEL390HF.getInfo(getOccam(), mod390HF, script, infoKey);
					infoKey.visit( new IFiscalModelKeyInfoVisitor<String>(){
						
						private String arrayNotNull() {
							JSONArray array = new JSONArray(info);
							Assert.assertNotNull(array);
							return null;
						}
						
						@Override public String visitCompute() { return arrayNotNull(); }
						@Override public String visitModelInvoiceVatBreakdown() { return arrayNotNull(); }
						@Override public String visitModelInvoiceIrpfBreakdown() {return arrayNotNull(); }
						@Override public String visitModelSalaryIrpfBreakdown() {return arrayNotNull(); }
						@Override public String visitModelOutVatAccrualInvoice() {return arrayNotNull(); }
						@Override public String visitModelInVatAccrualInvoice() {return arrayNotNull(); }
						
						@Override 
						public String visitComputeKey() {
							JSONObject json = new JSONObject(info);
							Assert.assertNotNull(json);
							JSONArray array = json.getJSONArray("messages");
							Assert.assertNotNull(array);
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
						@Override public String visitProrratedModelInvoiceVatBreakdown() {return null;}
					});
				}
			} catch (Exception e) {
				System.out.println( " \t [ERROR]" 
					+ " Class: " + scripts.getClass().getSimpleName() 
					+ " Script Key: " + script 
					+ " DAO Keys: " +
						((script.getKeys() == null)
							?" NO KEYS!"
							:Arrays.stream(script.getKeys())
							.filter( k -> k!= null)
							.map(k -> k.toString())
							.reduce(String::concat))
					);
				throw e;
			}
		}
	}
}

