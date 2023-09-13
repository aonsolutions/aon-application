import { AonElement } from './AonElement.js';

import { CONSTANT, EVENT, TAG, MATERIAL_ICONS, MSG, CSS } from '../environments/environments.js';
import { getDomains, getCustomers, updateDomains, getBooking, deleteDomainLinked, saveDomainLinked, updateCustomerBillable } from '../services/domainsService.js';

import '../css/aon-domain-customer.css';
import { AonIconButton } from './aon-icon-button.js';
import { AonButton } from './aon-button.js';
import { AonSelect } from './aon-select.js';
import { AonCheckbox } from './aon-checkbox.js';
import { AonDateUtils } from '../modules/utils/AonDateUtils.js';
import { getApp } from '../services/app.js';
import { AonStatus, DomainType, RegistryStatus } from '../models/enums.js';

export class AonDomainCustomer extends AonElement {

  mainContainer;

  domainList;
  customerList;

  loadingPanel;

  customerToSearch;

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
  domainTypeFilter;
  linkStatusFilter;
  domainAonStatusFilter;
  domainStatusFilter;
  customerStatusFilter;
  customerAccessFilter;
  customerLinkStatusFilter;

  customerPage;

  loadedDomains;
  loadedCustomers;

  selectedDomain;

  searchTimeout;
  customerSearchTimeout;

  DOMAIN_STATUS_OPTIONS = ["NOT_BILLABLE", "BILLABLE"];
  CUSTOMER_STATUS_OPTIONS = ["ACTIVE", "INACTIVE", "BLOCKED"];
  ADDITIONAL_INFOS = ['BILLABLE', 'DOMAIN_LINKED'];

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
  }
  
  async updateDomains() {
    let domains = await getDomains();
    this.loadedDomains = domains ?  domains.sort((a, b) => (a.name ? a.name : "").trim().localeCompare((b.name ? b.name : "").trim())) : [];
  }

  async updateCustomers() {
    let customers = await getCustomers({perPage: 2000, additional_info: this.this.ADDITIONAL_INFOS});
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
    this.mainContainer.style.position = "relative";

    this.buildSearchBar();
    this.buildDomainList();
    this.buildCustomerList();
    this.buildLoadingPanel();

    this.appendChild(this.loadingPanel);
    this.mainContainer.appendChild(this.searchBar);
    this.mainContainer.appendChild(this.domainList);
    this.mainContainer.appendChild(this.customerList);

    this.appendChild(this.mainContainer);
    this.displayLoadingPanel(true);
    this.updateDomains().then(() => {
      this.loadDomains();
      this.displayLoadingPanel(false);
      this.manageEmptyCustomerList();
    });
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
    this.searchInput.placeholder = MSG.DOMAIN_SEARCH;
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
    noCustomerDomainsOnlyCheckContainer.style.width = "24%";
    noCustomerDomainsOnlyCheckContainer.style.height = "50%";
    noCustomerDomainsOnlyCheckContainer.style.display = "flex";
    noCustomerDomainsOnlyCheckContainer.style.justifyContent = "center";
    noCustomerDomainsOnlyCheckContainer.style.alignItems = "center";
    
    this.noCustomerDomainsOnlyCheck = new AonCheckbox();
    this.noCustomerDomainsOnlyCheck.style.marginLeft = "0";
    this.noCustomerDomainsOnlyCheck.id = this.id + "CustomersSearchCheck";
    this.noCustomerDomainsOnlyCheck.checked = false;
    this.noCustomerDomainsOnlyCheck.description = MSG.NOT_LINKED1; 
    this.noCustomerDomainsOnlyCheck.title  = MSG.ONLY_DOMAINS_WITHOUT_LINKED_CUSTOMER;

    let domainTypeFilterContainer = document.createElement('div');
    domainTypeFilterContainer.style.width = "24%";
    domainTypeFilterContainer.style.height = "50%";
    domainTypeFilterContainer.style.display = "flex";
    domainTypeFilterContainer.style.justifyContent = "flex-start";
    domainTypeFilterContainer.style.alignItems = "center";
    domainTypeFilterContainer.style.gap = "5px";

    this.domainTypeFilter = new AonSelect();
    this.domainTypeFilter.id = this.id + "domainTypeFilter";
    this.domainTypeFilter.classList.add("domainLinkAonSelect");
    this.domainTypeFilter.title = MSG.TYPE;
    this.domainTypeFilter.readonly = false;
    this.domainTypeFilter.multiple = true;
    let domainTypeFilterOptions = [];
    this.domainTypeFilter.selectable = [];
    for (let dt in DomainType) {
      let item = {value: dt, name: DomainType[dt]};
      domainTypeFilterOptions.push(item);
      this.domainTypeFilter.selectable.push(item);
    }
    this.domainTypeFilter.options = JSON.stringify(domainTypeFilterOptions);
    this.domainTypeFilter.value = "";

    domainTypeFilterContainer.appendChild(this.domainTypeFilter);
    searchFiltersContainer.appendChild(domainTypeFilterContainer);

    this.linkStatusFilter = new AonSelect();
    this.linkStatusFilter.id = this.id + "linkStatusFilter";
    this.linkStatusFilter.classList.add("domainLinkAonSelect");
    this.linkStatusFilter.title = MSG.LINKING;
    this.linkStatusFilter.readonly = false;
    let linkStatusFilterOptions =
    [ {value: "", name: MSG.ALL1},
      {value: "linked", name: MSG.LINKED1},
      {value: "unlinked", name: MSG.UNLINKED}
    ];
    this.linkStatusFilter.options = JSON.stringify(linkStatusFilterOptions);
    this.linkStatusFilter.value = "";
    
    noCustomerDomainsOnlyCheckContainer.appendChild(this.linkStatusFilter);
    
    let domainAonStatusFilterContainer = document.createElement('div');
    domainAonStatusFilterContainer.style.width = "24%";
    domainAonStatusFilterContainer.style.height = "50%";
    domainAonStatusFilterContainer.style.display = "flex";
    domainAonStatusFilterContainer.style.justifyContent = "flex-start";
    domainAonStatusFilterContainer.style.alignItems = "center";
    domainAonStatusFilterContainer.style.gap = "5px";

    this.domainAonStatusFilter = new AonSelect();
    this.domainAonStatusFilter.id = this.id + "DomainAonStatusFilter";
    this.domainAonStatusFilter.classList.add("domainLinkAonSelect");
    this.domainAonStatusFilter.title = MSG.STATUS;
    this.domainAonStatusFilter.readonly = false;
    let domainAonStatusFilterOptions = [{value: "", name: MSG.ALL1}];
    this.DOMAIN_STATUS_OPTIONS.forEach(status => {
      domainAonStatusFilterOptions.push({
        value: status,
        name: this.getDomainStatusDescription(status)
      });
    });
    this.domainAonStatusFilter.options = JSON.stringify(domainAonStatusFilterOptions);
    this.domainAonStatusFilter.value = "";

    domainAonStatusFilterContainer.appendChild(this.domainAonStatusFilter);
    
    searchFiltersContainer.appendChild(domainAonStatusFilterContainer);

    let domainStatusFilterContainer = document.createElement('div');
    domainStatusFilterContainer.style.width = "24%";
    domainStatusFilterContainer.style.height = "50%";
    domainStatusFilterContainer.style.display = "flex";
    domainStatusFilterContainer.style.justifyContent = "flex-start";
    domainStatusFilterContainer.style.alignItems = "center";
    domainStatusFilterContainer.style.gap = "5px";

    this.domainStatusFilter = new AonSelect();
    this.domainStatusFilter.id = this.id + "DomainStatusFilter";
    this.domainStatusFilter.classList.add("domainLinkAonSelect");
    this.domainStatusFilter.title = MSG.ACCESS;
    this.domainStatusFilter.readonly = false;
    let domainStatusFilterOptions =
    [ {value: "", name: MSG.ALL1},
      {value: "active", name: MSG.ACTIVE},
      {value: "inactive", name: MSG.INACTIVE},
      {value: "expired", name: MSG.EXPIRED}
    ];
    this.domainStatusFilter.options = JSON.stringify(domainStatusFilterOptions);
    this.domainStatusFilter.value = "";

    [this.domainTypeFilter, this.noCustomerDomainsOnlyCheck, this.domainAonStatusFilter, this.domainStatusFilter, this.linkStatusFilter].forEach(filterEl => {
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
    this.customerSearchInput.placeholder = MSG.CUSTOMER_SEARCH;
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
    searchFiltersContainer.style.justifyContent = "space-evenly";
    searchFiltersContainer.style.alignItems = "center";
    searchFiltersContainer.style.flexWrap = "wrap";
    searchFiltersContainer.style.marginTop = "5px";

    let customerStatusFilterContainer = document.createElement('div');
    customerStatusFilterContainer.style.width = "25%";
    customerStatusFilterContainer.style.height = "50%";
    customerStatusFilterContainer.style.display = "flex";
    customerStatusFilterContainer.style.justifyContent = "flex-start";
    customerStatusFilterContainer.style.alignItems = "center";
    customerStatusFilterContainer.style.gap = "5px";

    this.customerStatusFilter = new AonSelect();
    this.customerStatusFilter.id = this.id + "CustomerStatusFilter";
    this.customerStatusFilter.classList.add("domainLinkAonSelect");
    this.customerStatusFilter.title = MSG.STATUS;
    this.customerStatusFilter.readonly = false;
    let customerStatusFilterOptions = [{value: "", name: MSG.ALL1}];
    this.DOMAIN_STATUS_OPTIONS.forEach(status => {
      customerStatusFilterOptions.push({
        value: status,
        name: this.getDomainStatusDescription(status)
      });
    });
    this.customerStatusFilter.options = JSON.stringify(customerStatusFilterOptions);
    this.customerStatusFilter.value = "";

    this.customerStatusFilter.addEventListener("change", (event) => this.customerSearchFunction());
    customerStatusFilterContainer.appendChild(this.customerStatusFilter);
    
    let customerAonStatusFilterContainer = document.createElement('div');
    customerAonStatusFilterContainer.style.width = "25%";
    customerAonStatusFilterContainer.style.height = "50%";
    customerAonStatusFilterContainer.style.display = "flex";
    customerAonStatusFilterContainer.style.justifyContent = "flex-start";
    customerAonStatusFilterContainer.style.alignItems = "center";
    customerAonStatusFilterContainer.style.gap = "5px";

    this.customerAccessFilter = new AonSelect();
    this.customerAccessFilter.id = this.id + "CustomerAccessFilter";
    this.customerAccessFilter.classList.add("domainLinkAonSelect");
    this.customerAccessFilter.title = MSG.ACCESS;
    this.customerAccessFilter.readonly = false;
    let customerAonStatusFilterOptions = [{value: "", name: MSG.ALL1}];
    this.CUSTOMER_STATUS_OPTIONS.forEach(status => {
      customerAonStatusFilterOptions.push({value: status, name: this.getCustomerStatusDescription(status)});
    });
    this.customerAccessFilter.options = JSON.stringify(customerAonStatusFilterOptions);
    this.customerAccessFilter.value = "";

    this.customerAccessFilter.addEventListener("change", (event) => this.customerSearchFunction());
    customerAonStatusFilterContainer.appendChild(this.customerAccessFilter);

    let customerLinkStatusFilterContainer = document.createElement('div');
    customerLinkStatusFilterContainer.style.width = "25%";
    customerLinkStatusFilterContainer.style.height = "50%";
    customerLinkStatusFilterContainer.style.display = "flex";
    customerLinkStatusFilterContainer.style.justifyContent = "flex-start";
    customerLinkStatusFilterContainer.style.alignItems = "center";
    customerLinkStatusFilterContainer.style.gap = "5px";

    this.customerLinkStatusFilter = new AonSelect();
    this.customerLinkStatusFilter.id = this.id + "CustomerLinkStatusFilter";
    this.customerLinkStatusFilter.classList.add("domainLinkAonSelect");
    this.customerLinkStatusFilter.title = MSG.LINKING;
    this.customerLinkStatusFilter.readonly = false;
    let domainAonLinkStatusFilterOptions =
    [ {value: "", name: MSG.ALL1},
      {value: "linked", name: MSG.LINKED1},
      {value: "notLinked", name: MSG.UNLINKED}
    ];

    this.customerLinkStatusFilter.options = JSON.stringify(domainAonLinkStatusFilterOptions);
    this.customerLinkStatusFilter.value = "";

    this.customerLinkStatusFilter.addEventListener("change", (event) => this.customerSearchFunction());

    customerLinkStatusFilterContainer.appendChild(this.customerLinkStatusFilter);
    
    searchFiltersContainer.appendChild(customerStatusFilterContainer);
    searchFiltersContainer.appendChild(customerAonStatusFilterContainer);
    searchFiltersContainer.appendChild(customerLinkStatusFilterContainer);

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
    this.domainList.classList.add("domainCustomerScroll");
  }
  
  buildCustomerList() {
    this.customerList = document.createElement('div');
    this.customerList.style.height = "80%";
    this.customerList.style.width = "45%";
    this.customerList.style.display = "flex";
    this.customerList.style.flexDirection = "column";
    this.customerList.style.gap = "5px";
    this.customerList.classList.add("domainCustomerScroll");
    this.customerList.addEventListener("scroll", (event) => {
      this.scrollFunction();
    });
  }

  buildLoadingPanel() {
    this.loadingPanel = document.createElement('div');
    this.loadingPanel.style.position = "absolute";
    this.loadingPanel.style.width = "100%";
    this.loadingPanel.style.height = "100%";
    this.loadingPanel.style.display = "none";
    this.loadingPanel.style.justifyContent = "center";
    this.loadingPanel.style.alignItems = "center";
    this.loadingPanel.style.backgroundColor = "rgba(240,248,255,0.5)";
    this.loadingPanel.style.zIndex = "2";

    let loadingLine = document.createElement('div');
    loadingLine.style.display = "flex";
    loadingLine.style.justifyContent = "center";
    loadingLine.style.alignItems = "center";

    let loadingIcon = document.createElement('span');
    loadingIcon.classList.add(CSS.MATERIAL_ICONS);
    loadingIcon.classList.add("linkLoadSpinner")
    loadingIcon.innerHTML = MATERIAL_ICONS.AUTORENEW;
    loadingIcon.style.fontSize = "40px";

    let loadingText = document.createElement('span');
    loadingText.innerText =  `${MSG.LOADING}...`;

    loadingLine.appendChild(loadingIcon);
    loadingLine.appendChild(loadingText);

    this.loadingPanel.appendChild(loadingLine);

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
  }
  
  search(inputText) {
    let filteredDomains = this.loadedDomains.filter((domainCompany) => 
        (  (domainCompany.domain && domainCompany.domain.name ? domainCompany.domain.name : "").toLowerCase().includes(inputText)
        || (domainCompany.company && domainCompany.company.document ? domainCompany.company.document : "").toLowerCase().includes(inputText)
        || (domainCompany.company && domainCompany.company.name ? domainCompany.company.name : "").toLowerCase().includes(inputText)
        || (domainCompany.domain && domainCompany.domain.description ? domainCompany.domain.description : "").toLowerCase().includes(inputText)
        || (domainCompany.domain && domainCompany.domain.aonCustomer ? domainCompany.domain.aonCustomer : null) == inputText
        || (domainCompany.domain && domainCompany.domain.id ? domainCompany.domain.id : null) == inputText
        )
        && this.checkCustomerToSearch(domainCompany)
        && this.checkLinkStatus(domainCompany)
        && (this.domainAonStatusFilter.value ? (domainCompany.domain ? this.domainAonStatusFilter.value === domainCompany.domain.aonStatus : false) : true)
        && this.checkAccess(domainCompany)
        && this.checkDomainType(domainCompany)
        // && (this.noCustomerDomainsOnlyCheck.getValue() ? !(domainCompany.domain ? domainCompany.domain.aonCustomer : null): true)
        );
        this.filterDomains(filteredDomains);
        
        this.manageCustomersToSearch();
  }

  checkDomainType(domainCompany) {
    let domainType = domainCompany.domain && domainCompany.domain.domainType ? domainCompany.domain.domainType : null;

    if (domainType) {
      let opt = {value: domainType, name: DomainType[domainType]};
      let selectedTypes = this.domainTypeFilter.getSelectable() || [];
      return selectedTypes.some(opt => opt.value === domainType);
    }

    return false;
  }

  manageCustomersToSearch() {
    if (this.customerToSearch) {
      let domainElements = [...this.domainList.children];
      let filteredDomainElements = domainElements.filter(element => element.style.display != "none")
      if (filteredDomainElements.length === 1) {
        let firstElement = filteredDomainElements[0];
        if (firstElement) {
          firstElement.click();
        }
      }
    }
    this.customerToSearch = null;
  }

  checkCustomerToSearch(domainCompany) {
    if (this.customerToSearch) {
      let aonCustomer = this.customerToSearch.id;
      return (domainCompany.domain && domainCompany.domain.aonCustomer ? domainCompany.domain.aonCustomer : null) === aonCustomer;
    }
    return true;
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
      let customerStatus = this.customerAccessFilter ? this.customerAccessFilter.value : "";
      let parameters = {
        perPage: entriesPerPage,
        page: this.customerPage++,
        value: this.customerSearchInput.value.toLowerCase(),
        additional_info: this.ADDITIONAL_INFOS
      };
      if (customerStatus) {
        parameters.status = customerStatus;
      }
      if (this.customerLinkStatusFilter) {
        switch (this.customerLinkStatusFilter.value) {
          case "linked":
            parameters.linked = true;
            break;
          case "notLinked":
            parameters.linked = false;
            break;
          default:
            break;
        }
      }
      if (this.customerStatusFilter && this.customerStatusFilter.value) {
        parameters.billable = (this.DOMAIN_STATUS_OPTIONS[1] === this.customerStatusFilter.value);
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
      let customerStatus = this.customerAccessFilter ? this.customerAccessFilter.value : "";
      
      let customers = [];
      let booking = null;
      
      if (aonCustomer) {
        let cust = await getCustomers({
          id: aonCustomer,
          additional_info: this.ADDITIONAL_INFOS
        });
        customers.push(cust);
        if (cust && domain) {
          let dName = domain.name;
          let dId = domain.id;
          let linkedCustomerDomain = cust.domain;
          if (dName && dId) {
            try {
              booking = await getBooking({
                domainName: dName,
                domainId: dId,
              });
              console.log(booking);
            } catch (error) {
              console.error(error);
            }
          }
        }

      } else {
        if (searchDocument) {
          let parameters = {
            perPage: 2000,
            value: searchDocument,
            additional_info: this.ADDITIONAL_INFOS
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
            value: searchAlias,
            additional_info: this.ADDITIONAL_INFOS
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

  resetDomainFilters() {
    this.domainAonStatusFilter.value = "";
    this.linkStatusFilter.value = "";
    this.domainStatusFilter.value = "";
  }

  resetCustomerFilters() {
    this.customerStatusFilter.value = "";
    this.customerAccessFilter.value = "";
    this.customerLinkStatusFilter.value = "";
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
    linkedIcon.title = MSG.LINKED;
    linkedIcon.classList.add("linkedIcon");
    linkedIcon.style.width = "25px";
    linkedIcon.style.display = "none";

    let domainOptionName = document.createElement("div");
    domainOptionName.style.display = "inline";
    domainOptionName.style.width = "calc(100% - 25px)";
    domainOptionName.title = `${domain.active ? MSG.ACTIVE : MSG.INACTIVE}`;
    
    let domainOptionNameSpan = document.createElement("a");
    domainOptionNameSpan.style.fontWeight = "bold";
    domainOptionNameSpan.classList.add("domainLinkA");
    domainOptionNameSpan.target = "_blank";
    domainOptionNameSpan.href = `https://${domain.name}`;
    domainOptionNameSpan.innerText = `${domain.name}`;
    domainOptionNameSpan.addEventListener("click", e => e.stopPropagation());
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
    companyOptionDocument.innerText = `${company && company.document ? company.document : ""}`;
    domainOptionSelectLine.appendChild(companyOptionDocument);

    let expireTypeContainer = document.createElement("div");
    expireTypeContainer.style.width = "130px";
    expireTypeContainer.style.display = "flex";
    expireTypeContainer.style.flexDirection = "column";
    expireTypeContainer.style.alignItems = "flex-start";
    if (domain && domain.expirationDate) {
      if (domain.active) {
        domainOptionNameSpan.classList.add(`expireDomainName`);
      }
      let companyOptionExpire = document.createElement("div");
      companyOptionExpire.style.width = "100%";
      companyOptionExpire.innerText = `Expira: ${AonDateUtils.formatDate(domain.expirationDate)}`;
      expireTypeContainer.appendChild(companyOptionExpire);
    }
    let domainTypeElement = document.createElement("div");
    domainTypeElement.style.width = "100%";
    domainTypeElement.innerText = `${this.getDomainTypeDescription(domain ? domain.domainType : "")}`;
    expireTypeContainer.appendChild(domainTypeElement);

    domainOptionSelectLine.appendChild(expireTypeContainer);

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
    customerOptionContainer.style.alignItems = "flex-start";
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

    let customerInfoContainer = document.createElement("div");
    customerInfoContainer.style.display = "flex";
    customerInfoContainer.style.flexDirection = "row";
    customerInfoContainer.style.width = "100%";
    customerInfoContainer.style.justifyContent = "flex-start";
    customerInfoContainer.style.alignItems = "flex-start";
    customerInfoContainer.style.gap = "5px";

    let customerDocumentDiv = document.createElement("div");
    customerDocumentDiv.style.width = "33%";
    // customerDocumentDiv.style.textAlign = "center";
    // customerDocumentDiv.style.margin = "auto";
    // customerDocumentDiv.style.height = "20px";
    customerDocumentDiv.innerText = customer.document;

    let customerIdDiv = document.createElement("div");
    customerIdDiv.style.width = "33%";
    // customerIdDiv.style.textAlign = "center";
    // customerIdDiv.style.margin = "auto";
    // customerIdDiv.style.height = "20px";
    customerIdDiv.innerText = customer.id ? `SIG: ${customer.id}` : "";

    let customerStatusAndBillingContainer =  document.createElement("div");
    customerStatusAndBillingContainer.style.display = "flex";
    customerStatusAndBillingContainer.style.flexDirection = "column";
    customerStatusAndBillingContainer.style.justifyContent = "flex-start";
    customerStatusAndBillingContainer.style.alignItems = "flex-start";
    customerStatusAndBillingContainer.style.width = "33%";
    customerStatusAndBillingContainer.style.margin = "auto";
    // customerStatusAndBillingContainer.style.height = "20px";


    let customerStatusDiv = document.createElement("div");
    customerStatusDiv.style.width = "100%";
    // customerStatusDiv.style.textAlign = "left";
    // customerStatusDiv.style.height = "20px";
    customerStatusDiv.dataset.value = `${customer.status}`;
    customerStatusDiv.innerText = `${MSG.STATUS}: ${this.getCustomerStatusDescription(customer.status)}`;

    let aonStatusDrop = document.createElement("select");
    aonStatusDrop.style.width = "100%";
    
    this.DOMAIN_STATUS_OPTIONS.forEach((status, index) => {
      let option = document.createElement("option");
      option.value = status;
      option.innerText = this.getDomainStatusDescription(status);
      if (index === 1 && customer.billable) {
        option.selected = true;
      }
      aonStatusDrop.appendChild(option);
    });

    

    aonStatusDrop.addEventListener("change", async () => {
      let isBillable = aonStatusDrop.value === this.DOMAIN_STATUS_OPTIONS[1];
      let updatedCustomer = await updateCustomerBillable({
        customer: customer,
        billable: isBillable,
      });
      customer.billable = isBillable;
      console.log(updatedCustomer);
    });


    customerStatusAndBillingContainer.appendChild(customerStatusDiv);
    customerStatusAndBillingContainer.appendChild(aonStatusDrop);

    customerInfoContainer.appendChild(customerDocumentDiv);
    customerInfoContainer.appendChild(customerIdDiv);
    customerInfoContainer.appendChild(customerStatusAndBillingContainer);

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
    unlinkDiv.style.height = "100%";
    
    let unlinkButton = new AonIconButton();
    unlinkButton.icon = isLinked ? MATERIAL_ICONS.LINK_OFF : MATERIAL_ICONS.LINK;
    unlinkButton.title = `${isLinked ? MSG.UNLINK : MSG.LINK}`;
    unlinkButton.addEventListener("click", () => this.linkClickHandler(isLinked ? null : customer));
    
    infoDiv.appendChild(customerNameDiv);
    infoDiv.appendChild(customerAliasDiv);
    infoDiv.appendChild(customerInfoContainer);
    
    if (this.selectedDomain) {
      unlinkDiv.appendChild(unlinkButton);
    } else if (customer.domainLinked && customer.domainLinked.length > 0) {
      let customerSearchButton = new AonIconButton();
      customerSearchButton.icon = MATERIAL_ICONS.SEARCH;
      customerSearchButton.title = `${MSG.FIND_LINKED_DOMAINS}`;
      customerSearchButton.addEventListener("click", () => {
        this.customerToSearch = customer;
        this.resetDomainFilters();
        // this.resetCustomerFilters();
        this.searchFunction();
      });
      unlinkDiv.appendChild(customerSearchButton);
    }
    
    customerOptionContainer.appendChild(infoDiv);
    customerOptionContainer.classList.add("linkedCustomer");
    infoDiv.style.width = "85%";
    customerOptionContainer.appendChild(unlinkDiv);

    return customerOptionContainer;
  }

  generateUsersFile(applicationName, users) {
    let text = "";
    if (users) {
      users.forEach(user => {
        text += `${user.description}\t\t${user.name}\n`;
      })
    }

    let file = new Blob([text], { type: "text/plain" });

    let a = document.createElement("a");
    let url = URL.createObjectURL(file);
    a.href = url;
    a.download = `usuarios_de_${applicationName}.txt`;
    a.style.display = "none";
    this.appendChild(a);
    a.click();
    setTimeout(() => {
      this.removeChild(a);
      window.URL.revokeObjectURL(url);
    }, 0);
  }

  createBookingElement(booking) {
    if (booking) {

      let domain = this.selectedDomain.domain;

      let bookingContainer = document.createElement("div");
      bookingContainer.style.display = "flex";
      bookingContainer.style.flexDirection = "column";
      bookingContainer.style.gap = "10px";
      bookingContainer.style.padding = "10px";
      bookingContainer.style.borderRadius = "5px";
      bookingContainer.style.backgroundColor = "AliceBlue";
      bookingContainer.style.width = "100%";
      bookingContainer.style.marginTop = "10px";

      let bookingTitleContainer = document.createElement("div");
      bookingTitleContainer.style.display = "block";
      bookingTitleContainer.style.width = "100%";
      bookingTitleContainer.style.textAlign = "left";
      bookingTitleContainer.style.fontWeight = "bold";
      bookingTitleContainer.style.textTransform = "uppercase";
      bookingTitleContainer.innerText = MSG.HIRING_DATA;
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
      childAppsTitleContainer.style.textAlign = "left";
      childAppsTitleContainer.style.fontWeight = "bold";
      let selectedDomainType = this.selectedDomain && this.selectedDomain.domain ? this.selectedDomain.domain.domainType : "";
      let selectedDomainTypeDesc = this.getDomainTypeDescription(selectedDomainType);
      childAppsTitleContainer.innerText = `${MSG.APPLICATIONS} ${selectedDomainTypeDesc ? `: ${selectedDomainTypeDesc}` : ""}`;

      childAppsContainer.appendChild(childAppsTitleContainer);
      let childApps = booking.apps;
      if (childApps) {
        childApps.sort(this.appsComparator).forEach(app => {
          let childApp = document.createElement("div");
          childApp.style.display = "block";
          childApp.style.marginLeft = "5px";
          childApp.style.width = "100%";
          let application = getApp(app);
          childApp.innerText = application ? application.title : app;
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
      parentAppsTitleContainer.style.textAlign = "left";
      parentAppsTitleContainer.style.fontWeight = "bold";
      parentAppsTitleContainer.innerText = MSG.PARENT_APPS;
      
      parentAppsContainer.appendChild(parentAppsTitleContainer);
      let parentApps = booking.parentApps;
      if (parentApps) {
        parentApps.forEach(app => {
          let parentApp = document.createElement("div");
          parentApp.style.display = "block";
          parentApp.style.marginLeft = "5px";
          parentApp.style.width = "100%";
          let application = getApp(app);
          parentApp.innerText = application ? application.title : app;
          parentAppsContainer.appendChild(parentApp);
        });
      }

      if (domain && domain.parentId) {
        appsContainer.appendChild(parentAppsContainer);
      } else if (domain && !domain.parentId) {
        childAppsContainer.style.width = "40%";

        let summaryContainer = document.createElement("div");
        summaryContainer.style.display = "flex";
        summaryContainer.style.flexDirection = "column";
        summaryContainer.style.justifyContent = "flex-start";
        summaryContainer.style.alignItems = "center";
        summaryContainer.style.width = "57%";

        let summaryTitleContainer = document.createElement("div");
        summaryTitleContainer.style.display = "block";
        summaryTitleContainer.style.width = "100%";
        summaryTitleContainer.style.textAlign = "left";
        summaryTitleContainer.style.fontWeight = "bold";
        summaryTitleContainer.innerText = MSG.ADDITIONAL_HIRING;

        summaryContainer.appendChild(summaryTitleContainer);

        let summary = booking.resume;
        let summaryApps = summary ? summary.apps : null;
        let summaryUsers = summary ? summary.user : null;
        
        let summaryDomains = summary ? summary.domain : {};

        this.createSummaryItem(summaryContainer, MSG.APPLICATIONS, summaryApps);
        this.createSummaryItem(summaryContainer, MSG.USERS, summaryUsers, [], "user");

        for (let dt in summaryDomains) {
          let domainType = this.getDomainTypeDescription(dt);
          let application = summaryDomains[dt];

          this.createSummaryItem(summaryContainer, `${domainType}: ${application.number}`, application.apps, application.childs, "domain");
        }
        



        appsContainer.appendChild(summaryContainer);
      }
      
      let numberOfUsersContainer = document.createElement("div");
      numberOfUsersContainer.style.display = "block";
      numberOfUsersContainer.style.width = "100%";
      let numberOfUsersTitleSpan = document.createElement("span");
      numberOfUsersTitleSpan.style.fontWeight = "bold";
      numberOfUsersTitleSpan.innerText = `${MSG.NUMBER_OF_USERS}: `;
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

  appsComparator(a, b) {
    let elementA = a ? a.toLowerCase().trim() : "";
    let elementB = b ? b.toLowerCase().trim() : "";

    if (elementA.includes("suite") && !elementB.includes("suite")) {
      return -1;
    } else if (!elementA.includes("suite") && elementB.includes("suite")) {
      return 1;
    }

    if (elementA.includes("pack") && !elementB.includes("pack")) {
      return -1;
    } else if (!elementA.includes("pack") && elementB.includes("pack")) {
      return 1;
    }

    if (elementA.includes("management") && !elementB.includes("management")) {
      return -1;
    } else if (!elementA.includes("management") && elementB.includes("management")) {
      return 1;
    }
    let appA = getApp(elementA);
    let appB = getApp(elementB);
    let appAName = appA ? appA.title : elementA;
    let appBName = appB ? appB.title : elementB;
    return appAName.localeCompare(appBName);

  }

  createSummaryItem(summaryContainer, title, summaryElements, childs, type) {
    if (summaryElements) {
      let summaryElementsContainer = document.createElement("div");
      summaryElementsContainer.style.display = "flex";
      summaryElementsContainer.style.flexDirection = "column";
      summaryElementsContainer.style.width = "100%";
      summaryContainer.appendChild(summaryElementsContainer);
      let summaryElementsTitle = document.createElement("div");
      summaryElementsTitle.style.display = "block";
      summaryElementsTitle.style.width = "100%";
      summaryElementsTitle.style.fontWeight = "bold";
      summaryElementsTitle.style.marginLeft = "5px";
      summaryElementsTitle.innerText = title;
      summaryElementsContainer.appendChild(summaryElementsTitle);
      if (type === "domain" && childs && childs.length > 0)  {
        summaryElementsTitle.style.cursor = "pointer";
        summaryElementsTitle.addEventListener("click", event => {
          this.createUsersList(childs, title ? title.split(":")[0] : "");
        });
      }
      let sumElements = Object.keys(summaryElements);
      sumElements.sort(this.appsComparator);

      for (const summaryElement of sumElements) {
        let summaryElementsItem = document.createElement("div");
        summaryElementsItem.style.display = "block";
        summaryElementsItem.style.width = "100%";
        summaryElementsItem.style.marginLeft = "10px";
        
        let itemTitle = "";
        if (type === "user") {
          let elementName = (summaryElement ? summaryElement : "").toLowerCase().trim();
          switch (elementName) {
            case "shared":
              itemTitle = MSG.SHARED;
              break;
            case "childdefinedusers":
              itemTitle = MSG.CONTRACTED_USERS;
              break;
            case "childbillingusers":
              itemTitle = MSG.BILLABLE_USERS;
              break;
            default:
              itemTitle = summaryElement;
              break;
          }
        } else  {
          let application = getApp(summaryElement);
          itemTitle = application ? application.title : summaryElement;
        }

        let users = childs.filter(c => c.apps && c.apps.includes(summaryElement));
        let totalUsers = users.map(c => c.maxDefinedUsers).reduce((a, b) => a + b, 0);
        summaryElementsItem.innerText = `${itemTitle}: ${summaryElements[summaryElement]}/${totalUsers} usr.`;
        summaryElementsContainer.appendChild(summaryElementsItem);
        if (type === "domain" && childs && childs.length > 0)  {
          summaryElementsItem.style.cursor = "pointer";
          summaryElementsItem.addEventListener("click", event => {
            this.createUsersList(users, itemTitle);
          });
        }
      }
    }
  }

  createUsersList(users, applicationName) {

    let dialog = this.getApplication().getDialog();
    dialog.clear();
    let title = `Empresas con "${applicationName}"`;
    dialog.setTitle(title);

    let usersContainer = document.createElement("div");
    usersContainer.style.display = "flex";
    usersContainer.style.flexDirection = "column";
    usersContainer.style.gap = "3px";
    usersContainer.style.overflowY = "auto";
    usersContainer.style.maxHeight = "75vh";
    usersContainer.style.justifyContent = "center";
    usersContainer.style.alignItems = "center";

    let infoContainer = document.createElement("div");
    infoContainer.style.width = "100%";
    infoContainer.style.display = "flex";
    infoContainer.style.flexDirection = "column";

    usersContainer.appendChild(infoContainer);
    let textToCopy = `${title}\n`;

    users
    .sort((a, b) => {
      const descA = (a.description ? a.description.toLowerCase() : "").trim();
      const descB = (b.description ? b.description.toLowerCase() : "").trim();

      if (descA < descB) {
        return -1;
      } else if (descA > descB) {
        return 1;
      }
      return 0;
    })
    .forEach((user, ind) => {

      let userContainer = document.createElement("div");
      userContainer.style.display = "flex";
      userContainer.style.justifyContent = "center";
      userContainer.style.alignItems = "center";
      userContainer.style.marginLeft = "10px";
      userContainer.style.fontSize = "1.2em";

      let userDescriptionContainer = document.createElement("div");
      userDescriptionContainer.style.width = "47.5%";
      userDescriptionContainer.style.textAlign = "right";
      userDescriptionContainer.innerText = user.description;
      
      
      let userSeparatorContainer = document.createElement("div");
      userSeparatorContainer.style.width = "5%";
      userSeparatorContainer.style.textAlign = "center";
      userSeparatorContainer.innerText = "-";
      
      let userNameContainer = document.createElement("div");
      userNameContainer.style.width = "47.5%";
      userNameContainer.style.textAlign = "left";
      userNameContainer.innerText = user.name;

      textToCopy += `${user.description} - ${user.name}` + (ind < users.length - 1 ? "\n" : "");

      userContainer.appendChild(userDescriptionContainer);
      userContainer.appendChild(userSeparatorContainer);
      userContainer.appendChild(userNameContainer);
      
      infoContainer.appendChild(userContainer);

    });

    let buttonContainer = document.createElement("div");
    buttonContainer.style.width = "100%";
    buttonContainer.style.display = "flex";
    buttonContainer.style.flexDirection = "row";
    buttonContainer.style.justifyContent = "center";
    buttonContainer.style.alignItems = "center";
    buttonContainer.style.gap = "3%";
    usersContainer.appendChild(buttonContainer);

    let downloadButton = new AonIconButton();
    downloadButton.icon = "download";
    downloadButton.background = "var(--aonBlue)";
    downloadButton.color = "white";
    downloadButton.title = MSG.DOWNLOAD;
    // downloadButton.style.width = "15%";
    downloadButton.style.marginTop = "10px";
    downloadButton.addEventListener("click", event => {
      this.generateUsersFile(applicationName, users);
    });
    buttonContainer.appendChild(downloadButton);

    let copyButton = new AonIconButton();
    copyButton.icon = "content_copy";
    copyButton.title = MSG.COPY;
    copyButton.background = "var(--aonBlue)";
    copyButton.color = "white";
    copyButton.style.marginTop = "10px";
    copyButton.addEventListener("click", event => {
      navigator.clipboard.writeText(textToCopy).then(() => {
        this.showToast({message: MSG.COPIED_TO_CLIPBOARD});
      }, err => {
        this.showError(err);
      })
    });
    buttonContainer.appendChild(copyButton);

    // dialog.width = "500px";
    dialog.setContent(usersContainer);
    dialog.autoclose = true;
    dialog.open();
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
    // if (this.selectedDomain) {
    //   this.customerSearchContainer.style.display = "flex";
    // } else {
    //   this.customerSearchContainer.style.display = "none";
    // }
  }

  linkClickHandler(customer) {
    let dialog = this.getApplication().getDialog();
    dialog.clear();
    dialog.setTitle(`${customer ? MSG.LINK_CLIENT: MSG.UNLINK_CLIENT}`);
    
    let confirmUnlinkContainer = document.createElement("div");
    confirmUnlinkContainer.style.display = "flex";
    confirmUnlinkContainer.style.flexWrap = "wrap";
    confirmUnlinkContainer.style.gap = "25px";
    confirmUnlinkContainer.style.justifyContent = "center";
    confirmUnlinkContainer.style.alignItems = "center";
    confirmUnlinkContainer.style.flexDirection = "row";
    
    let confirmUnlinkMessage = document.createElement("div");
    confirmUnlinkMessage.innerText = `${customer ? MSG.LINK_DOMAIN_QUESTION: MSG.UNLINK_DOMAIN_QUESTION}`;
    confirmUnlinkMessage.style.width = "100%";
    confirmUnlinkContainer.appendChild(confirmUnlinkMessage);

    let aonButtonAccept = new AonButton();
    aonButtonAccept.title = MSG.ACCEPT;
    confirmUnlinkContainer.appendChild(aonButtonAccept);
    aonButtonAccept.addEventListener("click", async (ev) => {
      let customerId = customer ? customer.id : null;
      let aonCustomer = this.selectedDomain && this.selectedDomain.domain ? this.selectedDomain.domain.aonCustomer : null;
      let updatedDomains = await updateDomains({
        domains: [this.selectedDomain],
        customer: customerId
      });

      if (customerId) {
        if (aonCustomer) {
          await deleteDomainLinked({
            domain: this.selectedDomain,
            customer: aonCustomer
          });          
        }
        await saveDomainLinked({
          domain: this.selectedDomain,
          customer: customerId
        });
      } else {
        await deleteDomainLinked({
          domain: this.selectedDomain,
          customer: aonCustomer
        });
      }
      if (this.selectedDomain) {
        this.selectedDomain.domain = (updatedDomains && updatedDomains[0] ? updatedDomains[0] : this.selectedDomain.domain);
      }
      this.customerListScrollPromise = null;
      this.customerSearchEnded = true;

      if (customer) {
        this.customerSearchInput.value = "";
        await this.findRelatedCustomers();
        this.showLinkedIcon();
        dialog.close();
      } else {
        dialog.close();
        this.removeAllDomains();
        await this.refreshLists();
      }
      this.manageCustomerSearch();
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
    return AonStatus[status] ? AonStatus[status] : status;
  }
  
  getCustomerStatusDescription(status) {
    return RegistryStatus[status] ? RegistryStatus[status] : status;
  }

  getDomainTypeDescription(type) {
    return DomainType[type] ? DomainType[type] : type;
  }

  // -------------------------------

  // --- OTROS ---

  displayLoadingPanel(display) {
    if (display) {
      this.loadingPanel.style.display = "flex";
    } else {
      this.loadingPanel.style.display = "none";
    }
  }

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
    this.displayLoadingPanel(true);
    await this.updateDomains();
    this.displayLoadingPanel(false);
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
        if ((this.customerSearchInput && this.customerSearchInput.value) || (this.customerAccessFilter && this.customerAccessFilter.value)) {
          message = MSG.CUSTOMER_SEARCH_NOT_FOUND;
        } else {
          message = MSG.CUSTOMER_SEARCH_DOMAIN_NOT_FOUND;
        }
      } else {
        message = MSG.CHOOSE_A_DOMAIN;
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