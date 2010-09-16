package com.code.aon.account.bridge.dao;

import java.io.File;

import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.PayMethodTypeDetailAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.TaxAccount;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class AccountBridgeAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("/AON-TRUNK/aon-account-bridge/src/main/java/com/code/aon/account/bridge/dao/IAccountBridgeAlias.java");
		String[] classes = new String[] {
			CustomerAccount.class.getName(),
			SupplierAccount.class.getName(),
			CreditorAccount.class.getName(),
			PayMethodTypeDetailAccount.class.getName(),
			ProductAccount.class.getName(),
			TaxAccount.class.getName(),
			RegistryBankAccount.class.getName(),
			LoanAccount.class.getName(),
			InvoiceDetailAccount.class.getName(),
			InvoiceTaxAccount.class.getName(),
			AccountEntryInvoice.class.getName(),
			AccountEntryFinanceBatch.class.getName(),
			AccountEntryFinanceTracking.class.getName(), };
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.account.bridge.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}