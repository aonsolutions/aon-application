package com.esferalia.aon.occam.test.fiscal.mod130;

import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.mod130.Model130ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod130ScriptTest extends AbstractOccamTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.COMMON_TERRITORY);
		Mod130 mod130 = FiscalFaker.getMod130(params);
		MODEL130.calculate(getOccam(), mod130);
		test( mod130, Model130ScriptProvider.obtainScript(mod130));
	}

	@Test
	public void testBizkaia() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.BIZKAIA);
		Mod130 mod130 = FiscalFaker.getMod130(params);
		MODEL130.calculate(getOccam(), mod130);
		test( mod130, Model130ScriptProvider.obtainScript(mod130));
	}

	private void test( Mod130 mod130, IModelScript<Mod130Key>[] scripts) {
		for (IModelScript<Mod130Key> script : scripts) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							for (Mod130Key key : script.getKeys() ) {
								if (key != null) {
									String info = MODEL130.getInfo(getOccam(), mod130, script, infoKey);
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

