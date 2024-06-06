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
  entryPoint: 'AccountEntryModule'
};

export const STATEMENT_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'StatementReportModule'
};

export const ACCOUNT_MODULE = {
  module: GWT_FISCAL,
  entryPoint: 'AccountModule'
};

export const ACCOUNT_OPERATING_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'AccountOperatingReport'
};

export const ACCOUNT_TRIAL_BALANCE_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'AccountTrialBalanceReport'
};

export const ACCOUNT_ANALYTICAL_REPORT = {
  module: GWT_FISCAL,
  entryPoint: 'AccountAnalyticalReport'
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

export const AMORTIZATION_TYPE = {
  module: GWT_FISCAL,
  entryPoint: 'AmortizationType'
};

export const DEPOSIT = {
  module: GWT_AIO,
  entryPoint: 'deposit'
};

export const EMPLOYEES = {
  title:'Integral de Nóminas',
  module: GWT_PAYROLL,
  entryPoint: 'EmployeeTree'
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
  title: MSG.CHARGES_AND_PAYMENTS,
  name: MSG.CHARGES_AND_PAYMENTS,
  module: GWT_FISCAL,
  entryPoint: 'Finance'
};

export const RAWDOC = {
  module: GWT_FISCAL,
  entryPoint: 'RawdocModule'
};

export const RAWDOC_RECORD = {
  module: GWT_FISCAL,
  entryPoint: 'RawdocRecordModule'
};

export const MAIN_CONTRATA = {
  title: 'Contratos',
  module: GWT_PAYROLL,
  entryPoint: 'MainContrata'
};

export const MAIN_DIGITAL_CERTIFICATES = {
  module: GWT_PAYROLL,
  entryPoint: 'MainCertificates'
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

export const MODEL_115 = {
  title: "Modelo 115",
  name: "Modelo 115",
  module: GWT_FISCAL,
  entryPoint: 'Model115'
};

export const MODEL_123 = {
  title: "Modelo 123",
  name: "Modelo 123",
  module: GWT_FISCAL,
  entryPoint: 'Model123'
};

export const MODEL_130 = {
  title: "Modelo 130",
  name: "Modelo 130",
  module: GWT_FISCAL,
  entryPoint: 'Model130'
};

export const MODEL_131 = {
  title: "Modelo 131",
  name: "Modelo 131",
  module: GWT_FISCAL,
  entryPoint: 'Model131'
};

export const MODEL_140 = {
  title: "Modelo 140",
  name: "Modelo 140",
  module: GWT_FISCAL,
  entryPoint: 'Model140'
};

export const MODEL_180 = {
  title: "Modelo 180",
  name: "Modelo 180",
  module: GWT_FISCAL,
  entryPoint: 'Model180'
};

export const MODEL_184 = {
  title: "Modelo 184",
  name: "Modelo 184",
  module: GWT_FISCAL,
  entryPoint: 'Model184'
};

export const MODEL_190 = {
  title: MSG.MODEL_190_DESCRIPTION,
  name: MSG.MODEL_190_DESCRIPTION,
  module: GWT_FISCAL,
  entryPoint: 'Model190'
};

export const MODEL_193 = {
  title:"Model 193",
  name: "Model 193",
  module: GWT_FISCAL,
  entryPoint: 'Model193'
};

export const MODEL_202 = {
  title:"Model 202",
  name: "Model 202",
  module: GWT_FISCAL,
  entryPoint: 'Model202'
};

export const MODEL_303 = {
  title: "Modelo 303. Autoliquidación de IVA",
  name: "Modelo 303. Autoliquidación de IVA",
  module: GWT_FISCAL,
  entryPoint: 'Model303'
};

export const MODEL_347 = {
  title: "Modelo 347",
  name: "Modelo 347",
  module: GWT_FISCAL,
  entryPoint: 'Model347'
};

export const MODEL_349 = {
  title: "Modelo 349",
  name: "Modelo 349",
  module: GWT_FISCAL,
  entryPoint: 'Model349'
};

export const MODEL_390 = {
  title: "Modelo 390",
  name: "Modelo 390",
  module: GWT_FISCAL,
  entryPoint: 'Model390'
};

export const INVOICE_STAT = {
  title: 'estadisticas',
  name: 'estadisticas',
  module: GWT_AIO,
  entryPoint: 'stat',
  subEntryPoint: 'StatControlPanel'
}

export const TASK_STAT = {
  title: MSG.STATISTICS,
  name: MSG.STATISTICS,
  module: GWT_AIO,
  entryPoint: 'issues',
  subEntryPoint: 'taskStat'
}

export const CHECKIT = {
  title: MSG.BANKS,
  name: MSG.BANKS,
  module: GWT_FISCAL,
  entryPoint: 'CheckItModule'
}

export const NORDIGEN = {
  title: MSG.BANKS,
  name: MSG.BANKS,
  module: GWT_FISCAL,
  entryPoint: 'NordigenModule'
}

export const BOOKING_PANEL = {
  title: MSG.BOOKING_PANEL,
  name: MSG.BOOKING_PANEL,
  module: GWT_FISCAL,
  entryPoint: 'BookingPanel'
}

export const BOOKING_RESUME = {
  title: MSG.BOOKING_RESUME,
  name: MSG.BOOKING_RESUME,
  module: GWT_FISCAL,
  entryPoint: 'DomainBookingResume'
}

export const QUESTION = {
  title: MSG.QUESTIONS,
  name: MSG.QUESTIONS,
  module: GWT_AIO,
  entryPoint: 'QuestionModule',
}
