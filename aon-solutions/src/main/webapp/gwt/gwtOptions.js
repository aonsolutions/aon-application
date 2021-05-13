import { MSG } from '../environments/environments.js'; 

export const GWT_FISCAL = 'aon_gwt_fiscal';
export const GWT_AIO = 'aon_gwt_aio';
export const GWT_PAYROLL = 'aon_gwt_payroll';


export const CUSTOMER = {
  title: MSG.CUSTOMERS,
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
  title:'Integral de Nóminas',
  module: GWT_AIO,
  entryPoint: 'employees'
};

export const CONVENIOS = {
  title: 'Convenios',
  module: GWT_PAYROLL,
  entryPoint: 'MainAgreement'
};

export const PAYROLL_CONFIG = {
  title: 'Configuración. Globales',
  module: GWT_PAYROLL,
  entryPoint: 'MainSystem'
};

export const PAYROLL_TRASH = {
  title: 'Papelera',
  module: GWT_PAYROLL,
  entryPoint: 'MainTrash'
};

export const CONFIGURATION_COMUNICA = {
  module: GWT_PAYROLL,
  entryPoint: 'MainConfigComunica'
}

export const FINANCE = {
  module: GWT_FISCAL,
  entryPoint: 'Finance'
};

export const RAWDOC = {
  module: GWT_FISCAL,
  entryPoint: 'RawdocModule'
};

export const MAIN_CONTRATA = {
  title: 'Contratos',
  module: GWT_PAYROLL,
  entryPoint: 'MainContrata'
};

export const MAIN_DIGITAL_CERTIFICATES = {
  module: GWT_PAYROLL,
  entryPoint: 'MainDigitalCertificates'
};

export const MAIN_CCC = {
  module: GWT_PAYROLL,
  entryPoint: 'MainCCC'
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
  title: MSG.MODEL_111_DESCRIPTION,
  name: MSG.MODEL_111_DESCRIPTION,
  module: GWT_FISCAL,
  entryPoint: 'Model111'
};

export const MODEL_190 = {
  title: MSG.MODEL_190_DESCRIPTION,
  name: MSG.MODEL_190_DESCRIPTION,
  module: GWT_FISCAL,
  entryPoint: 'Model90'
};
