import {AonElement} from '../../components/AonElement.js';
import {closeSession, getCompanies, getUserNotice, getUser, getTimeControl} from  '../../services/service.js';
import { EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import '../../components/aon-application.js';
import {AonSign} from '../signin/aon-sign.js';
import './aon-desktop.js';

export class AonParent extends AonElement {

	companies;
	selected;
	notice;
	constructor () {
		super();
		this.id = 'aonParent';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonParentMain" title="Parent" main="true"></aon-application>
		`;

		this.buildSidenav();
		this.init();
		let searchBox = this.getElement('aonHeaderSearchBox');
		searchBox.addEventListener(EVENT.KEYUP, () => {
			this.init({value: searchBox.value});
		});
	}

	buildSidenav() {
		let aonParent = this.getElement('aonParentMain');
		
		let taskOptions = [{
				name: MSG.PENDING_INVOICES,
				icon: 'inbox',
				fn: (count) => {
					if(this.notice &&count > 0) {
						this.init({ids: this.notice.invoice.inbox.domains})
					}
				}
			}, {
				name: MSG.REJECTED_INVOICES,
				icon: 'report',
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
		aonParent.addSidenavOptions(MSG.PENDING_TASKS.toUpperCase(), taskOptions);
		
		let filterOptions = [{
				name: MSG.ACTIVE,
				icon: 'domain',
				fn: () => this.init({active: true})
			}, {
				name: MSG.INACTIVE,
				icon: 'domain_disabled',
				fn: () => this.init({inactive: true})
			}, {
				name: MSG.SHARED,
				icon: 'share',
				fn: () => this.init({shared: true})
			}, {
				name: MSG.ENVIRONMENT,
				icon: 'apartment',
				fn: () => this.init({entorno:true})
			},{
				name: MSG.OFFICE,
				icon: 'work',
				fn: () => this.init({despacho:true})
			}
		];
		
		aonParent.addSidenavOptions(MSG.FILTERS.toUpperCase(), filterOptions);

		getTimeControl({parent: true}).then(r => {
			let aonSign = new AonSign();
			aonSign.setParent(true);
			aonSign.setTimeControl(r);
			aonParent.addSidenavWidget('CONTROL HORARIO', aonSign);
			let aonHeader = this.getElement('aonHeader');
			aonHeader.timeControlStatus(r);
		});

		this.getNotices();
	}

	init(filter) {
		let aonParent = this.getElement('aonParentMain');
		if(aonParent){
			aonParent.startLoader();
			this.build();
			getCompanies()
			.then( companies => {
				aonParent.stopLoader();
				if(companies.length ===1){
					this.companySelection(companies[0], true);
				} else {
					this.buildCompanies(companies.filter(f => this.companyFilter(f, filter)));
				}
		  	}, () => closeSession());
		}
   }

	companyFilter(f, q) {
		if(!q) {
			q = {
				active: true
			};
		}
		let value = true;
		if(q && q.value) {
			const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
			const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
			value = document || name;
		}

		if(q && q.active) {
			value = f.active && (f.parentId || f.type !== 'CONSULTANCY');
		}

		if(q && q.inactive) {
			value = !f.active;
		}

		if(q && q.shared) {
			value = f.shared;
		}

		if(q && q.entorno) {
			value = !f.parentId && f.type === 'CONSULTANCY';
		}

		if(q && q.despacho) {
			value = f.type === 'OFFICE';
		}

		if(q && q.ids) {
			let idFilter;
			q.ids.forEach((item, i) => {
				 idFilter = f.id == item || idFilter;
			});
			value = idFilter;
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

		application.updateSidenavCount(MSG.PENDING_INVOICES, inboxCount);
		application.updateSidenavCount(MSG.REJECTED_INVOICES, rejectedCount);
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

		let ul = this.createElement('ul');
		ul.id = "UlCompanies";
		ul.className = 'list-group';
		ul.style.marginLeft = '20px';
		ul.style.marginRight = '20px';
		content.appendChild(ul);
		aonParent.setContent(content);
	}

	buildCompanies(companies){
		let ul = this.getElement("UlCompanies");
		companies.map(company=>{
			ul.appendChild(this.buildLi(company, 'transparent'));
		})
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
			this.rootPanelHtml('<aon-desktop id="aonDesktop"></aon-desktop>');
		});
	}
}
if(!window.customElements.get('aon-parent')){
	window.customElements.define('aon-parent', AonParent);
}
