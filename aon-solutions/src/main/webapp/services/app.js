import * as MSG from "../environments/msg.js";

export const MenuApps = ['accounting', 'fiscal', 'payroll', 'comunica', 'documental', 'invoice', 'timecontrol', 'management', 'tools'];
export const EmployeeApps = ['documental', 'timecontrol', 'payroll', 'messenger'];
export const EnterpriseApps = ['accounting', 'fiscal', 'payroll', 'comunica', 'documental', 'invoice', 'timecontrol', 'management', 'messenger', 'ocr'];

export const TIMECONTROL = {
  app: 'timecontrol',
  title: 'Horario',
  description: 'Control de Horario.',
  icon: 'aon_app',
  color: '#9966cc',
  access: [{
      value: 'Asesor',
      name: 'Asesor'
    }, {
      value: 'Empresa',
      name: 'Empresa'
    }, {
      value: 'Empleado',
      name: 'Empleado'
    }],
  price: '-',
  disabled: true
}

export const Apps = {
  ACCOUNTING: {
    app: 'accounting',
    title: 'Contabilidad',
    description: 'Gestor de Contabilidad.',
    icon: 'aon_app',
    color: '#1FD8B9',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }],
    price: '75€/mes'
  },
  FISCAL: {
    app: 'fiscal',
    title: 'Fiscal',
    description: 'Gestor Fiscal.',
    icon: 'aon_app',
    color: '#1FBCE5',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }],
    price: '75€/mes'
  },
  PAYROLL: {
    app: 'payroll',
    title: 'Laboral',
    description: 'Gestor Laboral.',
    icon: 'aon_app',
    color: '#1F8CFF',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }, {
        value: 'Empleado',
        name: 'Empleado'
      }],
    price: '90€/mes'
  },
  COMUNICA: {
    app: 'comunica',
    title: 'Comunic@',
    description: 'Comunic@',
    icon: 'aon_app',
    color: '#6C75AB',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }, {
        value: 'Empleado',
        name: 'Empleado'
      }],
    apps: [TIMECONTROL],
    price: '60€/mes'
  },
  DOCUMENTAL: {
    app: 'documental',
    title: 'Documental',
    description: 'Gestor de Documentos.',
    icon: 'aon_app',
    color: '#6986BB',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }, {
        value: 'Empleado',
        name: 'Empleado'
      }],
    price: '-',
    disabled: true
  },
  TIMECONTROL,
  INVOICE: {
    app: 'invoice',
    title: MSG.AON_MSG_INVOICES,
    description: 'Gestion de Facturas.',
    icon: 'aon_app',
    color: '#B50061',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }, {
        value: 'Empleado',
        name: 'Empleado'
      }],
    price: '-'
    ,disabled: true
  },MESSENGER: {
    app: 'messenger',
    title: 'Mensajería',
    description: 'Mensajería',
    icon: 'aon_app',
    color: '#CADEFF',
    access: [{
        value: 'Asesor',
        name: 'Asesor'
      }, {
        value: 'Empresa',
        name: 'Empresa'
      }],
    price: '-',
    disabled: true
  }
}

export const AuxApps = {
  TOOLS: {
    app: 'tools',
    title: 'Herramientas',
    icon: 'aon_app',
    color: '#535353',
    price:'-'
  },
}

export const ClassicApps = {
  AON_SOLUTIONS: {
    app: 'aio',
    title: 'aonSolutions',
    description: 'aonSolutions.',
    logo: '../assets/apps/aon.png',
    price:'-'
  },
  BIDOQ: {
    app: 'bidoq',
    title: 'Bidoq',
    description: 'Bidoq.',
    logo: '../assets/apps/bidoq.png',
    price:'-'
  }
}

export const Services = {
  // ALMA: {
  //   app: 'alma',
  //   title: 'Alma',
  //   description: 'Alma',
  //   logo: '../assets/apps/alma.png',
  //   price: ''
  // },
  OCR: {
    app: 'ocr',
    title: 'OCR',
    description: 'Gestor OCR.',
    icon: 'aon_app',
    color: '#535353',
    price: 'Desde 45€/mes'
  },
  CONVENIOS: {
    app: 'convenios',
    title: 'Convenios',
    description: 'Convenios',
    icon: 'aon_app',
    color: '#329905',
    price: 'Desde 45€/mes'
  },
  BANK: {
    app: 'bank',
    title: 'Bank',
    description: 'Gestor de Bancos.',
    icon: 'aon_app',
    color: '#D38C5B',
    price: 'Desde 45€/mes'
  }
}

export const OtherServices = {
  FORMACION: {
    app: 'formacion',
    title: 'Formación',
    description: 'Formación.',
    icon: 'ayudat',
    color: '#7792d1',
    moreInfo: 'https://ayudatpymes.com/formacion/formacion-bonificada/',
    price:'-',
    disabled: true
  },
  ACELERA: {
    app: 'acelera',
    title: 'Acelera',
    description: 'Acelera.',
    icon: 'ayudat',
    color: '#ff5c41',
    moreInfo: 'https://ayudatpymes.com/despachos/aceleratudespacho/',
    price:'-',
    disabled: true
  },
  OUTSOURCING: {
    app: 'outsourcing',
    title: 'Outsourcing',
    description: 'Outsourcing.',
    icon: 'ayudat',
    color: '#535353',
    moreInfo: 'https://ayudatpymes.com/despachos/outsourcing/',
    price:'-',
    disabled: true
  }
}

export const getApp = (name) => {
  for(let key in Apps) {
    if(name.toLowerCase() === Apps[key].app){
      return Apps[key];
    }
  }

  for(let key in Services) {
    if(name.toLowerCase() === Services[key].app){
      return Services[key];
    }
  }

  for(let key in ClassicApps) {
    if(name.toLowerCase() === ClassicApps[key].app){
      return ClassicApps[key];
    }
  }

  for(let key in OtherServices) {
    if(name.toLowerCase() === OtherServices[key].app){
      return OtherServices[key];
    }
  }
  return undefined;
}

export const AccountingMenu =
  [{
    title:'Mantenimiento de Apuntes.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'AccountEntryModuleTEDI'
  },{
    title:'Documentos Pendientes.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'RawdocModule'
  },{
    title:'Cartera de cobros y pagos.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'Finance'
  }, {
    title: 'Extracto de cuenta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'StatementReportModule'
  },{
    title: 'Cuenta de Explotación (P Y G).',
    module: 'aon_gwt_fiscal',
    entryPoint:'AccountOperatingReport'
  },{
    title: 'Balance de Sumas y Saldos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'AccountTrialBalanceReport'
  },{
    title: 'Listado Diario de Movimientos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'JournalReportModule'
  },{
    title: 'Listado Mayor de Cuentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'LedgerReportModule'
  },{
    title: 'Balances de Cuentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'AccountBalanceReport'
  },{
    title: 'Panel de control de IVA.',
    module: 'aon_gwt_fiscal',
    entryPoint:'VATReport'
  },{
    title: 'Panel de control de IRPF.',
    module: 'aon_gwt_fiscal',
    entryPoint:'IRPFReport'
  },{
    title: 'Panel de Compras y Gastos / Ventas e Ingresos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'OperationReport'
  },{
    title: 'Deposito de Cuentas (D2).',
    module: 'aon_gwt_aio',
    entryPoint:'deposit'
  },{
    title: 'Fichas de Amortización',
    initAction: 'amortization_list'
  },{
    title: 'Tabla de tipos de Amortización',
    initAction: 'amortizationType_list'
  },{
    title: 'Bienes Afectos o de Inversión',
    initAction: 'investAsset_search'
  },{
    title: 'Extractos Bancarios',
    initAction: 'bankStatement_search'
  }];

export const PayrollMenu =
  [{
    title:'Integral de Nóminas',
    module: 'aon_gwt_aio',
    entryPoint: 'employees'
  },{
    title: 'Convenios',
    module: 'aon_gwt_payroll',
    entryPoint:'MainAgreement'
  },{
    title: 'Modelo 111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model111'
  },{
    title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model190'
  },{
    title: 'Configuración. Globales',
    module: 'aon_gwt_payroll',
    entryPoint:'MainSystem'
  },{
    title: 'Papelera',
    module: 'aon_gwt_payroll',
    entryPoint:'MainTrash'
  },{
    title: 'Remesa Transferencia de Nóminas',
    initAction: 'fbatch_search-Payroll'
  },{
    title: 'Vencimientos de Nóminas',
    initAction: 'finance_search-Payroll'
  }];

  export const ToolsMenu =
    [{
      title:'Creación de Empresas.',
      initAction: 'newDomain_form-enterprise',
      parent: true
    },{
      title:'Gestion de Seguridad.',
      module: 'aon_gwt_aio',
      entryPoint: 'scope'
    },{
      title: 'Carga de datos desde ficheros Excel (Predefinidos)',
      module: 'aon_gwt_aio',
      entryPoint:'import',
    },{
      title: 'Gestión Plantillas para carga de datos',
      module: 'aon_gwt_aio',
      entryPoint:'templates',
    },{
      title: 'Carga de datos',
      content:'<aon-imports></aon-imports>',
    }];

export const ArabaFiscalMenu =
  [{
    title:'Modelo 300 - IVA. Autoliquidación.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'Model303'
  },{
    title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model347'
  },{
    title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model349'
  },{
    title: 'Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model390HF'
  },{
    title: 'Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model111'
  },{
    title: 'Modelo 115-A - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model115'
  },{
    title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model123'
  },{
    title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model180'
  },{
    title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model184'
  },{
    title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model190'
  },{
    title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model193'
  },{
    title: 'SII - Suministro Inmediato de Información.',
    module: 'aon_gwt_aio',
    entryPoint:'sii'
  }];

export const GipuzkoaFiscalMenu =
  [{
    title:'Modelo 300/320 - IVA. Autoliquidación.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'Model303'
  },{
    title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model347'
  },{
    title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model349'
  },{
    title: 'Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model390HF'
  },{
    title: 'Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model111'
  },{
    title: 'Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model115'
  },{
    title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model123'
  },{
    title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model180'
  },{
    title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model184'
  },{
    title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model190'
  },{
    title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model193'
  },{
    title: 'SII - Suministro Inmediato de Información.',
    module: 'aon_gwt_aio',
    entryPoint:'sii'
  }];

export const BizkaiaFiscalMenu =
  [{
    title:'Modelo 303 - IVA. Autoliquidación.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'Model303'
  },{
    title: 'Modelo 140 - Libro-registro de operaciones económicas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model140'
  },{
    title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model347'
  },{
    title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model349'
  },{
    title: 'Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model390HF'
  },{
    title: 'Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model111'
  },{
    title: 'Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model115'
  },{
    title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model123'
  },{
    title: 'Modelo 130 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación directa.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model130'
  },{
    title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model180'
  },{
    title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model184'
  },{
    title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model190'
  },{
    title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model193'
  },{
    title: 'SII - Suministro Inmediato de Información.',
    module: 'aon_gwt_aio',
    entryPoint:'sii'
  }];

export const NavarraFiscalMenu =
  [{
    title:'Modelo F69 - IVA. Autoliquidación.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'Model303'
  },{
    title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model347'
  },{
    title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model349'
  },{
    title: 'Modelo 745/715 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model111'
  },{
    title: 'Modelo 759/760 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model115'
  },{
    title: 'Modelo 716 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model123'
  },{
    title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model180'
  },{
    title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model184'
  },{
    title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model190'
  },{
    title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model193'
  },{
    title: 'SII - Suministro Inmediato de Información.',
    module: 'aon_gwt_aio',
    entryPoint:'sii'
  }];

export const AeatFiscalMenu =
  [{
    title:'Modelo 303 - IVA. Autoliquidación.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'Model303'
  },{
    title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model347'
  },{
    title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model349'
  },{
    title: 'Modelo 390 - Declaración resumen anual IVA.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model390'
  },{
    title: 'Modelo 111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model111'
  },{
    title: 'Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model115'
  },{
    title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model123'
  },{
    title: 'Modelo 130 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación directa.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model130'
  },{
    title: 'Modelo 131 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación objetiva.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model131'
  },{
    title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model180'
  },{
    title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model184'
  },{
    title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model190'
  },{
    title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model193'
  },{
    title: 'Modelo 202 - Impuesto Sociedades. Pago fraccionado.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model202'
  },{
    title: 'Modelo 200 - Impuesto sobre Sociedades.',
    module: 'aon_gwt_fiscal',
    entryPoint:'Model200'
  },{
    title: 'SII - Suministro Inmediato de Información.',
    module: 'aon_gwt_aio',
    entryPoint:'sii'
  }];

  export const Packs = {
    SUITE: {
      app: 'pack_suite',
      icon: 'aon_app',
      title: 'Suite Completa',
      subtitle: ' Portal Asesor | Pack Cotización | Pack Tributación',
      color: 'black',
      apps: [Apps.ACCOUNTING, Apps.FISCAL, Apps.PAYROLL, Apps.COMUNICA, Apps.DOCUMENTAL, Apps.TIMECONTROL, Apps.INVOICE, Apps.MESSENGER],
      price: '295€/mes'
    },
    PORTAL: {
      app: 'pack_portal',
      icon: 'aon_app',
      title: 'Pack Portal',
      subtitle: ' Documental | Horario | Facturas | Mensajería',
      color: '#002469',
      apps: [Apps.DOCUMENTAL, Apps.TIMECONTROL, Apps.INVOICE, Apps.MESSENGER],
      price: '120€/mes'
    },
    PAYROLL: {
      app: 'pack_payroll',
      icon: 'aon_app',
      title: 'Pack Cotización',
      subtitle: ' Laboral | Comunic@ | Horario',
      color: '#002469',
      apps: [Apps.PAYROLL, Apps.COMUNICA, Apps.TIMECONTROL],
      price: '120€/mes'
    },
    FISCAL_ACCOUNTING: {
      app: 'pack_fiscal_accounting',
      icon: 'aon_app',
      title: 'Pack Tributación',
      subtitle: ' Fiscal | Contabilidad',
      color: '#002469',
      apps: [Apps.FISCAL, Apps.ACCOUNTING],
      price: '120€/mes'
    }
  };

export default Apps;
// String SQL1 = "alter table `domain_app` drop index IDX_UNQ_DOMAIN_APP;";
// String SQL2 = "alter table `user_app_role` drop index IDX_UNQ_USER_APP_ROLE;";
