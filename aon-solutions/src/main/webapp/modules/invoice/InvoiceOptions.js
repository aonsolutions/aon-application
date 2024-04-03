import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"
import * as UA from '../../services/userAgentService.js';

  export const CREATE_INVOICE_ISSUED = {
    id: CONSTANT.CREATE_INVOICE_ISSUED.initCap(),
    name: MSG.ISSUEDS,
    icon: MATERIAL_ICONS.UNARCHIVE
  }

  export const CREATE_INVOICE_RECEIVED = {
    id: CONSTANT.CREATE_INVOICE_RECEIVED.initCap(),
    name: MSG.RECEIVEDS,
    icon: MATERIAL_ICONS.ARCHIVE
  }

  export const CREATE_INVOICE_TICKET = {
    id: CONSTANT.CREATE_INVOICE_TICKET.initCap(),
    name: MSG.TICKET,
    icon: MATERIAL_ICONS.RECEIPT
  }

  // ***** INVOICES *****

  // INVOICE
  export const INVOICE_ISSUED = {
    id: CONSTANT.INVOICE_ISSUED.initCap(),
    name: MSG.ISSUEDS,
    icon: MATERIAL_ICONS.UNARCHIVE
  }

  export const INVOICE_RECEIVED = {
    id: CONSTANT.INVOICE_RECEIVED.initCap(),
    name: MSG.RECEIVEDS,
    icon: MATERIAL_ICONS.ARCHIVE
  }

  export const INVOICE_TICKET = {
    id: CONSTANT.INVOICE_TICKET.initCap(),
    name: MSG.TICKET,
    icon: MATERIAL_ICONS.RECEIPT
  }

  // RAWDOC & INVOFOX

  export const RAWDOC_INBOX_ISSUED = {
    id: CONSTANT.RAWDOC_INBOX_ISSUED.initCap(),
    name: MSG.ISSUEDS,
    icon: MATERIAL_ICONS.UNARCHIVE
  }

  export const RAWDOC_INBOX_RECEIVED = {
    id: CONSTANT.RAWDOC_INBOX_RECEIVED.initCap(),
    name: MSG.RECEIVEDS,
    icon: MATERIAL_ICONS.ARCHIVE
  }

  export const RAWDOC_INBOX_TICKET = {
    id: CONSTANT.RAWDOC_INBOX_TICKET.initCap(),
    name: MSG.TICKET,
    icon: MATERIAL_ICONS.RECEIPT
  }

  export const RAWDOC_INBOX = {
    id: CONSTANT.RAWDOC_INBOX.initCap(),
    name: MSG.PROFORMA,
    icon: MATERIAL_ICONS.INBOX,
    clickable: true,
    options: [RAWDOC_INBOX_ISSUED, RAWDOC_INBOX_RECEIVED, RAWDOC_INBOX_TICKET]
  }

  export const RAWDOC_REJECT = {
    id: CONSTANT.RAWDOC_REJECT.initCap(),
    name: MSG.PENDING_REVIEW,
    icon: MATERIAL_ICONS.ERROR
  }
  
  export const RAWDOC_DRAFT = {
    id: CONSTANT.RAWDOC_DRAFT.initCap(),
    name: MSG.TRASH,
    icon: MATERIAL_ICONS.DELETE
  }

  export const INVOICE_PENDINGS = {
    id: CONSTANT.PENDINGS.initCap(),
    name: MSG.PENDINGS,
    icon: MATERIAL_ICONS.INBOX,
    clickable: true,
    opened: true,
    options: [RAWDOC_INBOX_ISSUED, RAWDOC_INBOX_RECEIVED, RAWDOC_INBOX_TICKET]
  }

  // MAIN OPTION

  export const INVOICES = {
    id: CONSTANT.INVOICES.initCap(),
    title: MSG.INVOICES,
    name: MSG.INVOICES,
    options: [INVOICE_ISSUED, INVOICE_RECEIVED, INVOICE_TICKET,
      INVOICE_PENDINGS, RAWDOC_REJECT, RAWDOC_DRAFT]
  }

  // ********************
  
  // **** MANAGEMENT ****

  export const REGISTRY_CUSTOMER = {
    id: CONSTANT.REGISTRY_CUSTOMER.initCap(),
    name: MSG.CUSTOMERS,
    icon: MATERIAL_ICONS.CONTACT_PAGE
  }
  
  export const REGISTRY_SUPPLIER = {
    id: CONSTANT.REGISTRY_SUPPLIER.initCap(),
    name: MSG.SUPPLIERS,
    icon: MATERIAL_ICONS.CONTACT_PAGE
  }
  
  export const REGISTRY_CREDITOR = {
    id: CONSTANT.REGISTRY_CREDITOR.initCap(),
    name: MSG.CREDITORS,
    icon: MATERIAL_ICONS.CONTACT_PAGE
  }

  export const REGISTRY = {
    id: CONSTANT.HOLDERS.initCap(),
    name: MSG.HOLDERS,
    icon: MATERIAL_ICONS.PEOPLE,
    clickable: false,
    options: [REGISTRY_CUSTOMER, REGISTRY_SUPPLIER, REGISTRY_CREDITOR]
  }

  export const PRODUCT = {
    id: CONSTANT.PRODUCT.initCap(),
    name: MSG.PRODUCTS,
    icon: MATERIAL_ICONS.INVENTORY_2
  }

  export const EXPENSES = {
    id: CONSTANT.EXPENSES.initCap(),
    name: MSG.EXPENSES,
    icon: MATERIAL_ICONS.INVENTORY_2
  }

  export const CHARGES_PAYMENTS = {
    id: CONSTANT.CHARGES_PAYMENTS.initCap(),
    name: MSG.CHARGES_AND_PAYMENTS,
    icon: MATERIAL_ICONS.PAYMENT
  }

  export const VAT_PANEL = {
    id: CONSTANT.VAT_PANEL.initCap(),
    name: MSG.VAT_PANEL,
    icon: MATERIAL_ICONS.PAYMENT
  }

  export const RETENTION_PANEL = {
    id: CONSTANT.RETENTION_PANEL.initCap(),
    name: MSG.RETENTION_PANEL,
    icon: MATERIAL_ICONS.PAYMENT
  }

  export const INVEST = {
    id: CONSTANT.INVEST_ASSET.initCap(),
    name: MSG.INVEST_ASSET,
    icon: MATERIAL_ICONS.INVENTORY_2
  }
  
  export const CONCEPTS = {
    id: CONSTANT.CONCEPTS.initCap(),
    name: MSG.CONCEPTS,
    icon: MATERIAL_ICONS.LOCAL_MALL,
    clickable: false,
    options: [PRODUCT, EXPENSES, INVEST]
  }

  // MAIN OPTION

  export const MANAGEMENT = {
    id: CONSTANT.MANAGEMENT.initCap(),
    title: MSG.MANAGEMENT,
    name: MSG.MANAGEMENT,
    options:[ REGISTRY, CONCEPTS, CHARGES_PAYMENTS, VAT_PANEL, RETENTION_PANEL ]
    //  UA.isMobile() ? 
      
      // : [ REGISTRY, PRODUCT ]
  }

  // ********************    

  export const getOptions = (dur) => {
    return [INVOICES, MANAGEMENT];
  }