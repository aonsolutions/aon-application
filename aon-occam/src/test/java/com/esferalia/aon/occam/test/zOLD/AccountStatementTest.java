package com.esferalia.aon.occam.test.zOLD;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.junit.Ignore;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountStatementTest {
	
	private static String DOMAIN_NAME = "macayc-mac.ecastellano.dev";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";
	
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat DEC = new DecimalFormat("#,##0.00");

	// ACCOUNT ENTRY
	// @Test
	public void testStatement() {
		AccountingReportParams params = new AccountingReportParams()
				.setAccount(new Account().setId(604530))
				.setFromDate( AonDateUtils.getDate(2015, 0, 1))
				.setToDate( AonDateUtils.getDate(2015, 10, 25)); 
		AccountStatementReport report = ACCOUNTING.getAccountStatement(DOMAIN_NAME, DOMAIN_ID, USER, params);
		
		System.out.println( AonStringUtils.repeat('=', 145));
		System.out.println( AonStringUtils.center(report.getAccount().getFullName(), 145));
		System.out.println( AonStringUtils.repeat('=', 145));
		System.out.println();
		report.getSummary()
		.stream()
		.forEach( st -> {
			System.out.println( AonStringUtils.repeat(' ', 10)
				+ AonStringUtils.rightPad(st.getConcept(), 43)
				+ AonStringUtils.leftPad(AonMathUtils.isZero(st.getDebit())
						?AonStringUtils.SPACE
						:DEC.format(st.getDebit()),17)		
				+AonStringUtils.leftPad(AonMathUtils.isZero(st.getCredit())
						?AonStringUtils.SPACE
						:DEC.format(st.getCredit()),17)
				+AonStringUtils.leftPad(AonMathUtils.isZero(st.getDebitBalance())
						?AonStringUtils.SPACE
						:DEC.format(st.getDebitBalance()),17)		
				+AonStringUtils.leftPad(AonMathUtils.isZero(st.getUnpaidBalance())
						?AonStringUtils.SPACE
						:DEC.format(st.getUnpaidBalance()),17)
				+AonStringUtils.repeat(AonStringUtils.SPACE, 10)
				);
		});
		System.out.println();
		System.out.println( " ASIENTO    FECHA   CONCEPTO                                      DEBE            HABER     SALDO DEUDOR    SALDO ACREDOR CONTRAPR. NUM.DOCUMENTO");
		System.out.println( AonStringUtils.repeat('-', 145));
		report.getDetails()
		.stream()
		.forEach( as -> {
			System.out.println(
			 AonStringUtils.leftPad(AonNumberUtils.toString(as.getAccountEntry()), 8)
			+AonStringUtils.center(FMT.format(as.getEntryDate()),12)
			+AonStringUtils.rightPad(AonStringUtils.abbreviate(as.getConcept(),32), 33)
			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebit())?AonStringUtils.SPACE:DEC.format(as.getDebit()),17)		
			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getCredit())?AonStringUtils.SPACE:DEC.format(as.getCredit()),17)
			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebitBalance())?AonStringUtils.SPACE:DEC.format(as.getDebitBalance()),17)		
			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getUnpaidBalance())?AonStringUtils.SPACE:DEC.format(as.getUnpaidBalance()),17)
			+AonStringUtils.center(AonStringUtils.defaultString(as.getBalancingAccountCode()),11)
			+AonStringUtils.rightPad(AonStringUtils.defaultString(as.getDocumentNumber()), 33)
					);
		});
	}
	
	// ACCOUNT ENTRY
	// @Test
	@Ignore
	public void testBalance() {
		AccountingReportParams params = new AccountingReportParams()
				.setAccount(new Account().setId(603888))
				.setFromDate( AonDateUtils.getDate(2015, 0, 1))
				.setToDate( AonDateUtils.getDate(2015, 5, 31)); 
		ACCOUNTING.getAccountBalance(DOMAIN_NAME, DOMAIN_ID, USER, params )
		.forEach( st -> {
		System.out.println(
			 AonStringUtils.repeat(AonStringUtils.SPACE, 8)
			+AonStringUtils.repeat(AonStringUtils.SPACE, 12)
			+AonStringUtils.rightPad(AonStringUtils.abbreviate(st.getConcept(),32), 33)
			+AonStringUtils.leftPad(DEC.format(st.getDebit()),17)		
			+AonStringUtils.leftPad(DEC.format(st.getCredit()),17)
			+AonStringUtils.repeat(AonStringUtils.SPACE, 11)
			+AonStringUtils.repeat(AonStringUtils.SPACE, 33)
				);
		});
	}
	
}
