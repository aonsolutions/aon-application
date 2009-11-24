package com.code.aon.accounting.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.accounting.AccountBudget;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountSummary;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.AutoConcept;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Leasing;
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
		File file = new File("/AON-PROJECT/aon-accounting/src/main/java/com/code/aon/accounting/dao/IAccountingAlias.java");
		String[] classes = new String[13]; 
		classes[0] = AccountBudget.class.getName();
		classes[1] = AccountEntry.class.getName();
		classes[2] = AccountEntryDetail.class.getName();
		classes[3] = AccountSummary.class.getName();
		classes[4] = Amortization.class.getName();
		classes[5] = AmortizationDetail.class.getName();
		classes[6] = AmortizationType.class.getName();
		classes[7] = AutoConcept.class.getName();
		classes[8] = Balance.class.getName();
		classes[9] = BalanceDetail.class.getName();	
		classes[10] = Loan.class.getName();
		classes[11] = Leasing.class.getName();
		classes[12] = Period.class.getName();
		
		
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.accounting.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}