package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class AccountingInvoiceCalculationTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static Integer DOMAIN_ID = 5;
	private static String LOGIN = "jgarcia";
	
	private int fail = 0;
	private int registries = 0;
	private int tries = 10000;
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( com.mysql.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	@Test
	public void test0() throws IOException {
		fail = 0;
		registries = 0;
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountCodeProperty().eq("430000001"))
			.forEach( reg -> doTest0(reg.getId(), reg.getType().getInvoiceType() ));
	}	
	private void doTest0(Integer registry, InvoiceType type)  {
		AccountingInvoice ai = AccountingInvoiceDAO.initializeInvoice(ctx, type, registry, null, new Date());
		InvoiceVAT vat = ai.getFirstVat();

		
		vat.setBase(31.75);
		vat.setPercentage(10.00);
		vat.setQuota(3.17);
		double gap = InvoiceCalculator.getQuotaGap(vat, 3.17);
		
		System.out.println( "vat : " + vat.getQuota());
		System.out.println( "gap  : " + gap );
		vat.setQuotaEdited(AonMathUtils.isNotZero(gap));
		InvoiceCalculator.calculate(ai, vat);
		System.out.println( "vat : " + vat.getQuota());
		
		
	}	

	//	@Test
	public void test1() throws IOException {
		fail = 0;
		registries = 0;
		
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountDescriptionProperty().like("%GAR%"))
			.forEach( reg -> doTest1(reg.getId(), reg.getType().getInvoiceType() ));
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountDescriptionProperty().like("%ALV%"))
			.forEach( reg -> doTest1(reg.getId(), reg.getType().getInvoiceType() ));
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountDescriptionProperty().like("%FER%"))
		.forEach( reg -> doTest1(reg.getId(), reg.getType().getInvoiceType() ));
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountDescriptionProperty().like("%ARR%"))
		.forEach( reg -> doTest1(reg.getId(), reg.getType().getInvoiceType() ));
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountDescriptionProperty().like("%CAN%"))
		.forEach( reg -> doTest1(reg.getId(), reg.getType().getInvoiceType() ));
		RegistryDAO.getAccountingRegistries(ctx, p -> p.getAccountDescriptionProperty().like("%ANG%"))
		.forEach( reg -> doTest1(reg.getId(), reg.getType().getInvoiceType() ));
		
//		System.out.println( " ------------------------ Start SALES" );
//		doTest1(15751,InvoiceType.SALES);
//		System.out.println( " ------------------------ Start EXPENSES" );
//		doTest1(609337,InvoiceType.EXPENSES);
//		System.out.println( " ------------------------ Start PURCHASE" );
//		doTest1(617369,InvoiceType.PURCHASE);
		
		System.out.println( );
		System.out.println( registries + " Registries diferentes");
		System.out.println( tries + " intentos en cada registry");
		System.out.println( fail + " fallos");
		System.out.println( (AonMathUtils.round(fail * 100d / (tries * registries) )) + "% de fallos");

	}
	
	private void doTest1(Integer registry, InvoiceType type)  {
		++registries;
		System.out.println( "registry : " + registry + " " + type.getDescription());
		AccountingInvoice ai = AccountingInvoiceDAO.initializeInvoice(ctx, type, registry, null, new Date());
		ai.setAccountEntry(new AccountEntry()
				.setDomain(DOMAIN_ID)
				.setConfidential(false)
				.setEntryDate(new Date()));
		
		for ( int i = 0 ; i < tries ; i++ ) {
			double d = AonNumberUtils.todouble( AonRandomStringUtils.randomNumeric(7));
			d = AonMathUtils.round(d / 100);
			InvoiceCalculator.reverseCalculate(ai, d);
			boolean equals = AonNumberUtils.equals(ai.getTotalInvoice(), d);
			if (!equals) {
				fail++;
				System.out.println("\t" + fail + " - ("+d+") "+ai.getTotalInvoice() + " --> " + equals);
			} else {
				AccountEntry[] entries = InvoiceRecorder.recordInvoice(ai);
				for (AccountEntry ae : entries) {
					double sumD = 0.0;
					double sumC = 0.0;
					for (AccountEntryDetail aed : ae.getDetails()) {
						if (!aed.isDeleted()) {
							sumD = AonMathUtils.sum(sumD, aed.getDebit());	
							sumC = AonMathUtils.sum(sumC, aed.getCredit());
						}
					}
					if (!AonMathUtils.isZero( AonMathUtils.round(sumD - sumC))) {
						fail++;
						System.out.println(fail + " - DESCUADRE ("+d+") "+ai.getTotalInvoice() +"  deb: "+ sumD + " hab: " + sumC );	
					}
				}
			}
		}
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

	
}
