package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;

public class Mod111InsertQuarterlyTest extends AbstractOccamTest {
	
	@Test
	public void mod111InsertQuarterlyTest() {
		
		AON.insertInvoice(getOccam(), 
			InvoiceFaker.getExpensesProfRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(), 
			InvoiceFaker.getExpensesRentingRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(), 
			InvoiceFaker.getExpensesCapitalRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(), 
			InvoiceFaker.getExpensesTransportRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(), 
			InvoiceFaker.getPurchaseFarmerRetention(ctx,getConfiguration()));
		
		test();	
	}
	
	private void test() {
		Mod111 commonTerritory = insertModel( Administration.COMMON_TERRITORY);
		Mod111 araba = insertModel( Administration.ALAVA);
		Mod111 bizkaia = insertModel( Administration.BIZKAIA);
		Mod111 gipuzkoa = insertModel( Administration.GIPUZKOA);
		Mod111 navarra = insertModel( Administration.NAVARRA);
		
		Asserts.assertEqualsDouble("Mod111 Trim (Araba). Resultado no coincide.", commonTerritory.getDeclarationResult(), araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Trim (Bizkaia). Resultado no coincide.", commonTerritory.getDeclarationResult(), bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Trim (Gipuzkoa). Resultado no coincide.", commonTerritory.getDeclarationResult(), gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Trim (Navarra). Resultado no coincide.", commonTerritory.getDeclarationResult(), navarra.getDeclarationResult());
		
	}

	private Mod111 insertModel( Administration admon) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(new Date())
			.setMonthly(false)
			.setAdministration(admon);
		Mod111 mod111 = FiscalFaker.createMod111(params);
		MODEL111.save(getOccam(), mod111);
		Mod111 actual = MODEL111.get(getOccam(), mod111.getId());  
		Asserts.assertMod111(mod111, actual);
		return actual;
	}
}
