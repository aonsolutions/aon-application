package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;


public class AccountingInvoiceTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static Integer DOMAIN_ID = 5;
	private static String LOGIN = "jgarcia";
	
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	@Test
	public void testInvoices() throws IOException {
		Integer accountEntry = 2282565;
		AccountingInvoice ai = AccountingInvoiceDAO.getAccountingInvoice(ctx,accountEntry);
		AccountingRegistry ar = ai.getRegistry();
		Invoice invoice = ai.getInvoice();
		
		System.out.println("id ... " + invoice.getId());
		System.out.println("domain ... " + invoice.getDomain());
		System.out.println("activity ... " + invoice.getActivity());
		System.out.println("epigraph ... " + invoice.getEpigraph());
		System.out.println("investAsset ... " + invoice.getInvestAsset());
		System.out.println("project ... " + invoice.getProject());
		System.out.println("series ... " + invoice.getSeries());
		System.out.println("number ... " + invoice.getNumber());
		System.out.println("referenceCode ... " + invoice.getReferenceCode());
		System.out.println("issueDate ... " + invoice.getIssueDate());
		System.out.println("taxDate ... " + invoice.getTaxDate());
		System.out.println("rectificationType ... " + invoice.getRectificationType());
		System.out.println("securityLevel ... " + invoice.getSecurityLevel());
		System.out.println("rectificationInvoice ... " + invoice.getRectificationInvoice());
		System.out.println("registry ... " + invoice.getRegistry());
		System.out.println("registryDocument ... " + invoice.getRegistryDocument());
		System.out.println("registryDocumentType ... " + invoice.getRegistryDocumentType());
		System.out.println("registryDocumentCountry ... " + invoice.getRegistryDocumentCountry());
		System.out.println("registryName ... " + invoice.getRegistryName());
		System.out.println("registryAddress ... " + invoice.getRegistryAddress());
		System.out.println("addressStreetType ... " + invoice.getAddressStreetType());
		System.out.println("address ... " + invoice.getAddress());
		System.out.println("addressNumber ... " + invoice.getAddressNumber());
		System.out.println("addressTown ... " + invoice.getAddressTown());
		System.out.println("addressZIP ... " + invoice.getAddressZIP());
		System.out.println("addressGeozone ... " + invoice.getAddressGeozone());
		System.out.println("addressProvinceCode ... " + invoice.getAddressProvinceCode());
		System.out.println("addressProvince ... " +  invoice.getAddressProvince());
		System.out.println("scope ... " + invoice.getScope());
		System.out.println("type ... " + invoice.getType());
		System.out.println("transaction ... " + invoice.getTransaction());
		System.out.println("recorded ... " + invoice.isRecorded());
		System.out.println("surcharge ... " + invoice.isSurcharge());
		System.out.println("withholding ... " + invoice.isWithholding());
		System.out.println("withholdingFarmer ... " + invoice.isWithholdingFarmer());
		System.out.println("vatAccrualPayment ... " + invoice.isVatAccrualPayment());
		System.out.println("investment ... " + invoice.isInvestment());
		System.out.println("service ... " + invoice.isService());
		System.out.println("advance ... " + invoice.isAdvance());
		System.out.println("taxableBase ... " + invoice.getTaxableBase());
		System.out.println("vatQuota ... " + invoice.getVatQuota());
		System.out.println("retentionQuota ... " + invoice.getRetentionQuota());
		System.out.println("total ... " + invoice.getTotal());
		
		System.out.println("...............................................");
		
		System.out.println("ID ...: " + ar.getId());
		System.out.println("accountId ...: " + ar.getAccountId());
		System.out.println("accountCode ...: " + ar.getAccountCode());
		System.out.println("accountDescription ...: " + ar.getAccountDescription());
		System.out.println("alias ...: " + ar.getAlias());
		System.out.println("document ...: " + ar.getDocument());
		System.out.println("documentCountry ...: " + ar.getDocumentCountry());
		System.out.println("documentType ...: " + ar.getDocumentType());
		System.out.println("name ...: " + ar.getName());
		System.out.println("type ...: " + ar.getType());
		System.out.println("scope ...: " + ar.getScope());
		System.out.println("domain ...: " + ar.getDomain());
		System.out.println("surcharge ...: " + ar.isSurcharge());
		System.out.println("withholding ...: " + ar.isWithholding());
		System.out.println("withholdingFarmer ...: " + ar.isWithholdingFarmer());
		System.out.println("vatAccrualPayment ...: " + ar.isVatAccrualPayment());
		System.out.println("transaction ...: " + ar.getTransaction());
		
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
