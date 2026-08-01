package com.esferalia.aon.occam.test.fiscal.mod421;

import java.util.Arrays;

import org.json.JSONArray;
import static com.esferalia.aon.occam.test.OccamAssertions.*;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCPrintT1Script;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCPrintT4Script;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCResultT1Script;
import com.esferalia.aon.occam.api.model.fiscal.mod421.Model4212026ATCResultT4Script;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod421ScriptTest extends Mod421AbstractTest {
	
	@Test
	public void testCanariasExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(getTestDate())
				.setMonthly(false)
				.setAdministration(Administration.CANARIAS);
		Mod421 mod421 = FiscalFaker.getMod421(params);
		MODEL421.calculate(getOccam(), mod421);
		test( mod421, Model4212026ATCResultT1Script.values() );
		test( mod421, Model4212026ATCResultScript.values() );
		test( mod421, Model4212026ATCPrintT1Script.values() );
		
		FiscalFakerParams paramsBis = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(AonDateUtils.getLastDayOfYear(getTestDate()))
				.setMonthly(false)
				.setAdministration(Administration.CANARIAS);
		Mod421 mod421bis = FiscalFaker.getMod421(paramsBis);
		MODEL421.calculate(getOccam(), mod421bis);
		test( mod421bis, Model4212026ATCResultT4Script.values() );
		test( mod421bis, Model4212026ATCResultScript.values() );
		test( mod421bis, Model4212026ATCPrintT4Script.values() );
	}
	
	private void test( Mod421 mod421, IModelScript<Mod421Key>[] scripts) {
		System.out.println(FiscalTestSuite.toString(mod421));
		for (IModelScript<Mod421Key> script : scripts) {
			try {
				if (script != null) {
					for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
						if (infoKey != null && script.getKeys() != null) {
							String info = MODEL421.getInfo(getOccam(), mod421, script, infoKey);
							infoKey.visit( new IFiscalModelKeyInfoVisitor<String>(){
								
								private String arrayNotNull() {
									JSONArray array = new JSONArray(info);
									assertNotNull(array);
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
									assertNotNull(info);
									assertNotEquals(info, "");
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
							});
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

