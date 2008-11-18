package com.code.aon.account.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.account.Account;
import com.code.aon.account.AccountBudget;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountSummary;
import com.code.aon.account.Amortization;
import com.code.aon.account.AmortizationDetail;
import com.code.aon.account.AmortizationType;
import com.code.aon.account.AutoConcept;
import com.code.aon.account.Leasing;
import com.code.aon.account.Loan;
import com.code.aon.account.Period;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class AccountAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-account/src/main/java/com/code/aon/account/dao/IAccountAlias.java");
		String[] classes = new String[12]; 
		classes[0] = Account.class.getName();
		classes[1] = AccountBudget.class.getName();
		classes[2] = AutoConcept.class.getName();
		classes[3] = AccountEntry.class.getName();
		classes[4] = AccountEntryDetail.class.getName();
		classes[5] = AccountSummary.class.getName();
		classes[6] = Amortization.class.getName();
		classes[7] = AmortizationDetail.class.getName();
		classes[8] = AmortizationType.class.getName();
		classes[9] = Loan.class.getName();
		classes[10] = Leasing.class.getName();
		classes[11] = Period.class.getName();
		HibernateUtil.getSessionFactory();
		AliasWriter writer = new AliasWriter("com.code.aon.account.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}