import { AonElement } from './AonElement.js';

import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS } from '../environments/environments.js';
import { getDomains, getCustomers, updateDomains } from '../services/domainsService.js';

import '../css/aon-domain-customer.css';
import { AonIconButton } from './aon-icon-button.js';
import { AonButton } from './aon-button.js';
import { AonSelect } from './aon-select.js';
import { AonCheckbox } from './aon-checkbox.js';

export class AonDomainCustomer extends AonElement {

  mainContainer;

  domainList;
  customerList;

  confirmBar;
  confirmButton;

  searchBar;
  searchInput;
  domainsSearchCheck;
  customersSearchCheck;

  searchMode;

  noCustomerDomainsOnlyCheck;
  domainAonStatusFilter;

  loadedDomains;
  loadedCustomers;

  selectedDomain;
  selectedCustomer;

  searchTimeout;

  DOMAIN_STATUS_OPTIONS = ["NOT_BILLABLE", "BILLABLE"];

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor() {
		super();
	}

  connectedCallback() {
    this.initialize().then(() => this.build());
	}

  async initialize() {
    this.id = this.id || 'aonDomainCustomer';

    await this.updateDomains();
    // await this.updateCustomers();
  }
  
  async updateDomains() {
    let domains = await getDomains();
    this.loadedDomains = domains ?  domains.sort((a, b) => (a.name ? a.name : "").trim().localeCompare((b.name ? b.name : "").trim())) : [];
  }

  async updateCustomers() {
    let customers = await getCustomers({perPage: 2000});
    this.loadedCustomers = customers ?  customers.sort((a, b) => (a.name ? a.name : "").trim().localeCompare((b.name ? b.name : "").trim())) : [];
  }

  build() {
    this.clear();
    this.buildDomainCustomer();
  }

  buildDomainCustomer() {
    this.mainContainer = document.createElement('div');
    this.mainContainer.style.width = "95%";
    this.mainContainer.style.margin = "auto";
    this.mainContainer.style.height = "100%";
    this.mainContainer.style.display = "flex";
    this.mainContainer.style.flexWrap = "wrap";
    this.mainContainer.style.justifyContent = "space-between";
    this.mainContainer.style.alignItems = "center";

    this.buildSearchBar();
    this.buildDomainList();
    this.buildCustomerList();
    this.buildConfirmButton();

    this.mainContainer.appendChild(this.searchBar);
    this.mainContainer.appendChild(this.domainList);
    this.mainContainer.appendChild(this.customerList);
    this.searchBar.appendChild(this.confirmBar);

    this.appendChild(this.mainContainer);
    this.loadDomains();
    this.manageEmptyCustomerList();
  }

  // --- CONSTRUIR ELEMENTOS PRINCIPALES ---
  
  buildSearchBar() {
    this.searchBar = document.createElement('div');
    this.searchBar.style.width = "100%";
    this.searchBar.style.height = "70px";
    this.searchBar.style.display = "flex";
    this.searchBar.style.justifyContent = "flex-start";
    this.searchBar.style.alignItems = "flex-start";
    this.searchBar.style.padding = "5px";
    this.searchBar.style.gap = "10px";
    
    let searchInputAndModeContainer = document.createElement('div');
    searchInputAndModeContainer.style.display = "flex";
    searchInputAndModeContainer.style.justifyContent = "center";
    searchInputAndModeContainer.style.alignItems = "center";
    searchInputAndModeContainer.style.flexDirection = "row";
    searchInputAndModeContainer.style.width = "50%";
    searchInputAndModeContainer.style.backgroundColor = "AliceBlue";
    searchInputAndModeContainer.style.borderRadius = "100px";
    searchInputAndModeContainer.style.paddingLeft = "10px"
    searchInputAndModeContainer.classList.add("searchBar")
    
    this.searchInput = document.createElement('input');
    this.searchInput.style.width = "calc(100% - 30px)";
    this.searchInput.style.height = "40px";
    this.searchInput.style.border = "none";
    this.searchInput.style.borderRadius = "100px";
    this.searchInput.style.outline = "none";
    this.searchInput.style.padding = "5px";
    this.searchInput.style.fontSize = "14px";
    this.searchInput.style.fontWeight = "bold";
    this.searchInput.style.backgroundColor = "transparent";
    this.searchInput.placeholder = "Búsqueda";
    
    this.searchInput.addEventListener('keyup', (event) => this.searchFunction());
    this.searchInput.addEventListener("focus", (event) => searchInputAndModeContainer.style.border = "2px solid var(--aonBlue)");
    this.searchInput.addEventListener("focusout", (event) => searchInputAndModeContainer.style.border = "none");
    
    let searchIcon = document.createElement('span');
    searchIcon.classList.add(CSS.MATERIAL_ICONS);
    searchIcon.innerHTML = MATERIAL_ICONS.SEARCH;
    searchIcon.style.width = "20px";
    searchIcon.style.userSelect = "none";
    
    searchInputAndModeContainer.appendChild(searchIcon);
    searchInputAndModeContainer.appendChild(this.searchInput);
    
    let searchFiltersContainer = document.createElement('div');
    searchFiltersContainer.style.width = "35%";
    searchFiltersContainer.style.height = "100%";
    searchFiltersContainer.style.display = "flex";
    searchFiltersContainer.style.justifyContent = "center";
    searchFiltersContainer.style.alignItems = "center";
    searchFiltersContainer.style.flexWrap = "wrap";
    
    let noCustomerDomainsOnlyCheckContainer = document.createElement('div');
    noCustomerDomainsOnlyCheckContainer.style.width = "100%";
    noCustomerDomainsOnlyCheckContainer.style.height = "50%";
    noCustomerDomainsOnlyCheckContainer.style.display = "flex";
    noCustomerDomainsOnlyCheckContainer.style.justifyContent = "flex-start";
    noCustomerDomainsOnlyCheckContainer.style.alignItems = "center";
    
    this.noCustomerDomainsOnlyCheck = new AonCheckbox();
    this.noCustomerDomainsOnlyCheck.style.marginLeft = "0";
    this.noCustomerDomainsOnlyCheck.id = this.id + "CustomersSearchCheck";
    this.noCustomerDomainsOnlyCheck.checked = false;
    this.noCustomerDomainsOnlyCheck.description = "No vinculados"; 
    this.noCustomerDomainsOnlyCheck.title  = "Solo dominios sin cliente vinculado"

    noCustomerDomainsOnlyCheckContainer.appendChild(this.noCustomerDomainsOnlyCheck);
    
    let domainAonStatusFilterContainer = document.createElement('div');
    domainAonStatusFilterContainer.style.width = "100%";
    domainAonStatusFilterContainer.style.height = "50%";
    domainAonStatusFilterContainer.style.display = "flex";
    domainAonStatusFilterContainer.style.justifyContent = "flex-start";
    domainAonStatusFilterContainer.style.alignItems = "center";
    domainAonStatusFilterContainer.style.gap = "5px";

    this.domainAonStatusFilter = new AonSelect();
    this.domainAonStatusFilter.id = this.id + "DomainAonStatusFilter";
    this.domainAonStatusFilter.classList.add("domainLinkAonSelect");
    this.domainAonStatusFilter.title = "Estado del dominio";
    this.domainAonStatusFilter.readonly = false;
    let domainAonStatusFilterOptions = [{value: "", name: "Todos"}];
    this.DOMAIN_STATUS_OPTIONS.forEach(status => {
      domainAonStatusFilterOptions.push({value: status, name: this.getDomainStatusDescription(status)})
    });
    this.domainAonStatusFilter.options = JSON.stringify(domainAonStatusFilterOptions);
    this.domainAonStatusFilter.value = "";
    
    let domainAonStatusFilterLabel =  document.createElement('label');
    domainAonStatusFilterLabel.htmlFor = this.domainAonStatusFilter.id;
    domainAonStatusFilterLabel.innerText = "Estado dominio: ";
    domainAonStatusFilterLabel.title = "AonStatus del dominio";

    [this.noCustomerDomainsOnlyCheck, this.domainAonStatusFilter].forEach(filterEl => {
      filterEl.addEventListener('change', (event) => this.searchFunction());
    });

    
    // domainAonStatusFilterContainer.appendChild(domainAonStatusFilterLabel);
    domainAonStatusFilterContainer.appendChild(this.domainAonStatusFilter);
    
    searchFiltersContainer.appendChild(domainAonStatusFilterContainer);
    searchFiltersContainer.appendChild(noCustomerDomainsOnlyCheckContainer);
    
    this.searchBar.appendChild(searchInputAndModeContainer);
    this.searchBar.appendChild(searchFiltersContainer);
    
  }
  
  buildDomainList() {
    this.domainList = document.createElement('div');
    this.domainList.style.height = "80%";
    this.domainList.style.width = "45%";
    this.domainList.style.display = "flex";
    this.domainList.style.flexDirection = "column";
    this.domainList.style.gap = "5px";
    // this.domainList.style.overflowY = "scroll";
    this.domainList.classList.add("domainCustomerScroll");
  }
  
  buildCustomerList() {
    this.customerList = document.createElement('div');
    this.customerList.style.height = "80%";
    this.customerList.style.width = "45%";
    this.customerList.style.display = "flex";
    this.customerList.style.flexDirection = "column";
    this.customerList.style.gap = "5px";
    // this.customerList.style.overflowY = "scroll";
    this.customerList.classList.add("domainCustomerScroll");
  }
  
  buildConfirmButton() {
    this.confirmBar = document.createElement('div');
    this.confirmBar.style.width = "15%";
    this.confirmBar.style.height = "100%";
    this.confirmBar.style.display = "flex";
    this.confirmBar.style.flexDirection = "row";
    this.confirmBar.style.justifyContent = "center";
    this.confirmBar.style.alignItems = "center";
    
    this.confirmButton = document.createElement('button');
    this.confirmButton.innerHTML = "VINCULAR";
    this.confirmButton.classList.add("aonDomainConfirmButton");
    this.confirmButton.style.width = "100%";
    this.confirmButton.style.height = "100%";
    this.confirmButton.style.borderRadius = "10px";
    this.confirmButton.style.fontSize = "1em";
    this.confirmButton.style.fontWeight = "bold";
    
    this.enableOrDisableButton();
    
    this.confirmButton.addEventListener('click', async (event) => {
      await updateDomains({
        domains: [this.selectedDomain],
        customer: this.selectedCustomer
      });
      this.removeAllDomains();
      await this.refreshLists();
    });

    this.confirmBar.appendChild(this.confirmButton);
  }
  
  // ---------------------------------------
  
  // --- FILTROS ---
  
  filterCustomers(filtered) {
    let customerElements = [...this.customerList.children];
    for (let i = 0; i < this.loadedCustomers.length; i++) {
      let customer = this.loadedCustomers[i];
      if (filtered.includes(customer)) {
        customerElements.filter(element => element.dataset.ind == i)[0].style.display = "flex";
      } else {
        customerElements.filter(element => element.dataset.ind == i)[0].style.display = "none";
      }
    }
  }
  
  filterDomains(filtered) {
    let domainElements = [...this.domainList.children];
    for (let i = 0; i < this.loadedDomains.length; i++) {
      let domain = this.loadedDomains[i];
      if (filtered.includes(domain)) {
        domainElements.filter(element => element.dataset.ind == i)[0].style.display = "flex";
      } else {
        domainElements.filter(element => element.dataset.ind == i)[0].style.display = "none";
      }
    }
  }

  // ---------------

  // --- CARGAR LOS LISTADOS ---
  
  loadCustomers() {
    this.customerList.innerHTML = "";
    if (this.loadedCustomers) {
      for (let i = 0; i < this.loadedCustomers.length; i++) {
        let customerElement = this.createCustomerElement(this.loadedCustomers[i]);
        customerElement.dataset.ind = i;
        this.customerList.appendChild(customerElement);
      }
    }
  }
  
  loadDomains() {
    if (this.loadedDomains) {
      this.domainList.innerHTML = "";
      for (let i = 0; i < this.loadedDomains.length; i++) {
        let domainElement = this.createDomainElement(this.loadedDomains[i]);
        domainElement.dataset.ind = i;
        this.domainList.appendChild(domainElement);
      }
    }
  }

  // ---------------------------

  // --- FUNCIONES DE BÚSQUEDA ---

  searchFunction() {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => this.searchFilterAction(), 700);
  }

  searchFilterAction() {
    let lowerCasedInputText = this.searchInput.value.toLowerCase();
    this.clearSelections();
    this.removeAllCustomers();
    this.search(lowerCasedInputText);
    this.manageEmptyCustomerList();
  }
  
  search(inputText) {
    let filteredDomains = this.loadedDomains.filter((domainCompany) => 
    (  (domainCompany.domain ? domainCompany.domain.name : "").toLowerCase().includes(inputText)
        || (domainCompany.company ? domainCompany.company.document : "").toLowerCase().includes(inputText)
        || (domainCompany.company ? domainCompany.company.name : "").toLowerCase().includes(inputText)
        || (domainCompany.domain ? domainCompany.domain.description : "").toLowerCase().includes(inputText)
        )
        && (this.noCustomerDomainsOnlyCheck.getValue() ? !(domainCompany.domain ? domainCompany.domain.aonCustomer : null): true)
        && (this.domainAonStatusFilter.value ? (domainCompany.domain ? this.domainAonStatusFilter.value === domainCompany.domain.aonStatus : false) : true)
        );
        this.filterDomains(filteredDomains);
  }
  
  async findRelatedCustomers() {
    if (this.selectedDomain) {
      let company = this.selectedDomain.company;
      let domain = this.selectedDomain.domain;
      
      let aonCustomer = domain ? domain.aonCustomer : null;
      let searchDocument = company ? (company.document ? company.document.trim() : null) : null;
      let searchAlias = domain ? (company.name ? domain.name.trim().substring(0, 32) : null) : null;
      
      let customers = [];
      
      if (aonCustomer) {
        let cust = await getCustomers({
          id: aonCustomer
        });
        customers.push(cust);
      } else {
        if (searchDocument) {
          let newCustomers = await getCustomers({
            perPage: 2000,
            value: searchDocument
          });
          this.addNonExisting(customers, newCustomers);
        }
        if (searchAlias) {
          let newCustomers = await getCustomers({
            perPage: 2000,
            value: searchAlias
          });
          this.addNonExisting(customers, newCustomers);
        }
      }
      
      this.loadedCustomers = customers;
      this.loadCustomers();
      this.manageEmptyCustomerList();
      
    }
  }

  findSameDocumentDomains() {
    if (this.selectedCustomer && this.selectedCustomer.document) {
      let filteredDomains = this.loadedDomains.filter((domainCompany) => 
        (
          (domainCompany.company ? domainCompany.company.document : "").toLowerCase().includes(this.selectedCustomer.document.toLowerCase())
          || !(domainCompany.company ? domainCompany.company.document : "").trim()
        )
        && (this.noCustomerDomainsOnlyCheck.checked ? !(domainCompany.domain ? domainCompany.domain.aonCustomer : null): true)
        && (this.domainAonStatusFilter.value ? (domainCompany.domain ? this.domainAonStatusFilter.value === domainCompany.domain.aonStatus : false) : true)
      );
      this.filterDomains(filteredDomains);
    }
  }

  // -----------------------------

  // --- ELEMENTOS DE LISTADOS ---

  createDomainElement(domainCustomer) {
    let domain = domainCustomer.domain;
    let company = domainCustomer.company;
    
    let domainOptionContainer = document.createElement("div");
    domainOptionContainer.style.display = "flex";
    domainOptionContainer.style.minHeight = "60px";
    domainOptionContainer.style.flexDirection = "row";
    domainOptionContainer.style.alignItems = "center";
    domainOptionContainer.style.backgroundColor = "AliceBlue";
    domainOptionContainer.style.borderRadius = "5px";
    domainOptionContainer.style.padding = "5px";
    domainOptionContainer.style.cursor = "pointer";
    domainOptionContainer.classList.add("domainOption");

    let domainOption = document.createElement("div");
    domainOption.style.width = "80%";
    domainOption.style.display = "flex";
    domainOption.style.flexDirection = "row";
    domainOption.style.flexWrap = "wrap";
    domainOption.style.justifyContent = "flex-start";
    domainOption.style.alignItems = "center";

    let linkedIcon = document.createElement("span");
    linkedIcon.classList.add(CSS.MATERIAL_ICONS);
    linkedIcon.innerHTML = MATERIAL_ICONS.LINK;
    linkedIcon.title = "Vinculado";
    linkedIcon.style.width = "10%";

    let domainOptionName = document.createElement("div");
    domainOptionName.style.fontWeight = "bold";
    domainOptionName.style.width = "90%";
    domainOptionName.innerText = `${domain.name}`;
    
    if (domain && domain.aonCustomer) {
      domainOption.appendChild(linkedIcon);
    }
    domainOption.appendChild(domainOptionName);


    let domainOptionDescription = document.createElement("div");
    domainOptionDescription.style.width = "100%";
    domainOptionDescription.innerText = `${domain ? domain.description : ""}`;
    domainOption.appendChild(domainOptionDescription);

    let companyOptionDocument = document.createElement("div");
    companyOptionDocument.style.width = "calc(100% - 120px)";
    companyOptionDocument.style.margin = "auto";
    companyOptionDocument.innerText = `${company ? company.document : ""}`;
    domainOption.appendChild(companyOptionDocument);
    
    let aonStatusDrop = document.createElement("select");
    aonStatusDrop.style.width = "120px";
    
    this.DOMAIN_STATUS_OPTIONS.forEach((status) => {
      let option = document.createElement("option");
      option.value = status;
      option.innerText = this.getDomainStatusDescription(status);
      if (domain.aonStatus === status) {
        option.selected = true;
      }
      aonStatusDrop.appendChild(option);
    });

    domainOption.appendChild(aonStatusDrop);

    aonStatusDrop.addEventListener("click", (event) => {
      event.preventDefault();
      event.stopPropagation();
    });

    aonStatusDrop.addEventListener("change", () => {
      this.loadedDomains.filter((item) => item.domain.name === domain.name)[0].domain.aonStatus = aonStatusDrop.value;
    });
    
    domainOptionContainer.appendChild(domainOption);

    domainOptionContainer.addEventListener("click", (event) => this.domainClickHandler(event, domainCustomer));

    return domainOptionContainer;
  }

  createCustomerElement(customer) {
    let isLinked = this.selectedDomain && this.selectedDomain.domain && this.selectedDomain.domain.aonCustomer == customer.id;

    let customerOptionContainer = document.createElement("div");
    customerOptionContainer.style.display = "flex";
    customerOptionContainer.style.flexDirection = "row";
    customerOptionContainer.style.justifyContent = "center";
    customerOptionContainer.style.alignItems = "center";
    customerOptionContainer.style.flexWrap = "wrap";
    customerOptionContainer.style.backgroundColor = "AliceBlue";
    customerOptionContainer.style.borderRadius = "5px";
    customerOptionContainer.style.cursor = "pointer";
    customerOptionContainer.style.padding = "5px";
    customerOptionContainer.classList.add("customerOption");

    let customerNameDiv = document.createElement("div");
    customerNameDiv.style.width = "100%";
    customerNameDiv.style.height = "20px";
    customerNameDiv.style.fontWeight = "bold";
    customerNameDiv.innerText = customer.name ? customer.name.trim() : "";
    customerNameDiv.style.overflow = "hidden";
    customerNameDiv.style.whiteSpace = "nowrap";
    customerNameDiv.style.textOverflow = "ellipsis";

    let customerDocumentDiv = document.createElement("div");
    customerDocumentDiv.style.width = "50%";
    customerDocumentDiv.style.margin = "auto";
    customerDocumentDiv.style.height = "20px";
    customerDocumentDiv.innerText = customer.document;

    let customerStatusDiv = document.createElement("div");
    customerStatusDiv.style.width = "50%";
    customerStatusDiv.style.margin = "auto";
    customerStatusDiv.style.height = "20px";
    customerStatusDiv.dataset.value = `${customer.status}`;
    customerStatusDiv.innerText = `Estado: ${this.getCustomerStatusDescription(customer.status)}`;

    let infoDiv = document.createElement("div");
    infoDiv.style.display = "flex";
    infoDiv.style.flexDirection = "row";
    infoDiv.style.justifyContent = "center";
    infoDiv.style.alignItems = "center";
    infoDiv.style.flexWrap = "wrap";
    infoDiv.style.backgroundColor = "transparent";
    
    let unlinkDiv = document.createElement("div");
    unlinkDiv.style.display = "flex";
    unlinkDiv.style.flexDirection = "row";
    unlinkDiv.style.justifyContent = "center";
    unlinkDiv.style.alignItems = "center";
    unlinkDiv.style.backgroundColor = "transparent";
    unlinkDiv.style.width = "15%";
    
    
    let unlinkButton = new AonIconButton();
    unlinkButton.icon = MATERIAL_ICONS.LINK_OFF;
    unlinkButton.title = "Desvincular";
    unlinkButton.addEventListener("click", () => this.unlinkClickHandler());
    
    infoDiv.appendChild(customerNameDiv);
    infoDiv.appendChild(customerDocumentDiv);
    infoDiv.appendChild(customerStatusDiv);
    
    unlinkDiv.appendChild(unlinkButton);
    
    customerOptionContainer.appendChild(infoDiv);
    if (isLinked) {
      customerOptionContainer.classList.add("linkedCustomer");
      infoDiv.style.width = "85%";
      customerOptionContainer.appendChild(unlinkDiv);
    } else {
      infoDiv.style.width = "100%";
    }
    if (!isLinked) {
      customerOptionContainer.addEventListener("click", (event) => this.customerClickHandler(event, customer));
    }

    return customerOptionContainer;
  }

  // -----------------------------

  // --- MANEJADORES DE EVENTOS ---

  domainClickHandler(event, domainCustomer) {
    let domainOptionContainer = event.currentTarget;
    if (domainOptionContainer.classList.contains("selectedDomain")) {
      for (const option of this.domainList.children) {
        option.classList.remove("selectedDomain");
      }
      this.removeAllCustomers();
      this.selectedDomain = null;
      this.manageEmptyCustomerList();
    } else {
      for (const option of this.domainList.children) {
        option.classList.remove("selectedDomain");
      }
      domainOptionContainer.classList.add("selectedDomain");
      this.selectedCustomer = null;
      this.selectedDomain = domainCustomer;
      this.findRelatedCustomers();
    }
    this.enableOrDisableButton();
    console.log(this.selectedDomain);
  }

  customerClickHandler(event, customer) {
    let customerOptionContainer = event.currentTarget;
    if (customerOptionContainer.classList.contains("selectedCustomer")) {
      for (const option of this.customerList.children) {
        option.classList.remove("selectedCustomer");
      }
      this.selectedCustomer = null;
    } else {
      for (const option of this.customerList.children) {
        option.classList.remove("selectedCustomer");
      }
      customerOptionContainer.classList.add("selectedCustomer");
      this.selectedCustomer = customer;
    }
    this.enableOrDisableButton();
  }

  unlinkClickHandler() {
    let dialog = this.getApplication().getDialog();
    dialog.clear();
    dialog.setTitle("Desvincular cliente");
    
    let confirmUnlinkContainer = document.createElement("div");
    confirmUnlinkContainer.style.display = "flex";
    confirmUnlinkContainer.style.flexWrap = "wrap";
    confirmUnlinkContainer.style.gap = "25px";
    confirmUnlinkContainer.style.justifyContent = "center";
    confirmUnlinkContainer.style.alignItems = "center";
    confirmUnlinkContainer.style.flexDirection = "row";
    
    let confirmUnlinkMessage = document.createElement("div");
    confirmUnlinkMessage.innerText = "¿Desea desvincular este cliente del dominio?";
    confirmUnlinkMessage.style.width = "100%";
    confirmUnlinkContainer.appendChild(confirmUnlinkMessage);

    let aonButtonAccept = new AonButton();
    aonButtonAccept.title = "Aceptar";
    confirmUnlinkContainer.appendChild(aonButtonAccept);
    aonButtonAccept.addEventListener("click", async (ev) => {
      await updateDomains({
        domains: [this.selectedDomain],
        customer: null
      });
      this.removeAllDomains();
      await this.refreshLists();
      dialog.close();
    });
    
    dialog.setContent(confirmUnlinkContainer);
    dialog.width = "350px";
    dialog.open();
  }

  // ------------------------------

  // --- LIMPIAR COSAS ---

  removeAllDomains() {
    this.loadedDomains = [];
    this.selectedDomain = null;
    this.domainList.innerHTML = "";
  }
  
  removeAllCustomers() {
    this.loadedCustomers = [];
    this.selectedCustomer = null;
    this.customerList.innerHTML = "";
  }
  
  clearSelections() {
    this.selectedCustomer = null;
    for (const option of this.customerList.children) {
      option.classList.remove("selectedCustomer");
    }
    this.selectedDomain = null;
    for (const option of this.domainList.children) {
      option.classList.remove("selectedDomain");
    }
    this.enableOrDisableButton();
    
  }

  // ---------------------

  // --- FUNCIONES DE ENUMERADOS ---

  getDomainStatusDescription(status) {
    switch (status) {
      case "NOT_BILLABLE":
        return "No facturable";
        case "BILLABLE":
          return "Facturable";
      default:
        return "";
    }
  }

  getCustomerStatusDescription(status) {
    switch (status) {
      case "ACTIVE":
        return "Activo";
        case "INACTIVE":
          return "Inactivo";
        case "BLOCKED":
          return "Bloqueado";
      default:
        return "";
    }
  }

  // -------------------------------

  // --- OTROS ---

  async refreshLists() {
    this.selectedCustomer = null;
    this.selectedDomain = null;
    this.removeAllCustomers();
    await this.updateDomains();
    this.loadDomains();
    this.enableOrDisableButton();
    this.searchFilterAction();
    this.manageEmptyCustomerList();
  }

  addNonExisting(customers, newCustomers) {
    newCustomers.forEach(result => {
      if (customers.filter(customer => customer.id == result.id).length == 0) {
        customers.push(result);
      }
    });
  }
  
  manageEmptyCustomerList() {
    if (!this.loadedCustomers || this.loadedCustomers.length == 0) {
      this.customerList.innerHTML = "";
      let message = "";
      let messageSpan = document.createElement("span");
      if (this.selectedDomain) {
        message = "No se encontraron clientes relacionados con este dominio";
      } else {
        message = "Por favor, seleccione un dominio";
      }
      messageSpan.innerText = message;
      messageSpan.style.textAlign = "center";
      this.customerList.appendChild(messageSpan);
    }
  }
  
  enableOrDisableButton() {
    this.confirmButton.disabled = !(this.selectedCustomer && this.selectedDomain);
  }
  
  // -------------

}

if(!window.customElements.get(TAG.AON_DOMAIN_CUSTOMER)){
	window.customElements.define(TAG.AON_DOMAIN_CUSTOMER, AonDomainCustomer);
}