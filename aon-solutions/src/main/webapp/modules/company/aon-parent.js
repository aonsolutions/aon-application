import {AonElement} from '../../components/AonElement.js';
import {closeSession, getCompanies, getUserNotice, getUser, getTimeControl, getCompaniesBySchemas} from  '../../services/service.js';
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import {AonSign} from '../timecontrol/aon-sign.js';
import { AonApplication } from '../../components/aon-application.js';
import { AonDesktop } from './aon-desktop.js';

export class AonParent extends AonElement {

	companies;
	selected;
	notice;
	filter;
	_filter; // NEW FILTER

	more;

	setFilter(filter){
		if(filter.ids && filter.ids.length) filter.ids = filter.ids.join(",");
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
	}

	connectedCallback () {
		this.createApplication("aonParentMain", "Parent", new AonApplication(), true);

		this.buildSidenav();
		this.init();
		let searchBox = this.getElement('aonHeaderSearchBox');
		searchBox.addEventListener(EVENT.KEYUP, () => {
			this.init({value: searchBox.value});
			this.addFilter({value:searchBox.value});
		});
	}

	buildSidenav() {
		let aonParent = this.getElement('aonParentMain');
		let taskOptions = [{
				id: 'PendingInvoices',
				name: MSG.PENDING_INVOICES,
				icon: MATERIAL_ICONS.INBOX,
				fn: (count) => {
					if(this.notice &&count > 0) {
						this.init({ids: this.notice.invoice.inbox.domains})
					}
				}
			}, {
				id: 'RejectedInvoices',
				name: MSG.REJECTED_INVOICES,
				icon: MATERIAL_ICONS.REPORT,
				fn: (count) => {
					if(this.notice && count > 0) {
						this.init({ids: this.notice.invoice.rejected.domains})
					}
				}
			}, {
				name: MSG.REQUESTS_RECEIVED,
				icon: MATERIAL_ICONS.MOVE_TO_INBOX,
				fn: () => {}
			}, {
				name: MSG.REQUESTS_SENT,
				icon: MATERIAL_ICONS.OUTBOX,
				fn: () => {}
			}
		];
		aonParent.addSidenavOptions(MSG.ACTIVITY_SUMMARY.toUpperCase(), taskOptions);
		
		let filterOptions = [{
				name: MSG.ACTIVES,
				icon: 'domain',
				fn: () => this.init({active: true, domainActive:true})
			}, {
				name: MSG.INACTIVES,
				icon: 'domain_disabled',
				fn: () => this.init({inactive: true, domainActive:false})
			}, {
				name: MSG.SHARED,
				icon: 'share',
				fn: () => this.init({shared: true})
			}, {
				name: MSG.ENVIRONMENT,
				icon: 'apartment',
				fn: () => this.init({entorno:true, type:"CONSULTANCY"})
			},{
				name: MSG.OFFICE,
				icon: 'work',
				fn: () => this.init({despacho:true, type:"OFFICE"})
			}
		];
		
		aonParent.addSidenavOptions(MSG.FILTERS.toUpperCase(), filterOptions);

		getTimeControl({parent: true}).then(r => {
			let aonSign = new AonSign();
			aonSign.setParent(true);
			aonSign.setTimeControl(r);
			aonParent.addSidenavWidget(MSG.TIMECONTROL.toUpperCase(), aonSign);
			let aonHeader = this.getElement('aonHeader');
			aonHeader.timeControlStatus(r);
		});

		this.getNotices();
	}

	init(filter) {
		this.filter = filter;

		let aonParent = this.getElement('aonParentMain');
		
		if(aonParent){
			aonParent.startLoader();
			this.build();
			getCompanies()
			.then( companies => {
				aonParent.stopLoader();
				if(companies.length === 1){
					this.companySelection(companies[0], true);
				} else {
					this.page = 1;
					this.buildCompanies(companies.filter(f => this.companyFilter(f, filter)).slice(0, 30));
				}
		  	}, () => closeSession());
		}

		if(filter){
			this.setFilter(filter);
		}
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
				q.ids.forEach((item, i) => {
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
		let aonParent = this.getElement('aonParentMain');
		let content = this.createElement(TAG.DIV);
		let div = this.createElement(TAG.DIV);
		div.style.borderBottom = '1px solid #5f6368';
		div.style.marginTop = '15px';
		div.style.marginLeft = '20px';
		div.style.marginRight = '20px';
		div.style.paddingBottom = '10px';
		div.style.paddingLeft = '16px';
		div.innerHTML = MSG.COMPANIES.toUpperCase();
		content.appendChild(div);
		

		let ul = this.createElement(TAG.UL);
		ul.id = "UlCompanies";
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.NO_SCROLLBAR);
		ul.style.marginLeft = '20px';
		ul.style.marginRight = '20px';
		ul.style.height = 'calc(100vh - 104px)';
		ul.style.overflowY = 'auto';
		content.appendChild(ul);
		ul.addEventListener("scroll", () => {
			let scrollTop = ul.scrollTop;
			let offsetHeight = ul.offsetHeight;
			let physicalSize = ul.scrollHeight;
			let maxScrollPosition = physicalSize - offsetHeight;

			if (scrollTop >= maxScrollPosition) {
				this.loadMore();
			}
		});
		aonParent.setContent(content);
	}

	loadMore() {
		this.getApplication().startLoader();
		getCompanies().then( companies => {
			this.getApplication().stopLoader();
			let first = this.page * 30;
			this.page = this.page + 1;
			this.buildCompanies(companies.filter(f => this.companyFilter(f, this.filter)).slice(first, first + 30));	
		});
	}

	buildCompanies(companies){
		let ul = this.getElement("UlCompanies");
		for(let company of companies) {
			ul.appendChild(this.buildLi(company, 'transparent'));
		}
	}

	buildLi(company, color) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonLi';
		li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
		li.addEventListener(EVENT.CLICK, () => {
			this.companySelection(company, false);
		});

		li.addEventListener(EVENT.MOUSEOVER, () => {
			li.style.backgroundColor = '#ddd';
		});

		li.addEventListener(EVENT.MOUSELEAVE, () => {
			li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
		});

		let span = this.createElement(TAG.SPAN);
		span.className = 'aonLiSpan';

		let i = this.createElement('i');
		i.className = 'material-icons aonAvatar';

		
		if(company.type === 'OFFICE') i.innerHTML = 'work';
		else if(company.parent) i.innerHTML = 'apartment';
		else if(company.shared) i.innerHTML = 'share';
		else if(!company.active) i.innerHTML = 'domain_disabled';
		else i.innerHTML = 'business';
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
		let i2 = this.createElement('i');
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = 'keyboard_arrow_right';
		sp.appendChild(i2);
		li.appendChild(sp);
		return li;
	}

	companySelection(company, onlyOne) {
		const BASE_ID = 'aonHeader';
		localStorage.setItem('company', JSON.stringify(company));

		if(company.parentId || company.type !== 'CONSULTANCY'){
			let aonShowMenu = this.getElement('aonShowMenu');
			aonShowMenu.style.display = 'block';
		}
		let aonHeaderHelp = this.getElement(BASE_ID + 'Help');
		aonHeaderHelp.style.display = 'block';

		let aonHeaderSearch = this.getElement(BASE_ID + 'Search');
		aonHeaderSearch.style.display = 'none';

		let aonHeaderHome = this.getElement(BASE_ID + 'Home');
		aonHeaderHome.style.display = 'block';

		let aonHeaderCompanyName = this.getElement(BASE_ID + 'CompanyName');
		aonHeaderCompanyName.innerHTML = company.name;

		let aonHeaderCompany = this.getElement(BASE_ID + 'Company');
		aonHeaderCompany.style.display = 'block';

		if(!onlyOne){ 
			let aonHeaderCompanyList = this.getElement(BASE_ID + 'CompanyList');
			aonHeaderCompanyList.style.display = 'block';
		} else {
			aonHeaderHome.style.right = '140px';
			aonHeaderCompany.style.right = '180px';
		}
		
		localStorage.setItem("aon_domain_id", company.id);
		localStorage.setItem("aon_domain_name", company.domain);

		this.clearElementById('aonMenu');
		let aonMenu = this.getElement('aonMenu');
		aonMenu.init();

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
	}
}
if(!window.customElements.get('aon-parent')){
	window.customElements.define('aon-parent', AonParent);
}
