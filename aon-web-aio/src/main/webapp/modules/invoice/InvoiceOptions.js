import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import * as GWT from "../../gwt/gwt.js";
import * as JSF from "../aon-jsf-app.js";
import * as UA from "../../services/userAgentService.js";
import { AonInvoiceIssued } from "./aon-invoice-issued.js";
import { AonInvoiceProcessing } from "./aon-invoice-processing.js";

  export const jsfOfferLoad = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildEmptyToolbarOptions();
    application.setContent(new JSF.AonJsfOffer());
  }

  export const jsfOfferFormLoad = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildEmptyToolbarOptions();
    application.setContent(new JSF.AonJsfOfferForm());
  }

  export const gwtLoad = (option) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    document.body.classList.add('gwt-Selector');
    GWT.iLoad(option, application.CONTENT);
  }

  export const newInvoice = (type) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.aonInvoice(type);
  }

  export const invoiceList = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildInvoiceToolbarOptions(filter && filter.status === 'accounting', filter && filter.status === CONSTANT.PROCESSING);
    parent.aonInvoiceList(filter);
  }

  export const invoiceIssued = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    application.setContent(new AonInvoiceIssued());
  }

  export const invoiceProcessing = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    application.setContent(new AonInvoiceProcessing());
  }

  export const income = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildIncomeToolbarOptions();
    parent.aonIncome(/*filter*/);
  }

  export const expense = (filter) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildExpenseToolbarOptions();
    parent.aonExpense(/*filter*/);
  }

  export const info = (title, description) => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    application.confirmDialog(title, description, () => {});
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

  export const createIssuedInvoice = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildClosingInvoiceToolbarOptions();
    parent.aonInvoice('emitida');
  }

  export const createReceivedInvoice = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildClosingInvoiceToolbarOptions();
    parent.aonInvoice('recibida');
  }

  export const createTicketInvoice = () => {
     let application = document.querySelector(TAG.AON_APPLICATION);
      let parent = application.getParent();
      parent.buildClosingInvoiceToolbarOptions();
     parent.aonInvoice('ticket');
  }

  export const createOtherIncomes = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildIncomeToolbarOptions();
    parent.aonNewIncome();
  }

  export const createOtherExpenses = () => {
    let application = document.querySelector(TAG.AON_APPLICATION);
    let parent = application.getParent();
    parent.buildExpenseToolbarOptions();
    parent.aonNewExpense();
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
    fn: () => invoiceIssued()
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
    name: MSG.SIMPLIFIED_INVOICES,
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
    icon: "edit_document",
    actions: [
      {
        id: 'New',
        icon: 'add',
        action: () => newInvoice("emitida")
      }
    ],
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
    name: MSG.DRAFT + " F." + MSG.RECEIVEDS,
    icon: "edit_document",
    actions: [
      {
        id: 'New',
        icon: 'add',
        action: () => newInvoice("recibida")
      }
    ],
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
    name: MSG.DRAFT + " " + MSG.SIMPLIFIED+"/"+MSG.TICKETS,
    icon: "edit_note",
    actions: [
      {
        id: 'New',
        icon: 'add',
        action: () => newInvoice("ticket")
      }
    ],
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
    fn: () => invoiceProcessing()
  }

  export const RAWDOC_REJECT = {
    id: CONSTANT.RAWDOC_REJECT.initCap(),
    name: MSG.TO_REVIEW,
    icon: MATERIAL_ICONS.REPORT,
    fn: () => invoiceList( { status: CONSTANT.REJECTED })
  }
  
  export const RAWDOC_TRASH = {
    id: CONSTANT.RAWDOC_TRASH.initCap(),
    name: MSG.TRASH,
    icon: MATERIAL_ICONS.DELETE,
    fn: () => invoiceList( { status: CONSTANT.TRASH })
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
    icon: "add_card",
    actions: [
      {
        id: 'New',
        icon: 'add',
        action: () => createOtherIncomes()
      }
    ],
    fn: () => income()
  }

  export const OFFERS = {
    id: CONSTANT.OFFERS.initCap(),
    name: MSG.OFFERS,
    icon: MATERIAL_ICONS.CONTRACT,
    actions: [
      {
        id: 'New',
        icon: 'add',
        action: () => jsfOfferFormLoad()
      }
    ],
    fn: () => jsfOfferLoad()
  }

  export const OTHER_EXPENSES = {
    id: CONSTANT.OTHER_EXPENSES.initCap(),
    name: MSG.OTHER_EXPENSES,
    icon: "account_balance_wallet",
    actions: [
      {
        id: 'New',
        icon: 'add',
        action: () => createOtherExpenses()
      }
    ],
    fn: () => expense()
  }

  export const STAFF_EXPENSES = {
    id: CONSTANT.STAFF_EXPENSES.initCap(),
    name: MSG.STAFF_EXPENSES,
    icon: MATERIAL_ICONS.GROUP,
    fn: () => alert("EN DESARROLLO. Use temporalmente la opción de otros gastos.")
  }

  // MAIN OPTION
  export const getMainIncomesList = () => {
      return UA.isMobile()
        ? [INVOICE_ISSUED_BETA, PROFORMA_INVOICES, OTHER_INCOMES]
        : [INVOICE_ISSUED_BETA, PROFORMA_INVOICES, OTHER_INCOMES, OFFERS ]
  }

  export const MAIN_INCOMES = {
    id: CONSTANT.INCOMES.initCap(),
    title: MSG.INCOMES,
    name: MSG.INCOMES,
    options: getMainIncomesList(),
    button: {
      id: CONSTANT.INCOMES.initCap() + 'Info',
      title: MSG.INFO_INCOMES,
      icon: 'info',
      fn: () => info(
          MSG.INCOMES,
          `<b>Facturas Emitidas:</b> Facturas que envías a tus clientes por los servicios prestados o por la venta de tus productos.<br><br>
           <b>Facturas Proforma:</b> Borrador de factura en elaboración y que se pueden enviar al cliente como "PROFORMA" para que conozcan el coste del servicio o productos y las condiciones del mismo antes de emitir la factura definitiva para su evaluación y conformidad previa a la emisión de la factura definitiva.<br><br>
           <b>Otros Ingresos:</b> Son aquellos ingresos que recibes que no provienen de tu actividad económica como por ejemplo subvenciones, intereses bancarios, etc.<br><br>
           <b>Presupuestos:</b> Documento que detalla el coste del servicio o venta de productos que se va a realizar con un cliente.`
      )
    }
  }

  export const MAIN_INCOMES_TRIAL = {
    id: CONSTANT.INCOMES.initCap(),
    title: MSG.INCOMES,
    name: MSG.INCOMES,
    options: [INVOICE_ISSUED_BETA, PROFORMA_INVOICES, OTHER_INCOMES ],
    button: {
      id: CONSTANT.INCOMES.initCap() + 'Info',
      title: MSG.INFO_INCOMES,
      icon: 'info',
      fn: () => info(
          MSG.INCOMES,
          `<b>Facturas Emitidas:</b> Facturas que envías a tus clientes por los servicios prestados o por la venta de tus productos.<br><br>
           <b>Facturas Proforma:</b> Borrador de factura en elaboración y que se pueden enviar al cliente como "PROFORMA" para que conozcan el coste del servicio o productos y las condiciones del mismo antes de emitir la factura definitiva para su evaluación y conformidad previa a la emisión de la factura definitiva.<br><br>
           <b>Otros Ingresos:</b> Son aquellos ingresos que recibes que no provienen de tu actividad económica como por ejemplo subvenciones, intereses bancarios, etc.<br><br>
           <b>Presupuestos:</b> Documento que detalla el coste del servicio o venta de productos que se va a realizar con un cliente.`
      )
    }
  }


  export const MAIN_EXPENSES = {
    id: CONSTANT.EXPENSES.initCap(),
    title: MSG.EXPENSES,
    name: MSG.EXPENSES,
    options: [INVOICE_RECEIVED_BETA, RAWDOC_INBOX_RECEIVED_NEW, INVOICE_TICKET, RAWDOC_INBOX_TICKET_NEW, OTHER_EXPENSES], //, STAFF_EXPENSES],
    button: {
      id: CONSTANT.EXPENSES.initCap() + 'Info',
      title: MSG.INFO_EXPENSES,
      icon: 'info',
      fn: () => info(
        MSG.EXPENSES,
        `<b>Facturas Recibidas:</b> Facturas que te emiten tus proveedores por sus servicios prestados o compra de productos.<br><br>
         <b>Borrador Fras. Recibidas:</b> Documentos de factura recibida en proceso de revisión y registro, que una vez aceptado pasan a factura recibidas.<br><br>
         <b>Fra. Simplificadas/Ticket:</b> Documento sin datos del titular receptor del mismo, por lo que se considera factura simplificada, generalmente en formato ticket.<br><br>
         <b>Borrador Fra.Simp/Ticket:</b> Documento en proceso de revisión y registro, sin datos del titular receptor del mismo, por lo que se considera factura simplificada, generalmente en formato ticket, que una vez aceptado pasa a Fra. Simplificadas/Ticket.<br><br>
         <b>Otros Gastos:</b> Son aquellos gastos que tienes por tu actividad, pero del cual no existe factura simplificada/ticket como seguros, tasas municipales, intereses de prestamos, cuotas de  suscripcion a un colegio profesional, etc.<br><br>
         `
         //<b>Gastos de Personal:</b> Gastos de las nominas de los trabajadores o de las cuotas de autónomo.`
      )
    }

  }

  export const MAIN_DOCUMENTS = {
    id: CONSTANT.DOCUMENT.initCap(),
    title: MSG.PENDING,
    name: MSG.PENDING,
    options: [RAWDOC_PROCESSING, RAWDOC_REJECT, RAWDOC_TRASH],
    button: {
      id: CONSTANT.DOCUMENT.initCap() + 'Info',
      title: MSG.INFO_PENDING,
      icon: 'info',
      fn: () => info(
        MSG.PENDING,
        `<b>En Trámite:</b> Documentos subidos al portal y que se están gestionando por el contable o asesor. Una vez tramitados los veras en Facturas.<br><br>
         <b>A revisar:</b> Documentos subidos al portal, de los cuales existen alguna duda pendiente de aclaración para poder procesalos correctamente.<br><br>
         <b>Papelera:</b> Documentos rechazados que no se van a contabilizar por diferentes causas (titular erroneo, factura duplicada, no afectos a la actividad, documento ilegible, etc.).Estos documentos se eliminarán automáticamente transcurridos 30 DIAS.<br><br>`
      )
    }
  }

  export const MAIN_DOCUMENTS_TRIAL = {
    id: CONSTANT.DOCUMENT.initCap(),
    title: MSG.PENDING,
    name: MSG.PENDING,
    options: [RAWDOC_TRASH],
    button: {
      id: CONSTANT.DOCUMENT.initCap() + 'Info',
      title: MSG.INFO_PENDING,
      icon: 'info',
      fn: () => info(
        MSG.PENDING,
        `<b>En Trámite:</b> Documentos subidos al portal y que se están gestionando por el contable o asesor. Una vez tramitados los veras en Facturas.<br><br>
         <b>A revisar:</b> Documentos subidos al portal, de los cuales existen alguna duda pendiente de aclaración para poder procesalos correctamente.<br><br>
         <b>Papelera:</b> Documentos rechazados que no se van a contabilizar por diferentes causas (titular erroneo, factura duplicada, no afectos a la actividad, documento ilegible, etc.).Estos documentos se eliminarán automáticamente transcurridos 30 DIAS.<br><br>`
      )
    }
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
    icon: MATERIAL_ICONS.EURO_SYMBOL,
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
    icon: MATERIAL_ICONS.ACCOUNT_BALANCE
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

  export const MANAGEMENT_TRIAL = {
    id: CONSTANT.MANAGEMENT.initCap(),
    title: MSG.MANAGEMENT,
    name: MSG.MANAGEMENT,
    options:[ REGISTRY, CONCEPTS]
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

  export const getOptions = (trial) => {
    return trial 
      ? [MAIN_INCOMES_TRIAL, MAIN_EXPENSES, MAIN_DOCUMENTS_TRIAL, MANAGEMENT_TRIAL]
      : [MAIN_INCOMES, MAIN_EXPENSES, MAIN_DOCUMENTS, MANAGEMENT];
  }

  export const getNewOptions = () => {
    return [NEW_ISSUED_INVOICE, NEW_RECEIVED_INVOICE, NEW_TICKET];
  }