import { AonElement } from './AonElement.js';

import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS } from '../environments/environments.js';
import { Address } from '../models/registry/Address.js';
import { getDomains, getCustomers, updateDomains } from '../services/domainsService.js';

import '../css/aon-domain-customer.css';

export class AonDomainCustomer extends AonElement {

  STANDARD_SEARCH_MODE = "STANDARD";
  DOCUMENT_SEARCH_MODE = "DOCUMENT";

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

  selectedDomains;
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

    await this.updateCustomers();
    // await this.updateDomains();
  }
  
  async updateDomains() {
    let domains = await getDomains({
      registryDocument: this.selectedCustomer ? this.selectedCustomer.document : null,
      customerId: this.selectedCustomer ? this.selectedCustomer.id : null});
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
    // this.mainContainer.style.border = "1px solid black";
    this.mainContainer.style.display = "flex";
    this.mainContainer.style.flexWrap = "wrap";
    this.mainContainer.style.justifyContent = "space-between";
    this.mainContainer.style.alignItems = "center";

    this.buildSearchBar();
    this.buildDomainList();
    this.buildCustomerList();
    this.buildConfirmButton();

    this.mainContainer.appendChild(this.searchBar);
    this.mainContainer.appendChild(this.customerList);
    this.mainContainer.appendChild(this.domainList);
    this.searchBar.appendChild(this.confirmBar);



    this.appendChild(this.mainContainer);

    this.loadDomains();
    this.loadCustomers();

    this.searchMode = this.DOCUMENT_SEARCH_MODE;

    switch (this.searchMode) {
      // case this.DOCUMENT_SEARCH_MODE:
      //   this.hideAllDomains();
      //   break;
      default:
        break;
    }
  }

  buildSearchBar() {
    this.searchBar = document.createElement('div');
    this.searchBar.style.width = "100%";
    this.searchBar.style.height = "70px";
    this.searchBar.style.display = "flex";
    this.searchBar.style.justifyContent = "flex-start";
    this.searchBar.style.alignItems = "flex-start";
    // this.searchBar.style.borderBottom = "1px solid black";
    this.searchBar.style.padding = "5px";
    this.searchBar.style.gap = "10px";

    let searchInputAndModeContainer = document.createElement('div');
    searchInputAndModeContainer.style.display = "flex";
    searchInputAndModeContainer.style.justifyContent = "center";
    searchInputAndModeContainer.style.alignItems = "center";
    searchInputAndModeContainer.style.flexDirection = "column";
    searchInputAndModeContainer.style.width = "50%";
    searchInputAndModeContainer.style.gap = "5px";

    this.searchInput = document.createElement('input');
    this.searchInput.style.width = "100%";
    this.searchInput.style.height = "40px";
    this.searchInput.style.border = "none";
    this.searchInput.style.borderRadius = "100px";
    this.searchInput.style.outline = "none";
    this.searchInput.style.padding = "5px";
    this.searchInput.style.fontSize = "14px";
    this.searchInput.style.fontWeight = "bold";
    this.searchInput.style.backgroundColor = "AliceBlue";
    this.searchInput.placeholder = "Búsqueda";

    this.searchInput.addEventListener('keyup', (event) => this.searchFunction());

    let searchModeContainer = document.createElement('div');
    searchModeContainer.style.width = "100%";
    searchModeContainer.style.display = "flex";
    searchModeContainer.style.flexDirection = "row";
    searchModeContainer.style.justifyContent = "flex-start";
    searchModeContainer.style.alignItems = "center";
    searchModeContainer.style.gap = "10px";
    searchModeContainer.style.display = "none";

    let searchMode1Container = document.createElement('div');
    searchMode1Container.style.display = "flex";
    let searchMode1Radio = document.createElement('input');
    searchMode1Radio.type = "radio";
    searchMode1Radio.value = this.STANDARD_SEARCH_MODE;
    searchMode1Radio.name = "searchMode";
    searchMode1Radio.id = this.id + "SearchMode1";
    let searchMode1RadioLabel = document.createElement('label');
    searchMode1RadioLabel.innerText = "Estándar";
    searchMode1RadioLabel.htmlFor = searchMode1Radio.id;
    searchMode1Container.appendChild(searchMode1Radio);
    searchMode1Container.appendChild(searchMode1RadioLabel);

    let searchMode2Container = document.createElement('div');
    searchMode2Container.style.display = "flex";
    let searchMode2Radio = document.createElement('input');
    searchMode2Radio.type = "radio";
    searchMode2Radio.value = this.DOCUMENT_SEARCH_MODE;
    searchMode2Radio.name = "searchMode";
    searchMode2Radio.id = this.id + "SearchMode2";
    searchMode2Radio.checked = true;
    let searchMode2RadioLabel = document.createElement('label');
    searchMode2RadioLabel.innerText = "NIF";
    searchMode2RadioLabel.htmlFor = searchMode2Radio.id;
    searchMode2Container.appendChild(searchMode2Radio);
    searchMode2Container.appendChild(searchMode2RadioLabel);

    [searchMode1Radio, searchMode2Radio].forEach(radio => {
      radio.addEventListener('change', (event) => {
        this.searchMode = radio.value;
        this.clearAllSelected();
        // if (radio.value === this.DOCUMENT_SEARCH_MODE) {
        //   this.hideAllDomains();
        // }
        this.searchFunction();
      });
    });

    let searchModeLabel =  document.createElement('label');
    searchModeLabel.innerText = "Modo de búsqueda:";
    searchModeLabel.htmlFor = searchMode1Radio.id;

    searchModeContainer.appendChild(searchModeLabel);
    searchModeContainer.appendChild(searchMode1Container);
    searchModeContainer.appendChild(searchMode2Container);

    searchInputAndModeContainer.appendChild(this.searchInput);
    searchInputAndModeContainer.appendChild(searchModeContainer);

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

    this.noCustomerDomainsOnlyCheck = document.createElement('input');
    this.noCustomerDomainsOnlyCheck.type = "checkbox";
    this.noCustomerDomainsOnlyCheck.style.width = "20px";
    this.noCustomerDomainsOnlyCheck.style.height = "20px";
    this.noCustomerDomainsOnlyCheck.style.marginLeft = "0";
    this.noCustomerDomainsOnlyCheck.id = this.id + "CustomersSearchCheck";
    this.noCustomerDomainsOnlyCheck.checked = false;

    let noCustomerDomainsOnlyCheckLabel = document.createElement('label');
    noCustomerDomainsOnlyCheckLabel.innerText = "Sin cliente";
    noCustomerDomainsOnlyCheckLabel.title  = "Solo dominios sin cliente";
    noCustomerDomainsOnlyCheckLabel.htmlFor = this.noCustomerDomainsOnlyCheck.id;
    noCustomerDomainsOnlyCheckContainer.appendChild(this.noCustomerDomainsOnlyCheck);
    noCustomerDomainsOnlyCheckContainer.appendChild(noCustomerDomainsOnlyCheckLabel);

    searchFiltersContainer.appendChild(noCustomerDomainsOnlyCheckContainer);
    
    let domainAonStatusFilterContainer = document.createElement('div');
    domainAonStatusFilterContainer.style.width = "100%";
    domainAonStatusFilterContainer.style.height = "50%";
    domainAonStatusFilterContainer.style.display = "flex";
    domainAonStatusFilterContainer.style.justifyContent = "flex-start";
    domainAonStatusFilterContainer.style.alignItems = "center";
    domainAonStatusFilterContainer.style.gap = "5px";
    
    this.domainAonStatusFilter = document.createElement('select');
    this.domainAonStatusFilter.id = this.id + "DomainAonStatusFilter";
    let noFilterOption = document.createElement("option");
    noFilterOption.value = "";
    noFilterOption.innerText = "Sin filtro";
    this.domainAonStatusFilter.appendChild(noFilterOption);
    this.DOMAIN_STATUS_OPTIONS.forEach((status) => {
      let option = document.createElement("option");
      option.value = status;
      option.innerText = this.getDomainStatusDescription(status);
      this.domainAonStatusFilter.appendChild(option);
    });

    [this.noCustomerDomainsOnlyCheck, this.domainAonStatusFilter].forEach(filterEl => {
      filterEl.addEventListener('change', (event) => this.domainUpFilters());
    })

    let domainAonStatusFilterLabel =  document.createElement('label');
    domainAonStatusFilterLabel.htmlFor = this.domainAonStatusFilter.id;
    domainAonStatusFilterLabel.innerText = "Estado dominio: ";
    domainAonStatusFilterLabel.title = "AonStatus del dominio";

    domainAonStatusFilterContainer.appendChild(domainAonStatusFilterLabel);
    domainAonStatusFilterContainer.appendChild(this.domainAonStatusFilter);
    
    searchFiltersContainer.appendChild(domainAonStatusFilterContainer);
    
    this.searchBar.appendChild(searchInputAndModeContainer);
    this.searchBar.appendChild(searchFiltersContainer);

  }

  searchFunction() {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => this.searchFilterAction(), 700);
  }

  searchFilterAction() {
    let lowerCasedInputText = this.searchInput.value.toLowerCase();
    switch (this.searchMode) {
     case this.STANDARD_SEARCH_MODE:
        this.searchMode1(lowerCasedInputText);
        break;
      case this.DOCUMENT_SEARCH_MODE:
        this.searchMode2(lowerCasedInputText);
        break;
      default:
        break;
    }
  }

  searchMode1(inputText) {
    if (!this.selectedCustomer) {
      let filteredCustomers = this.loadedCustomers.filter((customer) =>
         (customer.name ? customer.name : "").toLowerCase().includes(inputText)
      || (customer.document ? customer.document : "").toLowerCase().includes(inputText)
      || (customer.alias ? customer.alias : "").toLowerCase().includes(inputText)
      );
      this.filterCustomers(filteredCustomers);
    }
    let filteredDomains = this.loadedDomains.filter((domainCompany) => 
        (  (domainCompany.domain ? domainCompany.domain.name : "").toLowerCase().includes(inputText)
        || (domainCompany.company ? domainCompany.company.document : "").toLowerCase().includes(inputText)
        || (domainCompany.company ? domainCompany.company.name : "").toLowerCase().includes(inputText)
        || (domainCompany.domain ? domainCompany.domain.description : "").toLowerCase().includes(inputText)
        )
        && (this.noCustomerDomainsOnlyCheck.checked ? !(domainCompany.domain ? domainCompany.domain.aonCustomer : null): true)
        && (this.domainAonStatusFilter.value ? (domainCompany.domain ? this.domainAonStatusFilter.value === domainCompany.domain.aonStatus : false) : true)
      );
    this.filterDomains(filteredDomains);
  }

  searchMode2(inputText) {
    let filteredCustomers = this.loadedCustomers.filter((customer) =>
       (customer.name ? customer.name : "").toLowerCase().includes(inputText)
    || (customer.document ? customer.document : "").toLowerCase().includes(inputText)
    || (customer.alias ? customer.alias : "").toLowerCase().includes(inputText)
    );
    this.filterCustomers(filteredCustomers);
    // this.findSameDocumentDomains();
    this.findSameDocumentDomainsServer();
  }

  domainUpFilters() {
    if (this.loadedDomains && this.selectedCustomer && this.selectedCustomer.document) {
      let filteredDomains = this.loadedDomains.filter((domainCompany) => 
        (this.noCustomerDomainsOnlyCheck.checked ? !(domainCompany.domain ? domainCompany.domain.aonCustomer : null): true)
        && (this.domainAonStatusFilter.value ? (domainCompany.domain ? this.domainAonStatusFilter.value === domainCompany.domain.aonStatus : false) : true)
      );
      this.filterDomains(filteredDomains);
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

  findSameDocumentDomainsServer() {
    if (this.selectedCustomer && this.selectedCustomer.document) {
      this.updateDomains().then(() => this.loadDomains());
    }
  }

  hideAllDomains() {
    if (this.loadedDomains) {
      this.filterDomains([]);
    }
  }

  removeAllDomains() {
    this.loadedDomains = [];
    this.selectedDomains = [];
    this.domainList.innerHTML = "";
  }

  clearAllSelected() {
    this.selectedCustomer = null;
    for (const option of this.customerList.children) {
      option.classList.remove("selectedCustomer");
    }
    this.selectedDomains = [];
    for (const option of this.domainList.children) {
      option.querySelector("input").checked = false;
    }
    this.enableOrDisableButton();
  
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
    this.confirmButton.innerHTML = "CONFIRMAR";
    this.confirmButton.style.width = "100%";
    this.confirmButton.style.height = "100%";
    this.confirmButton.style.borderRadius = "10px";
    this.confirmButton.style.color = "white";
    this.confirmButton.style.fontSize = "1em";
    this.confirmButton.style.fontWeight = "bold";

    this.enableOrDisableButton();

    this.confirmButton.addEventListener('click', async (event) => {
      await updateDomains({
        domains: this.selectedDomains,
        customer: this.selectedCustomer
      });
      this.removeAllDomains();
      await this.refreshLists();
    });

    this.confirmBar.appendChild(this.confirmButton);
  }

  async refreshLists() {
    this.selectedCustomer = null;
    this.selectedDomains = [];
    console.log("eguneratzen...");
    await this.updateCustomers();
    await this.updateDomains();
    this.loadCustomers();
    this.loadDomains();
    console.log(this.loadedCustomers);
    console.log(this.loadedDomains);
    this.enableOrDisableButton();
    console.log("eguneratuta!");
  }

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

  loadCustomers() {
    if (this.loadedCustomers) {
      this.customerList.innerHTML = "";
      for (let i = 0; i < this.loadedCustomers.length; i++) {
        let customer = this.loadedCustomers[i]; 

        let customerOptionContainer = document.createElement("div");
        customerOptionContainer.style.display = "flex";
        customerOptionContainer.style.flexDirection = "row";
        customerOptionContainer.style.justifyContent = "center";
        customerOptionContainer.style.flexWrap = "wrap";
        customerOptionContainer.style.backgroundColor = "AliceBlue";
        customerOptionContainer.style.borderRadius = "5px";
        customerOptionContainer.style.cursor = "pointer";
        customerOptionContainer.style.padding = "5px";
        customerOptionContainer.classList.add("customerOption");
        customerOptionContainer.dataset.ind = i;

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

        customerOptionContainer.appendChild(customerNameDiv);
        customerOptionContainer.appendChild(customerDocumentDiv);
        customerOptionContainer.appendChild(customerStatusDiv);

        customerOptionContainer.addEventListener("click", () => {
          if (customerOptionContainer.classList.contains("selectedCustomer")) {
            for (const option of this.customerList.children) {
              option.classList.remove("selectedCustomer");
            }
            this.selectedCustomer = null;
            switch (this.searchMode) {
              case this.STANDARD_SEARCH_MODE:
                this.searchFunction();   
                break;
              case this.DOCUMENT_SEARCH_MODE:
                this.removeAllDomains();
                break;
              default:
                break;
            }
          } else {
            for (const option of this.customerList.children) {
              option.classList.remove("selectedCustomer");
            }
            customerOptionContainer.classList.add("selectedCustomer");
            this.selectedCustomer = customer;
            switch (this.searchMode) {
              case this.DOCUMENT_SEARCH_MODE:
                this.findSameDocumentDomainsServer();
                break;
              default:
                break;
            }
          }
          this.enableOrDisableButton();
        });

        this.customerList.appendChild(customerOptionContainer);
      }
    }
  }

  enableOrDisableButton() {
    if (this.selectedCustomer && this.selectedDomains && this.selectedDomains.length > 0) {
      this.confirmButton.disabled = false;
      this.confirmButton.style.backgroundColor = "darkBlue";
    } else {
      this.confirmButton.disabled = true;
      this.confirmButton.style.backgroundColor = "lightsteelblue";
    }
  }

  loadDomains() {
    if (this.loadedDomains) {
      this.domainList.innerHTML = "";
      for (let i = 0; i < this.loadedDomains.length; i++) {
        let domainCustomer = this.loadedDomains[i];
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
        domainOptionContainer.dataset.ind = i;

        let domainCheck = document.createElement("input");
        domainCheck.type = "checkbox";
        domainCheck.style.width = "30px";
        domainCheck.style.height = "30px";
        domainCheck.name = "domainIndex";
        domainCheck.value = i;

        domainCheck.addEventListener("change", () => {
          this.selectedDomains = [...this.domainList.querySelectorAll("input[name='domainIndex']")]
          .filter((item) => item.checked)
          .map((item) => this.loadedDomains[item.value]);
          this.enableOrDisableButton();
        });


        let domainOption = document.createElement("div");
        domainOption.style.width = "80%";
        // domainOption.style.height = "40px";
        domainOption.style.display = "flex";
        domainOption.style.flexDirection = "row";
        domainOption.style.flexWrap = "wrap";
        domainOption.style.justifyContent = "center";


        let domainOptionName = document.createElement("div");
        domainOptionName.style.fontWeight = "bold";
        domainOptionName.style.width = "100%";
        domainOptionName.innerText = `${domain.name}`;
        domainOption.appendChild(domainOptionName);

        let domainOptionDescription = document.createElement("div");
        domainOptionDescription.style.width = "100%";
        domainOptionDescription.innerText = `${domain ? domain.description : ""}`;
        domainOption.appendChild(domainOptionDescription);

        let companyOptionDocument = document.createElement("div");
        companyOptionDocument.style.width = "50%";
        companyOptionDocument.style.margin = "auto";
        companyOptionDocument.innerText = `${company ? company.document : ""}`;
        domainOption.appendChild(companyOptionDocument);
        
        let aonStatusDrop = document.createElement("select");
        aonStatusDrop.style.width = "50%";
        
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

        aonStatusDrop.addEventListener("change", () => {
          this.loadedDomains.filter((item) => item.domain.name === domain.name)[0].domain.aonStatus = aonStatusDrop.value;
        });
        
        domainOptionContainer.appendChild(domainCheck);
        domainOptionContainer.appendChild(domainOption);

        this.domainList.appendChild(domainOptionContainer);
      }
    }
  }

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

}

if(!window.customElements.get(TAG.AON_DOMAIN_CUSTOMER)){
	window.customElements.define(TAG.AON_DOMAIN_CUSTOMER, AonDomainCustomer);
}