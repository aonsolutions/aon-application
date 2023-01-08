import { MSG, CONSTANT, AON_ICONS } from "../environments/environments.js";
import * as GWT from "../gwt/gwt.js";
import * as MSG_ES from "../environments/msg-es.js";

export const TIMECONTROL = {
  app: CONSTANT.TIMECONTROL,
  title: MSG.TIMECONTROL,
  description: MSG.TIMECONTROL,
  tag: MSG_ES.TIMECONTROL,
  icon: AON_ICONS.AON_TIMECONTROL,
  color: "#D1C36D",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
  price: " ", //  '-',
  disabled: false,
  options: {
    menu: false,
    add: false,
    upload: false,
    stat: false,
  }
};

export const ACCOUNTING = {
  app: CONSTANT.ACCOUNTING,
  title: MSG.ACCOUNTING,
  description: MSG.ACCOUNTING,
  tag: MSG_ES.ACCOUNTING,
  icon: AON_ICONS.AON_ACCOUNTING,
  color: "#002469",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
  ],
  price: " ", // '60€/mes',
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: true,
  },
};

export const FISCAL = {
  app: CONSTANT.FISCAL,
  title: MSG.FISCAL,
  description: MSG.FISCAL,
  tag: MSG_ES.FISCAL,
  icon: AON_ICONS.AON_FISCAL,
  color: "#002469",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
  ],
  price: " ", // '60€/mes',
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: false,
  },
};

export const PAYROLL = {
  app: CONSTANT.PAYROLL,
  title: MSG.PAYROLL,
  description: MSG.PAYROLL,
  tag: MSG.PAYROLL,
  icon: AON_ICONS.AON_PAYROLL,
  color: "#002469",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
  price: " ", // '90€/mes',
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: true,
  },
};

export const TREASURY = {
  app: CONSTANT.TREASURY,
  title: MSG.TREASURY,
  description: MSG.TREASURY,
  tag: MSG.TREASURY,
  icon: AON_ICONS.AON_TREASURY,
  color: "#002469",
  price: " ", // '90€/mes',
};


export const MARKETING = {
  app: CONSTANT.MARKETING,
  title: MSG.MARKETING,
  description: MSG.MARKETING,
  tag: MSG.MARKETING,
  icon: AON_ICONS.AON_MARKETING,
  color: "#002469",
  price: " ", // '90€/mes',
};

export const COMMERCIAL = {
  app: CONSTANT.COMMERCIAL,
  title: MSG.COMMERCIAL,
  description: MSG.COMMERCIAL,
  tag: MSG.COMMERCIAL,
  icon: AON_ICONS.AON_COMMERCIAL,
  color: "#002469",
  price: " ", // '90€/mes',
};

export const GROUPWARE = {
  app: 'groupware',
  title: 'Expedientes',
  description: 'Expedientes',
  tag: 'Expedientes',
  icon: AON_ICONS.AON_APP,
  color: "#002469",
  price: " ", // '90€/mes',
};

export const COMUNICA = {
  app: CONSTANT.COMUNICA,
  title: MSG.COMUNICA,
  description: MSG.COMUNICA,
  tag: MSG_ES.COMUNICA,
  icon: AON_ICONS.AON_COMUNICA,
  color: "#002469",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
// apps: [TIMECONTROL],
  price: " ", // '60€/mes'
};

export const DOCUMENTAL = {
  app: CONSTANT.DOCUMENTAL,
  title: MSG.DOCUMENTARY,
  description: MSG.DOCUMENTARY,
  tag: MSG_ES.DOCUMENTARY,
  icon: AON_ICONS.AON_DOCUMENTAL,
  color: "#6986BB",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
  price: " ", // '-',
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: true,
    stat: false
  }
};

export const WAREHOUSE = {
  app: CONSTANT.WAREHOUSE,
  title: MSG.WAREHOUSE,
  description: MSG.WAREHOUSE,
  tag: MSG_ES.WAREHOUSE,
  icon: AON_ICONS.AON_WAREHOUSE,
  color: "#002469",
  price: " ", // '-',
  disabled: true,
  options: {
    menu: false,
    add: false,
    upload: true,
    stat: false,
  },
};


export const INVOICE = {
  app: CONSTANT.INVOICE,
  title: MSG.INVOICES,
  description: MSG.INVOICES,
  tag: MSG_ES.INVOICES,
  icon: AON_ICONS.AON_INVOICE,
  color: "#4472C4",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
  price: " ", // '-',
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: true,
    stat: true,
  }
};

export const CONFIGURATION = {
  app: CONSTANT.CONFIGURATION,
  title: MSG.CONFIGURATION,
  description: MSG.CONFIGURATION,
  icon: AON_ICONS.AON_SETTINGS,
  color: "black",
};

export const MESSENGER = {
  app: CONSTANT.MESSENGER,
  title: MSG.REQUESTS,
  description: MSG.REQUESTS,
  tag: MSG_ES.REQUESTS,
  icon: AON_ICONS.AON_MESSENGER,
  color: "#1fd8b9",
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
  price: " ", // '-',
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: false,
    stat: true,
  },
};

export const TOOLS = {
  app: CONSTANT.TOOLS,
  title: MSG.TOOLS,
  icon: AON_ICONS.AON_TOOLS,
  color: "#535353",
  price: " ", // '-'
};

export const NOTES = {
  app: "note",
  title: MSG.NOTES,
  icon: AON_ICONS.AON_NOTES,
  color: "#ffc000",
  price: " ", // '-'
};

export const AON_SOLUTIONS = {
  app: "aio",
  title: "aonSolutions",
  description: "aonSolutions.",
  logo: "../assets/aon.png",
  price: " ", //  '-'
};

export const BIDOQ = {
  app: "bidoq",
  title: "Bidoq",
  description: "Bidoq.",
  logo: "../assets/apps/bidoq.png",
  price: " ", // '-'
};

export const SELFCONTA = {
  app: "selfconta",
  title: "Selfconta",
  description: "Selfconta.",
  logo: "../assets/apps/selfconta.png",
  price: " ", // '-'
};

export const AON_SALTRA = {
  app: CONSTANT.SALTRA,
  title: MSG.SALTRA,
  tag: MSG.SALTRA,
  description: MSG.SALTRA,
  access: [
    {
      value: "Asesor",
      name: "Asesor",
    },
    {
      value: "Empresa",
      name: "Empresa",
    },
    {
      value: "Empleado",
      name: "Empleado",
    },
  ],
  icon: AON_ICONS.AON_SALTRA,
  color: "#002469",
  price: " ", // '-'
};

export const ALMA = {
  app: "alma",
  title: "Alma",
  description: "Alma",
  logo: "../assets/apps/alma.png",
  price: " ",
};

export const OCR = {
  app: "ocr",
  title: "OCR",
  description: "Gestor OCR.",
  icon: AON_ICONS.AON_OCR,
  color: "#EA6D41",
  price: " ", // 'Desde 45€/mes'
};

export const CONVENIOS = {
  app: "convenios",
  title: "Convenios",
  description: "Convenios",
  icon: AON_ICONS.AON_CONVENIOS,
  color: "#EA6D41",
  price: " ", // 'Desde 45€/mes'
};

export const BANK = {
  app: "bank",
  title: "Bank",
  description: "Gestor de Bancos.",
  icon: AON_ICONS.AON_BANK,
  color: "#EA6D41",
  price: " ", // 'Desde 45€/mes'
};

export const OFFICE = {
  app: "office",
  icon: AON_ICONS.AON_OFFICE,
  title: "Despacho",
  color: "black",
  domainType: true,
};

export const API_SERVICE = {
  app: "api_service",
  title: "Cuentas de Servicio | Acceso API",
  description: "Cuentas de Servicio | Acceso API.",
  icon: AON_ICONS.AON_APP,
  color: "#EA6D41",
  price: " ", // 'Desde 45€/mes'
};

export const AULA = {
  app: "aula",
  title: "Aula",
  description: "Aula.",
  icon: AON_ICONS.AON_AULA,
  color: "#000000",
  moreInfo: "https://faqs.aonsolutions.es/",
  price: " ", // '-',
  disabled: false,
};

export const CUSTOM_VIEW = {
  app: "custom_view",
  title: MSG.CUSTOM_VIEW,
  description: MSG.CUSTOM_VIEW,
  icon: "aon_custom",
  color: "#CAF509",
  price: " ",
};

export const MenuApps = {
  OFFICE,
  // GARAGE,
  // ACADEMY,
  // COMMERCE,
  // HOTEL,
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  AON_SALTRA,
  INVOICE,
  // MARKETING,
  // TREASURY,
  DOCUMENTAL,
  TIMECONTROL,
  MESSENGER,
  WAREHOUSE,
  // GROUPWARE,
  NOTES,
  TOOLS
};

export const MobileMenuApps = [
  DOCUMENTAL,
  TIMECONTROL,
  INVOICE,
  MESSENGER,
  PAYROLL,
  COMUNICA,
  FISCAL,
  ACCOUNTING,
  WAREHOUSE
];

export const EmployeeApps = [
  "documental",
  "timecontrol",
  "payroll",
  "messenger",
  "invoice",
];

export const EnterpriseApps = [
  "accounting",
  "fiscal",
  "payroll",
  "comunica",
  "documental",
  "invoice",
  "timecontrol",
  "management",
  "messenger",
  "ocr",
  "aio",
  "bidoq",
  "selfconta",
  "saltra",
  "bank"
];

export const Apps = {
  OFFICE,
  // GARAGE,
  // ACADEMY,
  // COMMERCE,
  // HOTEL,
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  AON_SALTRA,
  INVOICE,
  // MARKETING,
  // TREASURY,
  DOCUMENTAL,
  TIMECONTROL,
  MESSENGER,
  NOTES,
  WAREHOUSE,
  // GROUPWARE
};

export const BookingApps = {
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  DOCUMENTAL,
  TIMECONTROL,
  INVOICE,
  MESSENGER,
  NOTES,
  WAREHOUSE
};

export const AuxApps = { TOOLS };
export const ClassicApps = { AON_SOLUTIONS, BIDOQ, SELFCONTA, AON_SALTRA };
export const Services = { OCR, CONVENIOS, BANK, AULA, API_SERVICE, CUSTOM_VIEW };

export const AllApps = {
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  DOCUMENTAL,
  TIMECONTROL,
  INVOICE,
  MESSENGER,
  OCR,
  CONVENIOS,
  BANK,
  AON_SOLUTIONS,
  BIDOQ,
  SELFCONTA,
  AON_SALTRA,
  WAREHOUSE
};

export const getApp = (name) => {
  for (let key in Apps) {
    if (name.toLowerCase() === Apps[key].app) {
      return Apps[key];
    }
  }

  for (let key in Services) {
    if (name.toLowerCase() === Services[key].app) {
      return Services[key];
    }
  }

  for (let key in ClassicApps) {
    if (name.toLowerCase() === ClassicApps[key].app) {
      return ClassicApps[key];
    }
  }
  return undefined;
};

export const getAppsByDur = (dur) => {
  let apps = [];
  if( dur.isAccounting())
    apps.push(Apps.ACCOUNTING);

  if(dur.isFiscal())
    apps.push(Apps.FISCAL);

  if((dur.isComunicaManager() || dur.isComunicaPortal() ) && !dur.isPayroll())
   apps.push(Apps.COMUNICA);

  if(dur.isPayroll())
    apps.push(Apps.PAYROLL);

  if(dur.isDocumental())
    apps.push(Apps.DOCUMENTAL);

  if(dur.isTimecontrol())
    apps.push(Apps.TIMECONTROL);

  if(dur.isInvoice())
    apps.push(Apps.INVOICE);

  if(dur.isSaltra() && !dur.isPayroll() && !dur.isComunica()){
    apps.push(Apps.AON_SALTRA);
  }

  return apps;
};

export const AccountingMenu = [
  {
    title: "Mantenimiento de Apuntes.",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountEntryModuleTEDI",
  },
  {
    title: "Documentos Pendientes.",
    module: "aon_gwt_fiscal",
    entryPoint: "RawdocModule",
  },
  {
    title: "Cartera de cobros y pagos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Finance",
  },
  {
    title: "Extracto de cuenta.",
    module: "aon_gwt_fiscal",
    entryPoint: "StatementReportModule",
  },
  {
    title: "Cuenta de Explotación (P Y G).",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountOperatingReport",
  },
  {
    title: "Balance de Sumas y Saldos.",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountTrialBalanceReport",
  },
  {
    title: "Listado Diario de Movimientos.",
    module: "aon_gwt_fiscal",
    entryPoint: "JournalReportModule",
  },
  {
    title: "Listado Mayor de Cuentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "LedgerReportModule",
  },
  {
    title: "Balances de Cuentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountBalanceReport",
  },
  {
    title: "Panel de control de IVA.",
    module: "aon_gwt_fiscal",
    entryPoint: "VATReport",
  },
  {
    title: "Panel de control de IRPF.",
    module: "aon_gwt_fiscal",
    entryPoint: "IRPFReport",
  },
  {
    title: "Panel de Compras y Gastos / Ventas e Ingresos.",
    module: "aon_gwt_fiscal",
    entryPoint: "OperationReport",
  },
  {
    title: "Deposito de Cuentas (D2).",
    module: "aon_gwt_aio",
    entryPoint: "deposit",
  },
  {
    title: "Fichas de Amortización",
    initAction: "amortization_list",
  },
  {
    title: "Tabla de tipos de Amortización",
    initAction: "amortizationType_list",
  },
  {
    title: "Bienes Afectos o de Inversión",
    initAction: "investAsset_search",
  },
  {
    title: "Extractos Bancarios",
    initAction: "bankStatement_search",
  },
];

export const AccountingPortalMenu = [
  {
    title: "Extracto de cuenta.",
    module: "aon_gwt_fiscal",
    entryPoint: "StatementReportModule",
  },
  {
    title: "Cuenta de Explotación (P Y G).",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountOperatingReport",
  },
  {
    title: "Balance de Sumas y Saldos.",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountTrialBalanceReport",
  },
  {
    title: "Listado Diario de Movimientos.",
    module: "aon_gwt_fiscal",
    entryPoint: "JournalReportModule",
  },
  {
    title: "Listado Mayor de Cuentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "LedgerReportModule",
  },
  {
    title: "Balances de Cuentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "AccountBalanceReport",
  }
];


export const PayrollMenu = [
  GWT.EMPLOYEES,
  GWT.CONVENIOS,
  GWT.MAIN_CONTRATA,
  GWT.MODEL_111,
  GWT.MODEL_190,
  GWT.PAYROLL_CONFIG,
  GWT.PAYROLL_TRASH,
  {
    title: "Remesa Transferencia de Nóminas",
    initAction: "fbatch_search-Payroll",
  },
  {
    title: "Vencimientos de Nóminas",
    initAction: "finance_search-Payroll",
  },
];

export const ToolsMenu = [
  {
    title: "Creación de Empresas.",
    initAction: "newDomain_form-enterprise",
    parent: true,
  },
  {
    title: "Carga de datos desde ficheros Excel (Predefinidos)",
    module: "aon_gwt_aio",
    entryPoint: "import",
  },
  {
    title: "Gestión Plantillas para carga de datos",
    module: "aon_gwt_aio",
    entryPoint: "templates",
  },
  {
    title: "Carga de datos",
    content: "<aon-imports></aon-imports>",
  },
  {
    title: "Expedientes",
    content: "<aon-project-panel></aon-project-panel>",
  },
];

export const ArabaFiscalMenu = [
  {
    title: "Modelo 300 - IVA. Autoliquidación.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model303",
  },
  {
    title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model347",
  },
  {
    title:
      "Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model349",
  },
  {
    title: "Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model390HF",
  },
  {
    title:
      "Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model111",
  },
  {
    title:
      "Modelo 115-A - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model115",
  },
  {
    title:
      "Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model123",
  },
  {
    title:
      "Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model180",
  },
  {
    title:
      "Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model184",
  },
  {
    title:
      "Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model190",
  },
  {
    title:
      "Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model193",
  },
  {
    title: "SII - Suministro Inmediato de Información.",
    module: "aon_gwt_aio",
    entryPoint: "sii",
  },
];

export const GipuzkoaFiscalMenu = [
  {
    title: "Modelo 300/320 - IVA. Autoliquidación.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model303",
  },
  {
    title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model347",
  },
  {
    title:
      "Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model349",
  },
  {
    title: "Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model390HF",
  },
  {
    title:
      "Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model111",
  },
  {
    title:
      "Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model115",
  },
  {
    title:
      "Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model123",
  },
  {
    title:
      "Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model180",
  },
  {
    title:
      "Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model184",
  },
  {
    title:
      "Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model190",
  },
  {
    title:
      "Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model193",
  },
  {
    title: "SII - Suministro Inmediato de Información.",
    module: "aon_gwt_aio",
    entryPoint: "sii",
  },
];

export const BizkaiaFiscalMenu = [
  {
    title: "Modelo 303 - IVA. Autoliquidación.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model303",
  },
  {
    title: "Modelo 140 - Libro-registro de operaciones económicas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model140",
  },
  {
    title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model347",
  },
  {
    title:
      "Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model349",
  },
  {
    title: "Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model390HF",
  },
  {
    title:
      "Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model111",
  },
  {
    title:
      "Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model115",
  },
  {
    title:
      "Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model123",
  },
  {
    title:
      "Modelo 130 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación directa.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model130",
  },
  {
    title:
      "Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model180",
  },
  {
    title:
      "Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model184",
  },
  {
    title:
      "Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model190",
  },
  {
    title:
      "Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model193",
  },
  {
    title: "SII - Suministro Inmediato de Información.",
    module: "aon_gwt_aio",
    entryPoint: "sii",
  },
];

export const NavarraFiscalMenu = [
  {
    title: "Modelo F69 - IVA. Autoliquidación.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model303",
  },
  {
    title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model347",
  },
  {
    title:
      "Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model349",
  },
  {
    title:
      "Modelo 745/715 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model111",
  },
  {
    title:
      "Modelo 759/760 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model115",
  },
  {
    title:
      "Modelo 716 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model123",
  },
  {
    title:
      "Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model180",
  },
  {
    title:
      "Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model184",
  },
  {
    title:
      "Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model190",
  },
  {
    title:
      "Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model193",
  },
  {
    title: "SII - Suministro Inmediato de Información.",
    module: "aon_gwt_aio",
    entryPoint: "sii",
  },
];

export const AeatFiscalMenu = [
  {
    title: "Modelo 303 - IVA. Autoliquidación.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model303",
  },
  {
    title: "Modelo 347 - Declaración anual operaciones con terceras personas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model347",
  },
  {
    title:
      "Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model349",
  },
  {
    title: "Modelo 390 - Declaración resumen anual IVA.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model390",
  },
  {
    title:
      "Modelo 111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model111",
  },
  {
    title:
      "Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model115",
  },
  {
    title:
      "Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model123",
  },
  {
    title:
      "Modelo 130 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación directa.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model130",
  },
  {
    title:
      "Modelo 131 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación objetiva.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model131",
  },
  {
    title:
      "Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model180",
  },
  {
    title:
      "Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model184",
  },
  {
    title:
      "Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model190",
  },
  {
    title:
      "Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model193",
  },
  {
    title: "Modelo 202 - Impuesto Sociedades. Pago fraccionado.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model202",
  },
  {
    title: "Modelo 200 - Impuesto sobre Sociedades.",
    module: "aon_gwt_fiscal",
    entryPoint: "Model200",
  },
  {
    title: "SII - Suministro Inmediato de Información.",
    module: "aon_gwt_aio",
    entryPoint: "sii",
  },
];

export const Packs = {
  SUITE: {
    app: "pack_suite",
    icon: "aon_app",
    title: "Suite Completa",
    subtitle: " Pack Portal | Pack Cotización | Pack Tributación",
    color: "black",
    apps: [
      Apps.ACCOUNTING,
      Apps.FISCAL,
      Apps.PAYROLL,
      Apps.COMUNICA,
      Apps.DOCUMENTAL,
      Apps.TIMECONTROL,
      Apps.INVOICE,
      Apps.MESSENGER,
      Apps.NOTES,
    ],
    price: " ", // '295€/mes'
  },
  PORTAL: {
    app: "pack_portal",
    icon: AON_ICONS.AON_PORTAL,
    title: "Pack Portal",
    subtitle: " Documental | Horario | Facturas | Mensajería",
    color: "#002469",
    apps: [
      Apps.DOCUMENTAL,
      Apps.TIMECONTROL,
      Apps.INVOICE,
      Apps.MESSENGER,
      Apps.NOTES,
    ],
    price: " ", // '120€/mes'
  },
  PAYROLL: {
    app: "pack_payroll",
    icon: AON_ICONS.AON_PACK,
    title: "Pack Cotización",
    subtitle: " Laboral | Comunic@",
    color: "#002469",
    apps: [Apps.PAYROLL, Apps.COMUNICA, Apps.TIMECONTROL, Apps.NOTES],
    price: " ", // '120€/mes'
  },
  FISCAL_ACCOUNTING: {
    app: "pack_fiscal_accounting",
    icon: AON_ICONS.AON_PACK,
    title: "Pack Tributación",
    subtitle: " Fiscal | Contabilidad",
    color: "#002469",
    apps: [Apps.FISCAL, Apps.ACCOUNTING, Apps.NOTES],
    price: " ", // '120€/mes'
  },
};

export const KIT_DIGITAL_FACE = {
  app: "kit_digital_face",
  icon: AON_ICONS.AON_KIT_DIGITAL,
  title: "FACe",
  subtitle: " Factura Electrónica",
  color: "#002469",
  apps: [],
  price: " ",
};

export const KIT_DIGITAL_CRM = {
  app: "kit_digital_crm",
  icon: AON_ICONS.AON_KIT_DIGITAL,
  title: "CRM",
  subtitle: " Gestión de Clientes",
  color: "#002469",
  apps: [],
  price: " ",
};

export const KIT_DIGITAL_ERP = {
  app: "kit_digital_erp",
  icon: AON_ICONS.AON_KIT_DIGITAL,
  title: "ERP",
  subtitle: " Gestión de Procesos",
  color: "#002469",
  apps: [],
  price: " ",
};

export const BASIC_MANAGEMENT = {
  app: "basic_management",
  icon: AON_ICONS.AON_MANAGEMENT,
  title: "Básico",
  subtitle: " Facturas",
  color: "#002469",
  apps: [Apps.INVOICE],
  price: " ",
};

export const STANDAR_MANAGEMENT = {
  app: "standar_management",
  icon: AON_ICONS.AON_MANAGEMENT,
  title: "Estándar",
  subtitle: " Gestión Básica | Comercial | Tesoreria ",
  color: "#002469",
  apps: [Apps.INVOICE, BASIC_MANAGEMENT],
  price: " ",
};

export const PROFESSIONAL_MANAGEMENT = {
  app: "professional_management",
  icon: AON_ICONS.AON_MANAGEMENT,
  title: "Profesional",
  subtitle: " Gestión Estándar | Marketing | Expedientes | Almacén ",
  color: "#002469",
  apps: [Apps.INVOICE, BASIC_MANAGEMENT, STANDAR_MANAGEMENT, Apps.WAREHOUSE],
  price: " ",
};

export const ENTERPRISE = {
  app: "enterprise",
  icon: AON_ICONS.AON_ENTERPRISE,
  title: "Empresa",
  color: "black",
  domainType: true,
};



export const ACADEMY = {
  app: "academy",
  icon: AON_ICONS.AON_OFFICE,
  title: "Academia",
  color: "black",
  domainType: true,
};

export const GARAGE = {
  app: "garage",
  icon: AON_ICONS.AON_GTA,
  title: "Taller",
  color: "black",
  domainType: true,
};

export const COMMERCE = {
  app: CONSTANT.COMMERCE,
  icon: AON_ICONS.AON_COMMERCE,
  title: "Comercio",
  color: "black",
  domainType: true,
};

export const HOTEL = {
  app: "hotel",
  icon: AON_ICONS.AON_HOTEL,
  title: "Hotel",
  color: "black",
  domainType: true,
};

export default Apps;
