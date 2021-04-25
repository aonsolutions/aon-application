import * as MSG_EN from './msg-en.js';
import * as MSG_EUS from './msg-eus.js';
import * as MSG_CAT from './msg-cat.js';
import * as MSG_ES from './msg-es.js';
import * as MSG_GAL from './msg-gal.js';

const ES = 'es';
const EN = 'en';
const EUS = 'eus';
const CAT = 'cat';
const GAL = 'gal';

let language = localStorage.getItem('aon_language') || ES;

let MSG = undefined;

if(EN === language) {
  MSG = MSG_EN;
} else if(EUS === language){
  MSG = MSG_EUS;
} else if(CAT === language) {
  MSG = MSG_CAT;
} else if(GAL === language) {
  MSG = MSG_GAL;
} else {
  MSG = MSG_ES;
}

// ----- A

export const ACCEPT = MSG.ACCEPT;
export const ACCOUNT = MSG.ACCOUNT;
export const ACCOUNTED = MSG.ACCOUNTED;
export const ACCOUNTEDS = MSG.ACCOUNTEDS;
export const ACCOUNTING = MSG.ACCOUNTING;
export const ACTIVATE = MSG.ACTIVATE;
export const ADD_CATEGORY = MSG.ADD_CATEGORY;
export const ADD_COMMENT = MSG.ADD_COMMENT;
export const ADD_FILE = MSG.ADD_FILE;
export const ADD_INVOICE = MSG.ADD_INVOICE;
export const ADD_TAG = MSG.ADD_TAG;
export const ADDITIONAL_INFORMATION = MSG.ADDITIONAL_INFORMATION;
export const ADDRESS = MSG.ADDRESS;
export const ALL_FILES = MSG.ALL_FILES;
export const AMOUNT = MSG.AMOUNT;
export const API_DOCUMENTATION = MSG.API_DOCUMENTATION;
export const ASESOR = MSG.ASESOR;

// ----- B

export const BACK = MSG.BACK;
export const BACKGROUND_ADJUST = MSG.BACKGROUND_ADJUST;
export const BILLING = MSG.BILLING;
export const BLOCKED_POPUP = "Ventana bloqueada!";
export const BOOKING = MSG.BOOKING;
export const BOX_CRITERION = MSG.BOX_CRITERION;
export const BUDGET = MSG.BUDGET;
export const BUDGETS = MSG.BUDGETS;
export const BUSINESS_NAME = MSG.BUSINESS_NAME;

// ----- C

export const CANCEL = MSG.CANCEL;
export const CATEGORY = MSG.CATEGORY;
export const CATEGORIES = MSG.CATEGORIES;
export const CITY = MSG.CITY;
export const COMMENT = MSG.COMMENT;
export const COMMENTS = MSG.COMMENTS;
export const COMPANIES = MSG.COMPANIES;
export const COMPANY = MSG.COMPANY;
export const COMPANY_COSTS = MSG.COMPANY_COSTS;
export const CONCEPT = MSG.CONCEPT;
export const CONCEPTS = MSG.CONCEPTS;
export const CONFIDENTIAL = MSG.CONFIDENTIAL;
export const CONTACT = MSG.CONTACT;
export const CONTACTS = MSG.CONTACTS;
export const CONTRACT = MSG.CONTRACT;
export const COUNTRY = MSG.COUNTRY;
export const CREDITOR = MSG.CREDITOR;
export const CREDITORS = MSG.CREDITORS;
export const CUSTOMER = MSG.CUSTOMER;
export const CUSTOMERS = MSG.CUSTOMERS;

// ----- D

export const DATE = MSG.DATE;
export const DEACTIVATE = MSG.DEACTIVATE;
export const DELETE = MSG.DELETE;
export const DELETE_CATEGORY = MSG.DELETE_CATEGORY;
export const DELETE_CONFIRM = "Estas seguro de eliminar";
export const DELETE_FILE = MSG.DELETE_FILE;
export const DELETE_FOREVER = MSG.DELETE_FOREVER;
export const DELETE_FOREVER_INVOICE_CONFIRMATION = MSG.DELETE_FOREVER_INVOICE_CONFIRMATION;
export const DELETE_TAG = MSG.DELETE_TAG;
export const DELETED_DATA = MSG.DELETED_DATA;
export const DETAILED = MSG.DETAILED;
export const DOCUMENT = MSG.DOCUMENT;
export const DOCUMENTS = MSG.DOCUMENTS;
export const DOCUMENTARY = MSG.DOCUMENTARY;
export const DOWNLOAD = MSG.DOWNLOAD;
export const DOWNLOAD_FILE = MSG.DOWNLOAD_FILE;
export const DOWNLOAD_FILES = MSG.DOWNLOAD_FILES;
export const DOWNLOAD_INVOICE = MSG.DOWNLOAD_INVOICE;
export const DOWNLOAD_INVOICES = MSG.DOWNLOAD_INVOICES;
export const DUPLICATE = MSG.DUPLICATE;
export const DUPLICATE_INVOICE = MSG.DUPLICATE_INVOICE;
export const DURATION = "Duración";

// ----- E

export const EDIT = MSG.EDIT;
export const EDIT_CATEGORY = MSG.EDIT_CATEGORY;
export const EDIT_FILE = MSG.EDIT_FILE;
export const EDIT_FILES = MSG.EDIT_FILES;
export const EDIT_TAG = MSG.EDIT_TAG;
export const EMAIL = MSG.EMAIL;
export const EMPLOYEE = MSG.EMPLOYEE;
export const ENTERPRISE = MSG.ENTERPRISE;
export const EXPIRATIONS = MSG.EXPIRATIONS;

// ----- F

export const FAX = MSG.FAX;
export const FILE = MSG.FILE;
export const FILE_DATA = MSG.FILE_DATA;
export const FILTER = "Filtro";
export const FILTERS = "Filtros";
export const FOOTER = MSG.FOOTER;

// ----- G

export const GENERAL_INFORMATION = MSG.GENERAL_INFORMATION;

// ----- H

export const HEADER = MSG.HEADER;
export const HOLDER = MSG.HOLDER;

// ----- I

export const INBOX = MSG.INBOX;
export const INVOICE = MSG.INVOICE;
export const INVOICE_CONCEPTS = MSG.INVOICE_CONCEPTS;
export const INVOICE_DATA = MSG.INVOICE_DATA;
export const INVOICE_ISSUED = MSG.INVOICE_ISSUED;
export const INVOICE_NUMBER = MSG.INVOICE_NUMBER;
export const INVOICE_RECEIVED = MSG.INVOICE_RECEIVED;
export const INVOICES = MSG.INVOICES;
export const IRPF = MSG.IRPF;
export const ISSUED = MSG.ISSUED;
export const ISSUEDS = MSG.ISSUEDS;

// ----- J

// ----- K

// ----- L

export const LAST_LOCATION = "Última ubicación";
export const LAST_STATUS = "Último estado";
export const LOCATION = "Ubicación";

// ----- M

export const MODEL_111 = MSG.MODEL_111;
export const MODEL_111_DESCRIPTION = MSG.MODEL_111_DESCRIPTION;
export const MODEL_190 = MSG.MODEL_190;
export const MODEL_190_DESCRIPTION = MSG.MODEL_190_DESCRIPTION;

// ----- N

export const NAME = MSG.NAME;
export const NEW_COMPANY = MSG.NEW_COMPANY;
export const NEXT = MSG.NEXT;
export const NUMBER = MSG.NUMBER;

// ----- O

// ----- P

export const PAYMETHOD = MSG.PAYMETHOD;
export const PAYROLL = "Nómina";
export const PAYROLLS = "Nóminas";
export const PAYSHEET = MSG.PAYSHEET;
export const PAYSHEETS = MSG.PAYSHEETS;
export const PENDING = MSG.PENDING;
export const PENDING_DOCUMENTS = MSG.PENDING_DOCUMENTS;
export const PENDING_INVOICES = MSG.PENDING_INVOICES;
export const PENDINGS = MSG.PENDINGS;
export const PERMISSIONS = MSG.PERMISSIONS;
export const PHONE = MSG.PHONE;
export const POSTAL_CODE = MSG.POSTAL_CODE;
export const PREVIOUS = MSG.PREVIOUS;
export const PRICE = MSG.PRICE;
export const PRINT_INVOICE = MSG.PRINT_INVOICE;
export const PRINTING_INVOICES = MSG.PRINTING_INVOICES;
export const PROCESSED_MOVEMENT = MSG.PROCESSED_MOVEMENT;
export const PROVINCE = MSG.PROVINCE;

// ----- Q

export const QUANTITY = MSG.QUANTITY;

// ----- R

export const RADIO = "Radio";
export const RECEIVED = MSG.RECEIVED;
export const RECEIVEDS = MSG.RECEIVEDS;
export const RECENTS = MSG.RECENTS;
export const RECORD = MSG.RECORD;
export const RECORD_INVOICE = MSG.RECORD_INVOICE;
export const RECTIFY = MSG.RECTIFY;
export const RECTIFY_INVOICE = MSG.RECTIFY_INVOICE;
export const REFERENCE = MSG.REFERENCE;
export const REJECT = MSG.REJECT;
export const REJECT_INVOICE = MSG.REJECT_INVOICE;
export const REJECT_INVOICES = MSG.REJECT_INVOICES;
export const REJECTED = MSG.REJECTED;
export const REJECTED_INVOICES = MSG.REJECTED_INVOICES;
export const REJECTEDS = MSG.REJECTEDS;
export const RESTORE = MSG.RESTORE;
export const RESTORE_INVOICE = MSG.RESTORE_INVOICE;
export const RESTORE_INVOICES = MSG.RESTORE_INVOICES;
export const RESUME_COSTS = "Resumen de costes";

// ----- S

export const SAVE = MSG.SAVE;
export const SAVED_DATA = MSG.SAVED_DATA;
export const SCOPE = MSG.SCOPE;
export const SEND = MSG.SEND;
export const SEND_FILE = MSG.SEND_FILE;
export const SEND_FILES = MSG.SEND_FILES;
export const SEND_INVOICE = MSG.SEND_INVOICE;
export const SEND_INVOICES = MSG.SEND_INVOICES;
export const SEPA_FILES = MSG.SEPA_FILES;
export const SERIE = MSG.SERIE;
export const SETTING = MSG.SETTING;
export const SHOW_FILE = MSG.SHOW_FILE;
export const SII = MSG.SII;
export const SII_TICKETBAI = MSG.SII_TICKETBAI;
export const SIZE = MSG.SIZE;
export const STATUS = "Estado";
export const SUPPLIED = MSG.SUPPLIED;
export const SUPPLIER = MSG.SUPPLIER;
export const SUPPLIERS = MSG.SUPPLIERS;
export const SURNAME = MSG.SURNAME;
export const SYSTEM_MESSAGES = MSG.SYSTEM_MESSAGES;

// ----- T

export const TAG = MSG.TAG;
export const TAGS = MSG.TAGS;
export const TAXES_DETAIL= MSG.TAXES_DETAIL;
export const TICKET = MSG.TICKET;
export const TICKETBAI = MSG.TICKETBAI;
export const TICKETS = MSG.TICKETS;
export const TITULAR_DATA = MSG.TITULAR_DATA;
export const TO_TRASH = MSG.TO_TRASH;
export const TOTAL = MSG.TOTAL;
export const TOTAL_SUPPLIED = MSG.TOTAL_SUPPLIED;
export const TRASH = MSG.TRASH;
export const TYPE = MSG.TYPE;
export const TYPES = MSG.TYPES;

// ----- U

export const UPDATED_CONTRACT = MSG.UPDATED_CONTRACT;
export const UPLOAD = MSG.UPLOAD;
export const UPLOAD_FILE = MSG.UPLOAD_FILE;
export const USER = MSG.USER;
export const USER_DATA = MSG.USER_DATA;
export const USER_MANAGEMENT = MSG.USER_MANAGEMENT;
export const USERS = MSG.USERS;

// ----- V

export const VAT = MSG.VAT;
export const VIEW_PAYROLL = "Ver nómina";
export const VIEW_PAYROLLS = "Ver nóminas";

// ----- W

export const WEB = MSG.WEB;

// ----- X

// ----- Y

// ----- Z
