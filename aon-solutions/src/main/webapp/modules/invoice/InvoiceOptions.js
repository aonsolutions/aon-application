import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js"
import { PAYROLL } from "../../environments/msg-en.js";
import { INCOMES } from "../../environments/msg.js";
import * as GWT from "../../gwt/gwt.js";

  export const gwtLoad = (option) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    document.body.classList.add('gwt-Selector');
    GWT.iLoad(option, application.CONTENT);
  }

  export const newInvoice = (type) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.aonInvoice(type)
  }

  export const invoiceList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildInvoiceToolbarOptions(filter && filter.status === 'accounting', filter && filter.status === CONSTANT.PROCESSING);
    parent.aonInvoiceList(filter);
  }

  export const customerList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildCustomerToolbarOptions();
    parent.aonCustomerList(filter);
  }

  export const supplierList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildSupplierToolbarOptions();
    parent.aonSupplierList(filter);
  }

  export const creditorList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildCreditorToolbarOptions();
    parent.aonCreditorList(filter);
  }

  export const productList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildProductToolbarOptions();
    parent.aonProductList(filter);
  }

  export const expenseList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildExpenseToolbarOptions();
    parent.aonProductList(filter);
  }

  export const investList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildInvestToolbarOptions();
    parent.aonInvestList(filter);
  }


  export const closingInvoiceList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildClosingInvoiceToolbarOptions();
    parent.aonClosingInvoiceList(filter);
  }

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
    name: MSG.ISSUED,
    icon: MATERIAL_ICONS.UNARCHIVE,
    fn: () => invoiceList({
      status: "accounting",
      type: "sales",
      page: 1,
      per_page: 50,
    })
  }

  export const INVOICE_ISSUED_BETA = {
    id: CONSTANT.INVOICE_ISSUED.initCap(),
    name: MSG.ISSUED_INVOICES,
    icon: MATERIAL_ICONS.UNARCHIVE,
    fn: () => invoiceList({
      status: "accounting",
      type: "sales",
      page: 1,
      per_page: 50,
    })
  }


  export const INVOICE_RECEIVED = {
    id: CONSTANT.INVOICE_RECEIVED.initCap(),
    name: MSG.RECEIVEDS,
    icon: MATERIAL_ICONS.ARCHIVE,
    fn: () => invoiceList({
      status: "accounting",
      type: "purchase,expenses",
      page: 1,
      per_page: 50,
    })
  }

  export const INVOICE_RECEIVED_BETA = {
    id: CONSTANT.INVOICE_RECEIVED.initCap(),
    name: MSG.RECEIVED_INVOICES,
    icon: MATERIAL_ICONS.ARCHIVE,
    fn: () => invoiceList({
      status: "accounting",
      type: "purchase,expenses",
      page: 1,
      per_page: 50,
    })
  }

  export const INVOICE_TICKET = {
    id: CONSTANT.INVOICE_TICKET.initCap(),
    name: MSG.SIMPLIFIED_INVOICES + "/" + MSG.TICKETS,
    icon: MATERIAL_ICONS.RECEIPT,
    fn: () => invoiceList({
      status: "accounting",
      type: "ticket",
      page: 1,
      per_page: 50,
    })
  }

  // RAWDOC 

  export const RAWDOC_INBOX_ISSUED = {
    id: CONSTANT.RAWDOC_INBOX_ISSUED.initCap(),
    name: MSG.ISSUEDS,
    icon: MATERIAL_ICONS.UNARCHIVE,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "emitida" })
  }

  export const PROFORMA_INVOICES = {
    id: CONSTANT.PROFORMA_INVOICES.initCap(),
    name: MSG.PROFORMA_INVOICES,
    icon: MATERIAL_ICONS.UNARCHIVE,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "emitida" })
  }

  export const RAWDOC_INBOX_RECEIVED = {
    id: CONSTANT.RAWDOC_INBOX_RECEIVED.initCap(),
    name: MSG.RECEIVEDS,
    icon: MATERIAL_ICONS.ARCHIVE,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "recibida" })
  }

  export const RAWDOC_INBOX_RECEIVED_NEW = {
    id: CONSTANT.RAWDOC_INBOX_RECEIVED_NEW.initCap(),
    name: MSG.DRAFT + " " + MSG.RECEIVED_INVOICES,
    icon: MATERIAL_ICONS.ARCHIVE,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "recibida" })
  }

  export const RAWDOC_INBOX_RECEIVED_DRAFT = {
    id: CONSTANT.RAWDOC_INBOX_RECEIVED.initCap(),
    name: MSG.PENDING_DRAFTS,
    icon: MATERIAL_ICONS.ARCHIVE,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "recibida" })
  }


  export const RAWDOC_INBOX_TICKET = {
    id: CONSTANT.RAWDOC_INBOX_TICKET.initCap(),
    name: MSG.TICKET,
    icon: MATERIAL_ICONS.RECEIPT,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "ticket" })
  }
  
  export const RAWDOC_INBOX_TICKET_NEW = {
    id: CONSTANT.RAWDOC_INBOX_TICKET_NEW.initCap(),
    name: MSG.DRAFT + " " + MSG.TICKET,
    icon: MATERIAL_ICONS.RECEIPT,
    fn: () => invoiceList({ status: CONSTANT.INBOX, type: "ticket" })
  }

  export const RAWDOC_INBOX = {
    id: CONSTANT.RAWDOC_INBOX.initCap(),
    name: MSG.PROFORMA,
    icon: MATERIAL_ICONS.INBOX,
    clickable: true,
    options: [RAWDOC_INBOX_ISSUED, RAWDOC_INBOX_RECEIVED, RAWDOC_INBOX_TICKET]
  }

  export const RAWDOC_PROCESSING = {
    id: CONSTANT.RAWDOC_PROCESSING.initCap(),
    name: MSG.PROCCESSING,
    icon: MATERIAL_ICONS.SCHEDULE,
    fn: () => invoiceList({ status: CONSTANT.PROCESSING })
  }

  export const RAWDOC_REJECT = {
    id: CONSTANT.RAWDOC_REJECT.initCap(),
    name: MSG.REVIEW,
    icon: MATERIAL_ICONS.REPORT,
    fn: () => invoiceList( { status: CONSTANT.REJECTED })
  }
  
  export const RAWDOC_TRASH = {
    id: CONSTANT.RAWDOC_TRASH.initCap(),
    name: MSG.TRASH,
    icon: MATERIAL_ICONS.DELETE,
    fn: () => invoiceList( { status: CONSTANT.DRAFT })
  }

  export const INVOICE_PENDINGS = {
    id: CONSTANT.PENDINGS.initCap(),
    name: MSG.PENDING_INVOICES,
    icon: MATERIAL_ICONS.INBOX,
    opened: true,
    options: [RAWDOC_INBOX_ISSUED, RAWDOC_INBOX_RECEIVED, RAWDOC_INBOX_TICKET]
  }

  export const OTHER_INCOMES = {
    id: CONSTANT.OTHER_INCOMES.initCap(),
    name: MSG.OTHER_INCOMES,
    icon: MATERIAL_ICONS.EURO,
    fn: () => alert("EN DESARROLLO")
  }

  export const OFFERS = {
    id: CONSTANT.OFFERS.initCap(),
    name: MSG.OFFERS,
    icon: MATERIAL_ICONS.CONTRACT,
    fn: () => alert("EN DESARROLLO")
  }

  export const OTHER_EXPENSES = {
    id: CONSTANT.OTHER_EXPENSES.initCap(),
    name: MSG.OTHER_EXPENSES,
    icon: MATERIAL_ICONS.EURO,
    fn: () => alert("EN DESARROLLO")
  }

  export const STAFF_EXPENSES = {
    id: CONSTANT.STAFF_EXPENSES.initCap(),
    name: MSG.STAFF_EXPENSES,
    icon: MATERIAL_ICONS.GROUP,
    fn: () => alert("EN DESARROLLO")    
  }

  // MAIN OPTION

  export const INVOICES = {
    id: CONSTANT.INVOICES.initCap(),
    title: MSG.INVOICES,
    name: MSG.INVOICES,
    options: [INVOICE_ISSUED, INVOICE_RECEIVED, INVOICE_TICKET,
      INVOICE_PENDINGS, RAWDOC_PROCESSING, RAWDOC_REJECT, RAWDOC_TRASH]
  }

  export const MAIN_INCOMES = {
    id: CONSTANT.INCOMES.initCap(),
    title: MSG.INCOMES,
    name: MSG.INCOMES,
    options: [INVOICE_ISSUED_BETA, PROFORMA_INVOICES, OTHER_INCOMES, OFFERS ]
  }

  export const MAIN_EXPENSES = {
    id: CONSTANT.EXPENSES.initCap(),
    title: MSG.EXPENSES,
    name: MSG.EXPENSES,
    options: [INVOICE_RECEIVED_BETA, RAWDOC_INBOX_RECEIVED_NEW, INVOICE_TICKET, RAWDOC_INBOX_TICKET_NEW, OTHER_EXPENSES, STAFF_EXPENSES]
  }

  export const MAIN_DOCUMENTS = {
    id: CONSTANT.DOCUMENT.initCap(),
    title: MSG.PENDING,
    name: MSG.PENDING,
    options: [RAWDOC_PROCESSING, RAWDOC_REJECT, RAWDOC_TRASH]
  }

  // ********************
  
  // **** MANAGEMENT ****

  export const REGISTRY_CUSTOMER = {
    id: CONSTANT.REGISTRY_CUSTOMER.initCap(),
    name: MSG.CUSTOMERS,
    icon: MATERIAL_ICONS.CONTACT_PAGE,
    fn: () => customerList()
  }
  
  export const REGISTRY_SUPPLIER = {
    id: CONSTANT.REGISTRY_SUPPLIER.initCap(),
    name: MSG.SUPPLIERS,
    icon: MATERIAL_ICONS.CONTACT_PAGE,
    fn: () => supplierList()
  }
  
  export const REGISTRY_CREDITOR = {
    id: CONSTANT.REGISTRY_CREDITOR.initCap(),
    name: MSG.CREDITORS,
    icon: MATERIAL_ICONS.CONTACT_PAGE,
    fn: () => creditorList()
  }

  export const REGISTRY = {
    id: CONSTANT.HOLDERS.initCap(),
    name: MSG.CONTACTS,
    icon: MATERIAL_ICONS.PEOPLE,
    clickable: false,
    options: [REGISTRY_CUSTOMER, REGISTRY_SUPPLIER, REGISTRY_CREDITOR]
  }

  export const PRODUCT = {
    id: CONSTANT.PRODUCT.initCap(),
    name: MSG.PRODUCTS_AND_SERVICES,
    icon: MATERIAL_ICONS.INVENTORY_2,
    fn: () => productList({ expense: false })
  }

  export const EXPENSES = {
    id: CONSTANT.EXPENSES.initCap(),
    name: MSG.EXPENSES,
    icon: MATERIAL_ICONS.INVENTORY_2,
    fn: () => expenseList({ expense: true })
  }

  export const CHARGES_PAYMENTS = {
    id: CONSTANT.CHARGES_PAYMENTS.initCap(),
    name: MSG.CHARGES_AND_PAYMENTS,
    icon: MATERIAL_ICONS.PAYMENT,
    fn: () => gwtLoad(GWT.FINANCE)
  }

  export const VAT_PANEL = {
    id: CONSTANT.VAT_PANEL.initCap(),
    name: MSG.VAT_PANEL,
    icon: MATERIAL_ICONS.PAYMENT,
    fn: () => gwtLoad(GWT.VAT_REPORT)
  }

  export const RETENTION_PANEL = {
    id: CONSTANT.RETENTION_PANEL.initCap(),
    name: MSG.RETENTION_PANEL,
    icon: MATERIAL_ICONS.PAYMENT,
    fn: () => gwtLoad(GWT.IRPF_REPORT)
  }

  export const FISCAL_DRAFT = {
    id: "fiscalModelDraft",
    name: "Precálculo Impuestos",
    icon: MATERIAL_ICONS.PAYMENT
  }

  export const INVEST = {
    id: CONSTANT.INVEST_ASSET.initCap(),
    name: MSG.INVEST_ASSET,
    icon: MATERIAL_ICONS.INVENTORY_2,
    fn: () => investList({})
  }
  
  export const CONCEPTS = {
    id: CONSTANT.CONCEPTS.initCap(),
    name: MSG.CONCEPTS,
    icon: MATERIAL_ICONS.LOCAL_MALL,
    clickable: false,
    options: [PRODUCT, EXPENSES, INVEST]
  }

  export const CLOSING_INVOICE = {
    id: 'ClosingInvoice',
    name: "Cierre de Facturación",
    icon: "disabled_by_default",
    fn: () => closingInvoiceList()
  }

  // MAIN OPTION

  export const MANAGEMENT = {
    id: CONSTANT.MANAGEMENT.initCap(),
    title: MSG.MANAGEMENT,
    name: MSG.MANAGEMENT,
    options:[ REGISTRY, CONCEPTS, CHARGES_PAYMENTS, FISCAL_DRAFT] //, CLOSING_INVOICE ]
  }


  export const INVOICE_SEARCH_OPTIONS = [
    {
      type: CONSTANT.DATE,
      name: "startDate",
      id: "startDate",
      title: MSG.FROM,
    },
    {
      type: CONSTANT.DATE,
      name: "endDate",
      id: "endDate",
      title: MSG.TO,
    },
    {
      type: CONSTANT.SELECT,
      name: "recorded",
      id: "recorded",
      title: MSG.STATUS
    },
  ];


  export const NEW_ISSUED_INVOICE = {
    name: MSG.ISSUEDS,
    title: MSG.ISSUEDS,
    icon: MATERIAL_ICONS.UNARCHIVE,
    permission: true,
    backgroundColor: "#4472C4",
    fn: () => newInvoice("emitida")
  };

  export const NEW_RECEIVED_INVOICE = {
    name: MSG.RECEIVEDS,
    title: MSG.RECEIVEDS,
    icon: MATERIAL_ICONS.ARCHIVE,
    permission: true,
    backgroundColor: "#4472C4",
    fn: () => newInvoice("recibida")
  };

  export const NEW_TICKET = {
    name: MSG.TICKET,
    title: MSG.TICKET,
    icon: MATERIAL_ICONS.RECEIPT,
    permission: true,
    backgroundColor: "#4472C4",
    fn: () => newInvoice("ticket")
  };

  // ********************    

  export const getOptions = (beta) => {
    return beta ? [MAIN_INCOMES, MAIN_EXPENSES, MAIN_DOCUMENTS, MANAGEMENT] : [INVOICES, MANAGEMENT];
  }

  export const getNewOptions = () => {
    return [NEW_ISSUED_INVOICE, NEW_RECEIVED_INVOICE, NEW_TICKET];
  }