package com.esferalia.aon.occam.test.fiscal.mod202;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import static com.esferalia.aon.occam.test.OccamAssertions.*;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model202ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202ScriptTest extends AbstractOccamTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.COMMON_TERRITORY);
		Mod202 mod202 = FiscalFaker.getMod202(params);
		MODEL202.calculate(getOccam(), mod202);
		test(mod202, Model202ScriptProvider.obtainScript(mod202));		
	}

	private void test( Mod202 mod202, List<IModelScript<Mod202Key>> list) {
		for (IModelScript<Mod202Key> script : list) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							for (Mod202Key key : script.getKeys() ) {
								if (key != null) {
									String info = MODEL202.getInfo(getOccam(), mod202, script, infoKey);
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
				}
			} catch (Exception e) {
				System.out.println( " \t [ERROR]" 
					+ " Class: " + list.getClass().getSimpleName() 
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

