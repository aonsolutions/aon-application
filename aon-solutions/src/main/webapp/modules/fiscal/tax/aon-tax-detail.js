import { AonElement } from "../../../components/AonElement.js";
import {
  getDomainUserRoles,
  openFileBase64,
  getAttach,
  getAeatCertificates,
  getFiscalModelsInvoinces,
  getEstimationModelsFiscalInvoinces,
  downloadInvoices,
  getFiscalModelsSalaries,
  getEstimationModelsFiscalSalaries,
  getFiscalModelsInvoincesCount,
  getFiscalModelsSalariesCount,
  getEstimationModelsFiscalInvoincesCount,
  getEstimationModelsFiscalSalariesCount,
  getFiscalModelsInvoincesExcel,
  getEstimationModelsFiscalInvoincesExcel,
} from "../../../services/service.js";
import { DomainUserRoles } from "../../../models/DomainUserRoles.js";

import "../../../components/aon-table.js";

import {
  CONSTANT,
  EVENT,
  MATERIAL_ICONS,
  MSG,
  TAG,
} from "../../../environments/environments.js";
import * as ACTION from "../../actions.js";
import { FISCAL } from "../../../services/app.js";
import {
  formatNumber,
  serializeForm,
  waitEl,
} from "../../../services/utils.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { ToolbarType } from "../../../models/enums.js";
import { DataAttachSource } from "../../../models/DataAttachSource.js";
import { AonTax } from "./aon-tax.js";
import { FiscalUtils } from "../FiscalUtils.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonNumber } from "../../../components/aon-number.js";
import { AonDate } from "../../../components/aon-date.js";
import { AonCheckbox } from "../../../components/aon-checkbox.js";
import { CONST_FISCAL } from "../FiscalEnums.js";
import { getDocumentNumber } from "../../invoice/Invoice.js";
import { AonTab } from "../../../components/aon-tab.js";
import { AonFutureTax } from "./aon-future-tax.js";
import { AonViewer } from "../../../components/aon-viewer.js";
import * as LS from "../../../services/localStorageService.js";
import { createFetchingList } from "../../../components/CreateComponent.js";
import { AonSearch } from "../../../components/aon-search.js";

export class AonTaxDetail extends AonElement {
  TOOLBAR;
  TABS;
  MAIN_CONTENT;
  CONTENT;
  FILE;
  RECEIVED_INVOICE_TABLE;
  ISSUED_INVOICE_TABLE;
  SALARY_TABLE;

  SELECTED_FILTER;
  SELECTED_TABLE;
  SELECTED_INVOICE_TYPE;
  SELECTED_TYPE;

  _roles;
  applicationEl;
  tax;
  type;

  moreSalary;
  moreReceivedInvoice;
  moreIssuedInvoice;

  // List of data
  salaries;
  receivedInvoices;
  issuedInvoices;

  salariesCount;
  receivedInvoicesCount;
  issuedInvoicesCount;

  filterSalary;
  filterReceivedInvoice;
  filterIssuedInvoice;

  receivedIndexes;
  issuedIndexes;

  /**
   *
   * @param {Object} tax
   * @param {Strung} type ("tax", "future")
   */
  constructor(tax, type) {
    super();
    console.log("AonTaxDetail");

    this.tax = tax;
    this.type = type;
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({}).then((r) => {
      this._roles = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.TOOLBAR = "taxDetailToolbar";
    this.TABS = "aonTaxDetailTabs";
    this.MAIN_CONTENT = "aonTaxDetailMainContent";
    this.CONTENT = "aonTaxDetailContent";
    this.FILE = "aonTaxDetailFile";
    this.RECEIVED_INVOICE_TABLE = "aonReceivedInvoiceTable";
    this.ISSUED_INVOICE_TABLE = "aonIssuedInvoiceTable";
    this.SALARY_TABLE = "aonSalaryDetailTable";

    this.SELECTED_FILTER = "";
    this.SELECTED_TABLE = "";
    this.SELECTED_INVOICE_TYPE = "";
    this.SELECTED_TYPE = "";

    this.moreSalary = true;
    this.moreReceivedInvoice = true;
    this.moreIssuedInvoice = true;

    this.salariesCount = 0;
    this.receivedInvoicesCount = 0;
    this.issuedInvoicesCount = 0;

    this.receivedIndexes = 0;
    this.issuedIndexes = 0;
  }

  build() {
    let toolbar = new AonToolbar();
    toolbar.id = this.TOOLBAR;
    toolbar.type = ToolbarType.SECONDARY;
    if (this.type == "tax")
      toolbar.title = this.tax.modelText + " (" + this.tax.periodText + " " + this.tax.year + ")";
    else if (this.type == "future")
      toolbar.title = this.tax.modelText + " (" + this.tax.periodText + ")";

    this.appendChild(toolbar);

    if (["FINISHED", "SENT"].includes(this.tax.status)) {
      toolbar.addButton2(ACTION.DOWNLOAD_PDF_2, () => this.getPdf(this.tax));
    }

    if (["CUSTOMER_CHECK"].includes(this.tax.status)) {
      toolbar.addButton2(ACTION.ACCOUNT_BALANCE, () =>
        this.openDialog(this.tax)
      );
    }

    // Search
    this.createSearch();

    toolbar.addButton2(ACTION.BACK, () => this.back());

    this.initTabs();
    this.initContent();

    this.init();
  }

  createSearch(){
    let toolSection = this.getElement("taxDetailToolbarHeaderTitleSection");

    let search = new AonSearch();
    search.id = 'taxDetailToolbarHeaderTitleSectionSearch';
    toolSection.insertBefore(search, toolSection.firstChild);

    const searchFn = (event) => this.dispatchEvent(new CustomEvent(EVENT.SEARCH,{detail: event.detail}));
    search.addEventListener(EVENT.SEARCH, searchFn);

    let startDate = {
      type: CONSTANT.DATE,
      name: "startDate",
      id: "startDate",
      title: MSG.FROM,
    };

    let endDate = {
      type: CONSTANT.DATE,
      name: "endDate",
      id: "endDate",
      title: MSG.TO,
    };

    let searchOptions = [startDate, endDate];

    search.buildOptionsFilter(searchOptions);

    let cleanFunc = async (event) => {
      this.resetSearchFilter();
      if(this.SELECTED_TYPE === 'invoice')
        this.createInvoiceTable();
      else if(this.SELECTED_TYPE === 'salary')
        this.createSalaryTable();
      this.updateCountFilter();
    };
    search.addEventListener(EVENT.RESET_FILTER, cleanFunc);

    let searchTimeout;

    let searchFunc = (event) => {
      if(event.detail.event === "keyup" && event.detail.search.length < 3 && event.detail.search.length != 0)
        return;

      clearTimeout(searchTimeout);

      searchTimeout = setTimeout(function() {
        console.log(event.detail);

        this.SELECTED_FILTER = {
          ...this.SELECTED_FILTER,
          startDate: event.detail.startDate,
          endDate: event.detail.endDate,
          search: event.detail.search
        }
        this.SELECTED_FILTER.page = 0;

        if(this.SELECTED_TYPE === 'invoice'){
          this.receivedIndexes = 0;
          this.issuedIndexes = 0;
          this.createInvoiceTable();
        } else if(this.SELECTED_TYPE === 'salary'){
          this.createSalaryTable();
        }

        this.updateCountFilter();
      }.bind(this), 500); // Tiempo de espera (500 ms)
    };
    search.addEventListener(EVENT.SEARCH_NEW, searchFunc);
    search.openSearch();
    search.querySelector("input").focus();
  }

  resetSearchFilter(){
    console.log("CLEAR FILTER");
    if(this.SELECTED_TYPE === 'invoice'){
      this.receivedIndexes = 0;
      this.issuedIndexes = 0;
    }

    this.moreSalary = true;
    this.moreReceivedInvoice = true;
    this.moreIssuedInvoice = true;

    let searchInput = this.getElement("taxDetailToolbarHeaderTitleSectionSearchSearchInput");
    searchInput.value = "";

    let startDateInput = this.getElement("startDate");
    startDateInput.value = "";
    startDateInput.firstChild.value = "";

    let endDateInput = this.getElement("endDate");
    endDateInput.value = "";
    endDateInput.firstChild.value = "";

    let aonSearch = this.getElement("taxDetailToolbarHeaderTitleSectionSearch");
    aonSearch.buildBadge();

    delete this.SELECTED_FILTER.search;
    delete this.SELECTED_FILTER.startDate;
    delete this.SELECTED_FILTER.endDate;
    this.SELECTED_FILTER.page = 0;
  }

  async loadTable(){
    console.log("--- loadTable ---");
    console.log(this.SELECTED_FILTER);
    
    if(this.SELECTED_TYPE === 'invoice'){
      if (this.type == "tax") {
        if(this.SELECTED_INVOICE_TYPE == 'received'){
          // ReceivedInvoice
          console.log("ReceivedInvoice");
          let receivedInvoice = await getFiscalModelsInvoinces(this.SELECTED_FILTER);
          console.log(receivedInvoice);
          this.receivedInvoices = receivedInvoice;

          if (receivedInvoice && receivedInvoice.length > 0) {
            let toolbar = this.getElement(this.TOOLBAR);
            toolbar.addButton2End(ACTION.DOWNLOAD_EXCEL_INVOICE, () =>
              this.downloadInvoiceExcel()
            );
          }

          if (!this.receivedInvoices || this.receivedInvoices.length < 25)
            this.moreReceivedInvoice = false;

          console.log("createInvoiceRows");
          console.log(this.receivedInvoices);
          this.createInvoiceRows(this.receivedInvoices);
          this.receivedIndexes = receivedInvoice.length;
        } else if(this.SELECTED_INVOICE_TYPE == 'issued'){
          // IssuedInvoice
          let issuedInvoice = await getFiscalModelsInvoinces(this.SELECTED_FILTER);
          this.issuedInvoices = issuedInvoice;

          if (issuedInvoice && issuedInvoice.length > 0) {
            let toolbar = this.getElement(this.TOOLBAR);
            toolbar.addButton2End(ACTION.DOWNLOAD_EXCEL_INVOICE, () =>
              this.downloadInvoiceExcel()
            );
          }

          if (!this.issuedInvoices || this.issuedInvoices.length < 25)
            this.moreIssuedInvoice = false;

          this.createInvoiceRows(this.issuedInvoices);
          this.issuedIndexes = issuedInvoice.length;
        }
      } else if (this.type == "future") {
        if(this.SELECTED_INVOICE_TYPE == 'received'){
          // ReceivedInvoice
          let receivedInvoice = await getEstimationModelsFiscalInvoinces(this.SELECTED_FILTER);
          this.receivedInvoices = receivedInvoice;

          if (receivedInvoice && receivedInvoice.length > 0) {
            let toolbar = this.getElement(this.TOOLBAR);
            toolbar.addButton2End(ACTION.DOWNLOAD_EXCEL_INVOICE, () =>
              this.downloadInvoiceExcel()
            );
          }

          this.createInvoiceRows(this.receivedInvoices);
          this.receivedIndexes = receivedInvoice.length;
        } else if(this.SELECTED_INVOICE_TYPE == 'issued'){
          // IssuedInvoice
          let issuedInvoice = await getEstimationModelsFiscalInvoinces(this.SELECTED_FILTER);
          this.issuedInvoices = issuedInvoice;

          if (issuedInvoice && issuedInvoice.length > 0) {
            let toolbar = this.getElement(this.TOOLBAR);
            toolbar.addButton2End(ACTION.DOWNLOAD_EXCEL_INVOICE, () =>
              this.downloadInvoiceExcel()
            );
          }

          this.createInvoiceRows(this.issuedInvoices);
          this.issuedIndexes = issuedInvoice.length;
        }
      }
    } else if(this.SELECTED_TYPE === 'salary'){
      if (this.type == "tax") {
        // Salaies
        let salaries = await getFiscalModelsSalaries(this.SELECTED_FILTER);
        this.salaries = salaries;
        if (!this.salaries || this.salaries.length < 100) this.moreSalary = false;
        this.createSalaryRows(salaries);
      } else if (this.type == "future") {
        // Salaries
        let salaries = await getEstimationModelsFiscalSalaries(this.SELECTED_FILTER);
        this.salaries = salaries;
        if (!this.salaries || this.salaries.length < 100) this.moreSalary = false;
        this.createSalaryRows(salaries);
      }
    }
  }

  async updateCountFilter(){
    console.log("--- updateCountFilter ---");
    console.log(this.SELECTED_FILTER);
    
    if(this.SELECTED_TYPE === 'invoice'){
      if (this.type == "tax") {
        if(this.SELECTED_INVOICE_TYPE == 'received'){
          // ReceivedInvoice
          let recievedInvoiceCount = await getFiscalModelsInvoincesCount(this.SELECTED_FILTER);
          if(recievedInvoiceCount){
            let aonReceivedInvoiceTab = this.getElement("aonReceivedInvoiceTab");
            aonReceivedInvoiceTab.innerHTML =  `F. Recibidas (${recievedInvoiceCount.count || 0})`;
          }
        } else if(this.SELECTED_INVOICE_TYPE == 'issued'){
          // IssuedInvoice
          let issuedInvoiceCount = await getFiscalModelsInvoincesCount(this.SELECTED_FILTER);
          if(issuedInvoiceCount){
            let aonIssuedInvoiceTab = this.getElement("aonIssuedInvoiceTab");
            aonIssuedInvoiceTab.innerHTML = `F. Emitidas (${issuedInvoiceCount.count || 0})`;
          }
        }
      } else if (this.type == "future") {
        if(this.SELECTED_INVOICE_TYPE == 'received'){
          // ReceivedInvoice
          let recievedInvoiceCount = await getEstimationModelsFiscalInvoincesCount(this.SELECTED_FILTER);
          if(recievedInvoiceCount){
            let aonReceivedInvoiceTab = this.getElement("aonReceivedInvoiceTab");
            aonReceivedInvoiceTab.innerHTML = `F. Recibidas (${recievedInvoiceCount.count || 0})`;
          }
        } else if(this.SELECTED_INVOICE_TYPE == 'issued'){
          // IssuedInvoice
          let issuedInvoiceCount = await getEstimationModelsFiscalInvoincesCount(this.SELECTED_FILTER);
          if(issuedInvoiceCount){
            let aonIssuedInvoiceTab = this.getElement("aonIssuedInvoiceTab");
            console.log(aonIssuedInvoiceTab);
            aonIssuedInvoiceTab.innerHTML = `F. Emitidas (${issuedInvoiceCount.count || 0})`;
          }
        }
      }
    } else if(this.SELECTED_TYPE === 'salary'){
      if (this.type == "tax") {
        // Salaies
        let salaryCount = await getFiscalModelsSalariesCount(this.SELECTED_FILTER);
        if(salaryCount){
          let aonSalaryTab = this.getElement("aonSalaryTab");
          aonSalaryTab.innerHTML =  `Nóminas (${salaryCount.count || 0})`;
        }
      } else if (this.type == "future") {
        // Salaries
        let salaryCount = await getEstimationModelsFiscalSalariesCount(this.SELECTED_FILTER);
        if(salaryCount){
          let aonSalaryTab = this.getElement("aonSalaryTab");
          aonSalaryTab.innerHTML =  `Nóminas (${salaryCount.count || 0})`;
        }
      }
    }
  }

  back() {
    if (this.type == "tax") this.getApplication().setContent(new AonTax());
    else if (this.type == "future") {
      let filter = {
        periodText: this.tax.periodText,
        year: this.tax.year,
        period: this.tax.period,
      };
      this.getApplication().setContent(new AonFutureTax(FISCAL, filter));
    }
  }

  initTabs() {
    let tab = new AonTab();
    tab.id = this.TABS;
    this.appendChild(tab);
  }

  initContent() {
    let contentDiv = this.createElement(TAG.DIV);
    contentDiv.id = this.MAIN_CONTENT;
    contentDiv.style.width = "100%";
    contentDiv.style.display = "flex";

    let content = this.createElement(TAG.DIV);
    content.id = this.CONTENT;
    content.style.width = "100%";
    contentDiv.appendChild(content);

    let fileDiv = this.createElement(TAG.DIV);
    fileDiv.id = this.FILE;
    fileDiv.style.display = "none";
    fileDiv.style.position = "relative";
    contentDiv.appendChild(fileDiv);

    this.appendChild(contentDiv);
  }

  async init() {
    this.getApplication().startLoader();

    let aonTab = this.getElement(this.TABS);

    if (this.type == "tax") {
      this.filterReceivedInvoice = {
        fsModel: this.tax.id,
        invoiceType: "received",
        limit: 25,
        page: 0,
      };
      this.filterIssuedInvoice = {
        fsModel: this.tax.id,
        invoiceType: "issued",
        limit: 25,
        page: 0,
      };
      this.filterSalary = { fsModel: this.tax.id, limit: 25, page: 0 };

      // ReceivedInvoice
      let recievedInvoiceCount = await getFiscalModelsInvoincesCount(this.filterReceivedInvoice);
      if(recievedInvoiceCount && recievedInvoiceCount.count !== 0){
        this.receivedInvoicesCount = recievedInvoiceCount.count;
        aonTab.addOption({
          id: "aonReceivedInvoiceTab",
          title: `F. Recibidas (${recievedInvoiceCount.count})`,
          fn: () => {
            this.resetSearchFilter();
            this.updateCountFilter();
            this.createReceivedInvoiceTable();
          },
        });
      }

      // IssuedInvoice
      let issuedInvoiceCount = await getFiscalModelsInvoincesCount(this.filterIssuedInvoice);
      if(issuedInvoiceCount && issuedInvoiceCount.count !== 0){
        this.issuedInvoicesCount = issuedInvoiceCount.count;
        aonTab.addOption({
          id: "aonIssuedInvoiceTab",
          title: `F. Emitidas (${issuedInvoiceCount.count})`,
          fn: () => {
            this.resetSearchFilter();
            this.updateCountFilter();
            this.createIssuedInvoiceTable();
          }
        });
      }

      // Salaies
      let salaryCount = await getFiscalModelsSalariesCount(this.filterSalary);
      if(salaryCount && salaryCount.count !== 0){
        this.salariesCount = salaryCount.count;
        aonTab.addOption({
          id: "aonSalaryTab",
          title: `Nóminas (${salaryCount.count})`,
          fn: () => {
            this.resetSearchFilter();
            this.updateCountFilter();
            this.createSalaryPayrollTable();
          }
        });
      }
      
    } else if (this.type == "future") {
      this.filterReceivedInvoice = {
        year: this.tax.year,
        period: this.tax.period,
        type: this.getTypeByDescription(this.tax.description),
        invoiceType: "received",
        limit: 25,
        page: 0,
      };

      this.filterIssuedInvoice = {
        year: this.tax.year,
        period: this.tax.period,
        type: this.getTypeByDescription(this.tax.description),
        invoiceType: "issued",
        limit: 25,
        page: 0,
      };

      this.filterSalary = {
        year: this.tax.year,
        period: this.tax.period,
        type: this.getTypeByDescription(this.tax.description),
        limit: 25,
        page: 0,
      };

      // ReceivedInvoice
      let recievedInvoiceCount = await getEstimationModelsFiscalInvoincesCount(this.filterReceivedInvoice);
      if(recievedInvoiceCount && recievedInvoiceCount.count && recievedInvoiceCount.count !== 0){
        this.receivedInvoicesCount = recievedInvoiceCount.count;
        aonTab.addOption({
          id: "aonReceivedInvoiceTab",
          title: `F. Recibidas (${recievedInvoiceCount.count})`,
          fn: () => {
            this.resetSearchFilter();
            this.updateCountFilter();
            this.createReceivedInvoiceTable();
          }
        });
      }
        
      // IssuedInvoice
      let issuedInvoiceCount = await getEstimationModelsFiscalInvoincesCount(this.filterIssuedInvoice);
      if(issuedInvoiceCount && issuedInvoiceCount.count && issuedInvoiceCount.count !== 0){
        this.issuedInvoicesCount = issuedInvoiceCount.count;
        aonTab.addOption({
          id: "aonIssuedInvoiceTab",
          title: `F. Emitidas (${issuedInvoiceCount.count})`,
          fn: () => {
            this.resetSearchFilter();
            this.updateCountFilter();
            this.createIssuedInvoiceTable();
          }
        });
      }
     
      // Salaries
      let salaryCount = await getEstimationModelsFiscalSalariesCount(this.filterSalary);
      if(salaryCount && salaryCount.count && salaryCount.count !== 0){
        this.salariesCount = salaryCount.count;
        aonTab.addOption({
          id: "aonSalaryTab",
          title: `Nóminas (${salaryCount.count})`,
          fn: () => {
            this.resetSearchFilter();
            this.updateCountFilter();
            this.createSalaryPayrollTable();
          }
        });
      }
    }

    this.getApplication().stopLoader();

    if (this.receivedInvoicesCount > 0)
      await this.createReceivedInvoiceTable();
    else if (this.issuedInvoicesCount > 0)
      await this.createIssuedInvoiceTable();
    else if (this.salariesCount > 0)
      await this.createSalaryPayrollTable();

  }

  startLoader(){
    let aonReceivedInvoiceTab = this.getElement("aonReceivedInvoiceTab");
    let aonIssuedInvoiceTab = this.getElement("aonIssuedInvoiceTab");
    let aonSalaryTab = this.getElement("aonSalaryTab");

    if(aonReceivedInvoiceTab) aonReceivedInvoiceTab.classList.add('blocked');
    if(aonIssuedInvoiceTab) aonIssuedInvoiceTab.classList.add('blocked');
    if(aonSalaryTab) aonSalaryTab.classList.add('blocked');

    this.getApplication().startLoader();
  }

  stopLoader(){
    let aonReceivedInvoiceTab = this.getElement("aonReceivedInvoiceTab");
    let aonIssuedInvoiceTab = this.getElement("aonIssuedInvoiceTab");
    let aonSalaryTab = this.getElement("aonSalaryTab");

    if(aonReceivedInvoiceTab) aonReceivedInvoiceTab.classList.remove('blocked');
    if(aonIssuedInvoiceTab) aonIssuedInvoiceTab.classList.remove('blocked');
    if(aonSalaryTab) aonSalaryTab.classList.remove('blocked');
    
    this.getApplication().stopLoader();
  }

  async createReceivedInvoiceTable() {
    this.startLoader()

    this.SELECTED_FILTER = this.filterReceivedInvoice;
    this.SELECTED_TABLE = this.RECEIVED_INVOICE_TABLE;
    this.SELECTED_INVOICE_TYPE = 'received';
    this.SELECTED_TYPE = 'invoice';

    await this.createInvoiceTable();
    this.stopLoader()
  }

  async createIssuedInvoiceTable() {
    this.startLoader()

    this.SELECTED_FILTER = this.filterIssuedInvoice;
    this.SELECTED_TABLE = this.ISSUED_INVOICE_TABLE;
    this.SELECTED_INVOICE_TYPE = 'issued';
    this.SELECTED_TYPE = 'invoice';

    await this.createInvoiceTable();
    this.stopLoader()
  }

  async createInvoiceTable() {
    this.removeInvoiceActions();
    this.hideFile();

    let content = this.getElement(this.CONTENT);
    this.clearElement(content);

    let invoiceTable = createFetchingList(this.SELECTED_TABLE);
    invoiceTable.selectable = "true";
    invoiceTable.selectedColor = true;
    content.appendChild(invoiceTable);

    let invoiceTableBody = invoiceTable.getElementsByTagName("tbody")[0];
    invoiceTableBody.style.height = "calc(100vh - 240px)";

    invoiceTable.addColumn(MSG.DATE, "date", "dateTable", "90px");
    invoiceTable.addColumn(MSG.INVOICE_NUMBER, "string", "reference", "150px");
    invoiceTable.addColumn(MSG.HOLDER, "string", "name", "auto");
    invoiceTable.addColumn(MSG.BASE, "number", "baseParse", "100px");
    invoiceTable.addColumn("IVA", "number", "vatParse", "100px");
    invoiceTable.addColumn("IRPF", "number", "irpfParse", "100px");
    invoiceTable.addColumn("Exento", "number", "exemptParse", "100px");
    invoiceTable.addColumn(MSG.TOTAL, "number", "totalParse", "100px");
    invoiceTable.addColumn("", "icons", "icons", "5%");

    invoiceTable.addEventListener("select", () => {
      if (invoiceTable.selected.length === 1) {
        this.addInvoiceActions();
      } else if (invoiceTable.selected.length === 0) {
        this.removeInvoiceActions();
      }
    });

    invoiceTable.addEventListener("more", async () => {
      console.log("More EVENT");
      
      this.startLoader()
      
      if(this.SELECTED_INVOICE_TYPE === 'received' && this.moreReceivedInvoice){
        await this.loadMoreReceivedInvoice();
      } else if(this.SELECTED_INVOICE_TYPE === 'issued' && this.moreIssuedInvoice){
        await this.loadMoreIssuedInvoice();
      }

      this.stopLoader()

      invoiceTable.setFetchingData(false);
    });
    
    await this.loadTable();
  }

  async loadMoreReceivedInvoice() {
    this.SELECTED_FILTER.page = this.SELECTED_FILTER.page + 1;

    let receivedInvoices;
    if (this.type == "tax") {
      receivedInvoices = await getFiscalModelsInvoinces(this.SELECTED_FILTER);
    } else if (this.type == "future") {
      receivedInvoices = await getEstimationModelsFiscalInvoinces(this.SELECTED_FILTER);
    }

    if (!receivedInvoices || receivedInvoices.length == 0) {
      console.log("---- END INVOICE (RECEIVED) -----");
      this.moreReceivedInvoice = false;
    }

    this.receivedInvoices.push(...receivedInvoices);

    this.createInvoiceRows(receivedInvoices);

    this.receivedIndexes = this.receivedInvoices.length;
  }

  async loadMoreIssuedInvoice() {
    this.SELECTED_FILTER.page = this.SELECTED_FILTER.page + 1;

    let issuedInvoices;
    if (this.type == "tax") {
      issuedInvoices = await getFiscalModelsInvoinces(this.SELECTED_FILTER);
    } else if (this.type == "future") {
      issuedInvoices = await getEstimationModelsFiscalInvoinces(this.SELECTED_FILTER);
    }

    if (!issuedInvoices || issuedInvoices.length == 0) {
      console.log("---- END INVOICE (ISSUED) -----");
      this.moreIssuedInvoice = false;
    }

    this.issuedInvoices.push(...issuedInvoices);

    this.createInvoiceRows(issuedInvoices);

    this.issuedIndexes = this.issuedInvoices.length;
  }

  createInvoiceRows(invoices) {
    let invoiceTable = this.getElement(this.SELECTED_TABLE);

    if (invoices) {
      invoices.forEach((invoice, index) => {

        if(this.SELECTED_INVOICE_TYPE === 'received'){
          index = this.receivedIndexes + index;
        } else if(this.SELECTED_INVOICE_TYPE === 'issued'){
          index = this.issuedIndexes + index;
        }

        if (!invoice.name) {
          invoice.name =
            invoice.type === "emitida"
              ? invoice.receiver
                ? invoice.receiver.name
                : ""
              : invoice.sender
              ? invoice.sender.name
              : "";
        }
        let date = new Date(invoice.date);
        let day = date.getDate();
        let month = date.getMonth() + 1;
        let year = date.getFullYear();
        invoice.dateTable =
          day.toString().zeros(2) +
          "/" +
          month.toString().zeros(2) +
          "/" +
          year.toString();
        invoice.documentNumber = getDocumentNumber(invoice);
       
        // Base & VAT & IRFP
        if(invoice.details){
          let { taxableBase, vatQuota } = invoice.details.filter(detail => detail.percentage || detail.withholding_percentage).reduce((totals, detail) => {
            totals.taxableBase += detail.amount || 0;
            totals.vatQuota += detail.quota || 0;
            return totals;
          }, { taxableBase: 0, vatQuota: 0 });

          if(taxableBase == 0) taxableBase = invoice.taxableBase;
          if(vatQuota == 0) vatQuota = invoice.vatQuota;

          invoice.baseParse = formatNumber(taxableBase, 2, "EUR");
          invoice.vatParse = formatNumber(vatQuota, 2, "EUR");
        } else {
          invoice.baseParse = formatNumber(invoice.taxableBase, 2, "EUR");
          invoice.vatParse = formatNumber(invoice.vatQuota, 2, "EUR");
        }

        // IRPF
        if(invoice.details){
          let { withholdingQuota } = invoice.details.filter(detail => detail.percentage || detail.withholding_percentage).reduce((totals, detail) => {
            totals.withholdingQuota += detail.withholding_quota || 0;
            return totals;
          }, { withholdingQuota: 0});

          if(withholdingQuota == 0) withholdingQuota = invoice.retentionQuota;

          invoice.irpfParse = formatNumber(withholdingQuota, 2, "EUR");
        } else {
          invoice.irpfParse = formatNumber(0, 2, "EUR");
        }

        // Exempt
        if(invoice.details){
          let { exemptQuota } = invoice.details.filter(detail => !detail.percentage && !detail.withholding_percentage).reduce((totals, detail) => {
            totals.exemptQuota += detail.amount || 0;
            return totals;
          }, { exemptQuota: 0});
          invoice.exemptParse = formatNumber(exemptQuota, 2, "EUR");
        } else {
          invoice.exemptParse = formatNumber(0, 2, "EUR");
        }

        invoice.totalParse = formatNumber(invoice.total, 2, "EUR");

       
        let icons = [];
        let icon = {
          icon: MATERIAL_ICONS.VISIBILITY,
          title: MSG.SHOW_FILE,
          color: "var(--aonTaxBuildPrintRes)",
          fn: async () => {
            this.startLoader()
            await this.showFile(invoice, index);
            this.stopLoader()
          },
        };
        icons.push(icon);
        invoice.icons = icons;

        let tr = invoiceTable.addRow(invoice, async () => {
          this.startLoader()
          await this.showFile(invoice, index);
          this.stopLoader()
        });

        // Add count title to CheckBox
        tr.firstChild.firstChild.title = `Seleccionar fila ${index + 1}`
      });
    }
  }

  async createSalaryPayrollTable() {
    this.startLoader()

    this.SELECTED_FILTER = this.filterSalary;
    this.SELECTED_TABLE = this.SALARY_TABLE;
    this.SELECTED_INVOICE_TYPE = undefined;
    this.SELECTED_TYPE = 'salary';

    await this.createSalaryTable();

    this.stopLoader()
  }

  async createSalaryTable() {
    this.removeInvoiceActions();
    this.removeInvoiceExcelAction();
    this.hideFile();

    let content = this.getElement(this.CONTENT);
    this.clearElement(content);

    let aonSalaryDetailTable = createFetchingList(this.SALARY_TABLE);
    aonSalaryDetailTable.selectedColor = true;
    content.appendChild(aonSalaryDetailTable);

    let aonSalaryDetailTableBody =aonSalaryDetailTable.getElementsByTagName("tbody")[0];
    aonSalaryDetailTableBody.style.height = "calc(100vh - 240px)";

    aonSalaryDetailTable.addColumn(MSG.DATE, "date", "endDate", "120px");
    aonSalaryDetailTable.addColumn(MSG.EMPLOYEE, "string", "employeeName", "auto");
    aonSalaryDetailTable.addColumn("Bruto", "number", "paymentParse", "100px");
    aonSalaryDetailTable.addColumn("Deducciones", "number", "deductionParse", "100px");
    aonSalaryDetailTable.addColumn("IRPF", "number", "irpfParse", "100px");
    aonSalaryDetailTable.addColumn("Neto", "number", "totalParse", "100px");
    aonSalaryDetailTable.addColumn("", "icons", "icons", "5%");
    aonSalaryDetailTable.addEventListener("more", async () => {
      if (this.moreSalary) {
        this.startLoader()
        await this.loadMoreSalary();
        this.stopLoader()
      }
    });

    await this.loadTable();
  }

  async loadMoreSalary() {
    this.SELECTED_FILTER.page = this.SELECTED_FILTER.page + 1;

    let salaries;
    if (this.type == "tax") {
      salaries = await getFiscalModelsSalaries(this.SELECTED_FILTER);
    } else if (this.type == "future") {
      salaries = await getEstimationModelsFiscalSalaries(this.SELECTED_FILTER);
    }

    if (!salaries || salaries.length == 0) this.moreSalary = false;

    this.salaries.push(...salaries);

    this.createSalaryRows(salaries);
  }

  createSalaryRows(salaries) {
    let aonSalaryTable = this.getElement(this.SALARY_TABLE);
    if (salaries) {
      salaries.forEach((salary, index) => {
        index = this.salaries.length > 100 ? (this.salaries.length + index) : index;

        salary.paymentParse = formatNumber(salary.totalPayment, 2, "EUR");
        salary.deductionParse = formatNumber(salary.totalDeduction, 2, "EUR");
        salary.irpfParse =  formatNumber(salary.irpf, 2, "EUR");
        salary.totalParse = formatNumber(salary.totalLiquid, 2, "EUR");

        let icons = [];
        let icon = {
          icon: MATERIAL_ICONS.VISIBILITY,
          title: MSG.SHOW_FILE,
          color: "var(--aonTaxBuildPrintRes)",
          fn: async () => {
            this.startLoader()
            await this.showFile(salary, index);
            this.stopLoader()
          },
        };
        icons.push(icon);
        salary.icons = icons;

        aonSalaryTable.addRow(salary, async () => {
          this.startLoader()
          await this.showFile(salary, index);
          this.stopLoader()
        });
      });
    }
  }

  hideFile() {
    let fileDiv = this.getElement(this.FILE);
    let contentDiv = this.getElement(this.CONTENT);

    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButton(ACTION.CLOSE_PDF.id);

    console.log(" ------ hideFile() : " + this.SELECTED_TABLE);
    
    if(this.SELECTED_TABLE){
      let visibilityButtons = this.querySelectorAll(`#${this.SELECTED_TABLE}Icon0`);
      visibilityButtons.forEach((visibilityButton, i) => visibilityButton.innerHTML = "visibility");

      let table = this.getElement(this.SELECTED_TABLE);
      if(table) table.addBackgroundTr();
    }
    
    fileDiv.style.display = "none";
    contentDiv.style.width = "100%";
  }

  async showFile(file, index) {
    //this.startLoader()

    console.log("---- SHOW FILE -----");
    console.log(file);
    console.log(index);

    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.addButton2End(ACTION.CLOSE_PDF, () => this.hideFile());

    let visibilityButtonId = `${this.SELECTED_TABLE}`;

    let visibilityButton = this.visibilityOffButton(visibilityButtonId, index);

    let visible = "visibility_off" === visibilityButton.innerHTML;

    let fileDiv = this.getElement(this.FILE);
    let contentDiv = this.getElement(this.CONTENT);

    if (visible) {
      visibilityButton.innerHTML = "visibility";
      fileDiv.style.display = "none";
      contentDiv.style.width = "100%";
      toolbar.removeButton(ACTION.CLOSE_PDF.id);
    } else {
      visibilityButton.innerHTML = "visibility_off";
      fileDiv.style.display = "block";
      fileDiv.style.width = "50%";
      contentDiv.style.width = "50%";

      this.clearElement(fileDiv);

      let json = {
        id: file.id,
        source: this.SELECTED_TYPE,
        domain_id: LS.getDomainId(),
        domain_name: LS.getDomainName(),
        login: LS.getDomainLogin(),
      };

      let viewer = new AonViewer();
      viewer.type = this.SELECTED_TYPE === "invoice" ? this.getInvoiceViewerType(file) : "application/pdf";
      viewer.file = this.SELECTED_TYPE === "invoice" ? this.getInvoiceViewerFile(file, json) : `ms/api/salary_exporter/salary?json=${btoa(JSON.stringify(json))}`;
      viewer.width = fileDiv.offsetWidth;

      /*
      console.log("--- aonTaxDetail ---");
      console.log(file);
			console.log(json);
			console.log(viewer.type);
			console.log(viewer.file);
      */
      /*
      viewer.addEventListener(EVENT.SEND_MAIL, () => this.sendInvoice());
			viewer.addEventListener(EVENT.PRINT_IMAGE, () => { getInvofoxTextContent(this.getInvoice().insight.invofoxId).then(t => viewer.printImageTextLayer(t)); } );
			viewer.addEventListener(EVENT.PRINT_PDF_PAGE, (e) => { if ( !e.detail.text ) getInvofoxTextContent(this.getInvoice().insight.invofoxId).then(t => viewer.printPdfTextLayer(e.detail.page, t)); } );
			*/
      fileDiv.appendChild(viewer);

      await waitEl("#aonViewerCanvasIFrame");
      viewer.removeButton("aonViewerButtonsDivEmailSpan");
    }
  }

  getInvoiceViewerType(invoice){
    return !invoice.file || this.type.toLowerCase() === 'emitida'
    ? 'application/pdf' : invoice.file.content_type;
  }

  getInvoiceViewerFile(invoice, json){
    return !invoice.file || this.type.toLowerCase() === 'emitida'
    ? '/ms/api/download_invoice_pdf?json=' + btoa(JSON.stringify(json))
    : invoice.file.path;
  }

  visibilityOffButton(id, index) {
    let visibilityButtons = this.querySelectorAll(`#${id}Icon0`);
    visibilityButtons.forEach((visibilityButton, i) => {
      if (i !== index) visibilityButton.innerHTML = "visibility";
    });

    return visibilityButtons[index];
  }

  addInvoiceActions() {
    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.addButton2End(ACTION.DOWNLOAD_INVOICE, () =>
      this.downloadInvoices()
    );
  }

  removeInvoiceActions() {
    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButton(ACTION.DOWNLOAD_INVOICE.id);
  }

  removeInvoiceExcelAction() {
    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.removeButton(ACTION.DOWNLOAD_EXCEL_INVOICE.id);
  }

  downloadInvoices() {
    let aonInvoiceTable = this.getElement(this.SELECTED_TABLE);
    let data = {
      domainId: localStorage.getItem("aon_domain_id"),
      domainName: localStorage.getItem("aon_domain_name"),
      domainLogin: localStorage.getItem("aon_domain_login"),
      ids: aonInvoiceTable.selected.map((r) => r.id),
      status: "accounting",
    };
    let json = btoa(JSON.stringify(data));
    downloadInvoices(json);
  }

  downloadInvoiceExcel() {
    let aonInvoiceTable = this.getElement(this.SELECTED_TABLE);
    /*
    let data = {
      domainId: localStorage.getItem("aon_domain_id"),
      domainName: localStorage.getItem("aon_domain_name"),
      domainLogin: localStorage.getItem("aon_domain_login"),
      ids: aonInvoiceTable.selected.map((r) => r.id)
    };

    let json = btoa(JSON.stringify(data));
    downloadInvoiceExcel(json);
    */

    let filter = {
      ...this.SELECTED_FILTER,
      ids: aonInvoiceTable.selected.map((r) => r.id),
      domainId: localStorage.getItem("aon_domain_id"),
      domainName: localStorage.getItem("aon_domain_name"),
      domainLogin: localStorage.getItem("aon_domain_login"),
      invoiceType: this.SELECTED_INVOICE_TYPE,
    };

    let json = btoa(JSON.stringify(filter));

    if (this.type == "tax") {
      getFiscalModelsInvoincesExcel(json);
    } else if (this.type == "future") {
      getEstimationModelsFiscalInvoincesExcel(json);
    }
  }

  getTypeByDescription(description) {
    if (description) {
      if (description.includes("IVA")) return "VAT";
      else if (description.includes("Alava")) return "IRPF_ALAVA";
      else if (description.includes("Bizkaia")) return "IRPF_BIZKAIA";
      else if (description.includes("Gipuzkoa")) return "IRPF_GIPUZKOA";
      else if (description.includes("Navarra")) return "IRPF_NAVARRA";
      else if (description.includes("AEAT")) return "IRPF_AEAT";
      else if (description.includes("Arrendamiento"))
        return "IRPF_ARRENDAMIENTO";
      else if (description.includes("Profesional")) return "IRFP_PROFESIONAL";
    }
    return null;
  }

  getAmount(sepaDetail) {
    return formatNumber(
      !sepaDetail.finance ? 0 : sepaDetail.finance.amount,
      2,
      "EUR"
    );
  }

  getTotal(sepaDoc) {
    if (!sepaDoc.fbatch_details || sepaDoc.fbatch_details.length === 0) {
      return formatNumber(0, 2, "EUR");
    }

    let total = sepaDoc.fbatch_details.reduce(
      (t, detail) => t + detail.amount,
      0
    );
    return formatNumber(total, 2, "EUR");
  }

  getPdf({ id: source_id, newModel }) {
    const source = DataAttachSource.getValueByName(newModel);
    if (!source) {
      this.showMessageError("DataAttachSource not found." + newModel);
      return;
    }

    getAttach({
      attachType: "data",
      file: true,
      source_id,
      source,
    })
      .then((r) => {
        if (r && r.id && r.contentType && r.content) {
          openFileBase64(r.content, r.contentType);
        } else {
          this.showMessageError("Declaración no encontrada!");
        }
      })
      .catch((error) => {
        this.showError(error);
      });
  }

  openDialog(resp) {
    this.applicationEl = this.getApplication();
    const dialog = this.applicationEl.getDialog();
    dialog.clear();
    if (!this.isMobile()) {
      dialog.width = "500px";
    }

    if (dialog.getButtonAccept()) dialog.getButtonAccept().remove();
    if (dialog.getButtonCancel()) dialog.getButtonCancel().remove();

    let div = this.builDialog(resp);
    dialog.setContent(div);

    if ("CUSTOMER_CHECK" === resp.status) {
      this.createFooterDialog(resp, dialog, div);
    } else {
      disabledForm(`${this.id}Form`, "aon-switch");
    }

    dialog.open();

    this.visibleFields(resp);

    this.eventData(resp);
  }

  builDialog(resp) {
    const div = this.createElement(TAG.DIV);
    const divImg = this.createElement(TAG.DIV);
    divImg.style.fontSize = 18;
    divImg.appendChild(FiscalUtils.createImgAdmin(resp.administration));

    const spanTextImg = this.createElement(TAG.SPAN);
    spanTextImg.style.marginLeft = 3;
    spanTextImg.textContent = `${MSG.MODEL} ${resp.newModel} (${resp.modelText})`;
    divImg.appendChild(spanTextImg);
    div.appendChild(divImg);

    const divOne = this.createElement(TAG.DIV);
    divOne.className = "aonFlexBetween colorGrey aonFontWeight-700";
    divOne.style.margin = "10px 0";

    const divTextOne = this.createElement(TAG.DIV);
    divTextOne.textContent = `${resp.periodText} - ${resp.year}`;
    divOne.appendChild(divTextOne);

    const divTextTwo = this.createElement(TAG.DIV);
    divTextTwo.style.textAlign = "end";
    divTextTwo.style.color = "black";
    divTextTwo.textContent = resp.resultFormat;
    divOne.appendChild(divTextTwo);
    div.appendChild(divOne);

    // Esto no aparece en el modelo 303 a ingresar, pues en el 303 se deja elegir el tipo de ingreso que se quiere hacer (para poder indicar aplazamiento)
    if (resp.model != "IVA" || resp.result <= 0)
      if (resp.typeText) {
        const divK = this.createElement(TAG.DIV);
        divK.classList.add("aonFlexBetween", "colorGrey", "aonFontWeight-700");
        divK.style.margin = "20px 0";
        const divT = this.createElement(TAG.DIV);
        divT.innerHTML = `${MSG.TYPE}: <span style="color:black;"> ${resp.typeText}</span>`;
        divK.appendChild(divT);
        div.appendChild(divK);
      }

    //---FORM------
    const form = this.createElement(TAG.FORM);
    form.id = `${this.id}Form`;
    div.appendChild(form);

    // Para el Modelo 303 a ingresar, se deja elegir el tipo de ingreso
    if (resp.model == "IVA" && resp.result > 0) {
      let types = [
        { value: "DEPOSIT", name: "Ingreso" },
        { value: "BANK", name: "Domiciliación" },
        { value: "DEPOSIT_CCT", name: "Ingreso a anotar en CCT" },
        { value: "DEFERRAL", name: "Solicitud de aplazamiento" },
      ];

      const aonSelectTipo = new AonSelect();
      aonSelectTipo.name = "tipodec";
      aonSelectTipo.id = "tipodec";
      aonSelectTipo.title = "Tipo";
      aonSelectTipo.setOptions(types);
      if (resp.type) aonSelectTipo.value = resp.type;
      aonSelectTipo.addEventListener(EVENT.SELECT, () => {
        resp.type = aonSelectTipo.value;
        this.visibleFields(resp);
      });
      form.appendChild(aonSelectTipo);
    }

    const aonSelect = new AonSelect();
    aonSelect.name = "iban";
    aonSelect.id = "iban";
    aonSelect.title = "IBAN";
    aonSelect.hidden = true;
    form.appendChild(aonSelect);

    const aonInputId = new AonInput();
    aonInputId.name = "id";
    aonInputId.id = "id";
    aonInputId.description = "id";
    aonInputId.value = resp.id;
    aonInputId.visible = false;
    form.appendChild(aonInputId);

    // NRC

    const divNrc = this.createElement(TAG.DIV);
    divNrc.hidden = true;
    divNrc.className = "aon-margin-0";
    divNrc.id = "divNrc";
    form.appendChild(divNrc);

    const aonInputNrc = new AonInput();
    aonInputNrc.className = "aonWidth75";
    aonInputNrc.style.width = "72%";
    aonInputNrc.id = "nrc";
    aonInputNrc.description = "NRC";
    aonInputNrc.name = "nrc";
    aonInputNrc.type = "text";
    if (resp.nrc) aonInputNrc.value = resp.nrc;
    divNrc.appendChild(aonInputNrc);

    // DATOS APLAZAMIENTO

    const divAplazamiento = this.createElement(TAG.DIV);
    divAplazamiento.hidden = true;
    divAplazamiento.className = "aon-margin-0";
    divAplazamiento.id = "divAplazamiento";
    form.appendChild(divAplazamiento);

    const aonInputPlazos = new AonNumber();
    aonInputPlazos.className = "aonWidth75";
    aonInputPlazos.id = "plazos";
    aonInputPlazos.description = "Número de Plazos";
    if (resp.plazos) aonInputPlazos.value = resp.plazos;
    divAplazamiento.appendChild(aonInputPlazos);

    const aonInputFechaPlazo = new AonDate();
    aonInputFechaPlazo.className = "aonWidth75";
    aonInputFechaPlazo.id = "fechaPlazo";
    aonInputFechaPlazo.title = "Fecha Primer Plazo";
    aonInputFechaPlazo.readonly = "CUSTOMER_CHECK" !== resp.status;
    divAplazamiento.appendChild(aonInputFechaPlazo);

    // CERTIFICADO ELECTRONICO

    const aonSelect2 = new AonSelect();
    aonSelect2.name = "certi";
    aonSelect2.id = "certi";
    aonSelect2.title = "Certificado para la Presentación";
    aonSelect2.hidden =
      resp.presModelAuto == 0 || "CUSTOMER_CHECK" != resp.status;
    form.appendChild(aonSelect2);

    //---END FORM---

    return div;
  }

  createFooterDialog(resp, dialog, divMain) {
    let textArea = undefined;
    let buttonAccept = undefined;

    const div = this.createElement(TAG.DIV);
    div.style.textAlign = "right";
    const checkBox = new AonCheckbox();
    const span = this.createElement(TAG.SPAN);
    span.style.color = "grey";
    span.style.fontSize = "12px";
    span.style.fontWeight = "500";
    span.textContent = "Acepto los datos reflejados";
    checkBox.id = this.DIALOG_CHECKBOX;
    checkBox.description = span.outerHTML;
    checkBox.addEventListener(EVENT.CHANGE, ({ target }) => {
      buttonAccept.disabled = target.checked ? false : true;
    });

    div.appendChild(checkBox);

    // Si esta configurado presentacion automatica del modelo, mostrar texto informandolo (tambien se muestra si está en entorno de pruebas de la AEAT)
    if (resp.presModelAuto == 1) {
      const divTextPres = this.createElement(TAG.DIV, "divTextPres");
      divTextPres.style.marginTop = 6;
      divTextPres.style.textAlign = "center";
      divTextPres.style.fontWeight = "bold";
      if (resp.testEnvironment) {
        divTextPres.innerHTML =
          '<span style="color:red;">ENTORNO DE PRUEBAS DE LA AEAT</span><br>Si acepta los datos, el modelo se presentará automaticamente.';
      } else {
        divTextPres.innerHTML =
          '<span style="color:red;">PRESENTACION DEL MODELO</span><br>Si acepta los datos, el modelo se presentará automaticamente.';
      }
      div.appendChild(divTextPres);
    }

    divMain.appendChild(div);

    // Boton Rechazar
    const buttonCancel = dialog.addCancelAction(() => {
      this.visibleFields(null);
      const tipodec = this.getElement("tipodec");
      if (tipodec) tipodec.disabled = true;
      div.innerHTML = "";
      textArea = new AonAutosizeTextarea();
      textArea.name = "reasonReject";
      textArea.title = "Motivo del rechazo";
      this.getElement(`${this.id}Form`).appendChild(textArea);

      buttonCancel.remove();
      buttonAccept.disabled = false;
    }, false);

    buttonCancel.innerHTML = "Rechazar";

    buttonAccept = dialog.addSendAction(() => {
      if (textArea) {
        const value = textArea.value;
        if (!value) return false;
      }
      this.save(resp).then(() => {
        dialog.close();
      });
    });

    buttonAccept.disabled = true;
    buttonCancel.style.padding = buttonAccept.style.padding = "0.5rem 1rem";
  }

  async eventData(resp) {
    const iban = this.getElement("iban");

    this.getApplicationParent()
      .getBanks()
      .then((result) => {
        if (result) {
          let options = result.map((r) => ({
            name: `${r.bank_account} - ${r.alias}`,
            value: `${this.replaceAllPoint(r.bank_account)}`,
          }));
          iban.setOptions(options);
          if (resp.iban) {
            iban.value = resp.iban;
          }
        }
      });

    const certi = this.getElement("certi");
    getAeatCertificates().then((certs) => {
      certi.setOptions(
        certs.map((s) => {
          return {
            value: s.id,
            name: s.name,
          };
        })
      );
      // Si solo hay un certificado, se muestra ese seleccionado por defecto
      if (certi.getOptions().length == 1) {
        certi.value = certi.getOptions()[0].value;
      }

      if (resp.fechaPlazo) {
        const fechaPlazo = this.getElement("fechaPlazo");
        fechaPlazo.value = resp.fechaPlazo;
      }
    });
  }

  replaceAllPoint(str) {
    return String(str).replaceAll(".", "");
  }

  getFormValues() {
    let banks = this.getApplicationParent().BANKS;
    let formObj = serializeForm(this.getElement(`${this.id}Form`));
    if (!isEmptyObject(banks) && formObj.iban) {
      const bankObj = banks.find(
        (bank) => this.replaceAllPoint(bank.bank_account) == formObj.iban
      );
      if (bankObj) {
        formObj["bankAlias"] = bankObj.alias;
        formObj["bic"] = bankObj.bic;
      }
    }

    const nrc = this.getElement("nrc");
    formObj["nrc"] = nrc.value;

    const certi = this.getElement("certi");
    formObj["certi"] = certi.value;

    const plazos = this.getElement("plazos");
    formObj["plazos"] = plazos.value;
    const fechaPlazo = this.getElement("fechaPlazo");
    formObj["fechaPlazo"] = fechaPlazo.value;

    return formObj;
  }

  visibleFields(resp) {
    let iban = this.getElement("iban");
    let divNrc = this.getElement("divNrc");
    let divAplazamiento = this.getElement("divAplazamiento");
    let certi = this.getElement("certi");
    let divTextPres = this.getElement("divTextPres");
    let ibanHidden = true;
    let nrcHidden = true;
    let aplazamientoHidden = true;
    let certiHidden = true;

    if (resp) {
      certiHidden = resp.presModelAuto == 0 || "CUSTOMER_CHECK" != resp.status;
      switch (resp.type) {
        case CONST_FISCAL.DEPOSIT:
          nrcHidden = false;
          break;
        case CONST_FISCAL.BANK:
        case CONST_FISCAL.PAYBACK:
          ibanHidden = false;
          break;
        case CONST_FISCAL.DEFERRAL:
          ibanHidden = false;
          aplazamientoHidden = false;
          certiHidden = true;
          break;
      }
    }

    iban.hidden = ibanHidden;
    divNrc.hidden = nrcHidden;
    divAplazamiento.hidden = aplazamientoHidden;
    if (certi) certi.hidden = certiHidden;
    if (divTextPres) divTextPres.hidden = certiHidden;
  }
}
window.customElements.define("aon-tax-detail", AonTaxDetail);
