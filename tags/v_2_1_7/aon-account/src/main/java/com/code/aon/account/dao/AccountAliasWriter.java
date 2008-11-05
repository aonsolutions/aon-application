package com.code.aon.account.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountSummary;
import com.code.aon.account.AutoConcept;
import com.code.aon.account.Leasing;
import com.code.aon.account.Loan;
import com.code.aon.account.Period;
import com.code.aon.common.dao.AliasWriter;

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
//		File file = new File("c:/IAccountAlias.java");
		String[] classes = new String[8]; 
		classes[0] = Account.class.getName();
		classes[1] = Period.class.getName();
		classes[2] = AutoConcept.class.getName();
		classes[3] = AccountEntry.class.getName();
		classes[4] = AccountEntryDetail.class.getName();
		classes[5] = AccountSummary.class.getName();
		classes[6] = Loan.class.getName();
		classes[7] = Leasing.class.getName();
		/*System.setProperty(HibernateUtil.HIBERNATE_CONFIGURATION_FILE_PROPERTY, 
				"com.code.aon.account.dao.hibernate.cfg.xml");*/
		AliasWriter writer = new AliasWriter("com.code.aon.account.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}