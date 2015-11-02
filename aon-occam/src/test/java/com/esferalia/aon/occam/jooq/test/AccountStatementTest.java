package com.esferalia.aon.occam.jooq.test;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountStatementTest {
	
	private static AONContext ctx;
	private static String DOMAIN_NAME = "macayc-mac.ecastellano.dev";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";
	
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat DEC = new DecimalFormat("#,##0.00");

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException,
			SQLException, AonConnectionException {
		Class.forName(org.gjt.mm.mysql.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
	}

	// ACCOUNT ENTRY
	@Test
	public void testStatement() {
		Integer account = 603888;
		Date from = AonDateUtils.getDate(2015, 0, 1); 
		Date to = AonDateUtils.getDate(2015, 8, 10); 
		AccountStatementReport report = AON.getAccountStatement(DOMAIN_NAME, DOMAIN_ID, USER, account, from, to);
		
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
	@Test
	@Ignore
	public void testBalance() {
		Integer account = 603888;
		Date from = AonDateUtils.getDate(2015, 0, 1); 
		Date to = AonDateUtils.getDate(2015, 5, 31); 
		AON.getAccountBalance(DOMAIN_NAME, DOMAIN_ID, USER, account, from, to)
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
