import {AonElement} from 'aonsolutions/components/AonElement.js';
import {closeSession, getCompanies, getUserNotice, getUser, getCompaniesBySchemas} from  'aonsolutions/services/service.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from 'aonsolutions/environments/environments.js';
import { AonDesktop } from 'aonsolutions/modules/company/aon-desktop.js';
import * as LS from 'aonsolutions/services/localStorageService.js';
import { AonDialogMenu } from 'aonsolutions/components/aon-dialog-menu.js';

export class AonParent extends AonElement {


	notice;
	filter;
	_filter; // NEW FILTER
	selected;
	companies;

	more;
	
	COMPANY_FILTER_TAB;

	setFilter(filter){
		if(filter.ids?.length) { 
			filter.ids = filter.ids.join(",");
		}
		this._filter = filter;
	}

	getFilter(){
		return this._filter;
	}

	addFilter(filter){
		this._filter = {...this.getFilter(), ...filter};
	}

	constructor () {
		super();
		this.id = 'aonParent';
		this.filter = {};
		this._filter = {};
		this.COMPANY_FILTER_TAB = "aonCompanyTabFilter";
	}

	connectedCallback () {
		this.init({id:'active', active: true, domainActive:true});
		let searchBox = this.getElement('aonHeaderSearchBox');
		searchBox.addEventListener(EVENT.KEYUP, () => {
			this.select({value:searchBox.value});
			this.addFilter({value:searchBox.value});
		});
	}

	init(filter) {
		//TODO: aonParent.startLoader();
		this.build();
		this.select(filter, companies => this.decorateTabs(companies) );
	}

	select(filter, callback) {
		this.filter = filter;
		
		this.clearSelectedTab(this._filter);		
		//TODO: aonParent.startLoader();
		getCompanies()
		.then( companies => {
			if ( LS.getCompany() ) {
				this.companySelection(LS.getCompany(), companies.length == 1 );
			}else if(companies.length === 1){
				this.companySelection(companies[0], true);
			} else {
				this.getElement("aonMenu").close();
				this.selectTab(filter);		
				//TODO: aonParent.stopLoader();
				this.page = 1;
				this.cleanCompanies();
				this.buildCompanies(companies.filter(f => this.companyFilter(f, filter)).slice(0, 30));
				callback?.(companies);
			}	
		}, () => closeSession());

		if(filter){
			this.setFilter(filter);
		}
	}
	
	decorateTabs(companies) {
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
			}/*, {
				id: 'consultancy',
				entorno:true,
				name: MSG.ENVIRONMENT
			},{
				id: 'office',
				despacho:true,
				name: MSG.OFFICE
			}*/
		];	
		for( let companyFilterTab of companyFilterTabs ){
			let companyFilterTabCompanies = companies.filter(f => this.companyFilter(f, companyFilterTab));
			let companyFilterTabSpan = this.getElement(`${this.COMPANY_FILTER_TAB}-${companyFilterTab.id}`);
			companyFilterTabSpan.innerHTML = `${companyFilterTab.name} (${companyFilterTabCompanies.length})`;
		}

	}

	selectTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.add(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}

	clearSelectedTab(filter) {
		this.getElement(`${this.COMPANY_FILTER_TAB}-${filter?.id}`)?.classList.remove(CSS.AON_TAB_ITEM_TEXT_SELECTED);
	}
	
	companyFilter(f, q) {
		if(!q) {
			q = {
				active: true
			};
		}

		let value = true;

		if(q){
			
			if(q.value) {
				const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
				const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
				value = document || name;
			}
	
			if(q.active) {
				value = f.active && (f.parentId || f.type !== 'CONSULTANCY');
			}
	
			if(q.inactive) {
				value = !f.active;
			}
	
			if(q.shared) {
				value = f.shared;
			}
	
			if(q.entorno) {
				value = !f.parentId && f.type === 'CONSULTANCY';
			}
	
			if(q.despacho) {
				value = f.type === 'OFFICE';
			}
	
			if(q.ids) {
				let idFilter;
				q.ids.split(',').forEach((item, i) => {
					idFilter = f.id == item || idFilter;
				});
				value = idFilter;
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
		let application = this.getApplication();
		let inboxCount = 0;
		let rejectedCount = 0;
		
		if(this.notice.invoice && this.notice.invoice.inbox && this.notice.invoice.inbox.count && this.notice.invoice.inbox.count > 0) 
			inboxCount = this.notice.invoice.inbox.count;

		if(this.notice.invoice && this.notice.invoice.rejected && this.notice.invoice.rejected.count && this.notice.invoice.rejected.count > 0) 
			rejectedCount = this.notice.invoice.rejected.count;

		application.updateSidenavCount('PendingInvoices', inboxCount);
		application.updateSidenavCount('RejectedInvoices', rejectedCount);
	}
	
	build() {
		let parentDiv = this.createDiv();
		parentDiv.className = CSS.AON_PARENT_DIV;
		this.appendChild(parentDiv);
		
		let welcomeDiv = this.createDiv();
		welcomeDiv.className = CSS.AON_WELCOME_DIV;
		let welcomeSpan = this.createSpan();
		welcomeSpan.innerHTML = MSG.WELCOME_TO_AON_SOLUTIONS;
		welcomeSpan.style.fontSize = '24px';
		welcomeSpan.style.fontWeight = '600';
		welcomeDiv.appendChild(welcomeSpan);
	
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
	
		let filterOptions = [{
				id: 'active',
				name: MSG.ACTIVES,
				icon: 'domain',
				fn: () => this.select({id:'active', active: true, domainActive:true})
			}, {
				id: 'inactive',
				name: MSG.INACTIVES,
				icon: 'domain_disabled',
				fn: () => this.select({id: 'inactive', inactive: true, domainActive:false})
			}, {
				id: 'shared',
				name: MSG.SHARED,
				icon: MATERIAL_ICONS.SHARE,
				fn: () => this.select({id:'shared', shared: true})
			}, {
				id: 'consultancy',
				name: MSG.ENVIRONMENT,
				icon: MATERIAL_ICONS.APARTMENT,
				fn: () => this.select({id:'consultancy', entorno:true, type:"CONSULTANCY"})
			},{
				id: 'office',
				name: MSG.OFFICE,
				icon: 'work',
				fn: () => this.select({id: 'office', despacho:true, type:"OFFICE"})
			}
		];	
	
		for( let filterOption of filterOptions ){
			let companyFilterTabA = this.createElement(TAG.A);
			companyFilterTabA.className = CSS.AON_TAB_ITEM;
			companyFilterTabA.addEventListener(EVENT.CLICK, (ev) => filterOption.fn(ev) );
	
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
	
		parentDiv.appendChild(welcomeDiv);
		parentDiv.appendChild(companyDiv);		
	
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
			this.buildCompanies(companies.filter(f => this.companyFilter(f, this.filter)).slice(first, first + 30));	
		})
		.finally(()=>{
			//TODO: this.getApplication().stopLoader();
		});
	}

	cleanCompanies(){
		let ul = this.getElement("UlCompanies");
		while ( ul.firstChild ) {
			ul.removeChild(ul.lastChild);
		}
	}

	buildCompanies(companies){
		let ul = this.getElement("UlCompanies");
		for(let company of companies) {
			ul.appendChild(this.buildLi(company, 'transparent'));
		}
	}


	buildLi(company, color) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonLiBeta' ;
		li.addEventListener(EVENT.CLICK, () => {
			this.companySelection(company, false);
			let portal = LS.isLeftMenu();
			LS.setPortalChecked(portal);
		});

		let span = this.createElement(TAG.SPAN);
		span.className = 'aonLiSpan';


		let icon = "business";
		if(company.type === 'OFFICE') icon = 'work';
		else if(company.parent) icon = MATERIAL_ICONS.APARTMENT;
		else if(company.shared) icon = MATERIAL_ICONS.SHARE;
		else if(!company.active) icon = 'domain_disabled';

		let i = this.createElement(TAG.I);
		i.className = 'material-icons aonAvatar';
		i.innerHTML = icon;

		let span2 = this.createElement(TAG.SPAN);
		span2.innerHTML = company.name;

		let span3 = this.createElement(TAG.SPAN);
		span3.className = 'aonLiSpanSubtitle';
		span3.innerHTML = company.document;

		span.appendChild(i);
		span.appendChild(span2);
		span.appendChild(span3);
		li.appendChild(span);

		let sp = this.createElement(TAG.SPAN);
		let i2 = this.createElement(TAG.I);
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = MATERIAL_ICONS.KEYBOARD_ARROW_RIGHT;
		sp.appendChild(i2);
		li.appendChild(sp);
		return li;
	}

	companySelection(company, onlyOne) {
		const BASE_ID = 'aonHeader';
		localStorage.setItem('company', JSON.stringify(company));
		localStorage.setItem("aon_domain_id", company.id);
		localStorage.setItem("aon_domain_name", company.domain);
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
		
		this.clearElementById('aonMenu');
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
}


if(!window.customElements.get(TAG.AON_PARENT)){
	console.log( 'Define <aon-new-parent> ^-^' );
	window.customElements.define(TAG.AON_PARENT, AonParent);
}
