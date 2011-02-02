package com.code.aon.accounting.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.accounting.AccountBudget;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountEntryLink;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.AccountSummary;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.AnnualReport;
import com.code.aon.accounting.AnnualReportDetail;
import com.code.aon.accounting.AutoConcept;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class AccountingAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/home/ecastellano/AON-TRUNK/aon-accounting/src/main/java/com/code/aon/accounting/dao/IAccountingAlias.java");
		String[] classes = new String[]{
			AccountBudget.class.getName(),
			AccountEntry.class.getName(),
			AccountEntryDetail.class.getName(),
			AccountEntryLink.class.getName(),
			AccountHelper.class.getName(),
			AccountSummary.class.getName(),
			Amortization.class.getName(),
			AmortizationDetail.class.getName(),
			AmortizationType.class.getName(),
			AnnualReport.class.getName(),
			AnnualReportDetail.class.getName(),
			AutoConcept.class.getName(),
			Balance.class.getName(),
			BalanceDetail.class.getName(),	
			Loan.class.getName(),
			Period.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.accounting.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}