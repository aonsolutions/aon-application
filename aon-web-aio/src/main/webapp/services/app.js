import { AON_SYMBOLS, COLORS, MATERIAL_ICONS, MSG, CONSTANT, AON_ICONS } from "../environments/environments.js";

import * as GWT from "../gwt/gwt.js";
import * as LS from  "./localStorageService.js";
import * as HELP from '../modules/aon-site-help.js';

import * as DocumentalOptions from  "../modules/documental/DocumentalOptions.js";
import * as InvoiceOptions from  "../modules/invoice/InvoiceOptions.js";
import { DomainUserRoles } from "../models/DomainUserRoles.js";

export const TIMECONTROL = {
  app: CONSTANT.TIMECONTROL,
  title: MSG.TIMECONTROL,
  description: MSG.TIMECTRL,
  tag: MSG.TIMECONTROL,
  aonSymbol: AON_SYMBOLS.TIMER,
  symbol: MATERIAL_ICONS.ALARM,
  color: "var(--aonTimecontrol)",
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
  price: " ", 
  disabled: false,
  options: {
    menu: false,
    add: false,
    upload: false,
    stat: false,
  },
  is: (dur) => new DomainUserRoles(dur).isTimecontrol(),
  has: (dur) => new DomainUserRoles(dur).hasTimeControl(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const ACCOUNTING = {
  app: CONSTANT.ACCOUNTING,
  title: MSG.ACCOUNTING,
  description: MSG.ACCOUNTING,
  tag: MSG.ACCOUNTING,
  iconSize: '24px',
  aonSymbol: AON_SYMBOLS.CALCULATOR,
  symbol: MATERIAL_ICONS.CALCULATE,
  // logo: '/images/apps/aon-RRMM.png',
  color: "var(--aonAccounting)",
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
  price: " ", 
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: true,
  },
  is: (dur) => new DomainUserRoles(dur).isAccounting(),
  has: (dur) => new DomainUserRoles(dur).hasAccounting(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const FISCAL = {
  app: CONSTANT.FISCAL,
  title: MSG.FISCAL,
  description: MSG.FISCAL,
  tag: MSG.FISCAL,
  aonSymbol: AON_SYMBOLS.BUILDING_2,
  symbol: MATERIAL_ICONS.ACCOUNT_BALANCE,
  color: "var(--aonFiscal)",
  // newColor: "var(--aonFiscal)",
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
  price: " ", 
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: false,
  },
  is: (dur) => new DomainUserRoles(dur).isFiscal(),
  has: (dur) => new DomainUserRoles(dur).hasFiscal(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const PAYROLL = {
  app: CONSTANT.PAYROLL,
  title: MSG.PAYROLL,
  description: MSG.PAYROLL,
  tag: MSG.PAYROLL,
  aonSymbol: AON_SYMBOLS.USERS,
  symbol: MATERIAL_ICONS.GROUP,
  color: "var(--aonPayroll)",
  // newColor: "var(--aonPayroll)",
  backgroundColor: 'var(--aonPayrollBackground)',
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
  price: " ",
  options: {
    menu: true,
    add: false,
    upload: false,
    stat: true,
  },
  is: (dur) => new DomainUserRoles(dur).isPayroll(),
  has: (dur) => new DomainUserRoles(dur).hasPayroll(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const TREASURY = {
  app: CONSTANT.TREASURY,
  title: MSG.TREASURY,
  description: MSG.TREASURY,
  tag: MSG.TREASURY,
  aonSymbol: AON_SYMBOLS.EURO,
  symbol: MATERIAL_ICONS.EURO_SYMBOL,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: " ",
  disabled: false,
  is: (dur) => new DomainUserRoles(dur).isTreasury(),
  has: (dur) => new DomainUserRoles(dur).hasTreasury(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};


export const MARKETING = {
  app: CONSTANT.MARKETING,
  title: MSG.MARKETING,
  description: MSG.MARKETING,
  tag: MSG.MARKETING,
  aonSymbol: AON_SYMBOLS.TARGET,
  symbol: "ads_click",
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  disabled: false,
  is: (dur) => new DomainUserRoles(dur).isMarketing(),
  has: (dur) => new DomainUserRoles(dur).hasMarketing(),
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
  price: " ",
  disabled: false,
  is: (dur) => new DomainUserRoles(dur).isSeres(),
  has: (dur) => new DomainUserRoles(dur).hasSeres(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};



export const COMMERCIAL = {
  app: CONSTANT.COMMERCIAL,
  title: MSG.COMMERCIAL,
  description: MSG.COMMERCIAL,
  tag: MSG.COMMERCIAL,
  aonSymbol: AON_SYMBOLS.HEART_HANDSHAKE,
  symbol: MATERIAL_ICONS.HANDSHAKE,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: " ",
  disabled: false,
  is: (dur) => new DomainUserRoles(dur).isCommercial(),
  has: (dur) => new DomainUserRoles(dur).hasCommercial(), 
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const GROUPWARE = {
  app: 'groupware',
  title: MSG.EXPEDIENTS,
  description: MSG.EXPEDIENTS,
  tag: MSG.EXPEDIENTS,
  aonSymbol: AON_SYMBOLS.FILES,
  symbol: MATERIAL_ICONS.NOTE_STACK,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isGroupware(),
  has: (dur) => new DomainUserRoles(dur).hasGroupware(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const COMUNICA = {
  app: CONSTANT.COMUNICA,
  title: MSG.COMUNICA,
  description: MSG.COMUNICA,
  tag: MSG.COMUNICA,
  _icon: AON_ICONS.AON_COMUNICA,
  symbol: MATERIAL_ICONS.ALTERNATE_EMAIL,
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
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isComunica(),
  has: (dur) => new DomainUserRoles(dur).hasComunica(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const DOCUMENTAL = {
  app: CONSTANT.DOCUMENTAL,
  title: LS.isFutureTheme() ? MSG.MY_CLOUD : MSG.DOCUMENTARY,
  description: LS.isFutureTheme() ? MSG.MY_CLOUD : MSG.DOCUMENTARY,
  tag: LS.isFutureTheme() ? MSG.MY_CLOUD : MSG.DOCUMENTARY,
  aonSymbol: AON_SYMBOLS.FOLDER_OPEN,
  symbol: MATERIAL_ICONS.FOLDER_OPEN,
  color: "var(--aonDocumental)",
  // newColor: "var(--aonDocumental)",
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
  price: " ",
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: true,
    stat: false
  },
  is: (dur) => new DomainUserRoles(dur).isDocumental(),
  has: (dur) => new DomainUserRoles(dur).hasDocumental(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const WAREHOUSE = {
  app: CONSTANT.WAREHOUSE,
  title: MSG.WAREHOUSE,
  description: MSG.WAREHOUSE,
  tag: MSG.WAREHOUSE,
  aonSymbol: AON_SYMBOLS.SHOPPING_CART,
  symbol: MATERIAL_ICONS.TROLLEY,
 	color: "var(--aonTopMenuAvailable)",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  disabled: false,
  options: {
    menu: false,
    add: false,
    upload: true,
    stat: false,
  },
  is: (dur) => new DomainUserRoles(dur).isWarehouse(),
  has: (dur) => new DomainUserRoles(dur).hasWarehouse(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};


export const MANAGEMENT = {
  app: CONSTANT.MANAGEMENT,
  title: MSG.MANAGEMENT,
  description: MSG.MANAGEMENT,
  tag: MSG.MANAGEMENT,
  aonSymbol: AON_SYMBOLS.FILE_BAR_CHART,
  symbol: MATERIAL_ICONS.MONITORING,
  color: "#002469",
  backgroundColor: "rgba(0, 36, 105, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  disabled: false,
  options: {
    menu: false,
    add: false,
    upload: true,
    stat: false,
  },
  is: (dur) => new DomainUserRoles(dur).isManagement(),
  has: (dur) => new DomainUserRoles(dur).hasManagement(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};


export const INVOICE = {
  app: CONSTANT.INVOICE,
  title: MSG.BILLING, //MSG.INVOICES,
  description: MSG.BILLING,
  tag: MSG.INVOICES,
  aonSymbol: AON_SYMBOLS.FILE_BAR_CHART,
  symbol: MATERIAL_ICONS.MONITORING,
  color: "var(--aonInvoice)",
  // newColor: "var(--aonInvoice)", 
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
  price: " ",
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: true,
    stat: true,
  },
  is: (dur) => new DomainUserRoles(dur).isInvoice(),
  has: (dur) => new DomainUserRoles(dur).hasInvoice(), 
  getMenuOptions: (dur) => InvoiceOptions.getOptions(dur)
};

export const CONFIGURATION = {
  app: CONSTANT.CONFIGURATION,
  title: MSG.CONFIGURATION,
  description: MSG.CONFIGURATION,
  icon: AON_ICONS.AON_SETTINGS,
  aonSymbol: AON_SYMBOLS.SETTINGS,
  color: "black",
  hover: 'aonSidenavHover',
  backgroundColor: "rgba(0, 0, 0, .2)", 
  is: () => true,
  has: () => true, 
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur),
};

export const MESSENGER = {
  app: CONSTANT.MESSENGER,
  title: MSG.REQUESTS,
  description: MSG.REQUESTS,
  tag: MSG.REQUESTS,
  aonSymbol: AON_SYMBOLS.SUBTITLES,
  symbol: MATERIAL_ICONS.SPEAKER_NOTES,
  color: "var(--aonMessenger)",
  // newColor: "va    r(--aonMessenger)",
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
  price: " ",
  disabled: false,
  options: {
    menu: false,
    add: true,
    upload: false,
    stat: true,
  },
  is: (dur) => new DomainUserRoles(dur).isMessenger(),
  has: (dur) => new DomainUserRoles(dur).hasMessenger(), 
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const TOOLS = {
  app: CONSTANT.TOOLS,
  title: MSG.TOOLS,
  icon: AON_ICONS.AON_TOOLS,
  color: "#535353",
  backgroundColor: "rgba(83, 83, 83, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  is: () => false,
  has: () => false, 
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const NOTES = {
  app: "note",
  title: MSG.NOTES,
  description: MSG.NOTES,
  icon: LS.isNewTheme() ? AON_ICONS.AON_NEW_NOTES : AON_ICONS.AON_NOTES,
  aonSymbol: AON_SYMBOLS.BOOKMARK,
  symbol: MATERIAL_ICONS.PINBOARD,
  newIcon: AON_ICONS.AON_NEW_NOTES,
  color: "var(--aonNote)",
  backgroundColor: "rgba(255, 192, 0, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  is: () => true,
  has: () => true, 
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const AON_SOLUTIONS = {
  app: "aio",
  title: "aonSolutions",
  description: "El único software fiscal, laboral y contable 100% en la nube.",

  icon: AON_ICONS.AON_APP,
  color: "var(--aonBlue)", 
  logo: "../assets/aon.png",
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isAon(),
  has: (dur) => new DomainUserRoles(dur).hasAon(),
};

export const BIDOQ = {
  app: "bidoq",
  title: "Bidoq",
  description: "Bidoq.",
  logo: "../assets/apps/bidoq.png",
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isBidoq(),
  has: (dur) => new DomainUserRoles(dur).hasBidoq(),
};

export const SELFCONTA = {
  app: "selfconta",
  title: "Selfconta",
  description: "Selfconta.",
  logo: "../assets/apps/selfconta.png",
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isSelfconta(),
  has: (dur) => new DomainUserRoles(dur).hasSelfconta(),
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
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isSaltra(),
  has: (dur) => new DomainUserRoles(dur).hasSaltra(),
  getMenuOptions: (dur) => DocumentalOptions.getOptions(dur)
};

export const AUTOBOOKING = {
  app: "autobooking",
  title: MSG.AUTOBOOKING,
  description: MSG.AUTOBOOKING,
  icon: AON_ICONS.AON_APP,
  color: "var(--aonBlue)", 
  logo: "../assets/aon.png",
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isAutoBooking(),
  has: (dur) => new DomainUserRoles(dur).hasAutoBooking(),
};

export const ALMA = {
  app: "alma",
  title: "Alma",
  description: "Alma",
  logo: "../assets/apps/alma.png",
  price: " ",
  is: () => false,
  has: () => false, 
};

export const OCR = {
  app: "ocr",
  title: "Asistente Reconocimiento de Facturas",
  description: "Gestor OCR.",
  icon: AON_ICONS.AON_OCR,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: " ",
  is: (dur) => new DomainUserRoles(dur).isOcr(),
  has: (dur) => new DomainUserRoles(dur).hasOcr(),
};


export const INVOFOX = {
  app: "invofox",
  title: "OCR",
  description: "Gestor OCR",
  // Not working.. why?
  // icon: AON_ICONS.AON_DOCUMENT_SCANNER,
  // newIcon: AON_ICONS.AON_DOCUMENT_SCANNER,
  logo: "../assets/img/document_scanner.png",
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isInvofox(),
  has: (dur) => new DomainUserRoles(dur).hasInvofox(),
};

export const CONVENIOS = {
  app: "convenios",
  title: "Convenios",
  description: "Convenios",
  icon: AON_ICONS.AON_CONVENIOS,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isConvenios(),
  has: (dur) => new DomainUserRoles(dur).hasConvenios(),
};

export const BANK = {
  app: "bank",
  title: "Bank",
  description: "Gestor de Bancos.",
  icon: AON_ICONS.AON_BANK,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
  is: (dur) => new DomainUserRoles(dur).isBank(),
  has: (dur) => new DomainUserRoles(dur).hasBank(),
};

export const CONSOLE = {
  app: CONSTANT.CONSOLE,
  icon: AON_ICONS.AON_APP,
  title: MSG.CONSOLE,
  description: MSG.CONSOLE,
  symbol: MATERIAL_ICONS.TERMINAL,
  color: COLORS.AON_BLACK,
  backgroundColor: "rgba(0, 0, 0, .2)",
  hover: 'aonSidenavHover',
  is: () => false,
  has: () => false,
};

export const OFFICE = {
  app: 'office',
  title: MSG.OFFICE, //MSG.INVOICES,
  description: MSG.OFFICE,
  tag: MSG.OFFICE,
  aonSymbol: AON_SYMBOLS.BRIEFCASE,
  symbol: MATERIAL_ICONS.BUSINESS_CENTER,
  // newColor: "var(--aonInvoice)", 
  backgroundColor: 'rgba(0, 0, 0, .2)',
  hover: 'sidenavHover',
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
  is: (dur) => new DomainUserRoles(dur).isOffice(),
  has: (dur) => new DomainUserRoles(dur).hasOffice() 
};

export const API_SERVICE = {
  app: "api_service",
  title: "Cuentas de Servicio | Acceso API",
  description: "Cuentas de Servicio | Acceso API.",
  icon: AON_ICONS.AON_APP,
  color: "#EA6D41",
  backgroundColor: "rgba(234, 109, 65, .2)",
  hover: 'aonSidenavHover',
  price: " ", 
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
  price: " ", 
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
  is: (dur) => new DomainUserRoles(dur).hasCustomView(),
  has: (dur) => new DomainUserRoles(dur).hasCustomView(),
  
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

export const EmployeeAonApps = [
  DOCUMENTAL,
  TIMECONTROL,
  PAYROLL,
  MESSENGER,
  INVOICE
];

export const EmployeeApps = [
  "documental",
  "timecontrol",
  "payroll",
  "messenger",
  "invoice",
];

export const EnterpriseAonApps = [
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  DOCUMENTAL,
  INVOICE,
  TIMECONTROL,
  MANAGEMENT,
  MESSENGER,
  INVOFOX,
  OCR,
  AON_SOLUTIONS,
  BIDOQ,
  SELFCONTA,
  AON_SALTRA,
  BANK,
  OFFICE
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
  MARKETING
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
  MANAGEMENT,
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
  symbol: MATERIAL_ICONS.DICTIONARY,
  title: "Academia",
  description: MSG.ACADEMY,
  domainType: true,
};

export const GARAGE = {
  app: "garage",
  icon: AON_ICONS.AON_GTA,
  symbol: MATERIAL_ICONS.CAR_REPAIR,
  title: "Taller",
  description: MSG.GARAGE,
  domainType: true,
};

export const COMMERCE = {
  app: CONSTANT.COMMERCE,
  icon: AON_ICONS.AON_COMMERCE,
  symbol: MATERIAL_ICONS.POINT_OF_SALE,
  title: "Comercio",
  description: MSG.COMMERCE,
  domainType: true,
};

export const HOTEL = {
  app: "hotel",
  icon: AON_ICONS.AON_HOTEL,
  title: "Hotel",
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
  price: " ", 
};

export const PACK_PAYROLL = {
  app: "pack_payroll",
  icon: AON_ICONS.AON_PACK,
  title: "Pack Cotización",
  subtitle: " Laboral | Comunic@",
  color: "#002469",
  apps: [Apps.PAYROLL, Apps.COMUNICA],
  price: " ", 
};

export const PACK_FISCAL_ACCOUNTING = {
  app: "pack_fiscal_accounting",
  icon: AON_ICONS.AON_PACK,
  title: "Pack Tributación",
  subtitle: " Fiscal | Contabilidad",
  color: "var(--aonAccounting)",
  apps: [Apps.FISCAL, Apps.ACCOUNTING],
  price: " ", 
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
  price: " ", 
};

export const Packs = {
  SUITE: PACK_SUITE,
  PORTAL: PACK_PORTAL,
  PAYROLL: PACK_PAYROLL,
  FISCAL_ACCOUNTING: PACK_FISCAL_ACCOUNTING
};

export const AuxApps = { TOOLS };
export const ClassicApps = { AON_SOLUTIONS, BIDOQ, SELFCONTA, AON_SALTRA };
export const Services = { INVOFOX, OCR, CONVENIOS, BANK, AULA, API_SERVICE };
export const ConsoleServices = { INVOFOX, OCR, CONVENIOS, BANK, AULA, SERES, API_SERVICE, CUSTOM_VIEW, AUTOBOOKING };

export const AllAonApps = [
  OFFICE,
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
  ];
  
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


export const NEW = {
	home : true,
	title: MSG.NEW,
	description: MSG.NEW,
	app: CONSTANT.NEW,
  aonSymbol: AON_SYMBOLS.PLUS_CIRCLE,
	symbol: MATERIAL_ICONS.ADD_CIRCLE_OUTLINE,
};

export const NOTIFICATION = {
	app: "notification",
  aonSymbol: AON_SYMBOLS.BELL,
  symbol: MATERIAL_ICONS.NOTIFICATIONS,
	title: MSG.NOTIFICATIONS,
	subtitle: "Notification",
}

export const HOME = {
	home : true,
	title: MSG.HOME,
	description: MSG.HOME,
	app: CONSTANT.HOME,
  aonSymbol: AON_SYMBOLS.HOME,
	symbol: MATERIAL_ICONS.HOME,
};

export const APPS = {
	home : true,
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	description: MSG.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPS,
};

export const APPLICATIONS = {
	home : true,
	title: MSG.APPLICATIONS,
	app: CONSTANT.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPLICATIONS,
};

export const AON_CLASSIC = {
	app: CONSTANT.AON_APPLICATION,
	title: MSG.CLASSIC_VIEW,
	description: MSG.CLASSIC_VIEW,
	logo: "../assets/aon.png",
};

export const SUPERSET = {
	app: CONSTANT.SUPERSET,
	title: MSG.SUPERSET,
	description: MSG.SUPERSET,
	logo: "../assets/apps/superset.png",
};

export const EXPAND_HIRIND = {
	app: CONSTANT.EXPAND_HIRIND,
	title: MSG.EXPAND_HIRIND,
	description: MSG.EXPAND_HIRIND,
  	aonSymbol: AON_SYMBOLS.BOX,
	symbol: MATERIAL_ICONS.STORE_MALL_DIRECTORY,
};

export const CONTENT_INDEX = {
	app: CONSTANT.CONTENT_INDEX,
	title: MSG.MANUALS,
	description: MSG.MANUALS,
	aonSymbol: AON_SYMBOLS.BOOK_OPEN,
	symbol: MATERIAL_ICONS.MENU_BOOK,
	goto: HELP.HELP_PORTAL_SITE,
};

export function getConstNewApps(dur, isAyudaT) {
//  if((url.includes('ayudat') && (dur.isAdmin() || dur.isEnterprise)) || (url.includes('infoautonomos')) && (dur.isAdmin() || dur.isEnterprise)) {
  if((isAyudaT && (dur.isAdmin() || dur.isEnterprise()))) {
    return {
      app: CONSTANT.APPS,
      title: MSG.PLAN,
      description: MSG.PLAN,
      symbol: MATERIAL_ICONS.APPS
    }
  } else {
      return {
      app: CONSTANT.APPS,
      title: MSG.APPLICATIONS,
      description: MSG.APPLICATIONS,
      symbol: MATERIAL_ICONS.APPS
    }
  }
};

export const NEW_APPS = {
	app: CONSTANT.APPS,
	title: MSG.APPLICATIONS,
	description: MSG.APPLICATIONS,
	symbol: MATERIAL_ICONS.APPS
};

export const PLAN_APPS = {
	app: 'planApps',
	title: MSG.PLAN,
	description: MSG.PLAN,
  aonSymbol: AON_SYMBOLS.BOX,
	symbol: MATERIAL_ICONS.STORE_MALL_DIRECTORY
};

export const AON_APPS = [ 
	AON_SOLUTIONS, 
	BIDOQ, 
	SELFCONTA, 
	AON_SALTRA,
];

export const MENU_APPS = [
  NEW,
  NEW_APPS,
  INVOICE,
  DOCUMENTAL,
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  COMUNICA,
  TIMECONTROL,
  NOTES,
  MESSENGER,
  WAREHOUSE,
  CONTENT_INDEX,
  AON_CLASSIC,
];

export const DESKTOP_APPS = [
  INVOICE,
  DOCUMENTAL,
  ACCOUNTING,
  FISCAL,
  PAYROLL,
  TIMECONTROL,
  NOTES,
  MESSENGER,
  AON_CLASSIC,
];


/*
export const Apps = Apps;
export const AuxApps = AuxApps;
export const MenuApps = MenuApps;
*/
export const HomeApps = {
	HOME,
	APPS,
	APPLICATIONS
};


export const COMMERCIAL_MENU = {
	app: "comercialMenu",
  aonSymbol: AON_SYMBOLS.HEART_HANDSHAKE,
	symbol: MATERIAL_ICONS.HANDSHAKE,
	title: MSG.COMMERCIAL,
	description: MSG.COMMERCIAL,
	subtitle: "Comercial",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavCommercialButton",
	apps: [],
	price: " ",
};

export const MANAGEMENT_MENU = {
	app: "managementMenu",
  aonSymbol: AON_SYMBOLS.FILE_BAR_CHART,
	symbol: MATERIAL_ICONS.MONITORING,
	title: MSG.MANAGEMENT,
	description: MSG.MANAGEMENT,
	subtitle: "Management",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavManagementButton",
	apps: [],
	price: " ",
};

export const TREASURY_MENU = {
	app: "treasuryMenu",
  aonSymbol: AON_SYMBOLS.EURO,
	symbol: MATERIAL_ICONS.EURO_SYMBOL,
	title: MSG.TREASURY,
	description: MSG.TREASURY,
	subtitle: "Treasury",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavTreasuryButton",
	apps: [],
	price: " ",
};

export const WAREHOUSE_MENU = {
	app: "warehouseMenu",
  aonSymbol: AON_SYMBOLS.SHOPPING_CART,
	symbol: MATERIAL_ICONS.TROLLEY,
	title: MSG.WAREHOUSE,
	description: MSG.WAREHOUSE,
	subtitle: "Warehouse",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavWarehouseButton",
	apps: [],
	price: " ",
};

export const GROUPWARE_MENU = {
	app: "groupwareMenu",
  aonSymbol: AON_SYMBOLS.FILES,
	symbol: MATERIAL_ICONS.NOTE_STACK,
	title: MSG.GROUPWARE,
	description: MSG.GROUPWARE,
	subtitle: "Groupware",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavGroupwareButton",
	apps: [],
	price: " ",
};

export const ACCOUNTING_MENU = {
	app: "accountingMenu",
  aonSymbol: AON_SYMBOLS.CALCULATOR,
	symbol: MATERIAL_ICONS.CALCULATE,
	title: MSG.ACCOUNTING,
	description: MSG.ACCOUNTING,
	subtitle: "Accounting",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavAccountingButton",
	apps: [],
	price: " ",
};

export const FISCAL_MENU = {
	app: "fiscalMenu",
  aonSymbol: AON_SYMBOLS.BUILDING_2,
	symbol: MATERIAL_ICONS.ACCOUNT_BALANCE,
	title: MSG.FISCAL,
	description: MSG.FISCAL,
	subtitle: "Fiscal",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavFiscalButton",
	apps: [],
	price: " ",
};

export const PAYROLL_MENU = {
	app: "payrollMenu",
  aonSymbol: AON_SYMBOLS.USERS,
	symbol: "group",
	title: MSG.PAYROLL,
	description: MSG.PAYROLL,
	subtitle: "Payroll",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavPayrollButton",
	apps: [],
	price: " ",
};

export const MARKETING_MENU = {
	app: "marketingMenu",
  aonSymbol: AON_SYMBOLS.TARGET,
	symbol: "ads_click",
	title: MSG.MARKETING,
	description: MSG.MARKETING,
	subtitle: "Marketing",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavMarketingButton",
	apps: [],
	price: " ",
};

export const CONFIGURATION_MENU = {
	app: "configurationMenu",
  aonSymbol: AON_SYMBOLS.SETTINGS_2,
	symbol: "construction",
	title: "Parámetros",
	description: "Parámetros",
	subtitle: "Parámetros",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavConfigurationButton",
	apps: [],
	price: " ",
};


export const ENTERPRISE_MENU = {
	app: "enterpriseMenu",
  aonSymbol: AON_SYMBOLS.BUILDING,
	symbol: "domain",
	title: MSG.ENTERPRISES,
	description: MSG.ENTERPRISES,
	subtitle: "Empresas",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavEnterpriseButton",
	apps: [],
	price: " ",
};

export const CONSOLE_MENU = {
	app: "consoleMenu",
  aonSymbol: AON_SYMBOLS.TERMINAL,
	symbol: "construction",
	title: MSG.CONFIGURATION,
	description: MSG.CONFIGURATION,
	subtitle: "Configuration",
	color: "var(--aonTopMenuAvailable)",
	style: "aonTopNavConfigurationButton",
	apps: [],
	price: " ",
};

export const TOP_MENU_APPS = [
	OFFICE,
	GARAGE,
	ACADEMY,
	COMMERCE,

	ENTERPRISE_MENU,
	COMMERCIAL_MENU,
	MANAGEMENT_MENU,
	TREASURY_MENU,
	WAREHOUSE_MENU,
	GROUPWARE_MENU,
	ACCOUNTING_MENU,
	FISCAL_MENU,
	PAYROLL_MENU,
	MARKETING_MENU,
	CONFIGURATION_MENU,

	CONSOLE_MENU,
];

export const TOP_MENU_APPS_HOME = [
  	HOME,
	OFFICE,
	GARAGE,
	ACADEMY,
	COMMERCE,

	ENTERPRISE_MENU,
	COMMERCIAL_MENU,
	MANAGEMENT_MENU,
	TREASURY_MENU,
	WAREHOUSE_MENU,
	GROUPWARE_MENU,
	ACCOUNTING_MENU,
	FISCAL_MENU,
	PAYROLL_MENU,
	MARKETING_MENU,
	CONFIGURATION_MENU,
	
	CONSOLE,
	CONSOLE_MENU,
];

export default Apps;
