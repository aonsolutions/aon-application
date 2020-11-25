import * as MSG_EN from './msg-en.js';
import * as MSG_EUS from './msg-eus.js';
import * as MSG_CAT from './msg-cat.js';
import * as MSG_ES from './msg-es.js';

const ES = 'es';
const EN = 'en';
const EUS = 'eus';
const CAT = 'cat';


let language = localStorage.getItem('aon_language') || ES;
let msg = './msg-es.js'

let MSG = MSG_ES;

if(EN === language) {
  MSG = MSG_EN;
} else if(EUS === language){
  MSG = MSG_EUS;
} else if(CAT === language) {
  MSG = MSG_CAT;
}

export const AON_MSG_INVOICE = MSG.AON_MSG_INVOICE;
export const AON_MSG_INVOICES = MSG.AON_MSG_INVOICES;
export const AON_MSG_RESTORE_INVOICE = MSG.AON_MSG_RESTORE_INVOICE;
export const AON_MSG_REJECT_INVOICE = MSG.AON_MSG_REJECT_INVOICE;
export const AON_MSG_PRINT_INVOICE = MSG.AON_MSG_PRINT_INVOICE;
export const AON_MSG_SEND_INVOICE = MSG.AON_MSG_SEND_INVOICE;
export const AON_MSG_RECTIFY_INVOICE = MSG.AON_MSG_RECTIFY_INVOICE;
export const AON_MSG_DELETE_FOREVER_INVOICE_CONFIRMATION = MSG.AON_MSG_DELETE_FOREVER_INVOICE_CONFIRMATION;
export const AON_MSG_INVOICE_DATA = MSG.AON_MSG_INVOICE_DATA;
export const AON_MSG_INVOICE_CONCEPTS = MSG.AON_MSG_INVOICE_CONCEPTS;
export const AON_MSG_INVOICE_ISSUED = MSG.AON_MSG_INVOICE_ISSUED;
export const AON_MSG_INVOICE_RECEIVED = MSG.AON_MSG_INVOICE_RECEIVED;
export const AON_MSG_INVOICE_NUMBER = MSG.AON_MSG_INVOICE_NUMBER;

export const AON_MSG_TICKET = MSG.AON_MSG_TICKET;

export const AON_MSG_TAXES_DETAIL= MSG.AON_MSG_TAXES_DETAIL;

export const AON_MSG_EXPIRATIONS= MSG.AON_MSG_EXPIRATIONS;

export const AON_MSG_PAYMETHOD = MSG.AON_MSG_PAYMETHOD;
export const AON_MSG_TO_TRASH = MSG.AON_MSG_TO_TRASH;
export const AON_MSG_DELETE_FOREVER = MSG.AON_MSG_DELETE_FOREVER;
export const AON_MSG_ADD_FILE = MSG.AON_MSG_ADD_FILE;
export const AON_MSG_ADD_COMMENT = MSG.AON_MSG_ADD_COMMENT;

export const AON_MSG_DATE = MSG.AON_MSG_DATE;
export const AON_MSG_AMOUNT = MSG.AON_MSG_AMOUNT;
export const AON_MSG_HOLDER = MSG.AON_MSG_HOLDER;
