package com.code.aon.account.bridge.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAccountBridgeAlias {



	/** 
	* DAOConstantsEntry for CustomerAccount entity.
	*/ 
	DAOConstantsEntry CUSTOMER_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(CustomerAccount.class);

	/** 
	* Alias value: CustomerAccount_account_id
	* Hibernate value: CustomerAccount.account.id
	*/
	String  CUSTOMER_ACCOUNT_ACCOUNT_ID = CUSTOMER_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CustomerAccount_customer_id
	* Hibernate value: CustomerAccount.customer.id
	*/
	String  CUSTOMER_ACCOUNT_CUSTOMER_ID = CUSTOMER_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CustomerAccount_id
	* Hibernate value: CustomerAccount.id
	*/
	String  CUSTOMER_ACCOUNT_ID = CUSTOMER_ACCOUNT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for SupplierAccount entity.
	*/ 
	DAOConstantsEntry SUPPLIER_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(SupplierAccount.class);

	/** 
	* Alias value: SupplierAccount_account_id
	* Hibernate value: SupplierAccount.account.id
	*/
	String  SUPPLIER_ACCOUNT_ACCOUNT_ID = SUPPLIER_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupplierAccount_id
	* Hibernate value: SupplierAccount.id
	*/
	String  SUPPLIER_ACCOUNT_ID = SUPPLIER_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupplierAccount_supplier_id
	* Hibernate value: SupplierAccount.supplier.id
	*/
	String  SUPPLIER_ACCOUNT_SUPPLIER_ID = SUPPLIER_ACCOUNT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for CreditorAccount entity.
	*/ 
	DAOConstantsEntry CREDITOR_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(CreditorAccount.class);

	/** 
	* Alias value: CreditorAccount_account_id
	* Hibernate value: CreditorAccount.account.id
	*/
	String  CREDITOR_ACCOUNT_ACCOUNT_ID = CREDITOR_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CreditorAccount_creditor_id
	* Hibernate value: CreditorAccount.creditor.id
	*/
	String  CREDITOR_ACCOUNT_CREDITOR_ID = CREDITOR_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CreditorAccount_id
	* Hibernate value: CreditorAccount.id
	*/
	String  CREDITOR_ACCOUNT_ID = CREDITOR_ACCOUNT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ProductAccount entity.
	*/ 
	DAOConstantsEntry PRODUCT_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(ProductAccount.class);

	/** 
	* Alias value: ProductAccount_account_id
	* Hibernate value: ProductAccount.account.id
	*/
	String  PRODUCT_ACCOUNT_ACCOUNT_ID = PRODUCT_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductAccount_id
	* Hibernate value: ProductAccount.id
	*/
	String  PRODUCT_ACCOUNT_ID = PRODUCT_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProductAccount_product_id
	* Hibernate value: ProductAccount.product.id
	*/
	String  PRODUCT_ACCOUNT_PRODUCT_ID = PRODUCT_ACCOUNT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProductAccount_type
	* Hibernate value: ProductAccount.type
	*/
	String  PRODUCT_ACCOUNT_TYPE = PRODUCT_ACCOUNT_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for RegistryBankAccount entity.
	*/ 
	DAOConstantsEntry REGISTRY_BANK_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(RegistryBankAccount.class);

	/** 
	* Alias value: RegistryBankAccount_account_id
	* Hibernate value: RegistryBankAccount.account.id
	*/
	String  REGISTRY_BANK_ACCOUNT_ACCOUNT_ID = REGISTRY_BANK_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RegistryBankAccount_id
	* Hibernate value: RegistryBankAccount.id
	*/
	String  REGISTRY_BANK_ACCOUNT_ID = REGISTRY_BANK_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RegistryBankAccount_registryBank_id
	* Hibernate value: RegistryBankAccount.registryBank.id
	*/
	String  REGISTRY_BANK_ACCOUNT_REGISTRY_BANK_ID = REGISTRY_BANK_ACCOUNT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for LoanAccount entity.
	*/ 
	DAOConstantsEntry LOAN_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(LoanAccount.class);

	/** 
	* Alias value: LoanAccount_account_id
	* Hibernate value: LoanAccount.account.id
	*/
	String  LOAN_ACCOUNT_ACCOUNT_ID = LOAN_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LoanAccount_id
	* Hibernate value: LoanAccount.id
	*/
	String  LOAN_ACCOUNT_ID = LOAN_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LoanAccount_loan_id
	* Hibernate value: LoanAccount.loan.id
	*/
	String  LOAN_ACCOUNT_LOAN_ID = LOAN_ACCOUNT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for LeasingAccount entity.
	*/ 
	DAOConstantsEntry LEASING_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(LeasingAccount.class);

	/** 
	* Alias value: LeasingAccount_account_id
	* Hibernate value: LeasingAccount.account.id
	*/
	String  LEASING_ACCOUNT_ACCOUNT_ID = LEASING_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LeasingAccount_id
	* Hibernate value: LeasingAccount.id
	*/
	String  LEASING_ACCOUNT_ID = LEASING_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LeasingAccount_leasing_id
	* Hibernate value: LeasingAccount.leasing.id
	*/
	String  LEASING_ACCOUNT_LEASING_ID = LEASING_ACCOUNT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for InvoiceDetailAccount entity.
	*/ 
	DAOConstantsEntry INVOICE_DETAIL_ACCOUNT_ENTRY = DAOConstants.getDAOConstant(InvoiceDetailAccount.class);

	/** 
	* Alias value: InvoiceDetailAccount_account_id
	* Hibernate value: InvoiceDetailAccount.account.id
	*/
	String  INVOICE_DETAIL_ACCOUNT_ACCOUNT_ID = INVOICE_DETAIL_ACCOUNT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: InvoiceDetailAccount_invoiceDetail_id
	* Hibernate value: InvoiceDetailAccount.invoiceDetail.id
	*/
	String  INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID = INVOICE_DETAIL_ACCOUNT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: InvoiceDetailAccount_invoiceDetail_invoice_id
	* Hibernate value: InvoiceDetailAccount.invoiceDetail.invoice.id
	*/
	String  INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID = INVOICE_DETAIL_ACCOUNT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: InvoiceDetailAccount_id
	* Hibernate value: InvoiceDetailAccount.id
	*/
	String  INVOICE_DETAIL_ACCOUNT_ID = INVOICE_DETAIL_ACCOUNT_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for AccountEntryInvoice entity.
	*/ 
	DAOConstantsEntry ACCOUNT_ENTRY_INVOICE_ENTRY = DAOConstants.getDAOConstant(AccountEntryInvoice.class);

	/** 
	* Alias value: AccountEntryInvoice_accountEntry_id
	* Hibernate value: AccountEntryInvoice.accountEntry.id
	*/
	String  ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID = ACCOUNT_ENTRY_INVOICE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AccountEntryInvoice_id
	* Hibernate value: AccountEntryInvoice.id
	*/
	String  ACCOUNT_ENTRY_INVOICE_ID = ACCOUNT_ENTRY_INVOICE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AccountEntryInvoice_invoice_id
	* Hibernate value: AccountEntryInvoice.invoice.id
	*/
	String  ACCOUNT_ENTRY_INVOICE_INVOICE_ID = ACCOUNT_ENTRY_INVOICE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AccountEntryFinanceBatch entity.
	*/ 
	DAOConstantsEntry ACCOUNT_ENTRY_FINANCE_BATCH_ENTRY = DAOConstants.getDAOConstant(AccountEntryFinanceBatch.class);

	/** 
	* Alias value: AccountEntryFinanceBatch_accountEntry_id
	* Hibernate value: AccountEntryFinanceBatch.accountEntry.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_BATCH_ACCOUNT_ENTRY_ID = ACCOUNT_ENTRY_FINANCE_BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AccountEntryFinanceBatch_financeBatch_id
	* Hibernate value: AccountEntryFinanceBatch.financeBatch.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_BATCH_FINANCE_BATCH_ID = ACCOUNT_ENTRY_FINANCE_BATCH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AccountEntryFinanceBatch_id
	* Hibernate value: AccountEntryFinanceBatch.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_BATCH_ID = ACCOUNT_ENTRY_FINANCE_BATCH_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AccountEntryFinanceTracking entity.
	*/ 
	DAOConstantsEntry ACCOUNT_ENTRY_FINANCE_TRACKING_ENTRY = DAOConstants.getDAOConstant(AccountEntryFinanceTracking.class);

	/** 
	* Alias value: AccountEntryFinanceTracking_accountEntry_id
	* Hibernate value: AccountEntryFinanceTracking.accountEntry.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID = ACCOUNT_ENTRY_FINANCE_TRACKING_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AccountEntryFinanceTracking_financeTracking_id
	* Hibernate value: AccountEntryFinanceTracking.financeTracking.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_ID = ACCOUNT_ENTRY_FINANCE_TRACKING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AccountEntryFinanceTracking_financeTracking_finance_id
	* Hibernate value: AccountEntryFinanceTracking.financeTracking.finance.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID = ACCOUNT_ENTRY_FINANCE_TRACKING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AccountEntryFinanceTracking_id
	* Hibernate value: AccountEntryFinanceTracking.id
	*/
	String  ACCOUNT_ENTRY_FINANCE_TRACKING_ID = ACCOUNT_ENTRY_FINANCE_TRACKING_ENTRY.getAliasNames()[3];


}