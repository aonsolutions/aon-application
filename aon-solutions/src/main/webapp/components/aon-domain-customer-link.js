import { AonElement } from './AonElement.js';

import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS } from '../environments/environments.js';
import { getDomains, getCustomers, updateDomains, getBooking } from '../services/domainsService.js';

import '../css/aon-domain-customer.css';
import { AonIconButton } from './aon-icon-button.js';
import { AonButton } from './aon-button.js';
import { AonSelect } from './aon-select.js';
import { AonCheckbox } from './aon-checkbox.js';
import { AonDateUtils } from '../modules/utils/AonDateUtils.js';

export class AonDomainCustomer extends AonElement {

  mainContainer;

  domainList;
  customerList;

  customerListScrollPromise;
  customerListScrollListener;

  searchBar;
  searchInput;
  customerSearchInput;
  domainsSearchCheck;
  customersSearchCheck;
  customerSearchEnded;
  customerSearchContainer;

  searchMode;

  noCustomerDomainsOnlyCheck;
  linkStatusFilter;
  domainAonStatusFilter;
  domainStatusFilter;
  customerStatusFilter;

  customerPage;

  loadedDomains;
  loadedCustomers;

  selectedDomain;

  searchTimeout;
  customerSearchTimeout;

  DOMAIN_STATUS_OPTIONS = ["NOT_BILLABLE", "BILLABLE"];
  CUSTOMER_STATUS_OPTIONS = ["ACTIVE", "INACTIVE", "BLOCKED"];

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
    this.customerListScrollListener = () => {};

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

    this.mainContainer.appendChild(this.searchBar);
    this.mainContainer.appendChild(this.domainList);
    this.mainContainer.appendChild(this.customerList);

    this.appendChild(this.mainContainer);
    this.loadDomains();
    this.manageEmptyCustomerList();
  }

  // --- CONSTRUIR ELEMENTOS PRINCIPALES ---

  buildDomainSearch() {

    let domainSearchContainer = document.createElement("div");
    domainSearchContainer.style.display = "flex";
    domainSearchContainer.style.flexDirection = "row";
    domainSearchContainer.style.flexWrap = "wrap";
    domainSearchContainer.style.width = "45%";
    domainSearchContainer.style.justifyContent = "flex-start";
    domainSearchContainer.style.alignItems = "flex-start";

    
    let searchBarContainer = document.createElement('div');
    searchBarContainer.style.display = "flex";
    searchBarContainer.style.justifyContent = "center";
    searchBarContainer.style.alignItems = "center";
    searchBarContainer.style.flexDirection = "row";
    searchBarContainer.style.width = "100%";
    searchBarContainer.style.backgroundColor = "AliceBlue";
    searchBarContainer.style.borderRadius = "100px";
    searchBarContainer.style.paddingLeft = "10px"
    searchBarContainer.classList.add("searchBar")
    
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
    this.searchInput.placeholder = "Búsqueda de dominio";
    this.searchInput.id = this.id + "DomainSearchInput";
    
    this.searchInput.addEventListener('keyup', (event) => this.searchFunction());
    this.searchInput.addEventListener("focus", (event) => searchBarContainer.style.border = "2px solid var(--aonBlue)");
    this.searchInput.addEventListener("focusout", (event) => searchBarContainer.style.border = "none");
    
    let searchIcon = document.createElement('span');
    searchIcon.classList.add(CSS.MATERIAL_ICONS);
    searchIcon.innerHTML = MATERIAL_ICONS.SEARCH;
    searchIcon.style.width = "20px";
    searchIcon.style.userSelect = "none";
    
    searchBarContainer.appendChild(searchIcon);
    searchBarContainer.appendChild(this.searchInput);
    
    let searchFiltersContainer = document.createElement('div');
    searchFiltersContainer.style.width = "100%";
    searchFiltersContainer.style.height = "100%";
    searchFiltersContainer.style.display = "flex";
    searchFiltersContainer.style.justifyContent = "space-evenly";
    searchFiltersContainer.style.alignItems = "center";
    searchFiltersContainer.style.flexWrap = "wrap";
    searchFiltersContainer.style.marginTop = "5px";
    
    let noCustomerDomainsOnlyCheckContainer = document.createElement('div');
    noCustomerDomainsOnlyCheckContainer.style.width = "25%";
    noCustomerDomainsOnlyCheckContainer.style.height = "50%";
    noCustomerDomainsOnlyCheckContainer.style.display = "flex";
    noCustomerDomainsOnlyCheckContainer.style.justifyContent = "center";
    noCustomerDomainsOnlyCheckContainer.style.alignItems = "center";
    
    this.noCustomerDomainsOnlyCheck = new AonCheckbox();
    this.noCustomerDomainsOnlyCheck.style.marginLeft = "0";
    this.noCustomerDomainsOnlyCheck.id = this.id + "CustomersSearchCheck";
    this.noCustomerDomainsOnlyCheck.checked = false;
    this.noCustomerDomainsOnlyCheck.description = "No vinculados"; 
    this.noCustomerDomainsOnlyCheck.title  = "Solo dominios sin cliente vinculado"
    
    this.linkStatusFilter = new AonSelect();
    this.linkStatusFilter.id = this.id + "linkStatusFilter";
    this.linkStatusFilter.classList.add("domainLinkAonSelect");
    this.linkStatusFilter.title = "Vinculación";
    this.linkStatusFilter.readonly = false;
    let linkStatusFilterOptions =
    [ {value: "", name: "Todos"},
      {value: "linked", name: "Vinculados"},
      {value: "unlinked", name: "Sin vincular"}
    ];
    this.linkStatusFilter.options = JSON.stringify(linkStatusFilterOptions);
    this.linkStatusFilter.value = "";
    
    noCustomerDomainsOnlyCheckContainer.appendChild(this.noCustomerDomainsOnlyCheck);
    // noCustomerDomainsOnlyCheckContainer.appendChild(this.linkStatusFilter);
    
    let domainAonStatusFilterContainer = document.createElement('div');
    domainAonStatusFilterContainer.style.width = "25%";
    domainAonStatusFilterContainer.style.height = "50%";
    domainAonStatusFilterContainer.style.display = "flex";
    domainAonStatusFilterContainer.style.justifyContent = "flex-start";
    domainAonStatusFilterContainer.style.alignItems = "center";
    domainAonStatusFilterContainer.style.gap = "5px";

    this.domainAonStatusFilter = new AonSelect();
    this.domainAonStatusFilter.id = this.id + "DomainAonStatusFilter";
    this.domainAonStatusFilter.classList.add("domainLinkAonSelect");
    this.domainAonStatusFilter.title = "Estado";
    this.domainAonStatusFilter.readonly = false;
    let domainAonStatusFilterOptions = [{value: "", name: "Todos"}];
    this.DOMAIN_STATUS_OPTIONS.forEach(status => {
      domainAonStatusFilterOptions.push({value: status, name: this.getDomainStatusDescription(status)});
    });
    this.domainAonStatusFilter.options = JSON.stringify(domainAonStatusFilterOptions);
    this.domainAonStatusFilter.value = "";

    domainAonStatusFilterContainer.appendChild(this.domainAonStatusFilter);
    
    searchFiltersContainer.appendChild(domainAonStatusFilterContainer);

    let domainStatusFilterContainer = document.createElement('div');
    domainStatusFilterContainer.style.width = "25%";
    domainStatusFilterContainer.style.height = "50%";
    domainStatusFilterContainer.style.display = "flex";
    domainStatusFilterContainer.style.justifyContent = "flex-start";
    domainStatusFilterContainer.style.alignItems = "center";
    domainStatusFilterContainer.style.gap = "5px";

    this.domainStatusFilter = new AonSelect();
    this.domainStatusFilter.id = this.id + "DomainStatusFilter";
    this.domainStatusFilter.classList.add("domainLinkAonSelect");
    this.domainStatusFilter.title = "Acceso";
    this.domainStatusFilter.readonly = false;
    let domainStatusFilterOptions =
    [ {value: "", name: "Todos"},
      {value: "active", name: "Activo"},
      {value: "inactive", name: "Inactivo"},
      {value: "expired", name: "Expirado"}
    ];
    this.domainStatusFilter.options = JSON.stringify(domainStatusFilterOptions);
    this.domainStatusFilter.value = "";

    [this.noCustomerDomainsOnlyCheck, this.domainAonStatusFilter, this.domainStatusFilter, this.linkStatusFilter].forEach(filterEl => {
      filterEl.addEventListener('change', (event) => this.searchFunction());
    });

    domainStatusFilterContainer.appendChild(this.domainStatusFilter);
    
    searchFiltersContainer.appendChild(domainStatusFilterContainer);
    searchFiltersContainer.appendChild(noCustomerDomainsOnlyCheckContainer);

    domainSearchContainer.appendChild(searchBarContainer);
    domainSearchContainer.appendChild(searchFiltersContainer);
    return domainSearchContainer;
  }

  buildCustomerSearch() {

    let domainSearchContainer = document.createElement("div");
    domainSearchContainer.style.display = "none";
    domainSearchContainer.style.flexDirection = "row";
    domainSearchContainer.style.flexWrap = "wrap";
    domainSearchContainer.style.width = "45%";
    domainSearchContainer.style.justifyContent = "flex-start";
    domainSearchContainer.style.alignItems = "flex-start";

    
    let searchBarContainer = document.createElement('div');
    searchBarContainer.style.display = "flex";
    searchBarContainer.style.justifyContent = "center";
    searchBarContainer.style.alignItems = "center";
    searchBarContainer.style.flexDirection = "row";
    searchBarContainer.style.width = "100%";
    searchBarContainer.style.backgroundColor = "AliceBlue";
    searchBarContainer.style.borderRadius = "100px";
    searchBarContainer.style.paddingLeft = "10px"
    searchBarContainer.classList.add("searchBar")
    
    this.customerSearchInput = document.createElement('input');
    this.customerSearchInput.style.width = "calc(100% - 30px)";
    this.customerSearchInput.style.height = "40px";
    this.customerSearchInput.style.border = "none";
    this.customerSearchInput.style.borderRadius = "100px";
    this.customerSearchInput.style.outline = "none";
    this.customerSearchInput.style.padding = "5px";
    this.customerSearchInput.style.fontSize = "14px";
    this.customerSearchInput.style.fontWeight = "bold";
    this.customerSearchInput.style.backgroundColor = "transparent";
    this.customerSearchInput.placeholder = "Búsqueda de cliente";
    this.customerSearchInput.id = this.id + "CustomerSearchInput";
    
    this.customerSearchInput.addEventListener('keyup', (event) => this.customerSearchFunction());
    this.customerSearchInput.addEventListener("focus", (event) => searchBarContainer.style.border = "2px solid var(--aonBlue)");
    this.customerSearchInput.addEventListener("focusout", (event) => searchBarContainer.style.border = "none");
    
    let searchIcon = document.createElement('span');
    searchIcon.classList.add(CSS.MATERIAL_ICONS);
    searchIcon.innerHTML = MATERIAL_ICONS.SEARCH;
    searchIcon.style.width = "20px";
    searchIcon.style.userSelect = "none";
    
    searchBarContainer.appendChild(searchIcon);
    searchBarContainer.appendChild(this.customerSearchInput);
    
    let searchFiltersContainer = document.createElement('div');
    searchFiltersContainer.style.width = "100%";
    searchFiltersContainer.style.height = "100%";
    searchFiltersContainer.style.display = "flex";
    searchFiltersContainer.style.justifyContent = "flex-start";
    searchFiltersContainer.style.alignItems = "center";
    searchFiltersContainer.style.flexWrap = "wrap";
    searchFiltersContainer.style.marginTop = "5px";
    
    let domainAonStatusFilterContainer = document.createElement('div');
    domainAonStatusFilterContainer.style.width = "45%";
    domainAonStatusFilterContainer.style.height = "50%";
    domainAonStatusFilterContainer.style.display = "flex";
    domainAonStatusFilterContainer.style.justifyContent = "flex-start";
    domainAonStatusFilterContainer.style.alignItems = "center";
    domainAonStatusFilterContainer.style.gap = "5px";

    this.customerStatusFilter = new AonSelect();
    this.customerStatusFilter.id = this.id + "CustomerStatusFilter";
    this.customerStatusFilter.classList.add("domainLinkAonSelect");
    this.customerStatusFilter.title = "Estado";
    this.customerStatusFilter.readonly = false;
    let domainAonStatusFilterOptions = [{value: "", name: "Todos"}];
    this.CUSTOMER_STATUS_OPTIONS.forEach(status => {
      domainAonStatusFilterOptions.push({value: status, name: this.getCustomerStatusDescription(status)});
    });
    this.customerStatusFilter.options = JSON.stringify(domainAonStatusFilterOptions);
    this.customerStatusFilter.value = "";

    this.customerStatusFilter.addEventListener("change", (event) => this.customerSearchFunction());

    domainAonStatusFilterContainer.appendChild(this.customerStatusFilter);
    
    searchFiltersContainer.appendChild(domainAonStatusFilterContainer);

    domainSearchContainer.appendChild(searchBarContainer);
    domainSearchContainer.appendChild(searchFiltersContainer);
    return domainSearchContainer;
  }
  
  buildSearchBar() {
    this.searchBar = document.createElement('div');
    this.searchBar.style.width = "100%";
    this.searchBar.style.display = "flex";
    this.searchBar.style.justifyContent = "space-between";
    this.searchBar.style.alignItems = "flex-start";
    this.searchBar.style.padding = "5px";
    this.searchBar.style.gap = "10px";

    let domainSearchContainer = this.buildDomainSearch();
    this.customerSearchContainer = this.buildCustomerSearch();
    this.searchBar.appendChild(domainSearchContainer);
    this.searchBar.appendChild(this.customerSearchContainer);
    // this.searchBar.appendChild(searchBarContainer);
    // this.searchBar.appendChild(searchFiltersContainer);
    
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
    this.customerList.addEventListener("scroll", (event) => {
      this.scrollFunction();
    })
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
  
  loadCustomers(linked) {
    this.customerList.innerHTML = "";
    if (this.loadedCustomers) {
      for (let i = 0; i < this.loadedCustomers.length; i++) {
        let customerElement = this.createCustomerElement(this.loadedCustomers[i]);
        customerElement.dataset.ind = i;
        if (linked && this.loadedCustomers.length === 1) {
          customerElement.classList.add("selectedCustomer");
        }
        this.customerList.appendChild(customerElement);
      }
    }
  }

  loadMoreCustomers(newCustomers) {
    if (newCustomers) {
      let loadedLastLength = this.loadedCustomers.length;
      this.loadedCustomers = [...this.loadedCustomers, ...newCustomers];
      for (let i = 0; i < newCustomers.length; i++) {
        let customer = newCustomers[i];
        let customerElement = this.createCustomerElement(customer);
        customerElement.dataset.ind = loadedLastLength + i;
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

  customerSearchFunction() {
    this.customerListScrollPromise = null;
    this.customerSearchEnded = true;
    if (this.customerSearchTimeout) {
      clearTimeout(this.customerSearchTimeout);
    }
    this.customerSearchTimeout = setTimeout(() => this.customerSearchFilterAction(), 700);
  }

  searchFilterAction() {
    let lowerCasedInputText = this.searchInput.value.toLowerCase();
    this.clearSelections();
    this.removeAllCustomers();
    this.search(lowerCasedInputText);
    this.manageEmptyCustomerList();
  }

  async customerSearchFilterAction() {
    let lowerCasedInputText = this.customerSearchInput.value.toLowerCase();
    this.removeAllCustomers();
    await this.customerSearch(lowerCasedInputText);
    // this.manageEmptyCustomerList();
  }
  
  search(inputText) {
    let filteredDomains = this.loadedDomains.filter((domainCompany) => 
        (  (domainCompany.domain && domainCompany.domain.name ? domainCompany.domain.name : "").toLowerCase().includes(inputText)
        || (domainCompany.company && domainCompany.company.document ? domainCompany.company.document : "").toLowerCase().includes(inputText)
        || (domainCompany.company && domainCompany.company.name ? domainCompany.company.name : "").toLowerCase().includes(inputText)
        || (domainCompany.domain && domainCompany.domain.description ? domainCompany.domain.description : "").toLowerCase().includes(inputText)
        )
        && (this.noCustomerDomainsOnlyCheck.getValue() ? !(domainCompany.domain ? domainCompany.domain.aonCustomer : null): true)
        && (this.domainAonStatusFilter.value ? (domainCompany.domain ? this.domainAonStatusFilter.value === domainCompany.domain.aonStatus : false) : true)
        && this.checkAccess(domainCompany)
        // && this.checkLinkStatus(domainCompany)
        );
        this.filterDomains(filteredDomains);
  }

  checkAccess(domainCompany) {
    let domain = domainCompany ? domainCompany.domain : null;
    if (domain) {
      let access = this.domainStatusFilter.value;
      switch (access) {
        case "":
          return true;
          break;
        case "active":
          return (domain.active ? true : false);
          break;
        case "inactive":
          return (!domain.active ? true : false);
          break;
        case "expired":
          return (domain.expirationDate ? true : false);
          break;
        default:
          break;
      }
    }
    return false;
  }

  checkLinkStatus(domainCompany) {
    let domain = domainCompany ? domainCompany.domain : null;
    if (domain) {
      let linkStatus = this.linkStatusFilter.value;
      switch (linkStatus) {
        case "":
          return true;
          break;
        case "linked":
          return (domain.aonCustomer ? true : false);
          break;
        case "unlinked":
          return (!domain.aonCustomer ? true : false);
          break;
        default:
          break;
      }
    }
    return false;
  }

  async customerSearch() {
    let inputText = this.customerSearchInput.value.toLowerCase();
    if (inputText) {
      this.customerPage  = 1;
      this.customerSearchEnded = false;
      await this.findPaginatedCustomers();
    } else {
      await this.findRelatedCustomers();
    }
    this.manageEmptyCustomerList();
  }
  
  findPaginatedCustomers() {
    return new Promise(async (res, rej) => {
      let entriesPerPage = 100;
      let customerStatus = this.customerStatusFilter ? this.customerStatusFilter.value : "";
      let parameters = {
        perPage: entriesPerPage,
        page: this.customerPage++,
        value: this.customerSearchInput.value.toLowerCase()
      };
      if (customerStatus) {
        parameters.status = customerStatus;
      }
      let newCustomers = await getCustomers(parameters);
      this.customerSearchEnded = !newCustomers || newCustomers.length < entriesPerPage;

      if (!this.loadedCustomers) {
        this.loadedCustomers = [];
      }
      this.loadMoreCustomers(newCustomers);
      this.manageEmptyCustomerList();
      res(true);
    });
  }

  scrollFunction() {
    if (!this.customerListScrollPromise && !this.customerSearchEnded) {
      let scrollTop = this.customerList.scrollTop;
      let scrollHeight = this.customerList.scrollHeight; // added
      let offsetHeight = this.customerList.offsetHeight;
      let contentHeight = scrollHeight - offsetHeight;
      if (contentHeight <= scrollTop + 200) {
        this.customerListScrollPromise = this.findPaginatedCustomers().then(() => {this.customerListScrollPromise = null});
      }
  }
  }
  
  async findRelatedCustomers() {
    if (this.selectedDomain) {
      let company = this.selectedDomain.company;
      let domain = this.selectedDomain.domain;
      
      let aonCustomer = domain ? domain.aonCustomer : null;
      let searchDocument = company ? (company.document ? company.document.trim() : null) : null;
      let searchAlias = domain ? (company.name ? domain.name.trim().substring(0, 32) : null) : null;
      let customerStatus = this.customerStatusFilter ? this.customerStatusFilter.value : "";
      
      let customers = [];
      let booking = null;
      
      if (aonCustomer) {
        let cust = await getCustomers({
          id: aonCustomer
        });
        customers.push(cust);
        if (cust && domain) {
          let dName = domain.name;
          let dId = domain.id;
          let linkedCustomerDomain = cust.domain;
          if (dName && dId) {
            booking = await getBooking({
              domainName: dName,
              domainId: dId,
            });
            console.log(booking);
          }
        }

      } else {
        if (searchDocument) {
          let parameters = {
            perPage: 2000,
            value: searchDocument
          };
          if (customerStatus) {
            parameters.status = customerStatus;
          }
          let newCustomers = await getCustomers(parameters);
          this.addNonExisting(customers, newCustomers);
        }
        if (searchAlias) {
          let parameters = {
            perPage: 2000,
            value: searchAlias
          };
          if (customerStatus) {
            parameters.status = customerStatus;
          }
          let newCustomers = await getCustomers(parameters);
          this.addNonExisting(customers, newCustomers);
        }
      }
      
      this.loadedCustomers = customers;
      this.loadCustomers(aonCustomer ? true : false);
      if (booking) {
        this.createBookingElement(booking);
      }
      this.manageEmptyCustomerList();
      
    }
  }

  createBookingElement(booking) {
    if (booking) {
      let bookingContainer = document.createElement("div");
      bookingContainer.style.display = "flex";
      bookingContainer.style.flexDirection = "column";
      bookingContainer.style.gap = "10px";
      bookingContainer.style.padding = "10px";
      bookingContainer.style.borderRadius = "5px";
      bookingContainer.style.backgroundColor = "AliceBlue";
      bookingContainer.style.width = "100%";
      // bookingContainer.style.height = "300px";
      bookingContainer.style.marginTop = "10px";

      let bookingTitleContainer = document.createElement("div");
      bookingTitleContainer.style.display = "block";
      bookingTitleContainer.style.width = "100%";
      bookingTitleContainer.style.textAlign = "center";
      bookingTitleContainer.style.fontWeight = "bold";
      bookingTitleContainer.style.textTransform = "uppercase";
      bookingTitleContainer.innerText = "Datos de contratación";
      bookingContainer.appendChild(bookingTitleContainer);
      
      let appsContainer = document.createElement("div");
      appsContainer.style.display = "flex";
      appsContainer.style.flexDirection = "row";
      appsContainer.style.justifyContent = "space-between";
      appsContainer.style.alignItems = "flex-start";
      
      let childAppsContainer = document.createElement("div");
      childAppsContainer.style.display = "flex";
      childAppsContainer.style.flexDirection = "column";
      childAppsContainer.style.justifyContent = "flex-start";
      childAppsContainer.style.alignItems = "center";
      childAppsContainer.style.width = "45%";
      let childAppsTitleContainer = document.createElement("div");
      childAppsTitleContainer.style.display = "block";
      childAppsTitleContainer.style.width = "100%";
      childAppsTitleContainer.style.textAlign = "center";
      childAppsTitleContainer.style.fontWeight = "bold";
      childAppsTitleContainer.innerText = "Aplicaciones";

      childAppsContainer.appendChild(childAppsTitleContainer);
      let childApps = booking.apps;
      if (childApps) {
        childApps.forEach(app => {
          let childApp = document.createElement("div");
          childApp.style.display = "block";
          childApp.style.width = "100%";
          childApp.innerText = app;
          // childApp.style.textIndent = "10px";
          childAppsContainer.appendChild(childApp);
        });
      }

      appsContainer.appendChild(childAppsContainer);

      let parentAppsContainer = document.createElement("div");
      parentAppsContainer.style.display = "flex";
      parentAppsContainer.style.flexDirection = "column";
      parentAppsContainer.style.justifyContent = "flex-start";
      parentAppsContainer.style.alignItems = "center";
      parentAppsContainer.style.width = "45%";
      let parentAppsTitleContainer = document.createElement("div");
      parentAppsTitleContainer.style.display = "block";
      parentAppsTitleContainer.style.width = "100%";
      parentAppsTitleContainer.style.textAlign = "center";
      parentAppsTitleContainer.style.fontWeight = "bold";
      parentAppsTitleContainer.innerText = "Aplicaciones del padre";
      
      parentAppsContainer.appendChild(parentAppsTitleContainer);
      let parentApps = booking.parentApps;
      if (parentApps) {
        parentApps.forEach(app => {
          let parentApp = document.createElement("div");
          parentApp.style.display = "block";
          parentApp.style.width = "100%";
          parentApp.innerText = app;
          // parentApp.style.textIndent = "5px";
          parentAppsContainer.appendChild(parentApp);
        });
      }

      appsContainer.appendChild(parentAppsContainer);
      
      let numberOfUsersContainer = document.createElement("div");
      numberOfUsersContainer.style.display = "block";
      numberOfUsersContainer.style.width = "100%";
      let numberOfUsersTitleSpan = document.createElement("span");
      numberOfUsersTitleSpan.style.fontWeight = "bold";
      numberOfUsersTitleSpan.innerText = "Número de usuarios: ";
      numberOfUsersContainer.appendChild(numberOfUsersTitleSpan);
      let numberOfUsersSpan = document.createElement("span");
      numberOfUsersSpan.innerText = booking.numberOfUsers;
      numberOfUsersContainer.appendChild(numberOfUsersSpan);
      
      
      bookingContainer.appendChild(appsContainer);
      if (booking.numberOfUsers) {
        bookingContainer.appendChild(numberOfUsersContainer);
      }

      this.customerList.appendChild(bookingContainer);
    }
  }

  // -----------------------------

  // --- ELEMENTOS DE LISTADOS ---

  createDomainElement(domainCustomer) {
    let domain = domainCustomer.domain;
    let company = domainCustomer.company;
    
    let domainOptionContainer = document.createElement("div");
    domainOptionContainer.style.display = "flex";
    domainOptionContainer.style.height = "fit-content";
    domainOptionContainer.style.minHeight = "105px";
    domainOptionContainer.style.overflow = "hidden";
    domainOptionContainer.style.flexDirection = "row";
    domainOptionContainer.style.alignItems = "flex-start";
    domainOptionContainer.style.backgroundColor = "AliceBlue";
    domainOptionContainer.style.borderRadius = "5px";
    domainOptionContainer.style.padding = "5px";
    domainOptionContainer.style.cursor = "pointer";
    domainOptionContainer.classList.add("domainOption");

    let domainOption = document.createElement("div");
    domainOption.style.width = "100%";
    domainOption.style.height = "100%";
    domainOption.style.display = "flex";
    domainOption.style.flexDirection = "column";
    // domainOption.style.flexWrap = "wrap";
    domainOption.style.justifyContent = "space-between";
    domainOption.style.alignItems = "flex-start";

    let domainOptionFirstLine = document.createElement("div");
    domainOptionFirstLine.style.width = "100%";
    domainOptionFirstLine.style.display = "flex";
    domainOptionFirstLine.style.flexDirection = "row";
    domainOptionFirstLine.style.alignItems = "center";
    domainOptionFirstLine.style.justifyContent = "flex-start";


    let linkedIcon = document.createElement("span");
    linkedIcon.classList.add(CSS.MATERIAL_ICONS);
    linkedIcon.innerHTML = MATERIAL_ICONS.LINK;
    linkedIcon.title = "Vinculado";
    linkedIcon.classList.add("linkedIcon");
    linkedIcon.style.width = "25px";
    linkedIcon.style.display = "none";

    let domainOptionName = document.createElement("div");
    domainOptionName.style.display = "inline";
    domainOptionName.style.width = "calc(100% - 25px)";
    domainOptionName.title = `${domain.active ? "Activo" : "Inactivo"}`;
    
    let domainOptionNameSpan = document.createElement("span");
    domainOptionNameSpan.style.fontWeight = "bold";
    domainOptionNameSpan.innerText = `${domain.name}`;
    if (!domain.active) {
      domainOptionNameSpan.classList.add(`inactiveDomainName`);
    }

    let domainOptionNameSchemaSpan = document.createElement("span");
    domainOptionNameSchemaSpan.style.fontWeight = "normal";
    domainOptionNameSchemaSpan.innerText = ` (${domain && domain.id ? domain.id : ""}@${domainCustomer.schema ? domainCustomer.schema : ""})`;
    domainOptionName.appendChild(domainOptionNameSpan);
    domainOptionName.appendChild(domainOptionNameSchemaSpan);

    
    domainOptionFirstLine.appendChild(linkedIcon);
    if (domain && domain.aonCustomer) {
      linkedIcon.style.display = "inline";
    }
    domainOptionFirstLine.appendChild(domainOptionName);
    domainOption.appendChild(domainOptionFirstLine);


    let domainOptionDescription = document.createElement("div");
    domainOptionDescription.style.width = "100%";
    domainOptionDescription.innerText = `${domain ? domain.description : ""}`;
    domainOption.appendChild(domainOptionDescription);

    let domainOptionDatesLine = document.createElement("div");
    domainOptionDatesLine.style.display = "flex";
    domainOptionDatesLine.style.flexDirection = "column";
    domainOptionDatesLine.style.alignItems = "right";
    domainOptionDatesLine.style.justifyContent = "flex-start";
    domainOptionDatesLine.style.marginRight = "10px";
    domainOptionDatesLine.style.width = "150px";
    
    
    if (domain && domain.lastAccessDate) {
      let companyOptionLastAccess = document.createElement("div");
      companyOptionLastAccess.innerText = `F.acceso.: ${AonDateUtils.formatDate(domain.lastAccessDate)}`;
      domainOptionDatesLine.appendChild(companyOptionLastAccess);
    }

    let domainOptionSelectLine = document.createElement("div");
    domainOptionSelectLine.style.width = "100%";
    domainOptionSelectLine.style.display = "flex";
    domainOptionSelectLine.style.flexDirection = "row";
    domainOptionSelectLine.style.alignItems = "flex-start";
    domainOptionSelectLine.style.justifyContent = "space-between";
    domainOptionSelectLine.style.gap = "10px";

    let companyOptionDocument = document.createElement("div");
    companyOptionDocument.style.width = "78px";
    // companyOptionDocument.style.margin = "auto";
    companyOptionDocument.innerText = `${company && company.document ? company.document : ""}`;
    domainOptionSelectLine.appendChild(companyOptionDocument);

    if (domain && domain.expirationDate) {
      if (domain.active) {
        domainOptionNameSpan.classList.add(`expireDomainName`);
      }
      let companyOptionExpire = document.createElement("div");
      companyOptionExpire.innerText = `Expira: ${AonDateUtils.formatDate(domain.expirationDate)}`;
      domainOptionSelectLine.appendChild(companyOptionExpire);
    }
    
    let aonStatusDrop = document.createElement("select");
    aonStatusDrop.style.width = "111px";
    
    this.DOMAIN_STATUS_OPTIONS.forEach((status) => {
      let option = document.createElement("option");
      option.value = status;
      option.innerText = this.getDomainStatusDescription(status);
      if (domain.aonStatus === status) {
        option.selected = true;
      }
      aonStatusDrop.appendChild(option);
    });

    domainOptionDatesLine.appendChild(aonStatusDrop);
    domainOptionSelectLine.appendChild(domainOptionDatesLine);
    domainOption.appendChild(domainOptionSelectLine);



    aonStatusDrop.addEventListener("click", (event) => {
      event.preventDefault();
      event.stopPropagation();
    });

    aonStatusDrop.addEventListener("change", async () => {
      this.loadedDomains.filter((item) => item.domain.name === domain.name)[0].domain.aonStatus = aonStatusDrop.value;

      //ACTUALIZAR EL DOMAIN
      await updateDomains({
        domains: [domainCustomer],
        customer: domain ? domain.aonCustomer : null,
      });
      console.log("estado actualizado");

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

    let customerAliasDiv = document.createElement("div");
    customerAliasDiv.style.width = "100%";
    customerAliasDiv.style.height = "20px";
    customerAliasDiv.innerText = customer.alias ? customer.alias.trim() : "";
    customerAliasDiv.style.overflow = "hidden";
    customerAliasDiv.style.whiteSpace = "nowrap";
    customerAliasDiv.style.textOverflow = "ellipsis";

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
    unlinkButton.icon = isLinked ? MATERIAL_ICONS.LINK_OFF : MATERIAL_ICONS.LINK;
    unlinkButton.title = `${isLinked ? "Desv" : "V"}incular`;
    unlinkButton.addEventListener("click", () => this.linkClickHandler(isLinked ? null : customer));
    
    infoDiv.appendChild(customerNameDiv);
    infoDiv.appendChild(customerAliasDiv);
    infoDiv.appendChild(customerDocumentDiv);
    infoDiv.appendChild(customerStatusDiv);
    
    unlinkDiv.appendChild(unlinkButton);
    
    customerOptionContainer.appendChild(infoDiv);
    // if (isLinked) {
    customerOptionContainer.classList.add("linkedCustomer");
    infoDiv.style.width = "85%";
    customerOptionContainer.appendChild(unlinkDiv);
    // } else {
    //   infoDiv.style.width = "100%";
    // }
    // if (!isLinked) {
    //   customerOptionContainer.addEventListener("click", (event) => this.customerClickHandler(event, customer));
    // }

    return customerOptionContainer;
  }

  // -----------------------------

  // --- MANEJADORES DE EVENTOS ---

  domainClickHandler(event, domainCustomer) {
    let domainOptionContainer = event.currentTarget;
    this.customerListScrollPromise = null;
    this.customerSearchEnded = true;
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
      this.selectedDomain = domainCustomer;
      this.findRelatedCustomers();
    }
    this.manageCustomerSearch();
  }

  manageCustomerSearch() {
    this.customerSearchInput.value = "";
    if (this.selectedDomain) {
      this.customerSearchContainer.style.display = "flex";
    } else {
      this.customerSearchContainer.style.display = "none";
    }
  }

  linkClickHandler(customer) {
    let dialog = this.getApplication().getDialog();
    dialog.clear();
    dialog.setTitle(`${customer ? "V": "Desv"}incular cliente`);
    
    let confirmUnlinkContainer = document.createElement("div");
    confirmUnlinkContainer.style.display = "flex";
    confirmUnlinkContainer.style.flexWrap = "wrap";
    confirmUnlinkContainer.style.gap = "25px";
    confirmUnlinkContainer.style.justifyContent = "center";
    confirmUnlinkContainer.style.alignItems = "center";
    confirmUnlinkContainer.style.flexDirection = "row";
    
    let confirmUnlinkMessage = document.createElement("div");
    confirmUnlinkMessage.innerText = `¿Desea ${customer ? "": "des"}vincular este cliente del dominio?`;
    confirmUnlinkMessage.style.width = "100%";
    confirmUnlinkContainer.appendChild(confirmUnlinkMessage);

    let aonButtonAccept = new AonButton();
    aonButtonAccept.title = "Aceptar";
    confirmUnlinkContainer.appendChild(aonButtonAccept);
    aonButtonAccept.addEventListener("click", async (ev) => {
      let updatedDomains = await updateDomains({
        domains: [this.selectedDomain],
        customer: customer ? customer.id : null
      });
      if (this.selectedDomain) {
        this.selectedDomain.domain = (updatedDomains && updatedDomains[0] ? updatedDomains[0] : this.selectedDomain.domain);
      }
      this.customerListScrollPromise = null;
      this.customerSearchEnded = true;
      if (customer) {
        this.customerSearchInput.value = "";
        await this.findRelatedCustomers();
        this.showLinkedIcon();
      } else {
        this.removeAllDomains();
        await this.refreshLists();
      }
      this.manageCustomerSearch();
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
    this.customerList.innerHTML = "";
  }
  
  clearSelections() {
    for (const option of this.customerList.children) {
      option.classList.remove("selectedCustomer");
    }
    this.selectedDomain = null;
    for (const option of this.domainList.children) {
      option.classList.remove("selectedDomain");
    }
    
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

  showLinkedIcon() {
    if (this.domainList) {
      let icon = this.domainList.querySelector(".selectedDomain .linkedIcon")
      if (icon) {
        icon.style.display = "inline";
      }

    }
  }

  async refreshLists() {
    this.selectedDomain = null;
    this.removeAllCustomers();
    await this.updateDomains();
    this.loadDomains();
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
        if ((this.customerSearchInput && this.customerSearchInput.value) || (this.customerStatusFilter && this.customerStatusFilter.value)) {
          message = "No se encontraron clientes relacionados con esta búsqueda";
        } else {
          message = "No se encontraron clientes relacionados con este dominio";
        }
      } else {
        message = "Por favor, seleccione un dominio";
      }
      messageSpan.innerText = message;
      messageSpan.style.textAlign = "center";
      this.customerList.appendChild(messageSpan);
    }
  }
  
  // -------------

}

if(!window.customElements.get(TAG.AON_DOMAIN_CUSTOMER)){
	window.customElements.define(TAG.AON_DOMAIN_CUSTOMER, AonDomainCustomer);
}