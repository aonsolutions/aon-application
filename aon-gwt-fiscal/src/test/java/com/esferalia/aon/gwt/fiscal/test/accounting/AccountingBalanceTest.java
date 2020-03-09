package com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileNotFoundException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;


public class AccountingBalanceTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "udapa.ecastellano.euk";
	private static int DOMAIN_ID = 3049;
	private static String USER = "montse";
	
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
		
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
		AccountingReportParams params = new AccountingReportParams()
				.setPeriod(37200)
				.setByMonth(true)
				; 
		// DecimalFormat nf = new DecimalFormat("#,##0.0#;(#,##0.0#)");
		AccountOperatingReport report = AccountStatementDAO.operatingReport(ctx, params);
		for (AccountOperatingAccount line : report.getAccounts()) {
			System.out.println(line.getCode());
		}
	}

}
