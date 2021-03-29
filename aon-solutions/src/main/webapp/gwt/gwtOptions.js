import * as MSG from "../../environments/msg.js";

export const GWT_FISCAL = 'aon_gwt_fiscal';
export const GWT_AIO = 'aon_gwt_aio';
export const GWT_PAYROLL = 'aon_gwt_payroll';

export const CUSTOMER = {
  name: MSG.CUSTOMERS,
  module: GWT_FISCAL,
  entryPoint: 'Customer'
};

export const SUPPLIER = {
  name: MSG.SUPPLIERS,
  module: GWT_FISCAL,
  entryPoint: 'Supplier'
};

export const CREDITOR = {
  name: MSG.CREDITORS,
  module: GWT_FISCAL,
  entryPoint: 'Creditor'
};

export const ACCOUNTING_PERIOD = {
  name: 'AccountingPeriodModule',
  module: GWT_FISCAL,
  entryPoint: 'AccountingPeriodModule'
};


export const ACCOUNT_ENTRY = {
  module: GWT_FISCAL,
  entryPoint: 'AccountEntryModuleTEDI'
};

export const STATEMENT_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'StatementReportModule'
};

export const ACCOUNT_OPERATING_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'AccountOperatingReport'
};

export const JOURNAL_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'JournalReportModule'
};

export const LEDGER_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'LedgerReportModule'
};

export const ACCOUNT_BALANCE_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'AccountBalanceReport'
};

export const VAT_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'VATReport'
};

export const IRPF_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'IRPFReport'
};

export const OPERATION_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'OperationReport'
};

export const DEPOSIT = {
  module: GWT_AIO,
  entryPoint: 'deposit'
};

export const EMPLOYEES = {
  module: GWT_AIO,
  entryPoint: 'employees'
};

export const CONVENIOS = {
  module: GWT_PAYROLL,
  entryPoint: 'MainAgreement'
};

export const PAYROLL_CONFIG = {
  module: GWT_PAYROLL,
  entryPoint: 'MainSystem'
};

export const PAYROLL_TRASH = {
  module: GWT_PAYROLL,
  entryPoint: 'MainTrash'
};

export const FINANCE = {
  module: GWT_FISCAL,
  entryPoint: 'Finance'
};

export const RAWDOC = {
  module: GWT_FISCAL,
  entryPoint: 'RawdocModule'
};

export const MAIN_CONTRATA = {
  module: GWT_PAYROLL,
  entryPoint: 'MainContrata'
};

export const MAIN_DIGITAL_CERTIFICATES = {
  module: GWT_PAYROLL,
  entryPoint: 'MainDigitalCertificates'
};

export const SECURITY_MANAGEMENT = {
  module: GWT_AIO,
  entryPoint: 'scope'
};

export const IMPORT = {
  module: GWT_AIO,
  entryPoint: 'import'
};

export const TEMPLATE = {
  module: GWT_AIO,
  entryPoint: 'templates'
};

export const MODEL_111 = {
  name: MSG.AON_MSG_MODEL_111_DESCRIPTION,
  module: GWT_FISCAL,
  entryPoint: 'Model111'
};

export const MODEL_190 = {
  name: MSG.AON_MSG_MODEL_111_DESCRIPTION,
  module: GWT_FISCAL,
  entryPoint: 'Model90'
};
