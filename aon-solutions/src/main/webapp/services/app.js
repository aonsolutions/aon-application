import { MSG, CONSTANT, AON_ICONS, COLORS, MATERIAL_ICONS } from "../environments/environments.js";
import * as GWT from "../gwt/gwt.js";
import * as MSG_ES from "../environments/msg-es.js";
import * as LS from  "./localStorageService.js";

import * as DocumentalOptions from  "../modules/documental/DocumentalOptions.js";
import * as InvoiceOptions from  "../modules/invoice/InvoiceOptions.js";

export const TIMECONTROL = {
  app: CONSTANT.TIMECONTROL,
  title: MSG.TIMECONTROL,
  description: MSG.TIMECONTROL,
  tag: MSG_ES.TIMECONTROL,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_TIMECONTROL : AON_ICONS.AON_TIMECONTROL,
  color: "#D1C36D",
  backgroundColor: "rgba(209, 195, 109, .2)",
  hover: 'aonTimecontrolHover',
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
  price: "-", //  '-',
  disabled: false,
  options: {
    menu: false,
    add: false,
    upload: false,
    stat: false,
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const ACCOUNTING = {
  app: CONSTANT.ACCOUNTING,
  title: MSG.ACCOUNTING,
  description: MSG.ACCOUNTING,
  tag: MSG_ES.ACCOUNTING,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_ACCOUNTING : AON_ICONS.AON_ACCOUNTING,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonAccountingHover',
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
  price: "60€/mes", // '60€/mes',
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: true,
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const FISCAL = {
  app: CONSTANT.FISCAL,
  title: MSG.FISCAL,
  description: MSG.FISCAL,
  tag: MSG_ES.FISCAL,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_FISCAL : AON_ICONS.AON_FISCAL,
  color: LS.isNewTheme() ? "#FB982E" : "#002469",
  backgroundColor: '#fef3e7',
  hover: 'aonFiscalHover',
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
  price: "60€/mes", // '60€/mes',
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: false,
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const PAYROLL = {
  app: CONSTANT.PAYROLL,
  title: MSG.PAYROLL,
  description: MSG.PAYROLL,
  tag: MSG.PAYROLL,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_PAYROLL :AON_ICONS.AON_PAYROLL,
  color: LS.isNewTheme() ? "#33A9A9" : "#002469",
  backgroundColor: '#e8f5f5',
  hover: 'aonPayrollHover',
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
  price: "90€/mes", // '90€/mes',
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: true,
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const TREASURY = {
  app: CONSTANT.TREASURY,
  title: MSG.TREASURY,
  description: MSG.TREASURY,
  tag: MSG.TREASURY,
  icon: AON_ICONS.AON_TREASURY,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: "90€/mes", // '90€/mes',
  disabled: false,
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};


export const MARKETING = {
  app: CONSTANT.MARKETING,
  title: MSG.MARKETING,
  description: MSG.MARKETING,
  tag: MSG.MARKETING,
  icon: AON_ICONS.AON_MARKETING,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: "90€/mes", // '90€/mes',
  disabled: false,
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const SERES = {
  app: CONSTANT.SERES,
  title: MSG.SERES,
  description: MSG.SERES,
  tag: MSG.SERES,
  icon: AON_ICONS.SERES,
  color: "#0000ff",
  backgroundColor: "rgba(0, 0, 255, .2)",
  hover: 'aonSidenavHover',
  price: "90€/mes", // '90€/mes',
  disabled: false,
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};



export const COMMERCIAL = {
  app: CONSTANT.COMMERCIAL,
  title: MSG.COMMERCIAL,
  description: MSG.COMMERCIAL,
  tag: MSG.COMMERCIAL,
  icon: AON_ICONS.AON_COMMERCIAL,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: "90€/mes", // '90€/mes',
  disabled: false,
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const GROUPWARE = {
  app: 'groupware',
  title: 'Expedientes',
  description: 'Expedientes',
  tag: 'Expedientes',
  icon: AON_ICONS.AON_APP,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: "90€/mes", // '90€/mes',
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const COMUNICA = {
  app: CONSTANT.COMUNICA,
  title: MSG.COMUNICA,
  description: MSG.COMUNICA,
  tag: MSG_ES.COMUNICA,
  icon: AON_ICONS.AON_COMUNICA,
  color: "var(--aonPayroll)",
  backgroundColor: "var(--aonPayrollBackground)",
  hover: 'aonPayrollHover',
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
  price: "60€/mes", // '60€/mes'
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const DOCUMENTAL = {
  app: CONSTANT.DOCUMENTAL,
  title: MSG.DOCUMENTARY,
  description: MSG.DOCUMENTARY,
  tag: MSG_ES.DOCUMENTARY,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_DOCUMENTAL : AON_ICONS.AON_DOCUMENTAL,
  color: LS.isNewTheme() ? "var(--aonDocumental)" : "#6986BB",
  backgroundColor: 'var(--aonDocumentalBackground)',
  hover: 'aonDocumentalHover',
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
  price: "-", // '-',
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: true,
    stat: false
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const WAREHOUSE = {
  app: CONSTANT.WAREHOUSE,
  title: MSG.WAREHOUSE,
  description: MSG.WAREHOUSE,
  tag: MSG_ES.WAREHOUSE,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_WAREHOUSE : AON_ICONS.AON_WAREHOUSE,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: "-", // '-',
  disabled: false,
  options: {
    menu: false,
    add: false,
    upload: true,
    stat: false,
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};


export const INVOICE = {
  app: CONSTANT.INVOICE,
  title: MSG.INVOICES,
  description: MSG.INVOICES,
  tag: MSG_ES.INVOICES,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_INVOICE : AON_ICONS.AON_INVOICE,
  color: LS.isNewTheme() ? "#4F91FF" : "#4472C4",
  backgroundColor: '#ebf2ff',
  hover: 'aonInvoiceHover',
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
  price: "-", // '-',
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: true,
    stat: true,
  },
  getMenuOptions: (dur) => InvoiceOptions.getOptions(dur)
};

export const CONFIGURATION = {
  app: CONSTANT.CONFIGURATION,
  title: MSG.CONFIGURATION,
  description: MSG.CONFIGURATION,
  icon: AON_ICONS.AON_SETTINGS,
  color: "black",
  hover: 'aonSidenavHover',
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur),
  backgroundColor: "rgba(0, 0, 0, .2)",
};

export const MESSENGER = {
  app: CONSTANT.MESSENGER,
  title: MSG.REQUESTS,
  description: MSG.REQUESTS,
  tag: MSG_ES.REQUESTS,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_MESSENGER : AON_ICONS.AON_MESSENGER,
  color: LS.isNewTheme() ? "#f6655a" : "#1fd8b9",
  backgroundColor: '#feedec',
  hover: 'aonMessengerHover',
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
  price: "-", // '-',
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: false,
    stat: true,
  },
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const CALENDAR = {
  app: CONSTANT.CALENDAR,
  title: "Calendario",
  icon: AON_ICONS.AON_CALENDAR,
  color: "#682627",
  backgroundColor: 'rgba(45, 45, 45, .2)',
  hover: 'aonSidenavHover',
  price: "-",
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const TOOLS = {
  app: CONSTANT.TOOLS,
  title: MSG.TOOLS,
  icon: AON_ICONS.AON_TOOLS,
  color: "#535353",
  backgroundColor: "rgba(83, 83, 83, .2)",
  hover: 'aonSidenavHover',
  price: "-", // '-'
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const NOTES = {
  app: "note",
  title: MSG.NOTES,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_NOTES : AON_ICONS.AON_NOTES,
  color: "#ffc000",
  backgroundColor: "rgba(255, 192, 0, .2)",
  hover: 'aonSidenavHover',
  price: "-", // '-'
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const AON_SOLUTIONS = {
  app: "aio",
  title: "aonSolutions",
  description: "El único software fiscal, laboral y contable 100% en la nube.",

  icon: AON_ICONS.AON_APP,
  color: "var(--aonBlue)", 
  logo: "../assets/aon.png",
  price: "-", //  '-'
};

export const BIDOQ = {
  app: "bidoq",
  title: "Bidoq",
  description: "Bidoq.",
  logo: "../assets/apps/bidoq.png",
  price: "-", // '-'
};

export const SELFCONTA = {
  app: "selfconta",
  title: "Selfconta",
  description: "Selfconta.",
  logo: "../assets/apps/selfconta.png",
  price: "-", // '-'
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
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: "-", // '-'
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
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
  title: "OCR | Asistente Registro Facturas",
  description: "Gestor OCR.",
  icon: AON_ICONS.AON_OCR,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: "Desde 45€/mes", // 'Desde 45€/mes'
};


export const INVOFOX = {
  app: "invofox",
  title: "OCR | INVOFOX",
  description: "Gestor OCR | INVOFOX.",
  icon: AON_ICONS.AON_OCR,
  icon: 'invofox',
  color: "#35A86C",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: "Desde 45€/mes", // 'Desde 45€/mes'
};

export const CONVENIOS = {
  app: "convenios",
  title: "Convenios",
  description: "Convenios",
  icon: AON_ICONS.AON_CONVENIOS,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: "Desde 45€/mes", // 'Desde 45€/mes'
};

export const BANK = {
  app: "bank",
  title: "Bank",
  description: "Gestor de Bancos.",
  icon: AON_ICONS.AON_BANK,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: "Desde 45€/mes", // 'Desde 45€/mes'
};

export const CONSOLE = {
  app: CONSTANT.CONSOLE,
  icon: AON_ICONS.AON_APP,
  title: MSG.CONSOLE,
  color: COLORS.AON_BLACK,
  backgroundColor: "rgba(0, 0, 0, .2)",
  hover: 'aonSidenavHover',
};

export const OFFICE = {
  app: "office",
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_OFFICE : AON_ICONS.AON_OFFICE,
  title: "Despacho",
  color: "black",
  backgroundColor: "rgba(0, 0, 0, .2)",
  hover: 'aonSidenavHover',
  domainType: true,
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const API_SERVICE = {
  app: "api_service",
  title: "Cuentas de Servicio | Acceso API",
  description: "Cuentas de Servicio | Acceso API.",
  icon: AON_ICONS.AON_APP,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: "Desde 45€/mes", // 'Desde 45€/mes'
};

export const AULA = {
  app: "aula",
  title: "Aula",
  description: "Aula.",
  icon: AON_ICONS.AON_AULA,
  color: "#000000",
  backgroundColor: "rgba(0, 0, 0, .2)",
  hover: 'aonSidenavHover',
  moreInfo: "https://faqs.aonsolutions.es/",
  price: "-", // '-',
  disabled: false,
};

export const CUSTOM_VIEW = {
  app: "custom_view",
  title: MSG.CUSTOM_VIEW,
  description: MSG.CUSTOM_VIEW,
  icon: "aon_custom",
  color: "#CAF509",
  backgroundColor: "rgba(202, 245, 9, .2)",
  hover: 'aonSidenavHover',
  price: " ",
};

export const MenuApps = {
  CONSOLE,
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
  // TREASURY,
  DOCUMENTAL,
  TIMECONTROL,
  MESSENGER,
  WAREHOUSE,
  MARKETING,
  // GROUPWARE,
  NOTES,
  TOOLS,
  CALENDAR
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
  "invofox",
  "ocr",
  "aio",
  "bidoq",
  "selfconta",
  "saltra",
  "bank"
];

export const Apps = {
  CONSOLE,
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
  MARKETING,
  CALENDAR
};

export const ConsultancyBookingApps = {
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  DOCUMENTAL,
  TIMECONTROL,
  INVOICE,
  MESSENGER
};

export const BookingApps = {
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  DOCUMENTAL,
  TIMECONTROL,
  MESSENGER,
  INVOICE,
  COMMERCIAL,
  MARKETING,
  TREASURY,
  GROUPWARE,
  WAREHOUSE
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
  title: "Gestión Básica",
  subtitle: " Facturas | Comercial",
  color: "#002469",
  apps: [Apps.INVOICE, COMMERCIAL],
  price: " ",
};

export const STANDAR_MANAGEMENT = {
  app: "standar_management",
  icon: AON_ICONS.AON_MANAGEMENT,
  title: "Gestión Estándar",
  subtitle: " Gestión Básica | Marketing | Tesoreria ",
  color: "#002469",
  apps: [Apps.INVOICE, COMMERCIAL, TREASURY, MARKETING],
  price: " ",
};

export const PROFESSIONAL_MANAGEMENT = {
  app: "professional_management",
  icon: AON_ICONS.AON_MANAGEMENT,
  title: "Gestión Profesional",
  subtitle: " Gestión Estándar | Expedientes | Almacén ",
  color: "#002469",
  apps: [Apps.INVOICE, COMMERCIAL, TREASURY, MARKETING, Apps.WAREHOUSE, GROUPWARE],
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
  icon: AON_ICONS.AON_ACADEMY,
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

export const PACK_PORTAL = {
  app: "pack_portal",
  icon: AON_ICONS.AON_PORTAL,
  title: "Pack Portal",
  subtitle: " Documental | Horario | Facturas | Mensajería",
  color: "#002469",
  apps: [
    Apps.DOCUMENTAL,
    Apps.TIMECONTROL,
    Apps.INVOICE,
    Apps.MESSENGER
  ],
  price: " 120€/mes", // '120€/mes'
};

export const PACK_PAYROLL = {
  app: "pack_payroll",
  icon: AON_ICONS.AON_PACK,
  title: "Pack Cotización",
  subtitle: " Laboral | Comunic@",
  color: "#002469",
  apps: [Apps.PAYROLL, Apps.COMUNICA],
  price: " 120€/mes", // '120€/mes'
};

export const PACK_FISCAL_ACCOUNTING = {
  app: "pack_fiscal_accounting",
  icon: AON_ICONS.AON_PACK,
  title: "Pack Tributación",
  subtitle: " Fiscal | Contabilidad",
  color: "#002469",
  apps: [Apps.FISCAL, Apps.ACCOUNTING],
  price: " 120€/mes", // '120€/mes'
};

export const PACK_SUITE = {
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
    PACK_FISCAL_ACCOUNTING,
    PACK_PAYROLL,
    PACK_PORTAL
  ],
  price: " ", // '295€/mes'
};

export const Packs = {
  SUITE: PACK_SUITE,
  PORTAL: PACK_PORTAL,
  PAYROLL: PACK_PAYROLL,
  FISCAL_ACCOUNTING: PACK_FISCAL_ACCOUNTING
};

export const AuxApps = { TOOLS };
export const ClassicApps = { AON_SOLUTIONS, BIDOQ, SELFCONTA, AON_SALTRA };
export const Services = { OCR, CONVENIOS, BANK, AULA, API_SERVICE };
export const ConsoleServices = { INVOFOX, OCR, CONVENIOS, BANK, AULA, SERES, API_SERVICE, CUSTOM_VIEW };

export const AllApps = {
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  DOCUMENTAL,
  TIMECONTROL,
  INVOICE,
  MESSENGER,
  COMMERCIAL,
  MARKETING,
  TREASURY,
  GROUPWARE,
  WAREHOUSE,
  OCR,
  INVOFOX,
  CONVENIOS,
  BANK,
  AON_SOLUTIONS,
  BIDOQ,
  SELFCONTA,
  AON_SALTRA
};

export const AllApps2 = {
  INVOICE,
  DOCUMENTAL,
  MESSENGER,
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  OCR,
  INVOFOX,
  AON_SOLUTIONS,
  ALMA,
  COMUNICA,
  BIDOQ,
  CONVENIOS,
  BANK,
  TIMECONTROL,
  //MANAGEMENT,
  PACK_SUITE,
  PACK_PORTAL,
  PACK_PAYROLL,
  PACK_FISCAL_ACCOUNTING,
  SELFCONTA,
  CUSTOM_VIEW,
  AULA,
  NOTES,
  AON_SALTRA,
  BASIC_MANAGEMENT,
  STANDAR_MANAGEMENT,
  PROFESSIONAL_MANAGEMENT,
  KIT_DIGITAL_FACE,
  KIT_DIGITAL_CRM,
  KIT_DIGITAL_ERP,
  API_SERVICE,
  WAREHOUSE,
  COMMERCIAL,
  MARKETING,
  TREASURY,
  GROUPWARE
};

export const getApp = (name) => {
  // for (let key in BookingApps) {
  //   if (name.toLowerCase() === BookingApps[key].app) {
  //     return BookingApps[key];
  //   }
  // }

  // for (let key in Services) {
  //   if (name.toLowerCase() === Services[key].app) {
  //     return Services[key];
  //   }
  // }

  // for (let key in ClassicApps) {
  //   if (name.toLowerCase() === ClassicApps[key].app) {
  //     return ClassicApps[key];
  //   }
  // }

  for (let key in AllApps2) {
    if (name.toLowerCase() === AllApps2[key].app) {
      return AllApps2[key];
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

export default Apps;
