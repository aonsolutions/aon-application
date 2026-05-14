import * as MSG_EN from './msg-en.js';
import * as MSG_EUS from './msg-eus.js';
import * as MSG_CAT from './msg-cat.js';
import * as MSG_ES from './msg-es.js';
import * as MSG_GAL from './msg-gal.js';
import * as MSG_DE from './msg-de.js';
import * as MSG_FR from './msg-fr.js';
import { Language } from '../models/Language.js';

let language = localStorage.getItem('aon_language') || Language.SPANISH;

let MSG = undefined;

if(Language.ENGLISH === language) {
  MSG = MSG_EN;
} else if(Language.BASQUE === language){
  MSG = MSG_EUS;
} else if(Language.CATALAN === language) {
  MSG = MSG_CAT;
} else if(Language.GALICIAN === language) {
  MSG = MSG_GAL;
} else if(Language.DEUTSCH == language){
  MSG = MSG_DE;
} else if(Language.FRENCH == language){
  MSG = MSG_FR;
}else {
  MSG = MSG_ES;
}

// ----- A

export const ABOUT = MSG.ABOUT;
export const ACADEMIES = MSG.ACADEMIES;
export const ACADEMY = MSG.ACADEMY;
export const ACCEPT = MSG.ACCEPT;
export const ACCEPTED = "Aceptada"; // TODO
export const ACCEPTED_WITH_ERRORS = "Aceptada con errores"; // TODO
export const ACCESS = MSG.ACCESS;
export const ACCESS_TO_YOUR_AON_ACCOUNT = MSG.ACCESS_TO_YOUR_AON_ACCOUNT;
export const ACCOUNT = MSG.ACCOUNT;
export const ACCOUNTED = MSG.ACCOUNTED;
export const ACCOUNTEDS = MSG.ACCOUNTEDS;
export const ACCOUNTING = MSG.ACCOUNTING;
export const ACTIVATE = MSG.ACTIVATE;
export const ACTIVE = MSG.ACTIVE;
export const ACTIVES= MSG.ACTIVES;
export const ACTIVITY = MSG.ACTIVITY;
export const ACTIVITY_SUMMARY = MSG.ACTIVITY_SUMMARY;
export const ADD = MSG.ADD;
export const ADD_CATEGORY = MSG.ADD_CATEGORY;
export const ADD_COMMENT = MSG.ADD_COMMENT;
export const ADD_COMPOSITION = MSG.ADD_COMPOSITION;
export const ADD_CREDITOR = "Añadir acreedor" // TODO
export const ADD_CUSTOMER = "Añadir cliente" // TODO
export const ADD_EXPENSE = "Añadir gasto";// TODO
export const ADD_DETAIL = MSG.ADD; // TODO
export const ADD_FILE = MSG.ADD_FILE;
export const ADD_FINANCE = MSG.ADD; // TODO
export const ADD_INVEST_ASSET = 'Añadir bien afecto';
export const ADD_INVOICE = MSG.ADD_INVOICE;
export const ADD_NEW = MSG.ADD_NEW;
export const ADD_PRODUCT = "Añadir producto";// TODO
export const ADD_PROJECT = "Añadir proyecto";// TODO
export const ADD_REMARKS = MSG.ADD_REMARKS;
export const ADD_SCOPE = "Añadir Ámbito"; // TODO
export const ADD_ALL_SCOPES = "Añadir todos los Ámbitos"; // TODO
export const ADD_SUPPLIER = "Añadir Proveedor" // TODO
export const ADD_TAG = MSG.ADD_TAG;
export const ADD_TAX = MSG.ADD_TAX;
export const ADD_TO_STOCK = MSG.ADD_TO_STOCK;
export const ADD_TYPE = MSG.TYPE;
export const ADD_WORKGROUP = "Añadir grupo de trabajo"; //TODO
export const ADDITIONAL_HIRING = MSG.ADDITIONAL_HIRING;
export const ADDITIONAL_INFORMATION = MSG.ADDITIONAL_INFORMATION;
export const ADDITIONAL_DATA = "Datos adicionales"; //TODO
export const ADDRESS = MSG.ADDRESS;
export const ADMINISTRATION = "Administración"; // TODO
export const AGENT = MSG.AGENT;
export const AGENTS = MSG.AGENTS;
export const ALIAS = 'Alias';// TODO
export const ALL = MSG.ALL;
export const ALL1 = MSG.ALL1;
export const ALL2 = MSG.ALL2;
export const ALL_FILES = MSG.ALL_FILES;
export const AMOUNT = MSG.AMOUNT;
export const AMORTIZATION = MSG.AMORTIZATION;
export const ANNUAL_VIEW = MSG.ANNUAL_VIEW;
export const AON_BLUE = 'Azul Aon';
export const AON_SERVICES = MSG.AON_SERVICES;
export const API_DOCUMENTATION = MSG.API_DOCUMENTATION;
export const APPLICATIONS = MSG.APPLICATIONS;
export const APPLICATION = "Aplicación";//TODO
export const APPROVED = 'Aprobada'; // TODO
export const ARCHIVED = "Archivada"; // TODO
export const ASESOR = MSG.ASESOR;
export const ATTACH_FILES_DRAGGING_DROPPING = MSG.ATTACH_FILES_DRAGGING_DROPPING;
export const ATTACH_FILES_DRAGGING_DROPPING_BACKGROUND = MSG.ATTACH_FILES_DRAGGING_DROPPING_BACKGROUND;
export const ATTACH_FILES_DRAGGING_DROPPING_LOGO = MSG.ATTACH_FILES_DRAGGING_DROPPING_LOGO;
export const AUTOBOOKING = "Auto-Contratación";
export const AVAILABLE = MSG.AVAILABLE;
export const AVERAGE = "Media";// TODO
export const ASSIGN = "Asignar";// TODO
export const ADVISER = "Asesor"; //TODO
export const ADVISERS = "Asesores"; //TODO
export const AUDIT = "Auditoría"; //TODO
// ----- B

export const BACK = MSG.BACK;
export const BACKGROUND = 'Fondo';
export const BACKGROUND_ADJUST = MSG.BACKGROUND_ADJUST;
export const BANK = MSG.BANK;
export const BANKS = MSG.BANKS;
export const BANK_ACCOUNT = 'Cuenta Bancaria';// TODO
export const BANK_ACCOUNTS = 'Cuentas Bancarias';
export const BANK_DATA = 'Datos Bancarios';// TODO
export const BASE = MSG.BASE;
export const BASQUE = MSG.BASQUE;
export const BI_MONTHLY = 'Bimestral';
export const BIC_SWIFT = 'Bic / Swift';// TODO
export const BARCODE = MSG.BARCODE;
export const BILLING = MSG.BILLING;
export const BILLABLE_USERS = MSG.BILLABLE_USERS;
export const BLOCKED_POPUP = "Ventana bloqueada!";// TODO
export const BLOCKED = "Bloqueado";// TODO
export const BOOKING = MSG.BOOKING;
export const BOOKING_PANEL = MSG.BOOKING_PANEL;
export const BOOKING_RESUME = "Resumen de Contratación"; // TODO
export const BORDER = 'Borde';
export const BOX_CRITERION = MSG.BOX_CRITERION;
export const BUDGET = MSG.BUDGET;
export const BUDGETS = MSG.BUDGETS;
export const BUSINESS_NAME = MSG.BUSINESS_NAME;
export const BOLD = "Negrita"; //TODO

// ----- C

export const CAMPAIGN = "Campaña";
export const CAMPAIGNS = "Campañas";
export const CANCEL = MSG.CANCEL;
export const CANCEL_INVOICE = "Anular"; // TODO
export const CATEGORY = MSG.CATEGORY;
export const CAU = "Cau";// TODO
export const CARRIER = "Agencia de Transporte"; // TODO
export const CARRIERS = "Agencias de Transporte"; // TODO
export const CATALAN = MSG.CATALAN;
export const CATEGORIES = MSG.CATEGORIES;
export const CAUSE = MSG.CAUSE;
export const CERTIFICATE = MSG.CERTIFICATE;
export const CERTIFICATES = MSG.CERTIFICATES;
export const CERTIFICATIONS = MSG.CERTIFICATIONS;
export const CHANGE_TYPE = MSG.CHANGE_TYPE;
export const CHARGES_AND_PAYMENTS = MSG.CHARGES_AND_PAYMENTS;
export const CHANGE_PASSWORD = MSG.CHANGE_PASSWORD; 
export const CHOOSE_A_DOMAIN = MSG.CHOOSE_A_DOMAIN;
export const CITY = MSG.CITY;
export const CLASSIC = MSG.CLASSIC;
export const CLASSIC_APPLICATIONS = MSG.CLASSIC_APPLICATIONS;
export const CLASSIC_VIEW = MSG.CLASSIC_VIEW;
export const CLIENT_FILE = MSG.CLIENT_FILE;
export const CLOSE = MSG.CLOSE;
export const CLOSE_SESSION = MSG.CLOSE_SESSION;
export const CLOSED = MSG.CLOSED;
export const CODE = MSG.CODE;
export const COMMENT = MSG.COMMENT;
export const COMMENTS = MSG.COMMENTS;
export const COMMERCE = MSG.COMMERCE;
export const COMMERCIAL = MSG.COMMERCIAL;
export const COMMERCIAL_REGISTRY_CODE = "Código de registro mercantil"; // TODO
export const COMMERCIAL_NAME = 'Nombre Comercial'; // TODO
export const COMMERCIAL_PRODUCT = 'Producto Comercial'; // TODO
export const COMMUNICATE = "Comunicar";// TODO
export const COMMUNICATE_CONFIRM = "¿Desea comunicar a la seguridad social?";// TODO
export const COMMUNICATE_INVOICE = "Comunicar Factura";// TODO
export const COMMUNICATION = MSG.COMMUNICATION;
export const COMMUNICATIONS = MSG.COMMUNICATIONS;
export const COMPANIES = MSG.COMPANIES;
export const COMPANY = MSG.COMPANY;
export const COMPANY_COSTS = MSG.COMPANY_COSTS;
export const COMPANY_MANAGEMENT = MSG.COMPANY_MANAGEMENT;
export const COMPANY_SELECTION = MSG.COMPANY_SELECTION;
export const COMPONENTS = MSG.COMPONENTS;
export const COMPOSITION = 'Composición';
export const COMUNICA = "Comunic@";// TODO
export const CONFIRM = "Confirmar";// TODO
export const CONCEPT = MSG.CONCEPT;
export const CONCEPTS = MSG.CONCEPTS;
export const CONFIDENTIAL = MSG.CONFIDENTIAL;
export const CONFIGURATION = MSG.CONFIGURATION;
export const CONNECTING_TO = MSG.CONNECTING_TO;
export const CONSOLE = MSG.CONSOLE; 
export const CONTACT = MSG.CONTACT;
export const CONTACT_DATA = MSG.CONTACT_DATA;
export const CONTACT_DATA2 = MSG.CONTACT_DATA2;
export const CONTACTS = MSG.CONTACTS;
export const CONTAINER = MSG.CONTAINER;
export const CONTENT_INDEX = MSG.CONTENT_INDEX;
export const CONTRACT = MSG.CONTRACT;
export const CONTRACT_PAYROLL = MSG.CONTRACT_PAYROLL;
export const CONTRACTED_PRODUCTS = MSG.CONTRACTED_PRODUCTS;
export const CONTRACTED_USERS = MSG.CONTRACTED_USERS;
export const CONTRACTS = MSG.CONTRACTS;
export const COPIED_TO_CLIPBOARD = MSG.COPIED_TO_CLIPBOARD;
export const COPY = MSG.COPY;
export const CUSTOMER_FEE = 'Cuotas';
export const COUNTRY = MSG.COUNTRY;
export const CREATE = MSG.CREATE;
export const CREATE_INVOICE = 'Crear Factura';
export const CREATE_QUERY = MSG.CREATE_QUERY;
export const CREATE_SERVICE_ACCOUNT = "Crear Cuenta de Servicio"; // TODO MSG.CREATE_SERVICE_ACCOUNT;
export const CREATION_DATE = 'Fecha de Creación'; // TODO
export const CREDITOR = MSG.CREDITOR;
export const CREDITORS = MSG.CREDITORS;
export const CUSTOM_VIEW = MSG.CUSTOM_VIEW;
export const CUSTOMER = MSG.CUSTOMER;
export const CUSTOMER_SEARCH = MSG.CUSTOMER_SEARCH;
export const CUSTOMER_SEARCH_DOMAIN_NOT_FOUND  = MSG.CUSTOMER_SEARCH_DOMAIN_NOT_FOUND;
export const CUSTOMER_SEARCH_NOT_FOUND  = MSG.CUSTOMER_SEARCH_NOT_FOUND;
export const CUSTOMERS = MSG.CUSTOMERS;
export const CUSTOMIZE_INVOICE = 'Personalizar Factura';
export const CONVERSATION = 'Conversación'; // TODO
export const CONTENT = 'Contenido'; // TODO
export const CREATED_BY = "Creado por"; //TODO

// ----- D

export const DATE = MSG.DATE;
export const DATE_CREATION = MSG.DATE_CREATION;
export const DATA = "Datos";//TODO
export const DARK = MSG.DARK;
export const DEACTIVATE = MSG.DEACTIVATE;
export const DEFAULT_CATEGORIES = MSG.DEFAULT_CATEGORIES; //TODO
export const DELETE = MSG.DELETE;
export const DELETE_ALL_SCOPES = "Eliminar todos los Ámbitos"; // TODO
export const DELETE_BACKGROUND_CONFIRM = MSG.DELETE_BACKGROUND_CONFIRM;
export const DELETE_CATEGORY = MSG.DELETE_CATEGORY;
export const DELETE_CONFIRM = MSG.DELETE_CONFIRM;
export const DELETE_DETAIL = MSG.DELETE; // TODO
export const DELETE_FILE = MSG.DELETE_FILE;
export const DELETE_FILE_CONFIRM = MSG.DELETE_FILE_CONFIRM;
export const DELETE_FINANCE = MSG.DELETE; // TODO
export const DELETE_FOREVER = MSG.DELETE_FOREVER;
export const DELETE_FOREVER_INVOICE_CONFIRMATION = MSG.DELETE_FOREVER_INVOICE_CONFIRMATION;
export const DELETE_INVOICE = 'Borrar Factura';
export const DELETE_LOGO_CONFIRM = MSG.DELETE_LOGO_CONFIRM;
export const DELETE_SCOPE = "Borrar Ámbito"; // TODO
export const DELETE_TAG = MSG.DELETE_TAG;
export const DELETE_WORKGROUP = "Borrar Grupo de trabajo"; // TODO
export const DELETE_TAX = MSG.DELETE_TAX;
export const DELETE_WAREHOUSE = MSG.DELETE_WAREHOUSE;
export const DELETED_DATA = MSG.DELETED_DATA;
export const DELIVERY_TAG = "Etiqueta de Envío";
export const DELIVERY = 'Albarán de Venta';
export const DESCRIPTION = MSG.DESCRIPTION;
export const DETAILED = MSG.DETAILED;
export const DEUTSCH = MSG.DEUTSCH;
export const DISCARDED = MSG.DISCARDED;
export const DOCUMENT = MSG.DOCUMENT;
export const DOCUMENTS = MSG.DOCUMENTS;
export const DOCUMENTS_IN_PROCESS = MSG.DOCUMENTS_IN_PROCESS;
export const DOCUMENTS_UNDER_REVIEW = MSG.DOCUMENTS_UNDER_REVIEW;
export const DOCUMENT_DATA = "Datos Documento";
export const DOCUMENTAL_FILE = MSG.DOCUMENTAL_FILE;
export const DOMAIN = "Dominio"; //TODO
export const DOMAIN_PARENT = "Dominio Padre"; //TODO
export const DOCUMENTARY = MSG.DOCUMENTARY;
export const DOMAIN_SEARCH = MSG.DOMAIN_SEARCH;
export const DOWNLOAD = MSG.DOWNLOAD;
export const DOWNLOAD_FILE = MSG.DOWNLOAD_FILE;
export const DOWNLOAD_FILES = MSG.DOWNLOAD_FILES;
export const DOWNLOAD_INVOICE = MSG.DOWNLOAD_INVOICE;
export const DOWNLOAD_EXCEL = MSG.DOWNLOAD + " Excel";
export const DOWNLOAD_EXCEL_INVOICE = MSG.DOWNLOAD_INVOICE + " Excel";
export const DOWNLOAD_INVOICES = MSG.DOWNLOAD_INVOICES;
export const DRAFT = 'Borrador';
export const DRAFTS = 'Borradores';
export const DROP_FILE = "Suelta el archivo";//TODO
export const DUPLICATE = MSG.DUPLICATE;
export const DUPLICATE_INVOICE = MSG.DUPLICATE_INVOICE;
export const DURATION = "Duración";

// ----- E

export const EDIT = MSG.EDIT;
export const EDIT_CATEGORY = MSG.EDIT_CATEGORY;
export const EDIT_FILE = MSG.EDIT_FILE;
export const EDIT_FILES = MSG.EDIT_FILES;
export const EDIT_TAG = MSG.EDIT_TAG;
export const EDIT_WAREHOUSE = MSG.EDIT_WAREHOUSE;
export const EDIT_WORKGROUP = "Editar grupo de trabajo"; //TODO
export const ELABORATION = "Elaboración"
export const ELABORATIONS = "Elaboraciones"
export const EMAIL = MSG.EMAIL;
export const EMAIL_ADD = 'Asignar dirección de correo electrónico';
export const EMAIL_VERIFICATION = 'Verificación dirección de correo electrónico';
export const EMAIL_ADD_DESCRIPTION = 'Es necesario asignar una dirección de correo electrónico a una cuenta para poder recuperar la contraseña, recibir notificaciones y confirmaciones, y para la seguridad de la cuenta'
export const EMAIL_VERIFICATION_DESCRIPTION = '*Introduzca el código de verificación que hemos enviado a su dirección de correo electrónico';
export const EMPLOYEE = MSG.EMPLOYEE;
export const EMPLOYEES = MSG.EMPLOYEES;
export const END_DATE = MSG.END_DATE;
export const ENGLISH = MSG.ENGLISH;
export const ENTERPRISE = MSG.ENTERPRISE;
export const ENTERPRISES = MSG.ENTERPRISES;
export const ENTRY = MSG.ENTRY;
export const ENVIRONMENT = MSG.ENVIRONMENT;
export const EQUIVALENCE_SURCHARGE = MSG.EQUIVALENCE_SURCHARGE;
export const ERROR = 'Error';
export const ERRORS = MSG.ERRORS;
export const ERR_EMPTY_VALUE = "Campos sin valor";
export const ERR_LOW_CONFIDENCE = "Campos con poca confianza";
export const ESTIMATED_TIME = "Tiempo estimado";
export const EXAMPLE = "Example";
export const EXEMPTION_CAUSE = MSG.EXEMPTION_CAUSE;
export const EXERCISE = MSG.EXERCISE;
export const EXIT = MSG.EXIT;
export const EXEMPT = MSG.EXEMPT;
export const EXEMPTION = MSG.EXEMPTION;
export const EXPAND_HIRIND = MSG.EXPAND_HIRIND;
export const EXPEDITION_DATE = MSG.EXPEDITION_DATE;
export const EXPENSE = MSG.EXPENSE;
export const EXPENSES = MSG.EXPENSES; 
export const EXPIRATION_DATE = "Fecha expiración"; // TODO;
export const EXPIRATIONS = MSG.EXPIRATIONS;
export const EXPIRED = MSG.EXPIRED;
export const EXPIRED_SESSION = MSG.EXPIRED_SESSION;
export const EXPORTED = "Exportadas";
export const EXTERNALLY_COMMUNICATED = "Comunicada Externamente"; // TODO

// ----- F

export const FACTURAE = "Facturae";
export const FAILED = "Fallido"; // TODO
export const FAX = MSG.FAX;
export const FILE = MSG.FILE;
export const FILE_DATA = MSG.FILE_DATA;
export const FILTER = "Filtro";//TODO
export const FILTERS = "Filtros";//TODO
export const FIND_LINKED_DOMAINS = MSG.FIND_LINKED_DOMAINS;
export const FISCAL = MSG.FISCAL;
export const FISCAL_DATA = 'Datos Fiscales'; // TODO
export const FOLIO = 'Folio';
export const FOOTER = MSG.FOOTER;
export const FORMALITIES = 'Trámites';//TODO
export const FROM = MSG.FROM;
export const FRENCH = MSG.FRENCH;
export const FUTURE = MSG.FUTURE;

// ----- G

export const GALICIAN = MSG.GALICIAN;
export const GARAGE = MSG.GARAGE;
export const GENERAL_DATA = 'Datos Generales'; //TODO
export const GENERAL_INFORMATION = MSG.GENERAL_INFORMATION;
export const GET_INVOICE = MSG.GET_INVOICE;
export const GET_INVOICES = MSG.GET_INVOICES;
export const GO_CONFIGURATION = MSG.GO_CONFIGURATION;
export const GLOBAL_CONFIGURATION = MSG.GLOBAL_CONFIGURATION;
export const GROUP_DATA = MSG.GROUP_DATA;
export const GROUP_MANAGEMENT = MSG.GROUP_MANAGEMENT;
export const GROUP = MSG.GROUP;
export const GROUPS = MSG.GROUPS;
export const GROUPWARE = MSG.GROUPWARE;
export const GROUPED = "Agrupadas";//TODO

// ----- H

export const HEADER = MSG.HEADER;
export const HELP = MSG.HELP;
export const HELP_RESULTS = MSG.HELP_RESULTS;
export const HIDE_FIELDS = "Ocultar campos"; //TODO
export const HIRING = MSG.HIRING;
export const HIRING_DATA = MSG.HIRING_DATA;
export const HISTORIC = MSG.HISTORIC;
export const HOLDER = MSG.HOLDER;
export const HOLDERS = MSG.HOLDERS;
export const HOME = MSG.HOME;
export const HOURS = MSG.HOURS;
export const HOUR = "Hora"; //TODO
export const HTTP_REQUEST = 'HTTP Request';
export const HTTP_REQUEST_HEADER = 'HTTP Request Header';

// ----- I

export const IN_DEVELOPMENT = MSG.IN_DEVELOPMENT;
export const IN_PREPARATION = "En Preparación";
export const IN_PROGRESS = "En Progreso"; // TODO
export const IN_TRASH = "En Papelera"; // TODO
export const INACTIVE = MSG.INACTIVE;
export const INACTIVES = MSG.INACTIVES;
export const INBOX = MSG.INBOX;
export const INCLUDE_COMPANY_DATA = MSG.INCLUDE_COMPANY_DATA;
export const INCLUDE_LOGO = MSG.INCLUDE_LOGO;
export const INCLUDE_REGISTRATION_DATA = 'Incluir Datos Registrales';
export const INCLUDE_CONTACT_DATA = 'Incluir Datos de Contacto';
export const INCOMES = 'Ingresos';
export const INFORMATION = MSG.INFORMATION;
export const INFO_INCOMES = MSG.INFO_INCOMES;
export const INFO_EXPENSES = MSG.INFO_EXPENSES;
export const INFO_PENDING = MSG.INFO_PENDING;
export const INSCRIPTION = 'Inscripción';
export const INTERNAL = "Interno"; //TODO
export const INVENTORIES = MSG.INVENTORIES;
export const INVENTORY = MSG.INVENTORY;
export const INVEST_ASSET = 'Bien Afecto';
export const INVEST_ASSETS = 'Bienes Afectos';
export const INVESTMENT = MSG.INVESTMENT;
export const INVOICE = MSG.INVOICE;
export const INVOICE_CONCEPTS = MSG.INVOICE_CONCEPTS;
export const INVOICE_CONFIGURATION = MSG.INVOICE_CONFIGURATION;
export const INVOICE_COMMUNICATION = MSG.INVOICE_COMMUNICATION;
export const INVOICE_DATA = MSG.INVOICE_DATA;
export const INVOICE_ISSUED = MSG.INVOICE_ISSUED;
export const INVOICE_NUMBER = MSG.INVOICE_NUMBER;
export const INVOICE_DOCUMENT_NUMBER = MSG.INVOICE_DOCUMENT_NUMBER;
export const INVOICE_OBJECT = MSG.INVOICE_OBJECT;
export const INVOICE_PRINTING = MSG.INVOICE_PRINTING;
export const INVOICE_RECEIVED = MSG.INVOICE_RECEIVED;

export const INVOICE_ID_DESCRIPTION = 'Identificador de la Factura.';
export const INVOICE_DOMAIN_DESCRIPTION = 'Dominio al que pertenece la factura.';
export const INVOICE_TYPE_DESCRIPTION = 'Tipo de la factura. emitida | recibida | ticket';
export const INVOICE_SERIE_DESCRIPTION = 'Serie de la factura. (Para facturas emitidas)';
export const INVOICE_NUMBER_DESCRIPTION = 'Número de la factura. (Para facturas emitidas)';
export const INVOICE_REFERENCE_DESCRIPTION = 'Referencia de la factura. (Para facturas recibidas o tickets)';
export const INVOICE_DATE_DESCRIPTION = 'Fecha de la factura.';
export const INVOICE_TRANSACTION_DESCRIPTION = 'Tipo de transacción de la factura. NAC | INTR | EXTR | CCM | ISP';
export const INVOICE_CATEGORY_DESCRIPTION = 'Categoría de la factura. (Cuenta Contable de la factura)';
export const INVOICE_TOTAL_DESCRIPTION = 'Importe total de la factura.';
export const INVOICE_SENDER_DESCRIPTION = 'Información del emisor de la factura.';
export const INVOICE_RECEIVER_DESCRIPTION = 'Información del receptor de la factura.';
export const INVOICE_INVESTMENT_DESCRIPTION = 'Indica si es una factura de inversión.';
export const INVOICE_SERVICE_DESCRIPTION = 'Indica si es una factura de servicio.';
export const INVOICE_WITHHOLDING_DESCRIPTION = 'Indica si la factura tiene retención';
export const INVOICE_WITHHOLDING_FARMER_DESCRIPTION = 'Indica si la factura tiene régimen especial de agricultura, ganaderia y pesca.';
export const INVOICE_VAT_ACCRUAL_PAYMENT_DESCRIPTION = 'Indica si la factura tiene régimen especial de criterio de caja.';
export const INVOICE_SURCHARGE_DESCRIPTION = 'Indica si la factura tiene recargo de equivalencia.';
export const INVOICE_RECTIFIED_DESCRIPTION = 'Indica si es una factura rectificada.';
export const INVOICE_RECTIFIER_DESCRIPTION = 'Indica si es una factura rectificativa.';
export const INVOICE_RECTIFICATION_INVOICE_DESCRIPTION = 'Identificador de la factura rectificado o de la factura rectificativa.';
export const INVOICE_COMMENTS_DESCRIPTION = 'Comentarios de la factura';
export const INVOICE_REMARKS_DESCRIPTION = 'Observaciones de la factura';
export const INVOICE_ACTIVITY_DESCRIPTION = 'Actividad de la factura.';
export const INVOICE_STATUS_DESCRIPTION = 'Estado de la factura inbox | rejected | draft | pending | scored';
export const INVOICE_TAXES_DESCRIPTION = 'Resumen de los impuestos de la factura';
export const INVOICE_DETAILS_DESCRIPTION = 'Detalles de la factura';
export const INVOICE_FINANCES_DESCRIPTION = 'Vencimientos de la factura.';
export const INVOICE_WORKPLACE_DESCRIPTION = 'Centro de Trabajo de la factura.';
export const INVOICED = "Facturado";
export const INVOICES = MSG.INVOICES;
export const IRPF = MSG.IRPF;
export const IRUS = 'Irus';
export const IS_REQUIRED = MSG.IS_REQUIRED;
export const IS_NOT_VALID_EMAIL = MSG.IS_NOT_VALID_EMAIL;
export const ISSUED = MSG.ISSUED;
export const ISSUED_INVOICES = "Facturas Emitidas";
export const ISSUEDS = MSG.ISSUEDS;
export const ISSUE = "Asunto"; //TODO

// ----- J

// ----- K

// ----- L

export const LABELS = "Etiquetas";
export const LABORAL_COSTS = MSG.LABORAL_COSTS;
export const LANGUAGE = MSG.LANGUAGE;
export const LAST = MSG.LAST;
export const LAST_ACCESS = "Último Acceso";
export const LAST_LOCATION = MSG.LAST_LOCATION;
export const LAST_STATUS = MSG.LAST_STATUS;
export const LAST_MODIFICATION = "Última modificación";
export const LEGAL_LITERALS = 'Literales Legales';
export const LINK = MSG.LINK;
export const LINK_CLIENT = MSG.LINK_CLIENT;
export const LINKING = MSG.LINKING;
export const LINKED = MSG.LINKED;
export const LINKED1 = MSG.LINKED1;
export const LINK_CUSTOMER = MSG.LINK_CUSTOMER;
export const LINK_DOMAINS = MSG.LINK_DOMAINS;
export const LINK_DOMAIN_QUESTION = MSG.LINK_DOMAIN_QUESTION;
export const LIST = 'Listado'; //TODO
export const LOADING = MSG.LOADING;
export const LOCATION = MSG.LOCATION;
export const LOCATIONS = MSG.LOCATIONS;
export const LOGIN = MSG.LOGIN;
export const LOGIN_SUBTITLE = MSG.LOGIN_SUBTITLE;
export const LROE = 'LROE';

// ----- M

export const MARK_ENTRY = MSG.MARK_ENTRY;
export const MARK_EXIT = MSG.MARK_EXIT;
export const MARKETING = MSG.MARKETING;
export const MANAGEMENT = MSG.MANAGEMENT;
export const MANUALS = MSG.MANUALS;
export const MENU = MSG.MENU;
export const MODEL = "Modelo"; //TODO
export const MODEL_111 = MSG.MODEL_111;
export const MODEL_111_DESCRIPTION = MSG.MODEL_111_DESCRIPTION;
export const MODEL_190 = MSG.MODEL_190;
export const MODEL_190_DESCRIPTION = MSG.MODEL_190_DESCRIPTION;
export const MODERN = MSG.MODERN;
export const MONTHLY_VIEW = MSG.MONTHLY_VIEW;
export const MOVED_TO_TRASH = MSG.MOVED_TO_TRASH;
export const MY_DATA = MSG.MY_DATA;
export const MY_MANAGER = MSG.MY_MANAGER;
export const MY_USER = MSG.MY_USER;
export const MAGIC_LINK = MSG.MAGIC_LINK;
export const MSG_SENT = "Mensaje enviado";//TODO
export const MAXIMIZE = "Maximizar";//TODO
export const MINIMIZE = "Minimizar";//TODO
export const MESSENGER_SERVICE = "Mensajería";//TODO
export const MONTH = "Mes"; //TODO
export const MONTHLY = "Mensual"; //TODO
export const MAILBOX = "Buzón";//TODO
export const MESSAGE = "Mensaje"; //TODO
export const MODIFIED_BY = "Modificado por"; //TODO
export const MOVE_STOCK = "Mover Stock"; //TODO
export const MINS = "Minutos"; //TODO
export const MY_CLOUD = MSG.MY_CLOUD;

// ----- N

export const NAME = MSG.NAME;
export const NEW = MSG.NEW;
export const NEWS = "Noticias";//TODO
export const NEW_COMPANY = MSG.NEW_COMPANY;
export const NEW_DOCUMENT = MSG.NEW_DOCUMENT;
export const NEW_ELABORATION = 'Nueva Elaboración';
export const NEW_EMPLOYEE = MSG.NEW_EMPLOYEE;
export const NEW_EXPENSE = MSG.NEW_EXPENSE;
export const NEW_INVEST_ASSET = 'Nuevo Bien Afecto'; //TODO
export const NEW_INVOICE = MSG.NEW_INVOICE;
export const NEW_ISSUED_INVOICE = MSG.NEW_ISSUED_INVOICE;
export const NEW_PASSWORD = 'Nueva Contraseña';
export const NEW_PRODUCT = MSG.NEW_PRODUCT;
export const NEW_RECEIVED_INVOICE = MSG.NEW_RECEIVED_INVOICE;
export const NEW_REQUEST = MSG.NEW_REQUEST;
export const NEW_SALE = 'Nuevo Pedido de Venta';
export const NEW_TICKET = MSG.NEW_TICKET;
export const NEW_WAREHOUSE= MSG.NEW_WAREHOUSE;
export const NEXT = MSG.NEXT;
export const NIF = MSG.NIF;
export const NOT_LINKED = MSG.NOT_LINKED;
export const NO_DATA = MSG.NO_DATA;
export const NO_LINK_CLIENT = MSG.NO_LINK_CLIENT;
export const NO_PERIOD = 'Sin Periodo';
export const NO_STOCK_AVAILABLE = MSG.NO_STOCK_AVAILABLE;
export const NO_VERIFACTU = "No Verifactu";
export const NOT_LINKED1 = MSG.NOT_LINKED1;
export const NOTIFICATION = MSG.NOTIFICATION;
export const NOTIFICATIONS = MSG.NOTIFICATIONS;
export const NUMBER = MSG.NUMBER;
export const NUMBER_OF_USERS = MSG.NUMBER_OF_USERS;
export const NUMBER_OF_PALLETS = MSG.NUMBER_OF_PALLETS;
export const NOTARY = 'Notario'; //TODO
export const NOTES = MSG.NOTES;
export const NOTE = MSG.NOTE;
export const NOTICE = "Noticia"; //TODO
// ----- O

export const OCR = "OCR";
export const OBSERVATION = MSG.OBSERVATION;
export const OFFERS = "Presupuestos";
export const OFFICE = MSG.OFFICE;
export const OFFICE_CATEGORIES = MSG.OFFICE_CATEGORIES;
export const ONLY_DOMAINS_WITHOUT_LINKED_CUSTOMER = MSG.ONLY_DOMAINS_WITHOUT_LINKED_CUSTOMER;
export const ONLY_PORTAL = MSG.ONLY_PORTAL;
export const OPEN = MSG.OPEN;
export const OPEN_MENU = MSG.OPEN_MENU;
export const OPEN_REQUESTS = MSG.OPEN_REQUESTS;
export const OPENED = MSG.OPENED;
export const OPERATION_DATE = MSG.OPERATION_DATE;
export const OPTIONS = MSG.OPTIONS;
export const OPTIONAL = MSG.OPTIONAL;
export const OR_ACCESS = MSG.OR_ACCESS;
export const OTHER_EXPENSES = 'Otros Gastos';
export const OTHER_INCOMES = 'Otros Ingresos';
export const OTHER_SERVICES = MSG.OTHER_SERVICES;
export const OTHERS = "Otros";
export const ONE = MSG.ONE;

// ----- P
export const PACKAGE = 'Envase';
export const PACKAGES = 'Envases';
export const PACKAGING = 'Empaquetado';
export const PARENT_APPS = MSG.PARENT_APPS;
export const PASSWORD = MSG.PASSWORD;
export const PAUSE = MSG.PAUSE;
export const PAYMETHOD = MSG.PAYMETHOD;
export const PAYMETHODS = MSG.PAYMETHODS;
export const PAYROLL = MSG.PAYROLL;
export const PAYSHEET = MSG.PAYSHEET;
export const PAYSHEETS = MSG.PAYSHEETS;
export const PENDING = MSG.PENDING;
export const PENDING_CORRECTION = "Pendiente de Corrección";
export const PENDING_DECISSION = "Pendiente de Decisión";
export const PENDING_DOCUMENTS = MSG.PENDING_DOCUMENTS;
export const PENDING_DRAFTS = 'Borradores Pendientes';
export const PENDING_INVOICES = MSG.PENDING_INVOICES;
export const PENDING_REVIEW = MSG.PENDING_REVIEW;
export const PENDING_TASKS = MSG.PENDING_TASKS;
export const PENDINGS = MSG.PENDINGS;
export const PERCENTAGE = 'Porcentaje'; //TODO
export const PERIOD = 'Periodo';
export const PERSONALIZED = 'Personalizado';
export const PERSONALIZATION = 'Personalización';
export const PERSONALIZED_THEME = 'Tema Personalizado';
export const PERMISSIONS = MSG.PERMISSIONS;
export const PHONE = MSG.PHONE;
export const PORTAL = 'Portal';
export const PORTAL_MENU = MSG.PORTAL_MENU;
export const POSTAL_CODE = MSG.POSTAL_CODE;
export const POSTAL_CODE_MIN = MSG.POSTAL_CODE_MIN;
export const PRESENCE = 'Presencia';
export const PREVIOUS = MSG.PREVIOUS;
export const PRICE = MSG.PRICE;
export const PRINT = MSG.PRINT;
export const PRINT_INVOICE = MSG.PRINT_INVOICE;
export const PRINTER = 'Impresora';
export const PROCCESSING = 'En Trámite';
export const PROCESS = "Trámite"; //TODO
export const PROCESS_TYPE = "Tipo de trámite"; //TODO
export const PROCESSED = MSG.PROCESSED;
export const PROCESSED_MOVEMENT  = MSG.PROCESSED_MOVEMENT;
export const PROCESSED_MOVEMENT_BJ = "La baja se ha procesado correctamente";//TODO
export const PRODUCT = MSG.PRODUCT;
export const PRODUCT_NOT_EMPTY = MSG.PRODUCT_NOT_EMPTY;
export const PRODUCT_MUST_BE_SELECTED = MSG.PRODUCT_MUST_BE_SELECTED;
export const PRODUCTS = MSG.PRODUCTS;
export const PRODUCTS_AND_SERVICES = "Productos y Servicios"; //MSG.PRODUCTS_AND_SERVICES;
export const PROFIT = "Beneficio"; // TODO
export const PROFORMA = "Proforma";
export const PROFORMA_INVOICES = MSG.PROFORMA_INVOICES; 
export const PROGRAMMED = MSG.PROGRAMMED;
export const PROJECT = "Proyecto"; //TODO
export const PROJECTS = "Proyectos"; //TODO
export const PROTOCOL = "Protocolo";
export const PROVINCE = MSG.PROVINCE;
export const PURCHASE_PRICE = 'Precio Coste'; // TODO
export const PARENT = "Padre"; // TODO
export const PLAN = "Planes";

// ----- Q

export const QUANTITY = MSG.QUANTITY;
export const QUARTERLY_VIEW = MSG.QUARTERLY_VIEW;
export const QUICK_ACCESS = MSG.QUICK_ACCESS;
export const QUERY = "Consulta"; //TODO
export const QUESTION = "Pregunta"; //TODO
export const QUESTIONS = "Preguntas"; // TODO
export const QUOTA = MSG.QUOTA;
export const QUOTE_GROUP = "Grupo de cotización";//TODO
// ----- R

export const RADIO = "Radio"; // TODO
export const RECEIVED = MSG.RECEIVED;
export const REGISTER = "Registro"; // TODO
export const RECEIVED_INVOICES = MSG.RECEIVED_INVOICES;
export const RECEIVEDS = MSG.RECEIVEDS;
export const RECENTS = MSG.RECENTS;
export const RECENTLY_OPENED = MSG.RECENTLY_OPENED;
export const RECORD = MSG.RECORD;
export const RECORD_INVOICE = MSG.RECORD_INVOICE;
export const RECOVER_PASSWORD = 'Recuperar Contraseña'; // TODO
export const RECTIFIED = MSG.RECTIFIED;
export const RECTIFIER = MSG.RECTIFIER;
export const RECTIFY = MSG.RECTIFY;
export const RECTIFY_INVOICE = MSG.RECTIFY_INVOICE;
export const REFERENCE = MSG.REFERENCE;
export const REFRESH = MSG.REFRESH;
export const REGISTERED_TRADEMARK = MSG.REGISTERED_TRADEMARK;
export const REGISTERED_TRADEMARK_AON = MSG.REGISTERED_TRADEMARK_AON;
export const REGISTRATION_DATA = 'Datos Registrales'; // TODO
export const REGISTRATION_DATE = 'Fecha de Registro'; // TODO
export const REJECT = MSG.REJECT;
export const REJECT_INVOICE = MSG.REJECT_INVOICE;
export const REJECT_INVOICES = MSG.REJECT_INVOICES;
export const REJECTED = MSG.REJECTED;
export const REJECTED_INVOICES = MSG.REJECTED_INVOICES;
export const REJECTEDS = MSG.REJECTEDS;
export const REMARKS = MSG.REMARKS;
export const REMINDER = MSG.REMINDER;
export const REOPEN = 'Reabrir';
export const REOPENED = 'Reabierto';
export const REPEAT_PASSWORD = 'Repetir Contraseña';
export const REPROCESS = MSG.REPROCESS;
export const REQUEST = MSG.REQUEST;
export const REQUESTS = MSG.REQUESTS;
export const REQUESTS_SENT = MSG.REQUESTS_SENT;
export const REQUESTS_RECEIVED = MSG.REQUESTS_RECEIVED;
export const RESTORE = MSG.RESTORE;
export const RESTORE_INVOICE = MSG.RESTORE_INVOICE;
export const RESTORE_INVOICES = MSG.RESTORE_INVOICES;
export const RESTORED_DATA = MSG.RESTORED_DATA;
export const RESULTS = MSG.RESULTS;
export const RESUME_COSTS = "Resumen Costes"; // TODO
export const RETENTION_PANEL = 'Panel de IRPF';
export const RETENTION_PERCENT = "% Retención"; 
export const REQUEST_EMPTY_DATA = "Sin datos nuevos para la consulta"; // TODO
export const REGIME = "Régimen"; // TODO
export const RGPD_URL = 'RGPD Url';
export const REQUEST_CLOSE_CONFIRM =  "Estás seguro de cerrar la solicitud?"; //TODO
export const REVIEW = MSG.REVIEW;

// ----- S

export const SALTRA = 'Saltra';
export const SALES_PREPARATION = 'Preparación de Pedidos';
export const SAVE = MSG.SAVE;
export const SAVED_DATA = MSG.SAVED_DATA;
export const SCOPE = MSG.SCOPE;
export const SCOPES = MSG.SCOPES;
export const SCHEDULE = MSG.SCHEDULE;
export const SEARCH = MSG.SEARCH;
export const SECTION = 'Sección'; // TODO
export const SECURITY = 'Seguridad';
export const SEE_ALL = MSG.SEE_ALL;
export const SELECT_LANGUAGE = MSG.SELECT_LANGUAGE;
export const SELECT_COMPANY_TYPE = MSG.SELECT_COMPANY_TYPE;
export const SEND = MSG.SEND;
export const SENT = "Enviadas"; // TODO
export const SEND_FILE = MSG.SEND_FILE;
export const SEND_FILES = MSG.SEND_FILES;
export const SEND_INVOICE = MSG.SEND_INVOICE;
export const SEND_INVOICES = MSG.SEND_INVOICES;
export const SENDER = "Remitente";//TODO
export const SEPA_FILES = MSG.SEPA_FILES;
export const SERES = 'Seres';
export const SERIE = MSG.SERIE;
export const SERVICE = MSG.SERVICE;
export const SERVICE_ACCOUNT = 'Cuenta de Servicio'// TODO MSG.SERVICE_ACCOUNT;
export const SERVICE_ACCOUNTS = 'Cuentas de Servicio'// TODO MSG.SERVICE_ACCOUNT;
export const SERVICES = MSG.SERVICES;
export const SETTING = MSG.SETTING;
export const SHARED = MSG.SHARED;
export const SHEET = 'Hoja';
export const SHOW_FILE = MSG.SHOW_FILE;
export const SIDE_MENU = MSG.SIDE_MENU;
export const SIGN_IN = MSG.SIGN_IN;
export const SIGN_IN_WITHOUT_PASSWORD = MSG.SIGN_IN_WITHOUT_PASSWORD;
export const SIGN_IN_WITH_CERTIFICATE = MSG.SIGN_IN_WITH_CERTIFICATE;
export const SIGNING = 'Fichaje' // TODO
export const SIF = 'Sistema Informático Facturación'; // TODO
export const SII = 'SII';
export const SII_TICKETBAI = MSG.SII_TICKETBAI;
export const SIMPLIFIED_INVOICE = "Factura Simplificada";
export const SIMPLIFIED_INVOICES = "Simplificadas";
export const SIMPLIFIED = "Simp.";
export const SIZE = MSG.SIZE;
export const SPANISH = MSG.SPANISH;
export const STATISTICS = MSG.STATISTICS;
export const STATUS = "Estado"; // TODO
export const STANDARD = MSG.STANDARD;
export const STOCK = "Stock"; //TODO
export const STREET_TYPE = 'Tipo vía';
export const SITUATION = "Situación"; // TODO
export const SIX_MONTHLY = 'Semestral';
export const SUITE_MENU = MSG.SUITE_MENU;
export const SUMMARIZED = MSG.SUMMARIZED;
export const SUPERSET = 'Superset' ;
export const SUPPLIED = MSG.SUPPLIED;
export const SUPPLIER = MSG.SUPPLIER;
export const SUPPLIERS = MSG.SUPPLIERS;
export const SUPPORT = MSG.SUPPORT;
export const SUPPORTING_DOCUMENTS = MSG.SUPPORTING_DOCUMENTS;
export const SURCHARGE = MSG.SURCHARGE;
export const SURCHARGE_RE = 'R.E.'; // TODO
export const SURNAME = MSG.SURNAME;
export const SURVEY = "Cuestionario"; // TODO
export const SURVEYS = "Cuestionarios"; // TODO
export const STAFF_EXPENSES = "Gastos de Personal";
export const STATUS_NOT_EMPTY = MSG.STATUS_NOT_EMPTY;
export const STORE = "Archivar"; //TODO
export const START_DATE = "Fecha inicio";//TODO
export const SYSTEM_MESSAGES = MSG.SYSTEM_MESSAGES;

// ----- T

export const TAG = MSG.TAG;
export const TASK = "Tarea"; //TODO
export const TASKS = "Tareas"; //TODO
export const TAGS = MSG.TAGS;
export const TARGET = "Cliente Potencial"; //TODO
export const TARGETS = "Clientes Potenciales"; //TODO
export const TAX = MSG.TAX;
export const TAX_BASE = MSG.TAX_BASE;
export const TAXES = MSG.TAXES;
export const TAXES_DETAIL = MSG.TAXES_DETAIL;
export const TEST_ENVIRONMENT = MSG.TEST_ENVIRONMENT;
export const THEME = 'Tema';
export const THEME_SELECTION = MSG.THEME_SELECTION;
export const TICKET = MSG.TICKET;
export const TICKETBAI = MSG.TICKETBAI;
export const TICKETS = MSG.TICKETS;
export const TIMECONTROL = MSG.TIMECONTROL;
export const TIMECTRL = MSG.TIMECTRL;
export const TITULAR_DATA = MSG.TITULAR_DATA;
export const THREE_MONTHLY = 'Trimestral';
export const TO = MSG.TO;
export const TO_REVIEW = "A revisar";
export const TO_TRASH = MSG.TO_TRASH;
export const TODAY = MSG.TODAY;
export const TOMORROW = MSG.TOMORROW;
export const TOOLS = MSG.TOOLS;
export const TOTAL = MSG.TOTAL;
export const TOTAL_SUPPLIED = MSG.TOTAL_SUPPLIED;
export const TRANSACTION_TYPE = MSG.TRANSACTION_TYPE;
export const TRASH = MSG.TRASH;
export const TREASURY = MSG.TREASURY; 
export const TYPE = MSG.TYPE;
export const TYPES = MSG.TYPES;
export const TASK_TRAY = "Bandeja Tareas";//TODO
export const TITLE = MSG.TITLE;
export const TRAY = "Bandeja";//TODO
export const TYPE_HERE = "Escriba aquí"; //TODO
export const TYPE_REQUEST = "Tipo solicitud";//TODO
export const TYPE_INCIDENT = "Tipo incidencia";//TODO
export const TYPE_CONTRACT = "Tipos de contrato";//TODO
export const TYPE_JOB = "Tipo de trabajo";//TODO
export const TO_SHOW = "Mostrar";//TODO


// ----- U

export const UNACCOUNT_INVOICES = MSG.UNACCOUNT_INVOICES;
export const UNLINK = MSG.UNLINK;
export const UNLINK_DOMAIN_QUESTION = MSG.UNLINK_DOMAIN_QUESTION;
export const UNLINK_CLIENT = MSG.UNLINK_CLIENT;
export const UNLINKED = MSG.UNLINKED;
export const UPDATED_CONTRACT = MSG.UPDATED_CONTRACT;
export const UPLOAD = MSG.UPLOAD;
export const UPLOAD_FILE = MSG.UPLOAD_FILE;
export const UPLOAD_INVOICE = MSG.UPLOAD_INVOICE;
export const UPLOAD_DOCUMENT = MSG.UPLOAD_DOCUMENT;
export const UPDATE = "Actualizar"; //TODO
export const UPDATE_INVOICE = 'Actualizar Factura';
export const USER = MSG.USER;
export const USER_CATEGORIES = MSG.USER_CATEGORIES
export const USER_DATA = MSG.USER_DATA;
export const USER_MANAGEMENT = MSG.USER_MANAGEMENT;
export const USERS = MSG.USERS;
export const UTILITIES = MSG.UTILITIES;
export const UPPER_MENU = MSG.UPPER_MENU;

// ----- V

export const VAT = MSG.VAT;
export const VAT_ACCRUAL_PAYMENT = 'Criterio de Caja'; // TODO
export const VAT_PANEL = 'Panel de IVA';
export const VAT_PERCENT = '% IVA';
export const VERIFACTU = 'Verifactu';
export const VERIFIED = 'Verificado';
export const VERIFICATION = 'Verificación';
export const VERIFICATION_CODE = 'Código Verificación';
export const VERSION = MSG.VERSION;
export const VIEW_FIELDS = "Ver campos"; //TODO
export const VIEW_PAYROLL = "Ver nómina"; // TODO
export const VIEW_PAYROLLS = "Ver nóminas"; // TODO
export const VIEW = "Ver"; // TODO
export const VOLUME = "Tomo";

// ----- W

export const WAREHOUSE = MSG.WAREHOUSE;
export const WAREHOUSES = MSG.WAREHOUSES;
export const WELCOME_TO_AON_SOLUTIONS = MSG.WELCOME_TO_AON_SOLUTIONS;
export const WEB = MSG.WEB;
export const BLACK_AND_WHITE = 'Blanco y Negro';
export const WITHHOLDING = MSG.WITHHOLDING;
export const WITHHOLDING_FARMER = 'Reg. agri. gan. y pesca'; // TODO
export const WORKGROUP = MSG.WORKGROUP;
export const WRITE_YOUR_TITLE = MSG.WRITE_YOUR_TITLE;
export const WRITE_A_COMMENT =  MSG.WRITE_A_COMMENT;
export const WRITE_A_DESCRIPTION = MSG.WRITE_A_DESCRIPTION;
export const WRITE_A_NOTE = MSG.WRITE_A_NOTE;
export const WRONG = "Incorrecto"; // TODO
export const WRONG_CODE = 'Código Erróneo';
export const WORKPLACE = "Centro de trabajo";//TODO
export const WORKSHOPS = MSG.WORKSHOPS;
export const WEEK_SCHEDULE = MSG.WEEK_SCHEDULE;
export const WEEK_FRIDAY_SCHEDULE = MSG.WEEK_FRIDAY_SCHEDULE;
// ----- X

// ----- Y

export const YEAR = MSG.YEAR;
export const YEARLY = "Anual";
export const YESTERDAY = MSG.YESTERDAY;

// ----- Z


//-------------DAYS

export const SUNDAY = MSG.SUNDAY;
export const MONDAY = MSG.MONDAY;
export const TUESDAY = MSG.TUESDAY;
export const WEDNESDAY = MSG.WEDNESDAY;
export const THURSDAY = MSG.THURSDAY;
export const FRIDAY = MSG.FRIDAY;
export const SATURDAY = MSG.SATURDAY;

//-------------MONTHS

export const JANUARY = MSG.JANUARY;
export const FEBRUARY = MSG.FEBRUARY;
export const MARCH = MSG.MARCH;
export const APRIL = MSG.APRIL;
export const MAY = MSG.MAY;
export const JUNE = MSG.JUNE;
export const JULY = MSG.JULY;
export const AUGUST = MSG.AUGUST;
export const SEPTEMBER = MSG.SEPTEMBER;
export const OCTOBER = MSG.OCTOBER;
export const NOVEMBER = MSG.NOVEMBER;
export const DECEMBER = MSG.DECEMBER;

