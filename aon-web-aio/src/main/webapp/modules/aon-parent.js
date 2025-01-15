import {AonElement} from 'aonsolutions/components/AonElement.js';
import {closeSession, getCompanies, getUserNotice, getUser, getCompaniesBySchemas, getTimeControl, getContracts} from  'aonsolutions/services/service.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG, CONSTANT } from 'aonsolutions/environments/environments.js';
import { AonDesktop } from 'aonsolutions/modules/company/aon-desktop.js';
import { AonApplication } from 'aonsolutions/components/aon-application.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonDialogMenu } from 'aonsolutions/components/aon-dialog-menu.js';
import { MenuApps, ClassicApps, Apps } from 'aonsolutions/services/app.js';
import { AonSign } from "aonsolutions/modules/timecontrol/aon-sign.js";


export class AonParent extends AonElement {

	notice;
	filter;
	selected;
	companies;

	more;
	PARENT;
	APPS_DIV;
	ENTERPRISES;
	INBOX_INVOICES;
	PENDING_INVOICES;
	REJECTED_INVOICES;
	COMPANY_FILTER_TAB;
	
	searchBoxTimeout;

	setFilter(filter){
		this.filter = filter;
	}

	getFilter(){
		return this.filter;
	}

	constructor () {
		super();
		this.id = 'aonParent';
		this.PARENT = 'aonParent';
		this.APPS_DIV = "appsDiv";
		this.COMPANY_FILTER_TAB = "aonCompanyTabFilter";
		this.ENTERPRISES = `${CONSTANT.ENTERPRISES.initCap()}All`;
		this.INBOX_INVOICES = `${CONSTANT.INVOICES.initCap()}Inbox`;
		this.PENDING_INVOICES = `${CONSTANT.INVOICES.initCap()}Pending`;
		this.REJECTED_INVOICES = `${CONSTANT.INVOICES.initCap()}Rejected`;
		this.filter = {	id: 'active', active: true, name: MSG.ACTIVES,};
	}

	connectedCallback () {
		this.init({id:'active', active: true, domainActive:true});
		let searchBox = this.getElement('aonHeaderSearchBox');
		searchBox.addEventListener(EVENT.KEYUP, () => {
			clearTimeout(this.searchBoxTimeout); 
			this.searchBoxTimeout = setTimeout(() => this.search(this), 1000);
		});
	}

	init(filter) {
		//TODO: aonParent.startLoader();
		
		let aonApplication = new AonApplication();
		aonApplication.setAttribute("sidenav_width", "300px");
		this.createApplication(this.PARENT, "", aonApplication);

		this.build();
		this.buildSidenav();
		this.select(filter, companies =>  { 
			this.decorateTabs(companies); 
			this.getApplication().updateSidenavCount(this.ENTERPRISES, companies?.length || 0);
		} );
	}
	
	search(el) {
		let searchBox = el.getElement('aonHeaderSearchBox');
		let searchDialogMenu =  el.getApplication().getOptionDialog();
		
		getCompanies().then(companies => {
			let searchCompanies = el.filterCompanies(companies, {...el.getFilter(),...{value:searchBox.value}});
			
			let searchOptions = [];
			
			if ( searchCompanies.length > 0 ) {
				let title = searchCompanies.length == 1 ? `${MSG.ONE} ${MSG.ENTERPRISE}` :`${searchCompanies.length} ${MSG.ENTERPRISES}`;
				searchOptions.push({
					icon: MATERIAL_ICONS.BUSINESS,
					name: `<span style="font-weight: bold; cursor: default" >${title}</span>`,
				});
			}
			
			searchCompanies.slice(0,10).forEach(company => {
				searchOptions.push({
					icon : this.getIcon(company),
					name : `<span>${company.name}</span><span style="float:right;">${company.document}</span>`,
					fn: () => {this.companySelection(company, false);},
				});				
			});
			
			searchDialogMenu.getContent().style.minWidth = `${searchBox.offsetWidth}px`; 
			
			const top  = searchBox.getBoundingClientRect().bottom ;
			const left = searchBox.getBoundingClientRect().left;
			searchDialogMenu.setMenuOptions(searchOptions, top, left);
			searchDialogMenu.open();
			
/*			getContracts({ page:0, perPage:11, name: searchBox.value})
			.then(contracts => {
				contracts.forEach(contract => console.log( JSON.stringify(contract)));	
			});
*/			
		});
		
		
	}

	select(filter, callback) {
		
		this.clearSelectedTab(this.filter);		
		//TODO: aonParent.startLoader();

		getCompanies().then( companies => {
			let cps = companies.filter(r => r.id == LS.getDomainId());
			if(cps.length > 0 && !cps[0].parent) {
				this.companySelection(cps[0], companies.length == 1 );
			} else if ( LS.getCompany() ) {
				this.companySelection(LS.getCompany(), companies.length == 1 );
			} else if(companies.length === 1) {
				this.companySelection(companies[0], true);
			} else {
				
				
				let aonMenu = this.getElement('aonMenu');
				aonMenu.init()
				.then(() => {
					LS.setDomainLogin(aonMenu.getDur().getUser().login);
					LS.setDomainId(aonMenu.getDur().getDomain().getId());
					LS.setDomainName(aonMenu.getDur().getDomain().getName());
					aonMenu.open();
				})
				.catch((err) => {
				});

				this.selectTab(filter);		
				//TODO: aonParent.stopLoader();
				this.page = 1;
				this.cleanCompanies();
				
				let filteredCompanies = this.filterCompanies(companies, filter);
				
				this.buildCompanies(filteredCompanies.slice(0, 30), filter);
				callback?.(companies);

			}	
		}, () => closeSession());

		if(filter){
			this.setFilter(filter);
		}
	}
	
	decorateTabs(companies, filter = {}) {
		let companyFilterTabs = [{
				id: 'active',
				active: true,
				name: MSG.ACTIVES,
			}, {
				id: 'inactive',
				inactive: true,
				name: MSG.INACTIVES
			}, {
				id: 'shared',
				shared: true,
				name: MSG.SHARED
			}, {
				id: 'consultancy',
				entorno:true,
				name: MSG.ENVIRONMENT
			},{
				id: 'office',
				despacho:true,
				name: MSG.OFFICE
			}
		];	
		for( let companyFilterTab of companyFilterTabs ){
			let companyFilterTabCompanies = companies.filter(f => this.companyFilter(f, { ...companyFilterTab, ...filter }));
			let companyFilterTabSpan = this.getElement(`${this.COMPANY_FILTER_TAB}-${companyFilterTab.id}`);
			if ( companyFilterTabCompanies.length === 0 ){
				companyFilterTabSpan.parentElement.classList.add(CSS.AON_COMPANY_FILTER_EMPTY);
			}
			else {
				companyFilterTabSpan.parentElement.classList.remove(CSS.AON_COMPANY_FILTER_EMPTY);
				companyFilterTabSpan.innerHTML = `${companyFilterTab.name} (${companyFilterTabCompanies.length})`;
			}
		}

	}

	selectTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.add(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}
	
	clearSelectedTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.remove(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}
	
	companyFilter(company, filter) {
		if(!filter) {
			filter = {
				active: true
			};
		}

		let value = true;

		if(filter){
			
			if(filter.value) {
				const document = company?.document?.toUpperCase().includes(filter.value.toUpperCase());
				const name = company?.name?.toUpperCase().includes(filter.value.toUpperCase());
				value &= document || name;
			}
	
			if(filter.active) {
				value &= company.active && (company.parentId || company.type !== 'CONSULTANCY');
			}
	
			if(filter.inactive) {
				value &= !company.active;
			}
	
			if(filter.shared) {
				value &= company.shared;
			}
	
			if(filter.entorno) {
				value &= !company.parentId && company.type === 'CONSULTANCY';
			}
	
			if(filter.despacho) {
				value &= company.type === 'OFFICE';
			}
	
			if(filter.ids) {
				let found = filter.ids.find(id => company.id == id ) ;
				console.log( found );
				value &= found !== undefined;
			}
		}

		return value;
	}

	getNotices(){
		getUserNotice().then(notice =>{
			this.notice = notice;
			this.updateCount();
		});
	}

	updateCount(){
		let inboxCount = 0;
		let rejectedCount = 0;
		let pendingCount = 0;
		let application = this.getApplication();
		
		if(this.notice?.invoice?.inbox?.count > 0) 
			inboxCount = this.notice.invoice.inbox.count;

		if(this.notice?.invoice?.rejected?.count > 0) 
			rejectedCount = this.notice.invoice.rejected.count;

		if(this.notice?.invoice?.pending?.count > 0) 
			pendingCount = this.notice.invoice.pending.count;

		application.updateSidenavCount(this.INBOX_INVOICES, inboxCount);
		application.updateSidenavCount(this.REJECTED_INVOICES, rejectedCount);
		application.updateSidenavCount(this.PENDING_INVOICES, pendingCount);
	}
	
	build() {

		let parentDiv = this.createDiv();
		parentDiv.className = CSS.AON_PARENT_DIV;
		this.getApplication().setContent(parentDiv);
		

		let welcomeDiv = this.createDiv();
		welcomeDiv.className = CSS.AON_WELCOME_DIV;
		let welcomeSpan = this.createSpan();
		welcomeSpan.innerHTML = MSG.WELCOME_TO_AON_SOLUTIONS;
		welcomeSpan.style.fontSize = '24px';
		welcomeSpan.style.fontWeight = '600';
		welcomeDiv.appendChild(welcomeSpan);
		
		// Companies
		let companyDiv = this.createDiv();
		companyDiv.className = CSS.AON_COMPANY_DIV;
	
		let companyTitleDiv = this.createDiv();
		companyTitleDiv.className = CSS.AON_COMPANY_TITLE_DIV;
	
		let userOption = new AonDialogMenu();
		userOption.id = 'aonCompanyDialogOption';
		this.appendChild(userOption);
	
		let companyTitleSpan = this.createSpan();
		companyTitleSpan.innerHTML = MSG.COMPANY_SELECTION;
		companyTitleSpan.classList.add("aonCompanyTitleSpan");
		companyTitleDiv.appendChild(companyTitleSpan);
	
		let companyFilterTabDiv = this.createDiv();
		companyFilterTabDiv.id = this.COMPANY_FILTER_TAB;
		companyFilterTabDiv.className = CSS.AON_TAB;
	
		let defaultFilter = { 
			type: undefined, 
			active: undefined, 
			inactive: undefined, 
			shared: undefined, 
			entorno: undefined, 
			despacho: undefined, 
			domainActive: undefined, 
		};
		
		let filterOptions = [
			{
				id: 'active',
				name: MSG.ACTIVES,
				icon: 'domain',
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{id:'active', active: true, domainActive:true, name: MSG.ACTIVES}})
			}, {
				id: 'inactive',
				name: MSG.INACTIVES,
				icon: 'domain_disabled',
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{id: 'inactive', inactive: true, domainActive:false, name: MSG.INACTIVES}})
			}, {
				id: 'shared',
				name: MSG.SHARED,
				icon: MATERIAL_ICONS.SHARE,
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{id:'shared', shared: true, name: MSG.SHARED}})
			}, {
				id: 'consultancy',
				name: MSG.ENVIRONMENT,
				icon: MATERIAL_ICONS.APARTMENT,
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{id:'consultancy', entorno:true, type:"CONSULTANCY", name: MSG.ENVIRONMENT}})
			},{
				id: 'office',
				name: MSG.OFFICE,
				icon: 'work',
				fn: () => this.select({ ...this.getFilter(), ...defaultFilter, ...{id: 'office', despacho:true, type:"OFFICE", name: MSG.OFFICE}})
			}
		];	
	
		for( let filterOption of filterOptions ){
			let companyFilterTabA = this.createElement(TAG.A);
			companyFilterTabA.className = CSS.AON_TAB_ITEM;
			companyFilterTabA.addEventListener(EVENT.CLICK, (ev) => {
				filterOption.fn(ev)
			});
	
			let companyFilterTabSpan = this.createElement(TAG.SPAN);
			companyFilterTabSpan.innerHTML = filterOption.name; 
			companyFilterTabSpan.className = CSS.AON_TAB_ITEM_TEXT;
			companyFilterTabSpan.id = `${this.COMPANY_FILTER_TAB}-${filterOption.id}`; 
			companyFilterTabA.appendChild(companyFilterTabSpan);
	
			companyFilterTabDiv.appendChild(companyFilterTabA);	
		}
	
		companyDiv.appendChild(companyTitleDiv);
		companyDiv.appendChild(companyFilterTabDiv);
	
		let ul = this.createElement(TAG.UL);
		ul.id = "UlCompanies";
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.NO_SCROLLBAR);
		ul.style.overflowY = 'auto';		
		ul.style.width = "100%";
	
		companyDiv.appendChild(ul);
		ul.addEventListener("scroll", () => {
			let scrollTop = ul.scrollTop;					
			let offsetHeight = ul.offsetHeight; 					
			let scrollHeight = ul.scrollHeight;	
			
			if ( ( scrollTop +  offsetHeight ) >= ( 0.75 * scrollHeight) ) {
				this.loadMore();
			}
		});

		let contentDiv = this.createDiv();
		contentDiv.appendChild(companyDiv);
	
		parentDiv.appendChild(welcomeDiv);
		parentDiv.appendChild(contentDiv);		
	
		const interval = setInterval(() => {
			let totalBottom = this.getTotalBottom(ul);
			let totalOffsetTop = this.getTotalOffsetTop(ul);
			if ( totalOffsetTop  ) {
				clearInterval(interval);
				ul.style.maxHeight = `calc(100vh - ${totalOffsetTop + totalBottom }px)`; 
			}
		}, 100);
	
		const appsInterval = setInterval(() => {
			let apps = this.getElement("applications");
			if (apps) {
				clearInterval(appsInterval); 
				apps.addEventListener(EVENT.CLICK, () => {
					let companyy = this.getElement("aonHeaderCompanyListButton");
					let companyyy = this.getElement("aonHeaderCompanyList");
					companyy.style.display = "block";
					companyyy.style.display = "block";
					let logo = this.getElement("aonLogo");
					logo.addEventListener("click", function handleClick() {
						companyy.style.display = "none";
						companyyy.style.display = "none";
						logo.removeEventListener("click", handleClick); 
					});
				});
			}
		}, 100); 
		if(!LS.isOnlyOne())
			LS.setCompanySelected(false);
		else 
			LS.setCompanySelected(true);
		}
	

		loadMore() {
		//TODO: this.getApplication().startLoader();
		getCompanies().then( companies => {
			let first = this.page * 30;
			this.page = this.page + 1;
			this.buildCompanies(this.filterCompanies(companies, this.filter).slice(first, first + 30), this.filter);	
		})
		.finally(()=>{
			//TODO: this.getApplication().stopLoader();
		});
	
		// Customers
		
	}

	buildSidenav() {

		let enterprisesOptions = {
		  id: CONSTANT.ENTERPRISES.initCap(),
		  name: MSG.ENTERPRISES.toUpperCase(),
		  app: ClassicApps.AON_SOLUTIONS,
		  options: [{
				id: this.ENTERPRISES,
				name: MSG.ALL2,
				icon: MATERIAL_ICONS.BUSINESS,
				app: ClassicApps.AON_SOLUTIONS,
				fn: () => {
					let enterprisesFilter = {ids:undefined, count:undefined};
					this.select({...this.getFilter(),...enterprisesFilter }, companies => this.decorateTabs(companies, enterprisesFilter));
				},
		  	}]
		};

		this.getApplication().addSidenavOptions3(enterprisesOptions);

		let invoiceOptions = {
		  id: CONSTANT.INVOICES.initCap(),
		  name: MSG.ACTIVITY.toUpperCase(),
		  app: Apps.INVOICE,
		  options: [{
			    id: this.INBOX_INVOICES,
			    name: MSG.PENDING_INVOICES,
			    icon: MATERIAL_ICONS.INBOX,
			    app: Apps.INVOICE,
			    fn: () => {
					let inboxFilter = {ids:this.notice?.invoice?.inbox?.domains, count:this.notice?.invoice?.inbox?.domainCount};
					this.select({...this.getFilter(),...inboxFilter }, companies => this.decorateTabs(companies, inboxFilter));
			    },
			  },
			  {
			    id: this.REJECTED_INVOICES,
			    name: MSG.REJECTED_INVOICES,
			    icon: MATERIAL_ICONS.REPORT,
				app: Apps.INVOICE,
			    fn: () => {
					let rejectedFilter = {ids:this.notice?.invoice?.rejected?.domains, count:this.notice?.invoice?.rejected?.domainCount};
					this.select({...this.getFilter(),...rejectedFilter}, companies => this.decorateTabs(companies, rejectedFilter ));
			    },
			  },
			  {
			    id: this.PENDING_INVOICES,
			    name: MSG.UNACCOUNT_INVOICES,
			    icon: MATERIAL_ICONS.LABEL_IMPORTANT,
				app: Apps.INVOICE,
			    fn: () => {
					let pendingFilter = {ids:this.notice?.invoice?.pending?.domains, count:this.notice?.invoice?.pending?.domainCount}; 
					this.select({...this.getFilter(),...pendingFilter}, companies => this.decorateTabs(companies, pendingFilter));
			    },
			  }]
		};
		
		this.getApplication().addSidenavOptions3(invoiceOptions);
		
		let helpOptions = {
		  id: CONSTANT.HELP.initCap(),
		  name: MSG.HELP.toUpperCase(),
		  app: Apps.HOME,
		  options: [{
			    id: CONSTANT.HELP.initCap() + "Notifications",
			    name: MSG.NOTIFICATIONS,
			    icon: MATERIAL_ICONS.RSS_FEED,
				app: Apps.HOME,
			    fn: () => {
			      // Filter selectOption method
			    }
			},{
  			    id: CONSTANT.HELP.initCap() + "ContentIndex",
  			    name: MSG.CONTENT_INDEX,
  			    icon: MATERIAL_ICONS.SCHOOL,
  				app: Apps.HOME,
  			    fn: () => {
  			      // Filter selectOption method
  			    },
			},
		  ]
		};
		
		this.getApplication().addSidenavOptions3(helpOptions);

		let appsDiv = this.createDiv();
		appsDiv.id = this.APPS_DIV;
		this.getApplication().getSidenav().appendChild(appsDiv);

		
		getTimeControl().then(r => {
			let option = {
				id: "signing",
				title: MSG.SIGNING.toUpperCase(),
				name: MSG.SIGNING.toUpperCase(),
				app: Apps.TIMECONTROL
			}

			let aonSign = new AonSign();
			this.getApplication().addSidenavWidget2(option, aonSign);

			aonSign.buildSignin(r);
			let aonHeader = this.getElement('aonHeader');
			aonHeader?.timeControlStatus(r);
		});
		
		this.getNotices();
		
	}

	cleanCompanies(){
		let ul = this.getElement("UlCompanies");
		while ( ul.firstChild ) {
			ul.removeChild(ul.lastChild);
		}
	}

	buildCompanies(companies, filter){
		let ul = this.getElement("UlCompanies");
		for(let company of companies) {
			ul.appendChild(this.buildLi(company, 'transparent', filter?.count?.[company.domain] ));
		}
	}

	buildLi(company, color, count) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonLiBeta' ;
		li.addEventListener(EVENT.CLICK, () => {
			this.companySelection(company, false);
			let portal = LS.isLeftMenu();
			LS.setPortalChecked(portal);
		});

		let companySpan = this.createElement(TAG.SPAN);
		companySpan.className = 'aonLiSpan';

		let icon = this.getIcon(company);

		let iconI = this.createElement(TAG.I);
		iconI.className = 'material-icons aonAvatar';
		iconI.innerHTML = icon;

		let nameSpan = this.createElement(TAG.SPAN);
		nameSpan.innerHTML = company.name;

		let docSpan = this.createElement(TAG.SPAN);
		docSpan.className = 'aonLiSpanSubtitle';
		docSpan.innerHTML = company.document;

		companySpan.appendChild(iconI);
		companySpan.appendChild(nameSpan);
		companySpan.appendChild(docSpan);
		li.appendChild(companySpan);

		let countSpan = this.createElement(TAG.SPAN);
		countSpan.className = 'aonLiSpanSubtitle';
		countSpan.innerHTML = count || '';
		li.appendChild(countSpan);

		let sp = this.createElement(TAG.SPAN);
		let i2 = this.createElement(TAG.I);
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = MATERIAL_ICONS.KEYBOARD_ARROW_RIGHT;
		sp.appendChild(i2);
		li.appendChild(sp);
		return li;
	}
	
	getIcon(company) {
		let icon = "business";
		if(company.type === 'OFFICE') icon = 'work';
		else if(company.parent) icon = MATERIAL_ICONS.APARTMENT;
		else if(company.shared) icon = MATERIAL_ICONS.SHARE;
		else if(!company.active) icon = 'domain_disabled';
		
		return icon;
		
	}

	companySelection(company, onlyOne) {
		const BASE_ID = 'aonHeader';
		localStorage.setItem('company', JSON.stringify(company));
		LS.setDomainId(company.id);
		LS.setDomainName(company.domain);
		LS.setDomainLogin(company.login);
		//localStorage.setItem("aon_domain_id", company.id);
		//localStorage.setItem("aon_domain_name", company.domain);
		localStorage.setItem("aon_domain_document", company.document);
		localStorage.setItem("onlyOne", onlyOne);
		
		

		if(!LS.isNewTheme() && (company.parentId || company.type !== 'CONSULTANCY')){
			let aonShowMenu = this.getElement('aonShowMenu');
			aonShowMenu.style.display = 'block';
		}
		let aonHeaderHelp = this.getElement(BASE_ID + 'Help');
		// aonHeaderHelp.style.display = 'block';

		// let aonHeaderSearch = this.getElement(BASE_ID + 'Search');
		// aonHeaderSearch.style.display = 'none';

		let aonHeaderHome = this.getElement(BASE_ID + 'Home');
		if(!LS.isNewTheme()) {
			aonHeaderHome.style.display = 'block';
		}

		let aonHeaderCompanyName = this.getElement(BASE_ID + 'CompanyName');
		aonHeaderCompanyName.innerHTML = company.name;

		let aonHeaderCompany = this.getElement(BASE_ID + 'Company');
		aonHeaderCompany.style.display = 'block';

		if(!onlyOne){ 
			LS.setCompanySelected(true);
			let aonHeaderCompanyList = this.getElement(BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'block';
			let aonHeaderCompanyListButton = this.getElement(BASE_ID + 'CompanyListButton');
			aonHeaderCompanyListButton.style.display = 'block';
		} else {
			aonHeaderHome.style.right = '140px';
			aonHeaderCompany.style.right = '180px';
		}
		
		//this.clearElementById('aonMenu');
		let aonMenu = this.getElement('aonMenu');
		aonMenu.init().then(() => aonMenu.open());

		getUser().then(user => {
			localStorage.setItem('aon_domain_login', user.login);

			let aonDesktop = new AonDesktop();
			aonDesktop.id = "aonDesktop";

			this.rootPanel(aonDesktop);
		});
	}


	getCompaniesSchemas(){
		getCompaniesBySchemas(this.getFilter())
		.then(console.log);

		return "Consultando...."
	}
	
	getTotalOffsetTop(element) {
		let totalOffsetTop = 0;
		for ( let el = element; el; el = el.offsetParent ) {
			totalOffsetTop += el.offsetTop;
		}
		return totalOffsetTop;
	}

	getTotalBottom(element) {
		let totalBottom = 0;
		for ( let el = element; el; el = el.parentElement ) {
			let style = getComputedStyle(el);
			totalBottom += parseFloat(style.paddingBottom) ;
			totalBottom += parseFloat(style.marginBottom);
		}
		return totalBottom;
	}
	
	filterCompanies(companies, filter) {
		let filteredCompanies = companies.filter(f => this.companyFilter(f, filter));
		return filteredCompanies.sort( (c1,c2) => ( filter.count?.[c2.domain] || 0 )  -  ( filter.count?.[c1.domain] || 1 ) );
	}
}


if(!window.customElements.get(TAG.AON_PARENT)){
	console.log( 'Define <aon-new-parent> ^-^' );
	window.customElements.define(TAG.AON_PARENT, AonParent);
}
