package com.code.aon.accounting.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.AutoConcept;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAccountingAlias {



	/** 
	* DAOConstantsEntry for AccountEntry entity.
	*/ 
	DAOConstantsEntry ACCOUNT_ENTRY_ENTRY = DAOConstants.getDAOConstant(AccountEntry.class);

	/** 
	* Alias value: AccountEntry_accountPeriod
	* Hibernate value: AccountEntry.accountPeriod
	*/
	String  ACCOUNT_ENTRY_ACCOUNT_PERIOD = ACCOUNT_ENTRY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AccountEntry_comments
	* Hibernate value: AccountEntry.comments
	*/
	String  ACCOUNT_ENTRY_COMMENTS = ACCOUNT_ENTRY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AccountEntry_entryDate
	* Hibernate value: AccountEntry.entryDate
	*/
	String  ACCOUNT_ENTRY_ENTRY_DATE = ACCOUNT_ENTRY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AccountEntry_id
	* Hibernate value: AccountEntry.id
	*/
	String  ACCOUNT_ENTRY_ID = ACCOUNT_ENTRY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AccountEntry_journal
	* Hibernate value: AccountEntry.journal
	*/
	String  ACCOUNT_ENTRY_JOURNAL = ACCOUNT_ENTRY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AccountEntry_securityLevel
	* Hibernate value: AccountEntry.securityLevel
	*/
	String  ACCOUNT_ENTRY_SECURITY_LEVEL = ACCOUNT_ENTRY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AccountEntry_type
	* Hibernate value: AccountEntry.type
	*/
	String  ACCOUNT_ENTRY_TYPE = ACCOUNT_ENTRY_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for AccountEntryDetail entity.
	*/ 
	DAOConstantsEntry ACCOUNT_ENTRY_DETAIL_ENTRY = DAOConstants.getDAOConstant(AccountEntryDetail.class);

	/** 
	* Alias value: AccountEntryDetail_accountEntry_id
	* Hibernate value: AccountEntryDetail.accountEntry.id
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AccountEntryDetail_accountEntry_type
	* Hibernate value: AccountEntryDetail.accountEntry.type
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AccountEntryDetail_accountEntry_entryDate
	* Hibernate value: AccountEntryDetail.accountEntry.entryDate
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AccountEntryDetail_accountEntry_accountPeriod
	* Hibernate value: AccountEntryDetail.accountEntry.accountPeriod
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AccountEntryDetail_accountEntry_securityLevel
	* Hibernate value: AccountEntryDetail.accountEntry.securityLevel
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AccountEntryDetail_accountEntry_journal
	* Hibernate value: AccountEntryDetail.accountEntry.journal
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_JOURNAL = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AccountEntryDetail_account_id
	* Hibernate value: AccountEntryDetail.account.id
	*/
	String  ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: AccountEntryDetail_balancingAccount_id
	* Hibernate value: AccountEntryDetail.balancingAccount.id
	*/
	String  ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT_ID = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: AccountEntryDetail_concept
	* Hibernate value: AccountEntryDetail.concept
	*/
	String  ACCOUNT_ENTRY_DETAIL_CONCEPT = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: AccountEntryDetail_credit
	* Hibernate value: AccountEntryDetail.credit
	*/
	String  ACCOUNT_ENTRY_DETAIL_CREDIT = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: AccountEntryDetail_debit
	* Hibernate value: AccountEntryDetail.debit
	*/
	String  ACCOUNT_ENTRY_DETAIL_DEBIT = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: AccountEntryDetail_id
	* Hibernate value: AccountEntryDetail.id
	*/
	String  ACCOUNT_ENTRY_DETAIL_ID = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: AccountEntryDetail_line
	* Hibernate value: AccountEntryDetail.line
	*/
	String  ACCOUNT_ENTRY_DETAIL_LINE = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: AccountEntryDetail_documentNumber
	* Hibernate value: AccountEntryDetail.documentNumber
	*/
	String  ACCOUNT_ENTRY_DETAIL_DOCUMENT_NUMBER = ACCOUNT_ENTRY_DETAIL_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for AccountHelper entity.
	*/ 
	DAOConstantsEntry ACCOUNT_HELPER_ENTRY = DAOConstants.getDAOConstant(AccountHelper.class);

	/** 
	* Alias value: AccountHelper_account_id
	* Hibernate value: AccountHelper.account.id
	*/
	String  ACCOUNT_HELPER_ACCOUNT_ID = ACCOUNT_HELPER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AccountHelper_balancingAccount_id
	* Hibernate value: AccountHelper.balancingAccount.id
	*/
	String  ACCOUNT_HELPER_BALANCING_ACCOUNT_ID = ACCOUNT_HELPER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AccountHelper_counter
	* Hibernate value: AccountHelper.counter
	*/
	String  ACCOUNT_HELPER_COUNTER = ACCOUNT_HELPER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AccountHelper_id
	* Hibernate value: AccountHelper.id
	*/
	String  ACCOUNT_HELPER_ID = ACCOUNT_HELPER_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Amortization entity.
	*/ 
	DAOConstantsEntry AMORTIZATION_ENTRY = DAOConstants.getDAOConstant(Amortization.class);

	/** 
	* Alias value: Amortization_accumulatedAccount_id
	* Hibernate value: Amortization.accumulatedAccount.id
	*/
	String  AMORTIZATION_ACCUMULATED_ACCOUNT_ID = AMORTIZATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Amortization_allocationAccount_id
	* Hibernate value: Amortization.allocationAccount.id
	*/
	String  AMORTIZATION_ALLOCATION_ACCOUNT_ID = AMORTIZATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Amortization_amortizationType_id
	* Hibernate value: Amortization.amortizationType.id
	*/
	String  AMORTIZATION_AMORTIZATION_TYPE_ID = AMORTIZATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Amortization_amortizationType_accumulatedAccount_id
	* Hibernate value: Amortization.amortizationType.accumulatedAccount.id
	*/
	String  AMORTIZATION_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT_ID = AMORTIZATION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Amortization_amortizationType_allocationAccount_id
	* Hibernate value: Amortization.amortizationType.allocationAccount.id
	*/
	String  AMORTIZATION_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT_ID = AMORTIZATION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Amortization_amortizationType_description
	* Hibernate value: Amortization.amortizationType.description
	*/
	String  AMORTIZATION_AMORTIZATION_TYPE_DESCRIPTION = AMORTIZATION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Amortization_amortizationType_fixedAssetAccount_id
	* Hibernate value: Amortization.amortizationType.fixedAssetAccount.id
	*/
	String  AMORTIZATION_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT_ID = AMORTIZATION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Amortization_amount
	* Hibernate value: Amortization.amount
	*/
	String  AMORTIZATION_AMOUNT = AMORTIZATION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Amortization_comments
	* Hibernate value: Amortization.comments
	*/
	String  AMORTIZATION_COMMENTS = AMORTIZATION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Amortization_deadline
	* Hibernate value: Amortization.deadline
	*/
	String  AMORTIZATION_DEADLINE = AMORTIZATION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Amortization_description
	* Hibernate value: Amortization.description
	*/
	String  AMORTIZATION_DESCRIPTION = AMORTIZATION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Amortization_feePeriod
	* Hibernate value: Amortization.feePeriod
	*/
	String  AMORTIZATION_FEE_PERIOD = AMORTIZATION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Amortization_fixedAssetAccount_id
	* Hibernate value: Amortization.fixedAssetAccount.id
	*/
	String  AMORTIZATION_FIXED_ASSET_ACCOUNT_ID = AMORTIZATION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Amortization_id
	* Hibernate value: Amortization.id
	*/
	String  AMORTIZATION_ID = AMORTIZATION_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Amortization_initialDate
	* Hibernate value: Amortization.initialDate
	*/
	String  AMORTIZATION_INITIAL_DATE = AMORTIZATION_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Amortization_percentage
	* Hibernate value: Amortization.percentage
	*/
	String  AMORTIZATION_PERCENTAGE = AMORTIZATION_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Amortization_saleAmount
	* Hibernate value: Amortization.saleAmount
	*/
	String  AMORTIZATION_SALE_AMOUNT = AMORTIZATION_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Amortization_securityLevel
	* Hibernate value: Amortization.securityLevel
	*/
	String  AMORTIZATION_SECURITY_LEVEL = AMORTIZATION_ENTRY.getAliasNames()[17];



	/** 
	* DAOConstantsEntry for AmortizationDetail entity.
	*/ 
	DAOConstantsEntry AMORTIZATION_DETAIL_ENTRY = DAOConstants.getDAOConstant(AmortizationDetail.class);

	/** 
	* Alias value: AmortizationDetail_accountEntry_id
	* Hibernate value: AmortizationDetail.accountEntry.id
	*/
	String  AMORTIZATION_DETAIL_ACCOUNT_ENTRY_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AmortizationDetail_allocation
	* Hibernate value: AmortizationDetail.allocation
	*/
	String  AMORTIZATION_DETAIL_ALLOCATION = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AmortizationDetail_amortization_id
	* Hibernate value: AmortizationDetail.amortization.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AmortizationDetail_amortization_accumulatedAccount_id
	* Hibernate value: AmortizationDetail.amortization.accumulatedAccount.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_ACCUMULATED_ACCOUNT_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AmortizationDetail_amortization_allocationAccount_id
	* Hibernate value: AmortizationDetail.amortization.allocationAccount.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_ALLOCATION_ACCOUNT_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AmortizationDetail_amortization_amortizationType_id
	* Hibernate value: AmortizationDetail.amortization.amortizationType.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_AMORTIZATION_TYPE_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AmortizationDetail_amortization_amortizationType_accumulatedAccount_id
	* Hibernate value: AmortizationDetail.amortization.amortizationType.accumulatedAccount.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: AmortizationDetail_amortization_amortizationType_allocationAccount_id
	* Hibernate value: AmortizationDetail.amortization.amortizationType.allocationAccount.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: AmortizationDetail_amortization_amortizationType_description
	* Hibernate value: AmortizationDetail.amortization.amortizationType.description
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_AMORTIZATION_TYPE_DESCRIPTION = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: AmortizationDetail_amortization_amortizationType_fixedAssetAccount_id
	* Hibernate value: AmortizationDetail.amortization.amortizationType.fixedAssetAccount.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: AmortizationDetail_amortization_description
	* Hibernate value: AmortizationDetail.amortization.description
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_DESCRIPTION = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: AmortizationDetail_amortization_fixedAssetAccount_id
	* Hibernate value: AmortizationDetail.amortization.fixedAssetAccount.id
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_FIXED_ASSET_ACCOUNT_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: AmortizationDetail_amortization_securityLevel
	* Hibernate value: AmortizationDetail.amortization.securityLevel
	*/
	String  AMORTIZATION_DETAIL_AMORTIZATION_SECURITY_LEVEL = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: AmortizationDetail_coefficient
	* Hibernate value: AmortizationDetail.coefficient
	*/
	String  AMORTIZATION_DETAIL_COEFFICIENT = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: AmortizationDetail_fiscalAllocation
	* Hibernate value: AmortizationDetail.fiscalAllocation
	*/
	String  AMORTIZATION_DETAIL_FISCAL_ALLOCATION = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: AmortizationDetail_fromDate
	* Hibernate value: AmortizationDetail.fromDate
	*/
	String  AMORTIZATION_DETAIL_FROM_DATE = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: AmortizationDetail_id
	* Hibernate value: AmortizationDetail.id
	*/
	String  AMORTIZATION_DETAIL_ID = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: AmortizationDetail_status
	* Hibernate value: AmortizationDetail.status
	*/
	String  AMORTIZATION_DETAIL_STATUS = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: AmortizationDetail_toDate
	* Hibernate value: AmortizationDetail.toDate
	*/
	String  AMORTIZATION_DETAIL_TO_DATE = AMORTIZATION_DETAIL_ENTRY.getAliasNames()[18];



	/** 
	* DAOConstantsEntry for AmortizationType entity.
	*/ 
	DAOConstantsEntry AMORTIZATION_TYPE_ENTRY = DAOConstants.getDAOConstant(AmortizationType.class);

	/** 
	* Alias value: AmortizationType_accumulatedAccount_id
	* Hibernate value: AmortizationType.accumulatedAccount.id
	*/
	String  AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT_ID = AMORTIZATION_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AmortizationType_allocationAccount_id
	* Hibernate value: AmortizationType.allocationAccount.id
	*/
	String  AMORTIZATION_TYPE_ALLOCATION_ACCOUNT_ID = AMORTIZATION_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AmortizationType_description
	* Hibernate value: AmortizationType.description
	*/
	String  AMORTIZATION_TYPE_DESCRIPTION = AMORTIZATION_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AmortizationType_fixedAssetAccount_id
	* Hibernate value: AmortizationType.fixedAssetAccount.id
	*/
	String  AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT_ID = AMORTIZATION_TYPE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AmortizationType_id
	* Hibernate value: AmortizationType.id
	*/
	String  AMORTIZATION_TYPE_ID = AMORTIZATION_TYPE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AmortizationType_percentage
	* Hibernate value: AmortizationType.percentage
	*/
	String  AMORTIZATION_TYPE_PERCENTAGE = AMORTIZATION_TYPE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for AutoConcept entity.
	*/ 
	DAOConstantsEntry AUTO_CONCEPT_ENTRY = DAOConstants.getDAOConstant(AutoConcept.class);

	/** 
	* Alias value: AutoConcept_description
	* Hibernate value: AutoConcept.description
	*/
	String  AUTO_CONCEPT_DESCRIPTION = AUTO_CONCEPT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AutoConcept_id
	* Hibernate value: AutoConcept.id
	*/
	String  AUTO_CONCEPT_ID = AUTO_CONCEPT_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Balance entity.
	*/ 
	DAOConstantsEntry BALANCE_ENTRY = DAOConstants.getDAOConstant(Balance.class);

	/** 
	* Alias value: Balance_id
	* Hibernate value: Balance.id
	*/
	String  BALANCE_ID = BALANCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Balance_name
	* Hibernate value: Balance.name
	*/
	String  BALANCE_NAME = BALANCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Balance_removable
	* Hibernate value: Balance.removable
	*/
	String  BALANCE_REMOVABLE = BALANCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Balance_type
	* Hibernate value: Balance.type
	*/
	String  BALANCE_TYPE = BALANCE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for BalanceDetail entity.
	*/ 
	DAOConstantsEntry BALANCE_DETAIL_ENTRY = DAOConstants.getDAOConstant(BalanceDetail.class);

	/** 
	* Alias value: BalanceDetail_accounts
	* Hibernate value: BalanceDetail.accounts
	*/
	String  BALANCE_DETAIL_ACCOUNTS = BALANCE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BalanceDetail_balance_id
	* Hibernate value: BalanceDetail.balance.id
	*/
	String  BALANCE_DETAIL_BALANCE_ID = BALANCE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BalanceDetail_code
	* Hibernate value: BalanceDetail.code
	*/
	String  BALANCE_DETAIL_CODE = BALANCE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: BalanceDetail_creditNature
	* Hibernate value: BalanceDetail.creditNature
	*/
	String  BALANCE_DETAIL_CREDIT_NATURE = BALANCE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: BalanceDetail_description
	* Hibernate value: BalanceDetail.description
	*/
	String  BALANCE_DETAIL_DESCRIPTION = BALANCE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: BalanceDetail_id
	* Hibernate value: BalanceDetail.id
	*/
	String  BALANCE_DETAIL_ID = BALANCE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: BalanceDetail_internalCalculation
	* Hibernate value: BalanceDetail.internalCalculation
	*/
	String  BALANCE_DETAIL_INTERNAL_CALCULATION = BALANCE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: BalanceDetail_sortKey
	* Hibernate value: BalanceDetail.sortKey
	*/
	String  BALANCE_DETAIL_SORT_KEY = BALANCE_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: BalanceDetail_title
	* Hibernate value: BalanceDetail.title
	*/
	String  BALANCE_DETAIL_TITLE = BALANCE_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: BalanceDetail_visible
	* Hibernate value: BalanceDetail.visible
	*/
	String  BALANCE_DETAIL_VISIBLE = BALANCE_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: BalanceDetail_zeroFlag
	* Hibernate value: BalanceDetail.zeroFlag
	*/
	String  BALANCE_DETAIL_ZERO_FLAG = BALANCE_DETAIL_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Loan entity.
	*/ 
	DAOConstantsEntry LOAN_ENTRY = DAOConstants.getDAOConstant(Loan.class);

	/** 
	* Alias value: Loan_amount
	* Hibernate value: Loan.amount
	*/
	String  LOAN_AMOUNT = LOAN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Loan_description
	* Hibernate value: Loan.description
	*/
	String  LOAN_DESCRIPTION = LOAN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Loan_expenses
	* Hibernate value: Loan.expenses
	*/
	String  LOAN_EXPENSES = LOAN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Loan_feeAmount
	* Hibernate value: Loan.feeAmount
	*/
	String  LOAN_FEE_AMOUNT = LOAN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Loan_id
	* Hibernate value: Loan.id
	*/
	String  LOAN_ID = LOAN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Loan_interest
	* Hibernate value: Loan.interest
	*/
	String  LOAN_INTEREST = LOAN_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Loan_loanDate
	* Hibernate value: Loan.loanDate
	*/
	String  LOAN_LOAN_DATE = LOAN_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Loan_payDay
	* Hibernate value: Loan.payDay
	*/
	String  LOAN_PAY_DAY = LOAN_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Loan_recurrence
	* Hibernate value: Loan.recurrence
	*/
	String  LOAN_RECURRENCE = LOAN_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Loan_registryBank_id
	* Hibernate value: Loan.registryBank.id
	*/
	String  LOAN_REGISTRY_BANK_ID = LOAN_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Loan_review
	* Hibernate value: Loan.review
	*/
	String  LOAN_REVIEW = LOAN_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Loan_securityLevel
	* Hibernate value: Loan.securityLevel
	*/
	String  LOAN_SECURITY_LEVEL = LOAN_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Loan_status
	* Hibernate value: Loan.status
	*/
	String  LOAN_STATUS = LOAN_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Loan_term
	* Hibernate value: Loan.term
	*/
	String  LOAN_TERM = LOAN_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for Period entity.
	*/ 
	DAOConstantsEntry PERIOD_ENTRY = DAOConstants.getDAOConstant(Period.class);

	/** 
	* Alias value: Period_deadline
	* Hibernate value: Period.deadline
	*/
	String  PERIOD_DEADLINE = PERIOD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Period_id
	* Hibernate value: Period.id
	*/
	String  PERIOD_ID = PERIOD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Period_initiationDate
	* Hibernate value: Period.initiationDate
	*/
	String  PERIOD_INITIATION_DATE = PERIOD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Period_status
	* Hibernate value: Period.status
	*/
	String  PERIOD_STATUS = PERIOD_ENTRY.getAliasNames()[3];


}