import { AonElement } from "../../components/AonElement.js";
import { insertInvoice, mobileAction, MOBILE_ACTION, selfconta, downloadInvoiceExcel, getInvoice, getRawdocCount, getInvofoxCount, invoiceDuplicateFix, refreshProcessing, saveInvoiceClosing } from "../../services/service.js";
import { Invoice } from "./Invoice.js";
import { AonInvoice } from "./aon-invoice.js";
import { AonMobileInvoice } from "./aon-mobile-invoice.js";
import { MSG, MATERIAL_ICONS, CONSTANT, EVENT, TAG } from "../../environments/environments.js";
import { downscaleImage } from "../../services/compressImg.js";
import { AonProductList } from "../product/aon-product-list.js";
import { AonMobileProductList } from "../product/aon-mobile-product-list.js";
import { Apps, INVOICE } from "../../services/app.js";
import { AonProduct } from "../product/aon-product.js";
import { AonCustomerList } from "../registry/customer/aon-customer-list.js";
import { AonSupplierList } from "../registry/supplier/aon-supplier-list.js";
import { AonCreditorList } from "../registry/creditor/aon-creditor-list.js";
import { AonMobileSupplierList } from "../registry/supplier/aon-mobile-supplier-list.js";
import { AonMobileCreditorList } from "../registry/creditor/aon-mobile-creditor-list.js";
import { AonMobileCustomerList } from "../registry/customer/aon-mobile-customer-list.js";
import { AonCustomer } from "../registry/customer/aon-customer.js";
import { AonSupplier } from "../registry/supplier/aon-supplier.js";
import { AonCreditor } from "../registry/creditor/aon-creditor.js";
import { SigninSidenav } from "../timecontrol/signinEnums.js";
import { AonInvestList } from "../product/aon-invest-list.js";
import { AonInvest } from "../product/aon-invest.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonUploadToast } from "../../components/aon-upload-toast.js";
import { AonInvoiceList } from "./aon-invoice-list.js";
import { AonMobileInvoiceList } from "./aon-mobile-invoice-list.js";
import { AonInvoiceHome } from "./aon-invoice-home.js";

import { FiscalUtils } from "../fiscal/FiscalUtils.js";

import * as ACTION from "../actions.js";
import * as OPTION from "./InvoiceOptions.js";

import "./aon-invoice-print.js";
import "../../components/aon-application.js";
import "../../components/aon-dialog-menu.js";

import { getCounter, addCounter, clearCounter } from "./InvoiceCounter.js";
import { AonFutureTax } from "../fiscal/tax/aon-future-tax.js";
import { generateJobId } from "./InvoiceUtils.js";
import { getReader } from "../../services/utils.js";
import { AonImageEditor } from "../../components/aon-image-editor.js";
import { createSelect } from "../../components/CreateComponent.js";

export class AonInvoicePanel extends AonElement {
  selectedOption;
  filter;
  invofoxFilter;
  counterActive;

  INVOICE;
  INPUT_FILE;
  INPUT_CAMERA;
  PRODUCT_LIST;
  CUSTOMER_LIST;
  SUPPLIER_LIST;
  CREDITOR_LIST;
  INVEST_LIST;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get status() {
    return this.getAttribute(CONSTANT.STATUS);
  }

  set status(status) {
    this.setAttribute(CONSTANT.STATUS, status);
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.innerHTML = `
			<aon-application id='${this.INVOICE}' title='${MSG.BILLING}' drag_and_drop='true'></aon-application>
			<aon-dialog-menu id='aonDialogAddOption'> </aon-dialog-menu>
			<input id='${this.INPUT_FILE}' style='display:none;' type='file' name='file' multiple>
			<input id='${this.INPUT_CAMERA}' type='file' accept='image/*' capture='camera' hidden />
		`;
    this.buildDur().then((r) => {
      this.build();
    });
  }

  initialize() {
    this.INVOICE = "aonInvoice";
    this.INPUT_FILE = this.INVOICE + "InputFile";
    this.INPUT_CAMERA = "aonMobileMenuCameraInput"; // this.INVOICE + 'InputCamera';
    this.PRODUCT_LIST = this.INVOICE + "ProductList";
    this.CUSTOMER_LIST = this.INVOICE + "CustomerList";
    this.SUPPLIER_LIST = this.INVOICE + "SupplierList";
    this.CREDITOR_LIST = this.INVOICE + "CreditorList";
    this.INVEST_LIST = this.INVOICE + "InvestList";

    this.status = this.status || CONSTANT.INBOX;
    this.filter = {
      status: this.status || CONSTANT.INBOX,
      page: 0,
      per_page: 50,
    };
    this.counterActive = true;

    this.option =
      this.option ||
      (CONSTANT.REJECTED === this.status
        ? OPTION.RAWDOC_REJECT
        : OPTION.RAWDOC_INBOX);
  }

  getFilter() {
    return this.filter;
  }

  build() {
    let aonInvoice = this.getElement(this.INVOICE);

    let input = this.getElement(this.INPUT_FILE);
    input.addEventListener(EVENT.CHANGE, () =>
      this.upload(input.files)
    );

    let inputCamera = this.getElement(this.INPUT_CAMERA);
    inputCamera.addEventListener(EVENT.CHANGE, ({ target }) =>
      this.uploadCamera(target.files)
    );

    aonInvoice.addEventListener(EVENT.AON_APPLICATION_DROP, (e) =>
      this.upload(e.detail)
    );
    this.buildInvoiceToolbarOptions();
    this.buildSidenavOptions();
    if (this.invoice && this.invoice.type) {
      this.aonInvoice(this.invoice.type, this.invoice);
    } else if (this.value) {
      this.aonInvoiceById(this.value);
    } else this.aonInvoiceHome();

    this.dispatchEvent(new CustomEvent(EVENT.BUILD, { panel: this }));

    let upload = this.getElement("aonInvoiceToolbarHeaderToolSectionUploadButton");
    upload.title = MSG.UPLOAD_INVOICE;

    let refresh = this.getElement("aonInvoiceToolbarHeaderToolSectionRefreshButton");
    refresh.title = MSG.REFRESH;

    let reprocess = this.getElement("aonInvoiceToolbarHeaderToolSectionSyncButton");
    reprocess.title = MSG.REPROCESS;

  }

  buildInvoiceToolbarOptions(acceptedInvoices, processing) {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption2(ACTION.ADD_INVOICE, () => this.addInvoice());
      if(this.isBeta()) this.getApplication().addToolbarOption("Close", "close", () => this.closingInvoice());
      this.getApplication().addToolbarOption("Refresh", "refresh", () => this.refreshInvoicePanel());
      this.getApplication().addToolbarOption("Upload", "file_upload", () => this.addInvoiceFile());
      if(processing) this.getApplication().addToolbarOption("Sync", "sync", () => this.refreshProcessing()); 
 
      if(acceptedInvoices) this.getApplication().addToolbarOption2(SigninSidenav.EXCEL, () => this.downloadInvoiceExcel());
    } else {
      this.getApplication().removeFloatOption();
      this.getApplication().addFloatOption(ACTION.ADD_INVOICE, () => this.addInvoice());
    } 
    this.buildToolbarSearchOption(acceptedInvoices);
  }

  buildProductToolbarOptions() {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption2(ACTION.ADD_PRODUCT, () => this.addProduct());
    } 
    // TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR PRODUCTO EN EL MÓVIL
    // else {
    //   this.getApplication().removeFloatOption();
    //   this.getApplication().addFloatOption(ACTION.ADD_PRODUCT, () => this.addProduct());
    // }

    this.buildToolbarSearchOption();
  }

  buildExpenseToolbarOptions() {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption2(ACTION.ADD_EXPENSE, () => this.addExpense());
    } 
    // TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR GASTO EN EL MÓVIL
    // else {
    //   this.getApplication().removeFloatOption();
    //   this.getApplication().addFloatOption(ACTION.ADD_EXPENSE, () => this.addExpense());
    // }
    this.buildToolbarSearchOption();
  }

  buildInvestToolbarOptions() {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption2(ACTION.ADD_INVEST_ASSET, () => this.addInvest());
    } 
    // TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR BIEN AFECTO EN EL MÓVIL
    // else {
    //   this.getApplication().removeFloatOption();
    //   this.getApplication().addFloatOption(ACTION.ADD_INVEST_ASSET, () => this.addInvest());
    // }

    this.buildToolbarSearchOption();
  }

  buildCustomerToolbarOptions() {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption2(ACTION.ADD_CUSTOMER, () => this.addCustomer());
    }
    // TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR CLIENTE EN EL MÓVIL
    // else {
    //   this.getApplication().removeFloatOption();
    //   this.getApplication().addFloatOption(ACTION.ADD_CUSTOMER, () => this.addCustomer());
    // }
    this.buildToolbarSearchOption();
  }

  buildSupplierToolbarOptions() {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption2(ACTION.ADD_SUPPLIER, () => this.addSupplier());
    }
    // TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR PROVEEDOR EN EL MÓVIL
    // else {
    //   this.getApplication().removeFloatOption();
    //   this.getApplication().addFloatOption(ACTION.ADD_SUPPLIER, () => this.addSupplier());
    // }
    this.buildToolbarSearchOption();
  }

  buildCreditorToolbarOptions() {
    this.clearToolbar();
    if(!this.isMobile()) {
      this.getApplication().addToolbarOption("Add", "add", () => this.addCreditor());
    }
    // TODO ACTIVAR CUANDO ESTE LA OPCIÓN DE AÑADIR ACREEDOR EN EL MÓVIL
    // else {
    //   this.getApplication().removeFloatOption();
    //   this.getApplication().addFloatOption(ACTION.ADD_CREDITOR, () => this.addCreditor());
    // }
    this.buildToolbarSearchOption();
  }

  clearToolbar() {
    let aonInvoice = this.getApplication();
    let toolbar = this.getElement(aonInvoice.TOOLBAR);
    toolbar.removeButtons();
  }

  buildToolbarSearchOption(acceptedInvoices) {
    const btnSearch = this.getApplication().addSearchOption();
    let searchFn = (event) => this.search(event.detail);
    btnSearch.addEventListener(EVENT.SEARCH_NEW, searchFn);
    if(acceptedInvoices) btnSearch.buildOptionsFilter(OPTION.INVOICE_SEARCH_OPTIONS);
  }

  downloadInvoiceExcel() {
    let aonInvoiceTable = document.getElementById("aonInvoiceTable");

    let data = {
      domainId: localStorage.getItem("aon_domain_id"),
      domainName: localStorage.getItem("aon_domain_name"),
      domainLogin: localStorage.getItem("aon_domain_login"),
      ids: aonInvoiceTable.selected.map((r) => r.id),
      description: this.getFilter().description,
      status: this.getFilter().status,
      type: this.getFilter().type,
      from: this.getFilter().from,
      to: this.getFilter().to,
    };
    let json = btoa(JSON.stringify(data));
    downloadInvoiceExcel(json);
  }

  buildSidenavOptions() {
    if (this.isMobile()) {
      this.getApplication().addMobileSidenavHeader(Apps.INVOICE);
    }

    this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) => {
      this.selectOption(e.detail);
    });

    OPTION.getOptions(this.getDur()).forEach((option) => {
      option.app = INVOICE;
      this.getApplication().addSidenavOptions3(option);
    });
    this.buildCounter();
  }

  buildCounter() {
    if (this.counterActive) {
      this.counterActive = false;
      clearCounter();
      this.invoiceCounter();
      if (this.getDur().isInvofox()) {
        this.invofoxCounter();
      }
    }
  }

  invoiceCounter() {
    getRawdocCount({}).then((r) => {
      this.counterActive = true;
      if (r.invoice && r.invoice.emitida && r.invoice.emitida > 0) {
        addCounter(OPTION.INVOICE_ISSUED, r.invoice.emitida);
      }
      this.updateCounterSpan(OPTION.INVOICE_ISSUED);

      if (r.invoice && r.invoice.recibida && r.invoice.recibida > 0) {
        addCounter(OPTION.INVOICE_RECEIVED, r.invoice.recibida);
      }
      this.updateCounterSpan(OPTION.INVOICE_RECEIVED);

      if (r.invoice && r.invoice.ticket && r.invoice.ticket > 0) {
        addCounter(OPTION.INVOICE_TICKET, r.invoice.ticket);
      }
      this.updateCounterSpan(OPTION.INVOICE_TICKET);

      if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.count && r.rawdoc.inbox.count > 0) {
        addCounter(OPTION.INVOICE_PENDINGS, r.rawdoc.inbox.count);
      }
      this.updateCounterSpan(OPTION.INVOICE_PENDINGS);

      if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.OUTPUT && r.rawdoc.inbox.OUTPUT > 0) {
        addCounter(OPTION.RAWDOC_INBOX_ISSUED, r.rawdoc.inbox.OUTPUT);
      }
      this.updateCounterSpan(OPTION.RAWDOC_INBOX_ISSUED);

      if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.INPUT && r.rawdoc.inbox.INPUT > 0) {
        addCounter(OPTION.RAWDOC_INBOX_RECEIVED, r.rawdoc.inbox.INPUT);
      }
      this.updateCounterSpan(OPTION.RAWDOC_INBOX_RECEIVED);

      if (r && r.rawdoc && r.rawdoc.inbox && r.rawdoc.inbox.TICKET && r.rawdoc.inbox.TICKET > 0) {
        addCounter(OPTION.RAWDOC_INBOX_TICKET, r.rawdoc.inbox.TICKET);
      }
      this.updateCounterSpan(OPTION.RAWDOC_INBOX_TICKET);

      if (r && r.rawdoc && r.rawdoc.processing && r.rawdoc.processing.count && r.rawdoc.processing.count > 0) {
        addCounter(OPTION.RAWDOC_PROCESSING, r.rawdoc.processing.count);
      }
      this.updateCounterSpan(OPTION.RAWDOC_PROCESSING);

      if (r && r.rawdoc && r.rawdoc.rejected && r.rawdoc.rejected.count && r.rawdoc.rejected.count > 0) {
        addCounter(OPTION.RAWDOC_REJECT, r.rawdoc.rejected.count);
      }
      this.updateCounterSpan(OPTION.RAWDOC_REJECT);

      if (r && r.rawdoc && r.rawdoc.draft && r.rawdoc.draft.count && r.rawdoc.draft.count > 0) {
        addCounter(OPTION.RAWDOC_DRAFT, r.rawdoc.draft.count);
      }
      this.updateCounterSpan(OPTION.RAWDOC_DRAFT);
      this.updateCounterHome();
    });
  }

  invofoxCounter() {
    let issuedFilter = {
      publicStatus: ["approved", "pendingCorrection"],
      type: ["invoice", "ticket"],
      companyActsLike: "issuer",
    };
    getInvofoxCount(issuedFilter).then((r) => {
      this.counterActive = true;
      if (r && r.count && r.count > 0) {
        addCounter(OPTION.INVOICE_PENDINGS, r.count);
        addCounter(OPTION.RAWDOC_INBOX_ISSUED, r.count);
      }
      this.updateCounterSpan(OPTION.INVOICE_PENDINGS);
      this.updateCounterSpan(OPTION.RAWDOC_INBOX_ISSUED);
      this.updateCounterHome();
    }).catch( e => this.counterActive = true);

    let receivedFilter = {
      publicStatus: ["approved", "pendingCorrection"],
      type: ["invoice"],
      companyActsLike: "ne+issuer",
    };
    getInvofoxCount(receivedFilter).then((r) => {
      this.counterActive = true;
      if (r && r.count && r.count > 0) {
        addCounter(OPTION.INVOICE_PENDINGS, r.count);
        addCounter(OPTION.RAWDOC_INBOX_RECEIVED, r.count);
      }
      this.updateCounterSpan(OPTION.INVOICE_PENDINGS);
      this.updateCounterSpan(OPTION.RAWDOC_INBOX_RECEIVED);
      this.updateCounterHome();
    }).catch( e => this.counterActive = true);

    let ticketFilter = {
      publicStatus: ["approved", "pendingCorrection"],
      type: ["ticket"],
      companyActsLike: "ne+issuer",
    };
    getInvofoxCount(ticketFilter).then((r) => {
      this.counterActive = true;
      if (r && r.count && r.count > 0) {
        addCounter(OPTION.INVOICE_PENDINGS, r.count);
        addCounter(OPTION.RAWDOC_INBOX_TICKET, r.count);
      }
      this.updateCounterSpan(OPTION.INVOICE_PENDINGS);
      this.updateCounterSpan(OPTION.RAWDOC_INBOX_TICKET);
      this.updateCounterHome();
    }).catch( e => this.counterActive = true);

    let rejectedFilter = {
      publicStatus: ["pendingDecission", "rejected"],
      type: ["invoice", "ticket"],
    };
    getInvofoxCount(rejectedFilter).then((r) => {
      this.counterActive = true;
      if (r && r.count && r.count > 0) {
        addCounter(OPTION.RAWDOC_REJECT, r.count);
      }
      this.updateCounterSpan(OPTION.RAWDOC_REJECT);
      this.updateCounterHome();
    }).catch( e => this.counterActive = true);

    let trashFilter = { publicStatus: ["discarded"] };
    getInvofoxCount(trashFilter).then((r) => {
      this.counterActive = true;
      if (r && r.count && r.count > 0) {
        addCounter(OPTION.RAWDOC_DRAFT, r.count);
      }
      this.updateCounterSpan(OPTION.RAWDOC_DRAFT);
      this.updateCounterHome();
    }).catch( e => this.counterActive = true);
  }

  updateCounterSpan(option) {
    let span = document.getElementById("aonMenuItemSpan" + option.id);
    if (span) {
      let count = getCounter()[option.id];
      span.innerHTML = count > 0 ? option.name + " (" + count + ")" : option.name;
      if(count > 0) span.style.fontWeight = "bold";
    } else setTimeout(this.updateCounterSpan, 100, option);
  }

  updateCounterHome() {
    let issued = getCounter()[OPTION.INVOICE_ISSUED.id] || 0;
    let received = getCounter()[OPTION.INVOICE_RECEIVED.id] || 0;
    let ticket = getCounter()[OPTION.INVOICE_TICKET.id] || 0;
    let total = issued + received + ticket;
    let pendingRecordNumber = this.getElement("pendingRecordNumber");
    if (pendingRecordNumber) pendingRecordNumber.innerHTML = total;

    let pendingIssuedCounter = getCounter()[OPTION.RAWDOC_INBOX_ISSUED.id] || 0;
    let pendingIssuedNumber = this.getElement("pendingIssuedNumber");
    if (pendingIssuedNumber)
      pendingIssuedNumber.innerHTML = pendingIssuedCounter;

    let pendingReceivedCounter =
      getCounter()[OPTION.RAWDOC_INBOX_RECEIVED.id] || 0;
    let pendingReceivedNumber = this.getElement("pendingReceivedNumber");
    if (pendingReceivedNumber)
      pendingReceivedNumber.innerHTML = pendingReceivedCounter;

    let pendingTicketCounter = getCounter()[OPTION.RAWDOC_INBOX_TICKET.id] || 0;
    let pendingTicketNumber = this.getElement("pendingTicketNumber");
    if (pendingTicketNumber)
      pendingTicketNumber.innerHTML = pendingTicketCounter;

    let rejectedCounter = getCounter()[OPTION.RAWDOC_REJECT.id] || 0;
    let rejectedNumber = this.getElement("rejectedNumber");
    if (rejectedNumber) rejectedNumber.innerHTML = rejectedCounter;

    let trash = getCounter()[OPTION.RAWDOC_DRAFT.id] || 0;
    let trashNumber = this.getElement("trashNumber");
    if (trashNumber) trashNumber.innerHTML = trash;
  }

  search(detail) {
    let value = detail.search;
    if (this.selectedOption && OPTION.REGISTRY_CREDITOR.id === this.selectedOption.id) {
      this.aonCreditorList({ page: 1, perPage: 50, value });
    } else if (this.selectedOption && OPTION.REGISTRY_SUPPLIER.id === this.selectedOption.id) {
      this.aonSupplierList({ page: 1, perPage: 50, value });
    } else if (this.selectedOption && OPTION.REGISTRY_CUSTOMER.id === this.selectedOption.id) {
      this.aonCustomerList({ page: 1, perPage: 50, value });
    } else if (this.selectedOption && OPTION.PRODUCT.id === this.selectedOption.id) {
      this.aonProductList({ expense: false, value });
    } else if (this.selectedOption && OPTION.EXPENSES.id === this.selectedOption.id) {
      this.aonProductList({ expense: true, value });
    } else if (this.selectedOption && OPTION.INVEST.id === this.selectedOption.id) {
      this.aonInvestList({ value });
    } else {
      this.filter.description = value;
      if (detail.recorded) this.filter.recorded = detail.recorded;
      this.filter.from = detail.startDate;
      this.filter.to = detail.endDate;
      this.filter.page = 1;
      this.filter.perPage = 50;
      this.aonInvoiceList(this.filter, this.invofoxFilter);
    }
  }

  aonInvoiceList(filter, invofoxFilter) {
    this.filter = filter;
    this.invofoxFilter = invofoxFilter;
    let table = this.isMobile()
      ? new AonMobileInvoiceList()
      : new AonInvoiceList();
    table.id = "aonInvoiceList";
    table.setFilter(this.filter);
    table.invofoxFilter = this.invofoxFilter;
    this.getApplication().setContent(table);
    this.getApplication().buildDragAndDrop(true);
  }

  aonCustomerList(filter) {
    let customerList = this.getElement(this.CUSTOMER_LIST);
    if (customerList) {
      customerList.setFilter(filter);
    } else {
      customerList = this.isMobile()
        ? new AonMobileCustomerList()
        : new AonCustomerList();
      customerList.id = this.CUSTOMER_LIST;
      customerList.filter = filter;
      this.getApplication().setContent(customerList);
    }
  }

  aonSupplierList(filter) {
    let supplierList = this.getElement(this.SUPPLIER_LIST);
    if (supplierList) {
      supplierList.setFilter(filter);
    } else {
      supplierList = this.isMobile()
        ? new AonMobileSupplierList()
        : new AonSupplierList();
      supplierList.id = this.SUPPLIER_LIST;
      supplierList.filter = filter;
      this.getApplication().setContent(supplierList);
    }
  }

  aonCreditorList(filter) {
    let creditorList = this.getElement(this.CREDITOR_LIST);
    if (creditorList) {
      creditorList.setFilter(filter);
    } else {
      creditorList = this.isMobile()
        ? new AonMobileCreditorList()
        : new AonCreditorList();
      creditorList.id = this.CREDITOR_LIST;
      creditorList.filter = filter;
      this.getApplication().setContent(creditorList);
    }
  }

  aonProductList(filter) {
    let productList = this.getElement(this.PRODUCT_LIST);
    if (productList) {
      productList.setFilter(filter);
    } else {
      productList = this.isMobile()
        ? new AonMobileProductList()
        : new AonProductList();
      productList.id = this.PRODUCT_LIST;
      productList.filter = filter;
      this.getApplication().setContent(productList);
    }
  }

  aonInvestList(filter) {
    let investList = this.getElement(this.INVEST_LIST);
    if (investList) {
      investList.setFilter(filter);
    } else {
      investList = new AonInvestList();
      investList.id = this.INVEST_LIST;
      investList.filter = filter;
      this.getApplication().setContent(investList);
    }
  }

  addCustomer() {
    let aonCustomer = new AonCustomer();
    aonCustomer.id = this.id + "Customer";
    aonCustomer.setCustomer();
    this.getApplication().setContent(aonCustomer);
  }

  addSupplier() {
    let aonSupplier = new AonSupplier();
    aonSupplier.id = this.id + "Supplier";
    aonSupplier.setSupplier();
    this.getApplication().setContent(aonSupplier);
  }

  addCreditor() {
    let aonCreditor = new AonCreditor();
    aonCreditor.id = this.id + "Creditor";
    aonCreditor.setCreditor();
    this.getApplication().setContent(aonCreditor);
  }

  addProduct() {
    let aonProduct = new AonProduct();
    aonProduct.id = this.id + "Product";
    this.getApplication().setContent(aonProduct);
  }

  addExpense() {
    let aonProduct = new AonProduct();
    aonProduct.id = this.id + "Expense";
    aonProduct.expense = true;
    this.getApplication().setContent(aonProduct);
  }

  addInvest() {
    let aonInvest = new AonInvest();
    aonInvest.id = this.id + "Invest";
    this.getApplication().setContent(aonInvest);
  }

  addInvoice() {
    let ayudat = this.getDur().isSelfconta();
    let aonInvoice = this.getApplication();
    let aonInvoiceToolbar = this.getElement(aonInvoice.TOOLBAR);
    let button = this.isMobile()
      ? this.getElement("aonInvoiceAddInvoiceButton")
      : this.getElement(aonInvoiceToolbar.TOOL_SECTION + ACTION.ADD_INVOICE.id + "Button");

    let height = window.innerHeight;
    let top = button.getBoundingClientRect().top;
    const left = button.getBoundingClientRect().left;

    if (height - top < height / 2) {
      top = top - (ayudat ? 205 : 170);
    }

    let d = document.getElementById("aonDialogAddOption");

    let options = OPTION.getNewOptions();
    if (ayudat) {
      let importSelfconta = {
        name: "Importación Selfconta",
        title: "Importación Selfconta",
        icon: "import_export",
        permission: ayudat,
        backgroundColor: "#4472C4",
        fn: () => this.importSelfconta(),
      };
      options.push(importSelfconta);
    }

    if (this.isMobile()) {
      let uploadFile = {
        name: MSG.UPLOAD_FILE,
        title: MSG.UPLOAD_FILE,
        icon: MATERIAL_ICONS.FILE_UPLOAD,
        permission: true,
        backgroundColor: "#4472C4",
        fn: () => this.addInvoiceFile(),
      };

      options.push(uploadFile);

      let openCamera = {
        name: "Camara",
        title: "Camara",
        icon: "camera_alt",
        permission: true,
        backgroundColor: "#4472C4",
        fn: () => {
          if(UA.isApp()) {
            let ionicData = { action: MOBILE_ACTION.CAMERA, id: this.INPUT_CAMERA, selector: 'aon-invoice-panel' };
            openCamera(ionicData, (result) => {
              this.buildInvoiceImageEditor(result);
            });
          } else this.openCamera();
        }
      };
      options.push(openCamera);
    }
    d.setMenuOptions(options, top, left);
    d.open();
  }

  importSelfconta() {
    let div = this.createElement(TAG.DIV);
    div.id = "aonInvoiceSelfcontaDiv";

    let div2 = this.createElement(TAG.DIV);
    div2.id = "aonInvoiceSelfcontaDiv2";
    div2.innerHTML = "¿Desea importar las facturas?";
    div.appendChild(div2);

    let yearSelect = new AonSelect();
    yearSelect.id = "aonInvoiceSelfcontaYear";
    yearSelect.title = MSG.YEAR;
    yearSelect.options = JSON.stringify([
      { name: "2024", value: 2024 },
      { name: "2023", value: 2023 },
      { name: "2022", value: 2022 },
      { name: "2021", value: 2021 },
      { name: "2020", value: 2020 },
      { name: "2019", value: 2019 },
      { name: "2018", value: 2018 },
    ]);
    div.appendChild(yearSelect);

    let aonApplication = this.getApplication();
    let d = aonApplication.getDialog();
    d.clear();
    if (!this.isMobile()) d.width = "400px";
    d.setTitle("Importar");
    d.setContent(div);
    d.addAcceptAction(async () => {
      aonApplication.startLoading();
      try {
        await selfconta(yearSelect.value);
        this.showToast({
          type: "success",
          message: "Datos Importados. Revisa las facturas rechazadas.",
        });
      } catch (error) {
        this.showToast(error);
      }
      aonApplication.stopLoading();
    });
    d.open();
  }

  async openCamera() {
    const isApp = await mobileAction({
      action: MOBILE_ACTION.CAMERA,
      id: this.INPUT_CAMERA,
      selector: "aon-invoice-panel",
    });
    if (!isApp) this.getElement(this.INPUT_CAMERA).click();
  }

  async receiveAppImage(file) {
    if (file.contentType.indexOf("image") >= 0) {
      //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
      const f = await downscaleImage(file, undefined, undefined, undefined);
      const data = {
        file: f,
        invoice: new Invoice().setType("recibida"),
      };
      let aonInvoice = document.getElementById("aonInvoice");
      aonInvoice.startLoader();
      await insertInvoice(data);
      this.aonInvoiceList({ status: "inbox" });
      aonInvoice.stopLoader();
    }
  }

  closingInvoice() {
    let closing = this.createDiv();
    let closingPeriod = createSelect();
    closingPeriod.options = JSON.stringify([
      { name: MSG.JANUARY, value: "01" }, { name: MSG.FEBRUARY, value: "02" }, { name: MSG.MARCH, value: "03" }, 
      { name: MSG.APRIL, value: "04" }, { name: MSG.MAY, value: "05" }, { name: MSG.JUNE, value: "06" },      
      { name: MSG.JULY, value: "07" }, { name: MSG.AUGUST, value: "08" }, { name: MSG.SEPTEMBER, value: "09" },
      { name: MSG.OCTOBER, value: "10" }, { name: MSG.NOVEMBER, value: "11" }, { name: MSG.DECEMBER, value: "12" },
      { name: "1 Trimestre", value: "1T" }, { name: "2 Trimestre", value: "2T" }, { name: "3 Trimestre", value: "3T" },
      { name: "4 Trimestre", value: "4T" } 
    ]);
    closing.appendChild(closingPeriod)
    let closingYear = createSelect("aonInvoiceClosingYear", MSG.YEAR);
    closingYear.options = JSON.stringify([{ name: "2024", value: 2024 }]);
    closing.appendChild(closingYear);
    
    let d = this.getApplication().getDialog();
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle("Cierre de Facturas Recibidas");
    d.setContent(closing);
    d.addAcceptAction(() => {
      let data =  {
        period: closingPeriod.value,
        year: closingYear.value
      };
    
      saveInvoiceClosing(data)
      .then(r => this.showToast({
          type: "success",
          message: "El cierre se ha realizado correctamente.",
        }))
      .catch(error => this.showToast(error));
    });
    d.open();
  }

  refreshInvoicePanel() {
    this.buildInvoiceToolbarOptions();
    this.buildCounter();
    this.aonInvoiceHome();
  }

  refreshProcessing() {
    refreshProcessing({}).then(r => {
      this.buildCounter();
      this.aonInvoiceList({status: CONSTANT.PROCESSING});
    }).catch(e => console.log(e));
  }

  addInvoiceFile() {
    this.getElement(this.INPUT_FILE).click();
  }

  upload(files) {
      let uploadToast = this.getElement("aonUploadToast");
      if (!uploadToast) {
        uploadToast = new AonUploadToast();
        this.appendChild(uploadToast);
      }
      let data = { uploaded: 0 };
      uploadToast.setJobId(generateJobId());
      for (let file of files) {
        uploadToast.addFile("invoice", file, data);
      }
  }

  uploadCamera(files) {
    getReader(files[0]).then(file => {
      this.buildInvoiceImageEditor(file);
    }).catch(() => null);
  } 

  buildInvoiceImageEditor(file) {
    let editor = new AonImageEditor();
    editor.setImage("data:image/jpeg;base64,"+ file.content);
    editor.addEventListener(EVENT.CROPPER, (e) => {
      let uploadToast = this.getElement('aonUploadToast');
		  if(!uploadToast){ 
			  uploadToast = new AonUploadToast();
	  		this.appendChild(uploadToast);
  		}
		  let data = { uploaded : 0 , prefix: 'CM'};
      uploadToast.setJobId(generateJobId()); 
			uploadToast.addFile("invoice", e.detail, data);

      this.rootPanel(new AonInvoicePanel());
		});
    this.rootPanel(editor); 
  }

  aonInvoice(type, invoice) {
    let aonInvoice = this.getApplication();
    if (this.isMobile() && aonInvoice.TOOLBAR) {
      let toolbar = this.getElement(aonInvoice.TOOLBAR);
      toolbar.removeButtons();
    }
    let component = this.isMobile() ? new AonMobileInvoice() : new AonInvoice();
    component.setType(type);
    component.setInvoice(invoice);
    if (invoice && invoice.file) {
      component.fileOpened = true;
      this.getApplication().buildDragAndDrop(false);
    }

    aonInvoice.setContent(component);
  }

  aonInvoiceHome() {
    this.getApplication().setContent(new AonInvoiceHome());
    this.getApplication().buildDragAndDrop(false);
  }

  aonInvoiceById(id) {
    getInvoice(id)
      .then((invoice) => this.aonInvoice(invoice.type, invoice))
      .catch((error) => this.showToast(error));
  }

  async selectOption(option) {
    let aonInvoice = this.getApplication();
    if (option) {
      let toolbar = this.getElement(aonInvoice.TOOLBAR);
      toolbar.option = option.name;
      this.selectedOption = option;
      if(option.id === OPTION.FISCAL_DRAFT.id){
          await this.getFiscalModelDraft();

      }
    }
  }

  async getFiscalModelDraft() {
    let aonInvoice = this.getApplication();

    let futureFiscalFilter = await FiscalUtils.getFutureFiscalFilter();

    let aonFutureTax =  new AonFutureTax(INVOICE, futureFiscalFilter);
    aonInvoice.setContent(aonFutureTax);    
  }
}
if (!window.customElements.get(TAG.AON_INVOICE_PANEL)) {
  window.customElements.define(TAG.AON_INVOICE_PANEL, AonInvoicePanel);
}
