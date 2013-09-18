package com.code.aon.ui.accounting;


public interface IAccountingConstants {
	
	// COMMON
	String ASTERISK = "*";
	String EMPTY = "";
	String CR = "\r\n";
	String OPEN_BRACKET = "(";
	String CLOSE_BRACKET = ")";
	String PIPE = "|";
	String COMMA = ",";
	String QUESTION_MARK = "?";
	String OPEN_EXPRESSION = "#{";
	String CLOSE_EXPRESSION = "}#";
	String DECIMAL_FORMAT_PATTERN = "#,##0.00";
	Double ZERO = new Double(0);

	
	// CONTROLLERS
	String ACCOUNT_APP_PARAM_CONTROLLER_NAME = "accAppParams";
	String ACCOUNT_CHECK_CONTROLLER = "accountCheck";
	String ACCOUNT_COLLECTIONS_CONTROLLER_NAME = "accountCollections";
	String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	String ACCOUNT_ENTRY_CONTROLLER_DETAIL_NAME = "accountEntryDetail";
	String AMORTIZATION_DETAIL_CONTROLLER = "amortizationDetail";	
	String FINANCIAL_STATEMENT_CONTROLLER_NAME = "financialStatement";
	String STATEMENT_CONTROLLER_NAME = "statement";
	String TRIAL_BALANCE_CONTROLLER_NAME = "trialBalance";
	String ACCOUNTING_COLLECTIONS_CONTROLLER_NAME =  "accountingCollections";
	String REPORT_CONTROLLER = "report";
	String COMPANY_CONTROLLER = "company";
	String JOURNAL_REPORT_CONTROLLER = "journalReport";
	String ACCOUNTING_REGENERATOR_CONTROLLER = "accountRegenerator";
	String LEDGER_REPORT_CONTROLLER = "ledgerReport";
	String OFFICIAL_TRIAL_BALANCE_CONTROLLER = "officialTrialBalance";
	String VAT_REPORT_CONTROLLER = "vatReport";
	String BALANCE_SHEET_CONTROLLER = "balanceSheet";
	String FISCAL_PARAMETERS_CONTROLLER = "fiscalParams";
	String PROFIT_AND_LOSS_CONTROLLER_NAME = "profitAndLossReport";
	
	// NAVIGATION KEYS
	String ACCOUNT_ENTRY_FORM_NAVKEY = "accountEntry_form";
	String ACCOUNT_ENTRY_LIST_NAVKEY = "accountEntry_list";
	String ACCOUNT_ENTRY_SEARCH_NAVKEY = "accountEntry_search";
	String ACCOUNT_LOAN_FEE_ENTRY_NAVKEY = "loanFeeEntry_form";
	String ACCOUNT_STMT_LIST_NAVKEY = "accountStatement_list";
	String AMORTIZATION_FORM_NAVKEY = "amortization_form";
	String BALANCE_SHEET_LIST_NAVKEY = "balance_sheet_list";
	String LEDGER_LIST_NAVKEY = "ledger_list";
	String LOAN_FORM_NAVKEY = "loan_form";
	String FINANCIAL_STATEMENT_LIST_NAVKEY = "financialStatement_list";
	String JOURNAL_LIST_NAVKEY =  "journal_list";
	String PERIOD_AMORTIZATION_LIST_NAVKEY = "periodAmortization_list";
	
	// OTHERS
	String ACCUMULATED_ACCOUNT_PREFIX = "Amortización Acumulada ";
	String ALLOCATION_ACCOUNT_PREFIX = "Amortización ";
	
}
