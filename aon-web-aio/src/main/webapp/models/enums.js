import { MSG } from "../environments/environments.js"
import { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER  } from "../environments/msg.js"
import { DomainUserRoles } from "./DomainUserRoles.js"

export const ToolbarType = {
  APPLICATION: 'application',
  SECONDARY: 'secondary'
}

export const OldRole = {
	GUEST: "GUEST",								// Invitado
	ADMIN: "ADMIN",								// Administrador
	CONFIG: "CONFIG",							// Configuración
	AUDITOR: "AUDITOR",							// Auditor
	CONFIDENTIALITY: "CONFIDENTIALITY",			// Acceso a la función de confidencialidad.
	PRODUCT: "PRODUCT",							// Acceso a Productos
	COMMERCIAL: "COMMERCIAL",					// Acceso a Comercial
	SALE: "SALE",								// Acceso a Ventas
	PURCHASE: "PURCHASE",						// Acceso a Compras
	WAREHOUSE: "WAREHOUSE",						// Acceso a Almacén
	ACCOUNTING: "ACCOUNTING",					// Acceso a Contabilidad
	FINANCE: "FINANCE",							// Acceso a Facturación y Tesoreria
	STATISTICS: "STATISTICS",					// Acceso a Estadísticas
	TASK_MONITORING: "TASK_MONITORING",			// Monitor de Tareas.
	E_SIGNATURE: "E_SIGNATURE",					// Capacidad de firmar documentos electrónicos.
	SYS_ADMIN: "SYS_ADMIN",						// Capacidad de modificar las expresiones de las percepciones y deducciones.
	TGC: "TGC",									// Acceso a los informes de nominas.
	DOCUMENT: "DOCUMENT", 						// Acceso a los documentos.
	DOCUMENT_MANAGER: "DOCUMENT_MANAGER",	 	// Administrador documental.
	PAYROLL: "PAYROLL", 						// Capacidad de modificar las expresiones de las percepciones y deducciones.
	FISCAL: "FISCAL", 				 			// Acceso a los informes de nominas.
	ACCOUNTING_MANAGER: "ACCOUNTING_MANAGER", 	// Gestor de Contabilidad.
	CALL_CENTER: "CALL_CENTER", 				// Acceso al Call Center.
	CALL_CENTER_MANAGER: "CALL_CENTER_MANAGER"	// Administrador Call Center.
}

export const Role = {
  	ADMIN: 'ADMIN',
	ACCOUNTING: 'ACCOUNTING', 		            // ACCESO A CONTABILIDAD -  MODO PORTAL/EMPRESA
	ACCOUNTING_MANAGER: 'ACCOUNTING_MANAGER',	// ACCESO A CONTABILIDAD -  MODO ASESOR
	FISCAL: 'FISCAL',				            // ACCESO A FISCAL -  MODO PORTAL/EMPRESA
	FISCAL_MANAGER: 'FISCAL_MANAGER',		    // ACCESO A FISCAL -  MODO ASESOR
	PAYROLL: 'PAYROLL',			                // ACCESO A LABORAL -  MODO EMPLEADO
	PAYROLL_MANAGER: 'PAYROLL_MANAGER',	        // ACCESO A LABORAL - MODO ASESOR
	PAYROLL_PORTAL: 'PAYROLL_PORTAL',		    // ACCESO A LABORAL -  MODO PORTAL/EMPRESA
	DOCUMENTAL: 'DOCUMENTAL',			        // ACCESO A DOCUMENTAL -  MODO EMPLEADO
	DOCUMENTAL_PORTAL: 'DOCUMENTAL_PORTAL',	    // ACCESO A DOCUMENTAL -  MODO PORTAL/EMPRESA
	DOCUMENTAL_MANAGER: 'DOCUMENTAL_MANAGER',	// ACCESO A DOCUMENTAL -  MODO ASESOR
	COMUNICA: 'COMUNICA',			            // ACCESO A COMUNIC@ -  MODO EMPLEADO
	COMUNICA_MANAGER: 'COMUNICA_MANAGER',	    // ACCESO A COMUNIC@ -  MODO ASESOR
	COMUNICA_PORTAL: 'COMUNICA_PORTAL',	        // ACCESO A COMUNIC@ -  MODO PORTAL/EMPRESA
	TIMECONTROL: 'TIMECONTROL',		            // ACCESO A CONTROL DE HORARIO -  MODO EMPLEADO
	TIMECONTROL_MANAGER: 'TIMECONTROL_MANAGER', // ACCESO A CONTROL DE HORARIO -  MODO ASESOR
	TIMECONTROL_PORTAL: 'TIMECONTROL_PORTAL',   // ACCESO A CONTROL DE HORARIO -  MODO PORTAL/EMPRESA
	MESSENGER: 'MESSENGER',			            // ACCESO A MENSAJERIA -  MODO EMPLEADO
	MESSENGER_PORTAL: 'MESSENGER_PORTAL',		// ACCESO A MENSAJERIA -  MODO PORTAL/EMPRESA
	MESSENGER_MANAGER: 'MESSENGER_MANAGER',	    // ACCESO A MENSAJERIA -  MODO ASESOR
	INVOICE: 'INVOICE',			                // ACCESO A FACTURAS - MODO EMPLEADO
	INVOICE_MANAGER: 'INVOICE_MANAGER',	   		// ACCESO A FACTURAS - MODO ASESOR
	INVOICE_PORTAL: 'INVOICE_PORTAL',		    // ACCESO A FACTURAS - MODO PORTAL/EMPRESA
	MANAGEMENT: 'MANAGEMENT',			        // ACCESO A GESTION - MODO PORTAL/EMPRESA
	MANAGEMENT_MANAGER: 'MANAGEMENT_MANAGER',	// ACCESO A GESTION - MODO ASESOR
	ALMA: 'ALMA',			           	        // ACCESO AL SERVICIO ALMA
	OCR: 'OCR',				                    // ACCESO AL SERVICIO OCR
	INVOFOX: 'INVOFOX',							// ACCESO AL SERVICIO OCR INVOFOX
	BANK: 'BANK',				                // ACCESO AL SERVICIO BANK
	CONVENIOS: 'CONVENIOS',			            // ACCESO AL SERVICIO CONVENIOS
	AON: 'AON',
  	AIO: 'AIO',
	BIDOQ: 'BIDOQ',
  	EMPLOYEE: 'EMPLOYEE',	                  	// USUARIO TIPO EMPLEADO
  	ENTERPRISE: 'ENTERPRISE',		       	    // USUARIO TIPO EMPRESA
  	CONFIDENTIALITY: 'CONFIDENTIALITY',	        // USUARIO CON ACCESO A DATOS CONFIDENCIALES
	ALPHA: 'ALPHA',				                // USUARIO CON ACCESO A FUNCIONALIDADES ALPHA
	BETA: 'BETA',				                // USUARIO CON ACCESO A FUNCIONALIDADES BETA
  	AON_AIO: 'AON_AIO',							// ACCESO A AON AIO
  	AON_SMB: 'AON_SMB',							// ACCESO A AON SMB
  	DEV: 'DEV',									// USUARIO TIPO DESARROLLADOR.
	SELFCONTA: 'SELFCONTA',
	WAREHOUSE: 'WAREHOUSE',
	COMMERCIAL: 'COMMERCIAL',
	TREASURY: 'TREASURY',
	MARKETING: 'MARKETING',
	GROUPWARE: 'GROUPWARE',
	SERES: 'SERES',
	OFFICE: 'OFFICE',			                // ACCESO A DESPACHO - MODO EMPLEADO
	OFFICE_MANAGER: 'OFFICE_MANAGER',	   		// ACCESO A DESPACHO - MODO ASESOR
	OFFICE_PORTAL: 'OFFICE_PORTAL',		    	// ACCESO A DESPACHO - MODO PORTAL/EMPRESA
}

export const Roles = [
	{ value: Role.ADMIN, is: (dur) => new DomainUserRoles(dur).isAdmin() },
	{ value: Role.ACCOUNTING, is: (dur) => new DomainUserRoles(dur).isAccounting()},
	{ value: Role.ACCOUNTING_MANAGER, is: (dur) => new DomainUserRoles(dur).isAccountingManager()},
	{ value: Role.FISCAL, is: (dur) => new DomainUserRoles(dur).isFiscal()},
	{ value: Role.FISCAL_MANAGER, is: (dur) => new DomainUserRoles(dur).isFiscalManager()},
	{ value: Role.PAYROLL, is: (dur) => new DomainUserRoles(dur).isPayroll()},
	{ value: Role.PAYROLL_MANAGER, is: (dur) => new DomainUserRoles(dur).isPayrollManager()},
	{ value: Role.PAYROLL_PORTAL, is: (dur) => new DomainUserRoles(dur).isPayrollPortal()},
	{ value: Role.DOCUMENTAL, is: (dur) => new DomainUserRoles(dur).isDocumental()},
	{ value: Role.DOCUMENTAL_PORTAL, is: (dur) => new DomainUserRoles(dur).isDocumentalPortal()},
	{ value: Role.DOCUMENTAL_MANAGER, is: (dur) => new DomainUserRoles(dur).isDocumentalManager()},
	{ value: Role.COMUNICA, is: (dur) => new DomainUserRoles(dur).isComunica()},
	{ value: Role.COMUNICA_MANAGER, is: (dur) => new DomainUserRoles(dur).isComunicaManager()},
	{ value: Role.COMUNICA_PORTAL, is: (dur) => new DomainUserRoles(dur).isComunicaPortal()},
	{ value: Role.TIMECONTROL, is: (dur) => new DomainUserRoles(dur).isTimecontrol()},
	{ value: Role.TIMECONTROL_MANAGER, is: (dur) => new DomainUserRoles(dur).isTimecontrolManager()},
	{ value: Role.TIMECONTROL_PORTAL, is: (dur) => new DomainUserRoles(dur).isTimecontrolPortal()},
	{ value: Role.MESSENGER, is: (dur) => new DomainUserRoles(dur).isMessenger()},
	{ value: Role.MESSENGER_PORTAL, is: (dur) => new DomainUserRoles(dur).isMessengerPortal()},
	{ value: Role.MESSENGER_MANAGER, is: (dur) => new DomainUserRoles(dur).isMessengerManager()},
	{ value: Role.INVOICE, is: (dur) => new DomainUserRoles(dur).isInvoice()},
	{ value: Role.INVOICE_MANAGER, is: (dur) => new DomainUserRoles(dur).isInvoiceManager()},
	{ value: Role.INVOICE_PORTAL, is: (dur) => new DomainUserRoles(dur).isInvoicePortal()},
	{ value: Role.MANAGEMENT, is: (dur) => new DomainUserRoles(dur).isManagement()},
	{ value: Role.MANAGEMENT_MANAGER, is: (dur) => new DomainUserRoles(dur).isManagementManager()},
	{ value: Role.ALMA, is: (dur) => new DomainUserRoles(dur).isAlma()},
	{ value: Role.OCR, is: (dur) => new DomainUserRoles(dur).isOcr()},
	{ value: Role.INVOFOX, is: (dur) => new DomainUserRoles(dur).isInvofox()},
	{ value: Role.BANK, is: (dur) => new DomainUserRoles(dur).isBank()},
	{ value: Role.CONVENIOS, is: (dur) => new DomainUserRoles(dur).isConvenios()},
	{ value: Role.AON_AIO, is: (dur) => new DomainUserRoles(dur).isAon()},
	{ value: Role.BIDOQ, is: (dur) => new DomainUserRoles(dur).isBidoq()},
	{ value: Role.EMPLOYEE, is: (dur) => new DomainUserRoles(dur).isEmployee()},
	{ value: Role.ENTERPRISE, is: (dur) => new DomainUserRoles(dur).isEnterprise()},
	{ value: Role.CONFIDENTIALITY, is: (dur) => new DomainUserRoles(dur).isConfidential()},
	{ value: Role.ALPHA, is: (dur) => new DomainUserRoles(dur).isAlpha()},
	{ value: Role.BETA, is: (dur) => new DomainUserRoles(dur).isBeta()},
	{ value: Role.DEV, is: (dur) => new DomainUserRoles(dur).isDev()},
	{ value: Role.SELFCONTA, is: (dur) => new DomainUserRoles(dur).isSelfconta()},
	{ value: Role.WAREHOUSE, is: (dur) => new DomainUserRoles(dur).isWarehouse()},
	{ value: Role.COMMERCIAL, is: (dur) => new DomainUserRoles(dur).isCommercial()},
	{ value: Role.TREASURY, is: (dur) => new DomainUserRoles(dur).isTreasury()},
	{ value: Role.MARKETING, is: (dur) => new DomainUserRoles(dur).isMarketing()},
	{ value: Role.GROUPWARE, is: (dur) => new DomainUserRoles(dur).isGroupware()},
	{ value: Role.SERES, is: (dur) => new DomainUserRoles(dur).isSeres()},
	{ value: Role.OFFICE, is: (dur) => new DomainUserRoles(dur).isOffice()},
	{ value: Role.OFFICE_MANAGER, is: (dur) => new DomainUserRoles(dur).isOfficeManager()},
	{ value: Role.OFFICE_PORTAL, is: (dur) => new DomainUserRoles(dur).isOfficePortal()},
];

export const OldModule = {
	MARKETING: 'MARKETING',
	CRM: 'CRM',
	MANAGEMENT: 'MANAGEMENT',
	TREASURY: 'TREASURY',
	WAREHOUSE: 'WAREHOUSE',
	GROUPWARE: 'GROUPWARE',
	ACCOUNTING: 'ACCOUNTING',
	FISCAL: 'FISCAL',
	PAYROLL: 'PAYROLL',
	DOCUMENT: 'DOCUMENT',
	GARAGE: 'GARAGE',
	ACADEMY: 'ACADEMY',
	HOTEL: 'HOTEL',
	INFOWEB: 'INFOWEB',
	PAYROLL_PORTAL: 'PAYROLL_PORTAL',
	DOCUMENT_PORTAL: 'DOCUMENT_PORTAL',
	POS: 'POS',
	COMUNICA: 'COMUNICA',
	CONFIGURATION: 'CONFIGURATION',
	AON_ONE: 'AON_ONE',
	ECOMMERCE: 'ECOMMERCE',
	CALL_CENTER: 'CALL_CENTER',
	FINANCE_PORTAL: 'FINANCE_PORTAL',
	AON_FINANCE: 'AON_FINANCE',
	SUITE_PORTAL: 'SUITE_PORTAL'
}

export const App = {
  	INVOICE: 'INVOICE',
  	DOCUMENTAL: 'DOCUMENTAL',
  	MESSENGER: 'MESSENGER',
  	ACCOUNTING: 'ACCOUNTING',
  	FISCAL: 'FISCAL',
  	PAYROLL: 'PAYROLL',
 	OCR: 'OCR',
	INVOFOX: 'INVOFOX',
  	AIO: 'AIO',
  	ALMA: 'ALMA',
  	COMUNICA: 'COMUNICA',
  	BIDOQ: 'BIDOQ',
  	CONVENIOS: 'CONVENIOS',
  	BANK: 'BANK',
  	TIMECONTROL: 'TIMECONTROL',
  	MANAGEMENT: 'MANAGEMENT',
	PACK_SUITE: 'PACK_SUITE',
	PACK_PORTAL: 'PACK_PORTAL',
	PACK_PAYROLL: 'PACK_PAYROLL',
	PACK_FISCAL_ACCOUNTING: 'PACK_FISCAL_ACCOUNTING',
	SELFCONTA: 'SELFCONTA',
	CUSTOM_VIEW: 'CUSTOM_VIEW',
	BASIC_MANAGEMENT: 'BASIC_MANAGEMENT',
	STANDAR_MANAGEMENT: 'STANDAR_MANAGEMENT',
	PROFESSIONAL_MANAGEMENT: 'PROFESSIONAL_MANAGEMENT',
	API_SERVICE: 'API_SERVICE',
	WAREHOUSE: 'WAREHOUSE',
	COMMERCIAL: 'COMMERCIAL',
	MARKETING: 'MARKETING',
	TREASURY: 'TREASURY',
	GROUPWARE: 'GROUPWARE',
	SERES: 'SERES',
	AUTOBOOKING: 'AUTOBOOKING'
}

export const RegistryType = {
	CARRIER: 'CARRIER',
	COMPANY: 'COMPANY',
	CREDITOR: 'CREDITOR',
	CUSTOMER: 'CUSTOMER',
	ENTERPRISE: 'ENTERPRISE',
	PERSON: 'PERSON',
	SELLER: 'SELLER',
	SUPPLIER: 'SUPPLIER',
	TARGET: 'TARGET',
	TASK_HOLDER: 'TASK_HOLDER'
}

export const DAYS = [
	SUNDAY,
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY
];

export const DAYS_ABR =[
	"Dom",
	"Lun",
	"Mar",
	"Mie",
	"Jue",
	"Vie",
	"Sab"
];

export const MONTHS =[
	JANUARY,
	FEBRUARY,
	MARCH,
	APRIL,
	MAY,
	JUNE,
	JULY,
	AUGUST,
	SEPTEMBER,
	OCTOBER,
	NOVEMBER,
	DECEMBER
];

export const MONTHS_ABR =[
	"Ene",
	"Feb",
	"Mar",
	"Abr",
	"May",
	"Jun",
	"Jul",
	"Ago",
	"Sep",
	"Oct",
	"Nov",
	"Dic"
];

export const InvestAssetType = {
	PREMISES: {
		id: 'PREMISES',
		name: 'Local' // MSG.PREMISES
	},
	OTHER_BUILDING: {
		id: 'OTHER_BUILDING',
		name: 'Otros Inmuebles' //MSG.OTHER_BUILDING
	},
	MEANS_OF_TRANSPORT: {
		id: 'MEANS_OF_TRANSPORT',
		name: 'Medios de Transporte' // MSG.MEANS_OF_TRANSPORT
	},
	FIXED_PHONE: {
		id: 'FIXED_PHONE',
		name: 'Teléfono Fijo' // MSG.FIXED_PHONE
	},
	CELLULAR_PHONE: {
		id: 'CELLULAR_PHONE',
		name: 'Teléfono Móvil' // MSG.CELLULAR_PHONE
	},
	FAX: {
		id: 'FAX',
		name: 'Fax' // MSG.FAX
	},
	FURNITURE: {
		id: 'FURNITURE',
		name: 'Mobiliario' //MSG.FURNITURE
	},
	MACHINERY: {
		id: 'MACHINERY',
		name: 'Maquinaria' // MSG.MACHINERY
	},
	COMPUTER_EQUIPMENT: {
		id: 'COMPUTER_EQUIPMENT',
		name: 'Equipos Informáticos' // MSG.COMPUTER_EQUIPMENT
	},
	INSTALLATION: {
		id: 'INSTALLATION',
		name: 'Instalación' // MSG.INSTALLATION
	},
	ACCOUNT_GROUP_20_ASSET: {
		id: 'ACCOUNT_GROUP_20_ASSET',
		name: 'Bienes Grupo 20 PGC' //MSG.ACCOUNT_GROUP_20_ASSET		
	},
	ACCOUNT_GROUP_21_ASSET: {
		id: 'ACCOUNT_GROUP_21_ASSET',
		name: 'Bienes Grupo 21 PGC' //MSG.ACCOUNT_GROUP_21_ASSET
	},
	ACCOUNT_GROUP_23_ASSET: {
		id: 'ACCOUNT_GROUP_23_ASSET',
		name: 'Bienes Grupo 23 PGC' //MSG.ACCOUNT_GROUP_23_ASSET
	},
	BUILDING_PLOT: {
		id: 'BUILDING_PLOT',
		name: 'Solar' //MSG.BUILDING_PLOT
	}
}

export const InvestAssetRegime = {
	PROPERTY: {
		id: 'PROPERTY',
		name: 'Propiedad' // MSG.PROPERTY
	},
	RENTING: {
		id: 'RENTING',
		name: 'Alquiler' // MSG.RENTING
	},
	FINANCIAL_LEASING: {
		id: 'FINANCIAL_LEASING',
		name: 'Arrendamiento Financiero' // MSG.FINANCIAL_LEASING
	},
	OTHER: {
		id: 'OTHER',
		name: 'Otro' // MSG.OTHER
	}
}

export const InvestAssetRegimeOptions = [
	{value: 'PROPERTY', name: 'Propiedad'}, // MSG.PROPERTY },
	{value: 'RENTING', name: 'Alquiler'}, // MSG.RENTING },
	{value: 'FINANCIAL_LEASING', name: 'Arrendamiento Financiero'}, // MSG.FINANCIAL_LEASING},
	{value: 'OTHER', name: 'Otro'} // MSG.OTHER}
];

export const InvestAssetTypeOptions = [
	{value: 'PREMISES', name: 'Local'}, // MSG.PREMISES },
	{value: 'OTHER_BUILDING', name: 'Otros Inmuebles'}, //MSG.OTHER_BUILDING},
	{value: 'MEANS_OF_TRANSPORT', name: 'Medios de Transporte'}, // MSG.MEANS_OF_TRANSPORT},
	{value: 'FIXED_PHONE', name: 'Teléfono Fijo'}, // MSG.FIXED_PHONE},
	{value: 'CELLULAR_PHONE', name: 'Teléfono Móvil' },// MSG.CELLULAR_PHONE },
	{value: 'FAX', name: 'Fax' }, // MSG.FAX},
	{value: 'FURNITURE', name: 'Mobiliario' }, //MSG.FURNITURE },
	{value: 'MACHINERY', name: 'Maquinaria' },// MSG.MACHINERY},
	{value: 'COMPUTER_EQUIPMENT', name: 'Equipos Informáticos'}, // MSG.COMPUTER_EQUIPMENT},
	{value: 'INSTALLATION', name: 'Instalación'}, // MSG.INSTALLATION},
	{value: 'ACCOUNT_GROUP_20_ASSET', name: 'Bienes Grupo 20 PGC'}, //MSG.ACCOUNT_GROUP_20_ASSET},
	{value: 'ACCOUNT_GROUP_21_ASSET', name: 'Bienes Grupo 21 PGC'}, //MSG.ACCOUNT_GROUP_21_ASSET },
	{value: 'ACCOUNT_GROUP_23_ASSET', name: 'Bienes Grupo 23 PGC'}, //MSG.ACCOUNT_GROUP_23_ASSET},
	{value: 'BUILDING_PLOT', name: 'Solar'} //MSG.BUILDING_PLOT}
]

export const DomainType = {
	ENTERPRISE: "Empresa",
	CONSULTANCY: "Asesoría",
	GARAGE: "Garaje",
	ACADEMY: "Academia",
	HOTEL: "Hotel",
	ADMIN: "Administración",
	OFFICE: "Despacho",
	GENERIC: "Genérico",
	COMMERCE: "Comercio",
	KIT_DIGITAL: "Kit Digital"
}

export const RegistryItemStatus = {
	ACTIVE: 'Activo',
	INTERESTED: 'Interesado',
	REFUSED: 'Rechazado',
	INACTIVE: 'Inactivo'
}

export const BookingItemStatus = {
	BILLABLE: 'Facturable',
	NOT_BILLABLE: 'No facturable',
	NOT_CONTRACTABLE: 'No contratable',
	INACTIVE: 'Inactivo'
}

export const AonStatus = {
	BILLABLE: 'Facturable',
	NOT_BILLABLE: 'No facturable'
}

export const RegistryStatus = {
	ACTIVE: 'Activo',
	INACTIVE: 'Inactivo',
	BLOCKED: 'Bloqueado'
}

export const RegistrySellerStatus = {
	ACTIVE: 'Activo',
    INACTIVE: 'Inactivo'
}

export const RegistrySellerType = {
    COMERCIAL: 'Comercial',
    SOPORTE: 'Soporte'
}