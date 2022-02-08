package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;

public class Mod303InsertTest extends AbstractOccamTest {
	
	@Test
	public void test() {
		
		AON.insertInvoice(getOccam(),InvoiceFaker.getSalesNational(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getSalesCanCeuService(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getSalesCanCeu(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesNational(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseNational(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseExtracommunity(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseExtracommunityVatImport(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseCanCeu(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseCanCeuVatImport(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesProfRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesRentingRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesCapitalRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesTransportRetention(ctx,getConfiguration()));
		AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseFarmerRetention(ctx,getConfiguration()));
		
		testMonthly();	
		testQuarterly();
	}
	
	private void testMonthly() {
		Mod303 commonTerritory = insertModel( Administration.COMMON_TERRITORY, true);
		Mod303 araba = insertModel( Administration.ALAVA, true);
		Mod303 bizkaia = insertModel( Administration.BIZKAIA, true);
		Mod303 gipuzkoa = insertModel( Administration.GIPUZKOA, true);
//		Mod303 navarra = insertModel( Administration.NAVARRA, true);
		
		Asserts.assertEqualsDouble("Mod303 Mensual (Araba). Resultado no coincide.", commonTerritory.getResult(), araba.getResult());
		Asserts.assertEqualsDouble("Mod303 Mensual (Bizkaia). Resultado no coincide.", commonTerritory.getResult(), bizkaia.getResult());
		Asserts.assertEqualsDouble("Mod303 Mensual (Gipuzkoa). Resultado no coincide.", commonTerritory.getResult(), gipuzkoa.getResult());
//		Asserts.assertEqualsDouble("Mod303 Mensual (Navarra). Resultado no coincide.", commonTerritory.getResult(), navarra.getResult());
		
	}

	private void testQuarterly() {
		Mod303 commonTerritory = insertModel( Administration.COMMON_TERRITORY, false);
		Mod303 araba = insertModel( Administration.ALAVA, false);
		Mod303 bizkaia = insertModel( Administration.BIZKAIA, false);
		Mod303 gipuzkoa = insertModel( Administration.GIPUZKOA, false);
//		Mod303 navarra = insertModel( Administration.NAVARRA, false);

		Asserts.assertEqualsDouble("Mod303 Trimestral (Araba). Resultado no coincide.", commonTerritory.getResult(), araba.getResult());
		Asserts.assertEqualsDouble("Mod303 Trimestral (Bizkaia). Resultado no coincide.", commonTerritory.getResult(), bizkaia.getResult());
		Asserts.assertEqualsDouble("Mod303 Trimestral (Gipuzkoa). Resultado no coincide.", commonTerritory.getResult(), gipuzkoa.getResult());
//		Asserts.assertEqualsDouble("Mod303 Trimestral (Navarra). Resultado no coincide.", commonTerritory.getResult(), navarra.getResult());
	}

	private Mod303 insertModel( Administration admon, boolean monthly) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(new Date())
			.setMonthly(monthly)
			.setAdministration(admon);
		Mod303 mod303 = FiscalFaker.getMod303(params);
		MODEL303.save(getOccam(), mod303);
		Mod303 actual = MODEL303.get(getOccam(), mod303.getId());  
		Asserts.assertMod303(mod303, actual);
		return actual;
	}

	}
