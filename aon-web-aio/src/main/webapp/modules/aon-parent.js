import {AonElement} from '../components/AonElement.js';
import {closeSession, getCompanies, getUserNotice, getCompaniesBySchemas, getTimeControl} from  '../services/service.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG, CONSTANT } from '../environments/environments.js';
import { AonApplication } from '../components/aon-application.js';
import * as LS from '../services/localStorageService.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { ClassicApps, Apps } from '../services/app.js';
import { AonSign } from "../modules/timecontrol/aon-sign.js";
import * as JSF from './aon-jsf-app.js';

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
	COMPANY_TITLE_SPAN;
	TRAMIT_INVOICES;
	
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
		this.COMPANY_TITLE_SPAN = "aonCompanySpanTitle";
		this.ENTERPRISES = `${CONSTANT.ENTERPRISES.initCap()}All`;
		this.INBOX_INVOICES = `${CONSTANT.INVOICES.initCap()}Inbox`;
		this.PENDING_INVOICES = `${CONSTANT.INVOICES.initCap()}Pending`;
		this.REJECTED_INVOICES = `${CONSTANT.INVOICES.initCap()}Rejected`;
		this.TRAMIT_INVOICES = `${CONSTANT.INVOICES.initCap()}Tramit`;
		this.filter = {	id: 'active', active: true, name: MSG.ACTIVES,};
	}

	connectedCallback () {
		this.init({id:'active', active: true, domainActive:true});
	}

	init(filter) {
		this.domainId = parseInt(localStorage.getItem('aon_domain_id'));
		//TODO: aonParent.startLoader();
		let aonApplication = new AonApplication();
		aonApplication.setAttribute("sidenav_width", "300px");
		this.createApplication(this.PARENT, "", aonApplication);

		
		if(LS.isOnlyOne()) {			
			this.build();
			this.buildSidenav();
		} else {
			let loader = this.getElement("aonParentLoader");
			loader.start();
		}
		
		this.select(filter, companies => {
			// Filtrar empresas excluyendo la actual (la que coincide con isLocationCompany)
			let finalCompanies = companies.filter(company => !this.isLocationCompany(company));
			// total de la lista filtrada
			this.decorateTabs(finalCompanies);
			this.getApplication().updateSidenavCount(this.ENTERPRISES, finalCompanies.length || 0);
			this.getApplication().updateSidenavTitle(CONSTANT.ENTERPRISES, `${MSG.ENTERPRISES}`);
		});
	}

	select(filter, callback) {
		this.clearSelectedTab(this.filter);
		//TODO: aonParent.startLoader();
		let limit = 100;
		return new Promise((resolve, reject) => {
			getCompanies({limit}).then( companies => {
				let cps = companies.filter(r => {
					return r.id === parseInt(LS.getDomainId());
				});
				if(cps.length > 0 && !cps[0].parent) {
					this.companySelection(cps[0], companies.length === 1 );
				} else if ( LS.getCompany() && !LS.getCompany().domainManagement ) {
					this.companySelection(LS.getCompany(), companies.length === 1 );
				} else if(companies.length === 1) {
					this.companySelection(companies[0], true);
				} else {
					if ( LS.getCompany() ) {
						this.getAonHeader().showCompanyOption(LS.getCompany(), false);
					}
					let aonMenu = this.getElement('aonMenu');
					aonMenu.init().then(() => {
						LS.setDomainLogin(aonMenu.getDur().getUser().login);
						LS.setDomainId(aonMenu.getDur().getDomain().getId());
						LS.setDomainName(aonMenu.getDur().getDomain().getName());
						aonMenu.open();
					}).catch((err) => {
						reject(err);
					});

					this.selectTab(filter);
					//TODO: aonParent.stopLoader();
					this.page = 1;
					this.cleanCompanies();
					
	  				let filteredCompanies = this.filterCompanies(companies, filter);
					this.buildCompanies(filteredCompanies.slice(0, 30), filter);
					
					if ( companies.length == limit ){
						getCompanies().then(companies => {
							this.cleanCompanies();
							let filteredCompanies = this.filterCompanies(companies, filter);
							this.buildCompanies(filteredCompanies.slice(0, 30), filter);
							resolve(companies);
							callback?.(companies);
						});
					} else {
						resolve(companies);
						callback?.(companies);
					}
				}	
			}, () => closeSession());
	
			if(filter){
				this.setFilter(filter);
			}
		});
	}
	
	decorateTabs(companies, filter = {}) {
		let companyFilterTabs = [{
				id: 'active',
				active: true,
				name: MSG.ACTIVES,
			}, {
				id: 'office',
				despacho:true,
				name: MSG.OFFICE
			}, {
				id: 'consultancy',
				entorno:true,
				name: MSG.ENVIRONMENT
			}, {
				id: 'shared',
				shared: true,
				name: MSG.SHARED
			}, {
				id: 'inactive',
				inactive: true,
				name: MSG.INACTIVES
			}
		];
		let companyTitleSpan = this.getElement(this.COMPANY_TITLE_SPAN);
		companyTitleSpan.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
		let tabCompanies = {};
		for( let companyFilterTab of companyFilterTabs ){
			let companyFilterTabCompanies = companies.filter(f => !this.isLocationCompany(f) && this.companyFilter(f, { ...companyFilterTab, ...filter }));
			let companyFilterTabSpan = this.getElement(`${this.COMPANY_FILTER_TAB}-${companyFilterTab.id}`);
			if ( companyFilterTabCompanies.length === 0 ){
				companyFilterTabSpan.parentElement.classList.add(CSS.AON_COMPANY_FILTER_EMPTY);
			}
			else {
				tabCompanies[companyFilterTab.id] = companyFilterTab;
				companyFilterTabSpan.parentElement.classList.remove(CSS.AON_COMPANY_FILTER_EMPTY);
				companyFilterTabSpan.innerHTML = `${companyFilterTab.name} (${companyFilterTabCompanies.length})`;
			}
		}
		
		if ( tabCompanies[this.filter.id ]  )
			return;

		for ( let id in tabCompanies ) {
			this.select( tabCompanies[id] );
			return;
		}
	}

	selectTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.add(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}
	
	clearSelectedTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.remove(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}
	companyFilter(company, filter) {
		if (!filter) {
			filter = { active: true };
		}

		if (company.id === this.domainId && company.domainManagement || company.id === 1) {
			return false;
		}

		let value = true;

		if (filter.value) {
		const document = company?.document?.toUpperCase().includes(filter.value.toUpperCase());
		const name = company?.name?.toUpperCase().includes(filter.value.toUpperCase());
		value &&= document || name;
		}

		if (filter.active !== undefined) {
		value &&= company.active === filter.active;
		}

		if (filter.inactive !== undefined) {
		value &&= !company.active === filter.inactive;
		}

		if (filter.shared !== undefined) {
		value &&= company.shared === filter.shared;
		}

		if (filter.entorno !== undefined) {
		value &&= company.domainManagement === filter.entorno;
		}

		if (filter.despacho !== undefined) {
		value &&= (filter.despacho ? company.type === 'OFFICE' : company.type !== 'OFFICE');
		}

		if ('ids' in filter && Array.isArray(filter.ids)) {
			let found = filter.ids.find(id => company.id == id);
			value &&= found !== undefined;
		}
		return value;
	}

	getNotices(application){
		getUserNotice().then(notice =>{
			this.notice = notice;
			this.updateCount(application);
		}).catch( err => {
			this.notice = undefined;
			this.updateCount(application);
		});
	}

	updateCount(application){
		let inboxCount = 0;
		let rejectedCount = 0;
		let pendingCount = 0;
		let processedCount = 0;
		let processingCount = 0;
		
		if(this.notice?.invoice?.inbox?.count > 0) 
			inboxCount = this.notice.invoice.inbox.count;

		if(this.notice?.invoice?.rejected?.count > 0) 
			rejectedCount = this.notice.invoice.rejected.count;

		if(this.notice?.invoice?.pending?.count > 0) 
			pendingCount = this.notice.invoice.pending.count;

		if(this.notice?.invoice?.processed?.count > 0) 
			processedCount = this.notice.invoice.processed.count;

		if(this.notice?.invoice?.processing?.count >0)
			processingCount = this.notice.invoice.processing.count;

		application.updateSidenavCount(this.INBOX_INVOICES, inboxCount);
		application.updateSidenavCount(this.REJECTED_INVOICES, rejectedCount);
		application.updateSidenavCount(this.PENDING_INVOICES, pendingCount);
		application.updateSidenavCount(this.TRAMIT_INVOICES, processedCount + processingCount);
		application.updateSidenavTitle(CONSTANT.INVOICES, `${MSG.ACTIVITY}`);
	}
	
	build() {
		let parentDiv = this.createDiv();
		parentDiv.className = CSS.AON_PARENT_DIV;
		this.getApplication().setContent(parentDiv);

		let welcomeDiv = this.createDiv();
		welcomeDiv.className = CSS.AON_WELCOME_DIV;

		let welcomeSpan = this.createSpan();
		this.getWelcomeMessage().then( msg => welcomeSpan.innerHTML = msg ); 
		welcomeSpan.classList.add(CSS.AON_WELCOME_MESSAGE);
		welcomeDiv.appendChild(welcomeSpan);
		
				
		let welcomeImg = this.createElement(TAG.IMG);
		welcomeImg.onerror = () => 	welcomeImg.style.display = 'none'; // Hide image if it fails to load
		// welcomeImg.onload = () => this.getApplication().openRightSidenav() ;// Show image if it loads successfully 
		this.getWelcomeImage().then( img => welcomeImg.src = img );
		this.getWelcomeMessage().then( msg  => welcomeImg.title = msg );
		welcomeImg.classList.add(CSS.AON_WELCOME_LOGO);
		this.getApplication().getRightSidenav().appendChild(welcomeImg);		
		// Companies
		let companyDiv = this.createDiv();
		companyDiv.className = CSS.AON_COMPANY_DIV;
	
		let companyTitleDiv = this.createDiv();
		companyTitleDiv.className = CSS.AON_COMPANY_TITLE_DIV;
	
		let userOption = new AonDialogMenu();
		userOption.id = 'aonCompanyDialogOption';
		this.appendChild(userOption);
	
		let companyTitleSpan = this.createSpan();
		companyTitleSpan.id = this.COMPANY_TITLE_SPAN;
		companyTitleSpan.innerHTML = MSG.COMPANY_SELECTION;
		companyTitleSpan.classList.add("aonCompanyTitleSpan");
		companyTitleSpan.classList.add(CSS.AON_COMPANY_FILTER_LOADING);
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
			domainActive: undefined
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
	}

	buildSidenav() {
		const application = this.getApplication();
		let enterprisesOptions = {
		  id: CONSTANT.ENTERPRISES,
		  app: ClassicApps.AON_SOLUTIONS,
		  name: `<span class="${CSS.AON_COMPANY_FILTER_LOADING}">${MSG.ENTERPRISES.toUpperCase()}</span>`,
		  options: [{
				id: this.ENTERPRISES,
				name: MSG.ALL2,
				app: ClassicApps.AON_SOLUTIONS,
				fn: () => {
					let enterprisesFilter = {ids:undefined, count:undefined};
					this.select({...this.getFilter(),...enterprisesFilter }, companies => this.decorateTabs(companies, enterprisesFilter));
				}
		  	}]
		};
		application.addSidenavOptions3(enterprisesOptions);

		let invoiceOptions = {
			id: CONSTANT.INVOICES,
			app: Apps.INVOICE,
			name: `<span class="${CSS.AON_COMPANY_FILTER_LOADING}">${MSG.ACTIVITY.toUpperCase()}</span>`,
			options: [
				{
					id: this.TRAMIT_INVOICES,
					name: MSG.DOCUMENTS_IN_PROCESS,
					app: Apps.INVOICE,
					fn: () => {
						let processedDomains = this.notice?.invoice?.processed?.domains || [];
						let processingDomains = this.notice?.invoice?.processing?.domains || [];
						let domains = processedDomains.concat(processingDomains);
						domains = [...new Set(domains)];
						
						let domainCount = {};
						let processedCount = this.notice?.invoice?.processed?.domainCount || [];
						let processingCount = this.notice?.invoice?.processing?.domainCount || [];
						for (var key in processedCount){
							domainCount[key] = domainCount[key] ? domainCount[key] + processedCount[key] : processedCount[key];
						}

						for (var key in processingCount){
							domainCount[key] = domainCount[key] ? domainCount[key] + processingCount[key] : processingCount[key];
						}

						let tramitFilter = { ids: domains, count: domainCount };
						this.select({...this.getFilter(), ...tramitFilter}, companies => this.decorateTabs(companies, tramitFilter));																  
					}
				},
				{
					id: this.REJECTED_INVOICES,
					name: MSG.DOCUMENTS_UNDER_REVIEW,
					app: Apps.INVOICE,
					fn: () => {
						let reviewFilter = { ids: this.notice?.invoice?.rejected?.domains, count: this.notice?.invoice?.rejected?.domainCount };
						this.select({...this.getFilter(), ...reviewFilter}, companies => this.decorateTabs(companies, reviewFilter));
					}
				},
				{
					id: this.PENDING_INVOICES,
					name: MSG.UNACCOUNT_INVOICES,
					app: Apps.INVOICE,
					fn: () => {
						let unaccountedFilter = { ids: this.notice?.invoice?.pending?.domains, count: this.notice?.invoice?.pending?.domainCount };
						this.select({...this.getFilter(), ...unaccountedFilter}, companies => this.decorateTabs(companies, unaccountedFilter));
					}
				},
				{
					id: this.INBOX_INVOICES,
					name: MSG.DRAFTS+"/"+MSG.PROFORMA,
					app: Apps.INVOICE,
					fn: () => {
						let draftsFilter = { ids: this.notice?.invoice?.inbox?.domains, count: this.notice?.invoice?.inbox?.domainCount };
						this.select({...this.getFilter(), ...draftsFilter}, companies => this.decorateTabs(companies, draftsFilter));
					}
				}
			]
		};

		application.addSidenavOptions3(invoiceOptions);
		
		let helpOptions = {
		  id: CONSTANT.HELP,
		  name: MSG.HELP.toUpperCase(),
		  app: Apps.HOME,
		  options: [{
			    id: CONSTANT.HELP.initCap() + "Notifications",
			    name: MSG.NOTIFICATIONS,
				app: Apps.HOME,
			    fn: () => this.rootPanel(new JSF.AonJsfHelpNotification())
			},{
  			    id: CONSTANT.HELP.initCap() + "ContentIndex",
  			    name: MSG.CONTENT_INDEX,
  				app: Apps.HOME,
  			    fn: () => this.rootPanel(new JSF.AonJsfHelpContent())
			}
		  ]
		};
		
		application.addSidenavOptions3(helpOptions);

		let appsDiv = this.createDiv();
		appsDiv.id = this.APPS_DIV;
		application.getSidenav().appendChild(appsDiv);

		getTimeControl().then(r => {
			let aonSign = new AonSign();
			application.addSidenavWidgetComponet(aonSign);

			aonSign.buildSignin(r);
			let aonHeader = this.getElement('aonHeader');
			aonHeader?.timeControlStatus(r);
		});
		
		this.getNotices(application);
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
		docSpan.className = 'aonLiSpanSubtitle'  ;
		docSpan.innerHTML = company.document || `<span class='${CSS.AON_INPUT_BOX_LABEL_SPAN_WARNING}' >Por favor, introduzca un CIF/NIF/Documento v�lido.</span>`;

		companySpan.appendChild(iconI);
		companySpan.appendChild(nameSpan);
		companySpan.appendChild(docSpan);
		li.appendChild(companySpan);

		let countSpan = this.createElement(TAG.SPAN);
		countSpan.className = 'aonLiSpanSubtitle';
		countSpan.style.fontWeight = "bold";
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
	
	getAonHeader() {
		return document.querySelector(TAG.AON_HEADER);
	}
	
	getIcon(company) {
		return this.getAonHeader()?.getIcon(company);
	}
	
	companySelection(company, onlyOne) {
		this.getAonHeader()?.companySelection(company, onlyOne);
	}

	getCompaniesSchemas(){
		getCompaniesBySchemas(this.getFilter()).then(console.log);
		return "Consultando....";
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
	
	isSharedCompany( company ) {
		return !company?.parentId || company?.parentId != LS.getCompany()?.id;
	}

	isLocationCompany( company ) {
		return company?.domain?.toUpperCase() == window?.location?.hostname?.toUpperCase()
			|| company?.domain?.toUpperCase() == LS.getCompany()?.domain?.toUpperCase();
	}
	
	getWelcomeImage() {
		return new Promise((resolve, reject) => {
        resolve(`${window.location.protocol}//${LS.getCompany()?.domain || window.location.hostname}:${window.location.port}/aonDocuments/company.logo`);
		});
    }

	async getWelcomeMessage() {
		const company = LS.getCompany();
		const dur = this.getDur();
		const isConsultancy = company?.type === 'CONSULTANCY';

		const buildSpan = (textLight, textBold) =>
			`<span style='font-weight:lighter;'>${textLight}</span> <span style='font-weight:bolder;'>${textBold}</span>`;

		try {
			if (isConsultancy) {
				const durBuilt = await this.buildDur();
				return buildSpan(MSG.ENVIRONMENT, durBuilt.domain.description);
			}

			if (company?.name) {
				return buildSpan(MSG.ENVIRONMENT, company.name);
			}

			if (dur) {
				return buildSpan(MSG.ENVIRONMENT, dur.domain.description);
			}

			const durBuilt = await this.buildDur();
				return buildSpan(MSG.ENVIRONMENT, durBuilt.domain.description);

		} catch (err) {
			return `<span style='font-weight:bolder;'>${MSG.WELCOME_TO_AON_SOLUTIONS}</span>`;
		}
	}
}

if(!window.customElements.get(TAG.AON_PARENT)){
    if(!localStorage.getItem('sass') === 'true')
      console.log( 'Define <aon-new-parent> ^-^' );
	window.customElements.define(TAG.AON_PARENT, AonParent);
}