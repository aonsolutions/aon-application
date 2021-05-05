import { CONSTANT, MATERIAL_ICONS, MSG } from "../../environments/environments.js"

export const RAWDOC_INBOX = {
    id: CONSTANT.RAWDOC_INBOX.initCap(),
    name: MSG.INBOX,
    icon: MATERIAL_ICONS.INBOX
  }
  
  export const RAWDOC_REJECT = {
    id: CONSTANT.RAWDOC_REJECT.initCap(),
    name: MSG.REJECTEDS,
    icon: MATERIAL_ICONS.REPORT
  }
  
  export const RAWDOC_DRAFT = {
    id: CONSTANT.RAWDOC_DRAFT.initCap(),
    name: MSG.TRASH,
    icon: MATERIAL_ICONS.DELETE
  }
  
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
  
  // export const REGISTRY_TARGET = {
  //   id: CONSTANT.REGISTRY_TARGET.initCap(),
  //   name: MSG.TARGETS,
  //   icon: MATERIAL_ICONS.CONTACT_PAGE
  // }
  
  export const OFFER = {
    id: CONSTANT.OFFER.initCap(),
    name: MSG.PENDINGS,
    icon: MATERIAL_ICONS.PENDING_ACTIONS
  }

  export const PRODUCT = {
    id: CONSTANT.PRODUCT.initCap(),
    name: MSG.PRODUCTS,
    icon: MATERIAL_ICONS.PENDING_ACTIONS
  }

  export const CONFIGURATION_PRINT = {
    id: CONSTANT.CONFIGURATION_PRINT.initCap(),
    name: MSG.PRINTING_INVOICES,
    icon: MATERIAL_ICONS.PRINT
  }

  export const CONFIGURATION_SII_TBAI = {
    id: CONSTANT.CONFIGURATION_SII_TBAI.initCap(),
    name: MSG.SII_TICKETBAI,
    icon: MATERIAL_ICONS.SETTING    
  }   