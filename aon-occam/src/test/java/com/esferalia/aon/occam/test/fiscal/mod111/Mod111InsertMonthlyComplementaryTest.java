package com.esferalia.aon.occam.test.fiscal.mod111;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod111InsertMonthlyComplementaryTest extends AbstractOccamTest {
	
	@Test
	public void mod111InsertMonthlyComplementaryTest() {
		List<Invoice> invoices = new LinkedList<>();
		invoices.add(AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesProfRetention(ctx,getConfiguration())));
		invoices.add(AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesRentingRetention(ctx,getConfiguration())));
		invoices.add(AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesCapitalRetention(ctx,getConfiguration())));
		invoices.add(AON.insertInvoice(getOccam(),InvoiceFaker.getExpensesTransportRetention(ctx,getConfiguration())));
		invoices.add(AON.insertInvoice(getOccam(),InvoiceFaker.getPurchaseFarmerRetention(ctx,getConfiguration())));
		Double quota = getWithHoldingQuota(invoices);
		test(quota);	
	}
	
	private Double getWithHoldingQuota(List<Invoice> invoices) {
		double quota = 0;
		if (invoices != null && !invoices.isEmpty()) {
			for (Invoice inv : invoices) {
				if (inv != null && inv.getDetails() != null && !inv.getDetails().isEmpty()) {
					for (InvoiceDetail detail : inv.getDetails()) {
						if (detail.getInvoiceTaxes() != null && !detail.getInvoiceTaxes().isEmpty()) {
							for (InvoiceTax tax : detail.getInvoiceTaxes()) {
								if (tax.getTaxType() == TaxType.RETENTION && 
									tax.getWithholdingType() == WithholdingType.PROFESSIONAL
									|| tax.getWithholdingType() == WithholdingType.FARMER
									|| tax.getWithholdingType() == WithholdingType.TRANSPORT_OPERATOR) {
									
									quota = AonMathUtils.round(quota + tax.getDeductibleQuota());
								}
							}
						};
					}
				}
			}
		}
		return quota;
	}

	private void test(Double quota) {
		Mod111 commonTerritory = insertModel( Administration.COMMON_TERRITORY);
		Mod111 araba = insertModel( Administration.ALAVA);
		Mod111 bizkaia = insertModel( Administration.BIZKAIA);
		Mod111 gipuzkoa = insertModel( Administration.GIPUZKOA);
		Mod111 navarra = insertModel( Administration.NAVARRA);
		
		Asserts.assertEqualsDouble("Mod111 Mensual (AEAT). Resultado y facturas no coincide.", quota, commonTerritory.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Mensual (Araba). Resultado y facturas no coincide.", quota, araba.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Mensual (Bizkaia). Resultado y facturas no coincide.", quota, bizkaia.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Mensual (Gipuzkoa). Resultado y facturas no coincide.", quota, gipuzkoa.getDeclarationResult());
		Asserts.assertEqualsDouble("Mod111 Mensual (Navarra). Resultado y facturas no coincide.", quota, navarra.getDeclarationResult());
	}

	private Mod111 insertModel( Administration admon) {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
			.setIssueDate(new Date())
			.setMonthly(true)
			.setAdministration(admon)
			.setComplementary(true)
			;
		Mod111 mod111 = FiscalFaker.createMod111(params);
		MODEL111.save(getOccam(), mod111);
		Mod111 actual = MODEL111.get(getOccam(), mod111.getId());  
		Asserts.assertMod111(mod111, actual);
		return actual;
	}
}
