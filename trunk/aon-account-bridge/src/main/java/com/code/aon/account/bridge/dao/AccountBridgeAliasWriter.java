package com.code.aon.account.bridge.dao;

import java.io.File;

import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.common.dao.AliasWriter;

public class AccountBridgeAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("/PROYECTOS/aon-account-bridge/src/com/code/aon/account/bridge/dao/IAccountBridgeAlias.java");
//		File file = new File("c:/IAccountBridgeAlias.java");
		String[] classes = new String[9];
		classes[0] = CustomerAccount.class.getName();
		classes[1] = SupplierAccount.class.getName();
		classes[2] = CreditorAccount.class.getName();
		classes[3] = RegistryBankAccount.class.getName();
		classes[4] = LoanAccount.class.getName();
		classes[5] = LeasingAccount.class.getName();
		classes[6] = AccountEntryInvoice.class.getName();
		classes[7] = AccountEntryFinanceBatch.class.getName();
		classes[8] = AccountEntryFinanceTracking.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.account.bridge.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}