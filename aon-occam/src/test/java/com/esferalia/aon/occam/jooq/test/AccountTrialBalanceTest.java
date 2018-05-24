package com.esferalia.aon.occam.jooq.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceParams;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountTrialBalanceTest {
	
//	private static String DOMAIN_NAME = "macayc-mac.ecastellano.euk";
//	private static int DOMAIN_ID = 536;
//	private static String USER = "mac";
	
	private static String DOMAIN_NAME = "udapa.ecastellano.euk";
	private static int DOMAIN_ID = 3049;
	private static String USER = "montse";

	//	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat DEC = new DecimalFormat("#,##0.00");

	// ACCOUNT ENTRY
	@Test
	public void testStatement() throws FileNotFoundException {
		AccountTrialBalanceParams params = new AccountTrialBalanceParams()
//				.setPeriod(32705) // 2016	MACAYC
//				.setPeriod(53762)  // 2018	MACAYC
				.setPeriod(49062)  // 2017	UDAPA
				.setCode("572")
				.setFromDate( AonDateUtils.getDate(2018, 2, 1))
				.setToDate( AonDateUtils.getDate(2018, 6, 31))
				.setLowLevelAccountVisible(true)
			;
//		PrintStream out = new PrintStream("/home/ecastellano/balance.txt");
//		PrintStream out = System.out;
	
		AONContext ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
		Date start = new Date();
		AccountTrialBalanceReport report = AccountStatementDAO.trialBalance(ctx, params);
		Date end = new Date();
		System.out.println((end.getTime() - start.getTime()) + "ms.");
		
//		out.print( AonStringUtils.rightPad("CUENTA", 10 ));
//		out.print( "|");
//		out.print( AonStringUtils.center("SALDOS ANTERIORES",35));
//		out.print( "|");
//		out.print( AonStringUtils.center("SALDO APERTURA",35));
//		out.print( "|");
//		out.print( AonStringUtils.center("SALDO ANTERIOR EN EJERCICIO",35));
//		out.print( "|");
//		out.print( AonStringUtils.center("SUMAS PERIODO",35));
//		out.print( "|");
//		out.print( AonStringUtils.center("SALDOS FINALES",35));
//		out.print( "|");
//		out.println();
//		out.print( AonStringUtils.repeat("-", 10 ));
//		out.print( "|");
//		out.print( AonStringUtils.repeat("-",35));
//		out.print( "|");
//		out.print( AonStringUtils.repeat("-",35));
//		out.print( "|");
//		out.print( AonStringUtils.repeat("-",35));
//		out.print( "|");
//		out.print( AonStringUtils.repeat("-",35));
//		out.print( "|");
//		out.print( AonStringUtils.repeat("-",35));
//		out.print( "|");
//		out.println();
//		for (AccountTrialBalance bal : report.getBalances().values()) {
//			out.print( AonStringUtils.rightPad(bal.getCode(), 10 ));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getBeforePeriodDebitBalance())?AonStringUtils.SPACE:DEC.format(bal.getBeforePeriodDebitBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getBeforePeriodUnpaidBalance())?AonStringUtils.SPACE:DEC.format(bal.getBeforePeriodUnpaidBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getInPeriodOpeningDebitBalance())?AonStringUtils.SPACE:DEC.format(bal.getInPeriodOpeningDebitBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getInPeriodOpeningUnpaidBalance())?AonStringUtils.SPACE:DEC.format(bal.getInPeriodOpeningUnpaidBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getInPeriodBeforeDebitBalance())?AonStringUtils.SPACE:DEC.format(bal.getInPeriodBeforeDebitBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getInPeriodBeforeUnpaidBalance())?AonStringUtils.SPACE:DEC.format(bal.getInPeriodBeforeUnpaidBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getInPeriodDebit())?AonStringUtils.SPACE:DEC.format(bal.getInPeriodDebit()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getInPeriodCredit())?AonStringUtils.SPACE:DEC.format(bal.getInPeriodCredit()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getAfterPeriodDebitBalance())?AonStringUtils.SPACE:DEC.format(bal.getAfterPeriodDebitBalance()),17));
//			out.print( "|");
//			out.print( AonStringUtils.leftPad(AonMathUtils.isZero(bal.getAfterPeriodUnpaidBalance())?AonStringUtils.SPACE:DEC.format(bal.getAfterPeriodUnpaidBalance()),17));
//			out.print( "|");
//			out.println();
//		}
//		out.flush();
//		out.close();
//		System.out.println( AonStringUtils.repeat('=', 145));
//		System.out.println( AonStringUtils.center(report.getAccount().getFullName(), 145));
//		System.out.println( AonStringUtils.repeat('=', 145));
//		System.out.println();
//		report.getSummary()
//		.stream()
//		.forEach( st -> {
//			System.out.println( AonStringUtils.repeat(' ', 10)
//				+ AonStringUtils.rightPad(st.getConcept(), 43)
//				+ AonStringUtils.leftPad(AonMathUtils.isZero(st.getDebit())
//						?AonStringUtils.SPACE
//						:DEC.format(st.getDebit()),17)		
//				+AonStringUtils.leftPad(AonMathUtils.isZero(st.getCredit())
//						?AonStringUtils.SPACE
//						:DEC.format(st.getCredit()),17)
//				+AonStringUtils.leftPad(AonMathUtils.isZero(st.getDebitBalance())
//						?AonStringUtils.SPACE
//						:DEC.format(st.getDebitBalance()),17)		
//				+AonStringUtils.leftPad(AonMathUtils.isZero(st.getUnpaidBalance())
//						?AonStringUtils.SPACE
//						:DEC.format(st.getUnpaidBalance()),17)
//				+AonStringUtils.repeat(AonStringUtils.SPACE, 10)
//				);
//		});
//		System.out.println();
//		System.out.println( " ASIENTO    FECHA   CONCEPTO                                      DEBE            HABER     SALDO DEUDOR    SALDO ACREDOR CONTRAPR. NUM.DOCUMENTO");
//		System.out.println( AonStringUtils.repeat('-', 145));
//		report.getDetails()
//		.stream()
//		.forEach( as -> {
//			System.out.println(
//			 AonStringUtils.leftPad(AonNumberUtils.toString(as.getAccountEntry()), 8)
//			+AonStringUtils.center(FMT.format(as.getEntryDate()),12)
//			+AonStringUtils.rightPad(AonStringUtils.abbreviate(as.getConcept(),32), 33)
//			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebit())?AonStringUtils.SPACE:DEC.format(as.getDebit()),17)		
//			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getCredit())?AonStringUtils.SPACE:DEC.format(as.getCredit()),17)
//			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebitBalance())?AonStringUtils.SPACE:DEC.format(as.getDebitBalance()),17)		
//			+AonStringUtils.leftPad(AonMathUtils.isZero(as.getUnpaidBalance())?AonStringUtils.SPACE:DEC.format(as.getUnpaidBalance()),17)
//			+AonStringUtils.center(AonStringUtils.defaultString(as.getBalancingAccountCode()),11)
//			+AonStringUtils.rightPad(AonStringUtils.defaultString(as.getDocumentNumber()), 33)
//					);
//		});
	}
	
}
