export const Apps = {
    INVOICE: {
      app: 'invoice',
      title: 'Facturas',
      description: 'Gestor de Facturas.',
      icon: 'aon_app',
      color: '#A2A8B6'
    },
    DOCUMENTAL: {
      app: 'documental',
      title: 'Documental',
      description: 'Gestor de Documentos.',
      icon: 'aon_app',
      color: '#6986BB'
    },
    HELPDESK: {
      app: 'helpdesk',
      title: 'Help Desk',
      description: 'Gestor de Ayuda (Help Desk).',
      icon: 'aon_app',
      color: '#D38C5B'
    },
    ACCOUNTING: {
      app: 'accounting',
      title: 'Contabilidad',
      description: 'Gestor de Contabilidad.',
      icon: 'aon_app',
      color: '#D8B03D'
    },
    FISCAL: {
      app: 'fiscal',
      title: 'Fiscal',
      description: 'Gestor Fiscal.',
      icon: 'aon_app',
      color: '#3A85C3'
    },
    PAYROLL: {
      app: 'payroll',
      title: 'Laboral',
      description: 'Gestor Laboral.',
      icon: 'aon_app',
      color: '#90BD75'
    },
    OCR: {
      app: 'ocr',
      title: 'OCR',
      description: 'Gestor OCR.',
      icon: 'aon_app',
      color: '#535353'
    },
    AIO: {
      app: 'aio',
      title: 'AiO',
      description: 'AON SOLUTIONS AiO.',
      logo: 'assets/aon.png'
    },
    TOOLS: {
      app: 'tools',
      title: 'Herramientas',
      description: 'Utilidades Generales de Aon Solutions.',
      icon: 'aon_app',
      color: 'gray'
    },
    CONTRATA: {
      app: 'contrata',
      title: 'Contrat@',
      description: 'Contrat@',
      icon: 'aon_app',
      color: '#6C75AB'
    },
    PORTAL: {
      app: 'portal',
      title: 'Portal',
      description: 'Portal',
      icon: 'aon_app',
      color: '#002469'
    },
    MESSENGER: {
      app: 'messenger',
      title: 'Messenger',
      description: 'Messenger',
      icon: 'aon_app',
      color: '#CADEFF'
    },
    CONVENIOS: {
      app: 'serviconvenios',
      title: 'ServiConvenios',
      description: 'ServiConvenios',
      logo: 'assets/apps/serviconvenios.png',
      icon: 'aon_app',
      color: '#329905'
    }
};

export const AccountingMenu =
  [{
    title:'Mantenimiento de Apuntes.',
    module: 'aon_gwt_fiscal',
    entryPoint: 'AccountEntryModuleTEDI'
  },{
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

export default Apps;
// String SQL1 = "alter table `domain_app` drop index IDX_UNQ_DOMAIN_APP;";
// String SQL2 = "alter table `user_app_role` drop index IDX_UNQ_USER_APP_ROLE;";
