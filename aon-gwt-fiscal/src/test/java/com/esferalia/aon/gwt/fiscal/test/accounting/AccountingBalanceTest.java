package com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class AccountingBalanceTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.euk";
	private static int DOMAIN_ID = 536;
	private static String USER = "mac";
	
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
		
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
		AccountingReportParams params = new AccountingReportParams()
				.setPeriod(32705)
				.setFromDate( AonDateUtils.getDate(2016, 0, 1))
				.setToDate( AonDateUtils.getDate(2016, 11, 31))
				.setBalanceType(BalanceType.BALANCE_NORMAL)
				; 
		DecimalFormat nf = new DecimalFormat("#,##0.0#;(#,##0.0#)");
		AccountBalanceReport report = AccountStatementDAO.balanceReport(ctx, params);
		for (BalanceLine line : report.getBalances().values()) {
			int indent = (line.getLevel() * 3);
			System.out.println(
				  AonStringUtils.repeat(" ", indent)
				+ AonStringUtils.rightPad( line.getDescription(), (80 - indent)) 
//				+ AonStringUtils.leftPad( nf.format( line.getAmount()), 15 ) 
				);
		}
	}

}
