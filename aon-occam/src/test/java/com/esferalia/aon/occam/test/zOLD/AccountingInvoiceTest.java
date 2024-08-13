package com.esferalia.aon.occam.test.zOLD;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.mysql.jdbc.Driver;

import net.aonsolutions.core.pool.AonConnectionException;


public class AccountingInvoiceTest {

	private static CloseableAONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static Integer DOMAIN_ID = 5;
	private static String LOGIN = "jgarcia";
	
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	// @Test
	public void test1() throws IOException {
		Integer registryId = 609337;
		AccountingInvoice ai = AccountingInvoiceDAO.initializeInvoice(ctx, InvoiceType.EXPENSES, registryId, null, new Date());
		Invoice inv = ai.getInvoice();
		for ( int i = 0 ; i < 100000 ; i++ ) {
			double d = AonNumberUtils.todouble( AonRandomStringUtils.randomNumeric(7));
			d = AonMathUtils.round(d / 100);
			InvoiceCalculator.reverseCalculate(inv, d);
			double total = AonMathUtils.round(ai.getInvoice().getTaxableBase() + ai.getInvoice().getVatQuota() -  ai.getInvoice().getRetentionQuota());
			boolean equals = AonNumberUtils.equals(ai.getTotalInvoice(), total);
			if (!equals) {
				System.out.println(i + ".- " + "("+d+") "+ai.getTotalInvoice() +" == "+ total + " --> " + equals);
			}
		}
	}
	
//	private void print( AccountingInvoice ai ) throws IOException {	
//		AccountingRegistry ar = ai.getRegistry();
//		Invoice invoice = ai.getInvoice();
//		InvoiceWithholding withholding = ai.getWithholdingData(); 
//		
//		System.out.println();
//		System.out.println("*****************************************************");
//		System.out.println("*****************************************************");
//		System.out.println("id ... " + invoice.getId());
//		System.out.println("domain ... " + invoice.getDomain());
//		System.out.println("activity ... " + invoice.getActivity().getId());
//		System.out.println("epigraph ... " + invoice.getEpigraph());
//		System.out.println("investAsset ... " + invoice.getInvestAsset());
//		System.out.println("project ... " + invoice.getProject());
//		System.out.println("series ... " + invoice.getSeries());
//		System.out.println("number ... " + invoice.getNumber());
//		System.out.println("referenceCode ... " + invoice.getReferenceCode());
//		System.out.println("issueDate ... " + invoice.getIssueDate());
//		System.out.println("taxDate ... " + invoice.getTaxDate());
//		System.out.println("rectificationType ... " + invoice.getRectificationType());
//		System.out.println("securityLevel ... " + invoice.getSecurityLevel());
//		System.out.println("rectificationInvoice ... " + invoice.getRectificationInvoice());
//		System.out.println("registry ... " + invoice.getRegistry());
//		System.out.println("registryDocument ... " + invoice.getRegistryDocument());
//		System.out.println("registryDocumentType ... " + invoice.getRegistryDocumentType());
//		System.out.println("registryDocumentCountry ... " + invoice.getRegistryDocumentCountry());
//		System.out.println("registryName ... " + invoice.getRegistryName());
//		System.out.println("registryAddress ... " + invoice.getRegistryAddress());
//		System.out.println("addressStreetType ... " + invoice.getAddress().getStreetType());
//		System.out.println("address ... " + invoice.getAddress().getAddress());
//		System.out.println("addressNumber ... " + invoice.getAddress().getNumber());
//		System.out.println("addressTown ... " + invoice.getAddress().getCity());
//		System.out.println("addressZIP ... " + invoice.getAddress().getZip());
//		System.out.println("addressGeozone ... " + invoice.getAddress().getGeozone());
//		System.out.println("addressProvinceCode ... " + invoice.getAddress().getGeozoneCode());
//		System.out.println("addressProvince ... " +  invoice.getAddress().getProvince());
//		System.out.println("scope ... " + invoice.getScope());
//		System.out.println("type ... " + invoice.getType());
//		System.out.println("transaction ... " + invoice.getTransaction());
//		System.out.println("recorded ... " + invoice.isRecorded());
//		System.out.println("surcharge ... " + invoice.isSurcharge());
//		System.out.println("withholding ... " + invoice.isWithholding());
//		System.out.println("withholdingFarmer ... " + invoice.isWithholdingFarmer());
//		System.out.println("vatAccrualPayment ... " + invoice.isVatAccrualPayment());
//		System.out.println("investment ... " + invoice.isInvestment());
//		System.out.println("service ... " + invoice.isService());
//		System.out.println("advance ... " + invoice.isAdvance());
//		System.out.println("taxableBase ... " + invoice.getTaxableBase());
//		System.out.println("vatQuota ... " + invoice.getVatQuota());
//		System.out.println("retentionQuota ... " + invoice.getRetentionQuota());
//		System.out.println("total ... " + invoice.getTotal());
//		
//		System.out.println("...............................................");
//		
//		System.out.println("ID ...: " + ar.getId());
//		System.out.println("accountId ...: " + ar.getAccountId());
//		System.out.println("accountCode ...: " + ar.getAccountCode());
//		System.out.println("accountDescription ...: " + ar.getAccountDescription());
//		System.out.println("alias ...: " + ar.getAlias());
//		System.out.println("document ...: " + ar.getDocument());
//		System.out.println("documentCountry ...: " + ar.getDocumentCountry());
//		System.out.println("documentType ...: " + ar.getDocumentType());
//		System.out.println("name ...: " + ar.getName());
//		System.out.println("type ...: " + ar.getType());
//		System.out.println("scope ...: " + ar.getScope());
//		System.out.println("domain ...: " + ar.getDomain());
//		System.out.println("surcharge ...: " + ar.isSurcharge());
//		System.out.println("withholding ...: " + ar.isWithholding());
//		System.out.println("withholdingFarmer ...: " + ar.isWithholdingFarmer());
//		System.out.println("vatAccrualPayment ...: " + ar.isVatAccrualPayment());
//		System.out.println("transaction ...: " + ar.getTransaction());
//		
//		System.out.println("...............................................");
//		System.out.println("...............................................");
//
//		for (InvoiceVAT vat : ai.getVats()) {
//			System.out.println("vatDeductionType ...: " + vat.getVatDeductionType());
//			System.out.println("base ...: " + vat.getBase());
//			System.out.println("percentage ...: " + vat.getPercentage());
//			System.out.println("quota ...: " + vat.getQuota());
//			System.out.println("surcharge ...: " + vat.getSurcharge());
//			System.out.println("surchargeQuota ...: " + vat.getSurchargeQuota());
//			System.out.println("investAsset ...: " + vat.getInvoiceDetail().getInvestAsset());
//			System.out.println("deductiblePercent ...: " + vat.getDeductiblePercent());
//			System.out.println("deductibleQuota ...: " + vat.getDeductibleQuota());
//			System.out.println("withholding ...: " + vat.isWithholding());
//			if (vat.getOutputAccount() == null) {
//				System.out.println("outputAccount ...: NULL " );
//			} else {
//				System.out.println("outputAccount - Id ...: " + vat.getOutputAccount().getId());
//				System.out.println("outputAccount - Code ...: " + vat.getOutputAccount().getCode());
//				System.out.println("outputAccount - Description ...: " + vat.getOutputAccount().getDescription());
//			}
//
//			if (vat.getInputAccount() == null) {
//				System.out.println("inputAccount ...: NULL " );
//			} else {
//				System.out.println("inputAccount - Id ...: " + vat.getInputAccount().getId());
//				System.out.println("inputAccount - Code ...: " + vat.getInputAccount().getCode());
//				System.out.println("inputAccount - Description ...: " + vat.getInputAccount().getDescription());
//			}
//
//			if (vat.getAdjAccount() == null) {
//				System.out.println("adjAccount ...: NULL " );
//			} else {
//				System.out.println("adjAccount - Id ...: " + vat.getAdjAccount().getId());
//				System.out.println("adjAccount - Code ...: " + vat.getAdjAccount().getCode());
//				System.out.println("adjAccount - Description ...: " + vat.getAdjAccount().getDescription());
//			}
//
//			if (vat.getAdjDirectTaxAccount() == null) {
//				System.out.println("adjDirectTaxAccount ...: NULL " );
//			} else {
//				System.out.println("adjDirectTaxAccount - Id ...: " + vat.getAdjDirectTaxAccount().getId());
//				System.out.println("adjDirectTaxAccount - Code ...: " + vat.getAdjDirectTaxAccount().getCode());
//				System.out.println("adjDirectTaxAccount - Description ...: " + vat.getAdjDirectTaxAccount().getDescription());
//			}
//
//			if (vat.getInvoiceDetail().getExpAccount() == null) {
//				System.out.println("expAccount ...: NULL " );
//			} else {
//				System.out.println("expAccount - Id ...: " + vat.getInvoiceDetail().getExpAccount().getId());
//				System.out.println("expAccount - Code ...: " + vat.getInvoiceDetail().getExpAccount().getCode());
//				System.out.println("expAccount - Description ...: " + vat.getInvoiceDetail().getExpAccount().getDescription());
//			}
//			
//		}
//		System.out.println("...............................................");
//		System.out.println("...............................................");
//		System.out.println("withholdingType ...: " + withholding.getWithholdingType());
//		System.out.println("base ...: " + withholding.getBase());
//		System.out.println("percentage ...: " + withholding.getPercentage());
//		System.out.println("quota ...: " + withholding.getQuota());
//		System.out.println("accountId ...: " + withholding.getAccountId());
//		System.out.println("accountCode ...: " + withholding.getAccountCode());
//		System.out.println("accountDescription ...: " + withholding.getAccountDescription());
//	}
		
	@AfterClass
	public static void afterClass() {
		ctx.close();
	}

	
}
