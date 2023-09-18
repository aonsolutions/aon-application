package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model111ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111.Mod111Declaration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod111ScriptTest extends AbstractOccamTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.COMMON_TERRITORY);
		Mod111 mod111 = FiscalFaker.getMod111(params);
		MODEL111.calculate(getOccam(), mod111);
		test( mod111, Model111ScriptProvider.obtainScript(mod111));
	}

	@Test
	public void testAraba() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.ALAVA);
		Mod111 mod111 = FiscalFaker.getMod111(params);
		MODEL111.calculate(getOccam(), mod111);
		test( mod111, Model111ScriptProvider.obtainScript(mod111));
	}

	@Test
	public void testBizkaia() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.BIZKAIA);
		Mod111 mod111 = FiscalFaker.getMod111(params);
		MODEL111.calculate(getOccam(), mod111);
		test( mod111, Model111ScriptProvider.obtainScript(mod111));
	}

	@Test
	public void testGipuzkoa() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.GIPUZKOA);
		Mod111 mod111 = FiscalFaker.getMod111(params);
		MODEL111.calculate(getOccam(), mod111);
		test( mod111, Model111ScriptProvider.obtainScript(mod111));
	}

	@Test
	public void testNavarra() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.NAVARRA);
		Mod111 mod111 = FiscalFaker.getMod111(params);
		MODEL111.calculate(getOccam(), mod111);
		test( mod111, Model111ScriptProvider.obtainScript(mod111));
	}

	private void test( Mod111 mod111, IModelScript<Mod111Key>[] scripts) {
		for (IModelScript<Mod111Key> script : scripts) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							for (Mod111Key key : script.getKeys() ) {
								String info = MODEL111.getInfo(getOccam(), mod111, script, infoKey);
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
										Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
										if (key != null && Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
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
									@Override public String visitProrratedModelInvoiceVatBreakdown() {return null;}
								});
							}
						}
					}
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

