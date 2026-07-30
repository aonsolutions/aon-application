package com.esferalia.aon.occam.test.fiscal.mod123;

import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import static com.esferalia.aon.occam.test.OccamAssertions.*;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.mod123.Model123ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123.Mod123Declaration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod123ScriptTest extends AbstractOccamTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.COMMON_TERRITORY);
		Mod123 mod123 = FiscalFaker.getMod123(params);
		MODEL123.calculate(getOccam(), mod123);
		test( mod123, Model123ScriptProvider.obtainScript(mod123));
	}

	@Test
	public void testAraba() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.ALAVA);
		Mod123 mod123 = FiscalFaker.getMod123(params);
		MODEL123.calculate(getOccam(), mod123);
		test( mod123, Model123ScriptProvider.obtainScript(mod123));
	}

	@Test
	public void testBizkaia() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.BIZKAIA);
		Mod123 mod123 = FiscalFaker.getMod123(params);
		MODEL123.calculate(getOccam(), mod123);
		test( mod123, Model123ScriptProvider.obtainScript(mod123));
	}

	@Test
	public void testGipuzkoa() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.GIPUZKOA);
		Mod123 mod123 = FiscalFaker.getMod123(params);
		MODEL123.calculate(getOccam(), mod123);
		test( mod123, Model123ScriptProvider.obtainScript(mod123));
	}

	@Test
	public void testNavarra() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.NAVARRA);
		Mod123 mod123 = FiscalFaker.getMod123(params);
		MODEL123.calculate(getOccam(), mod123);
		test( mod123, Model123ScriptProvider.obtainScript(mod123));
	}

	private void test( Mod123 mod123, IModelScript<Mod123Key>[] scripts) {
		for (IModelScript<Mod123Key> script : scripts) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							for (Mod123Key key : script.getKeys() ) {
								String info = MODEL123.getInfo(getOccam(), mod123, script, infoKey);
								infoKey.visit( new IFiscalModelKeyInfoVisitor<String>(){
									
									private String arrayNotNull() {
										JSONArray array = new JSONArray(info);
										assertNotNull(array);
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
										Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
										if (key != null && Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
											assertNotNull(info);
											assertNotEquals(info, "");
										} else if (key != null) {
											JSONObject json = new JSONObject(info);
											assertNotNull(json);
											JSONArray array = json.getJSONArray("messages");
											assertNotNull(array);
										}
										return null;
									}
									
									@Override 
									public String visitNone() { 
										assertEquals(AonStringUtils.EMPTY,info);
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

