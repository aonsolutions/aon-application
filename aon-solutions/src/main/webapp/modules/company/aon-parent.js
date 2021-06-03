import {AonElement} from '../../components/AonElement.js';
import {closeSession, getUserAppRole, getCompanies, getUserNotice, getUser, getTimeControl} from  '../../services/service.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js';
import '../../components/aon-application.js';
import '../signin/aon-sign.js';
import './aon-desktop.js';

export class AonParent extends AonElement {

	companies;
	selected;

	constructor () {
		super();
		this.id = 'aonParent';
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonParentMain" title="Parent" main="true"></aon-application>
		`;

		getUserNotice().then(r => {
			this.buildSidenav(r);
			this.init();
		});

		let searchBox = this.getElement('aonHeaderSearchBox');
		searchBox.addEventListener(EVENT.KEYUP, () => {
			this.init({value: searchBox.value});
		});
	}

	buildSidenav(notice) {
		let aonParent = this.getElement('aonParentMain');

		let inboxCount = 0;
		if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
			inboxCount = notice.invoice.inbox.count;
		}

		let rejectedCount = 0;
		if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
			rejectedCount = notice.invoice.rejected.count;
		}

		let taskOptions = [
			{
				name: 'Notificaciones',
				icon: 'notifications',
				fn: () => {}
			},{
				name: MSG.PENDING_INVOICES,
				count: inboxCount,
				icon: 'inbox',
				fn: () => {
					if(inboxCount > 0) {
						this.init({ids: notice.invoice.inbox.domains})
					}
				}
			}, {
				name: MSG.REJECTED_INVOICES,
				count: rejectedCount,
				icon: 'report',
				fn: () => {
					if(rejectedCount > 0) {
						this.init({ids: notice.invoice.rejected.domains})
					}
				}
			}, {
				name: 'Solicitudes Abiertas',
				icon: 'assignment',
				fn: () => {}
			}, {
				name: 'Solicitudes para ti',
				icon: 'assignment_ind',
				fn: () => {}
			}
		];
		aonParent.addSidenavOptions('TAREAS PENDIENTES', taskOptions);

		let filterOptions = [{
				name: 'Activas',
				icon: 'domain',
				fn: () => this.init({active: true})
			}, {
				name: 'Inactivas',
				icon: 'domain_disabled',
				fn: () => this.init({inactive: true})
			}, {
				name: 'Compartidas',
				icon: 'share',
				fn: () => this.init({shared: true})
			}, {
				name: 'Entorno',
				icon: 'apartment',
				fn: () => this.init({entorno:true})
			},{
				name: 'Despacho',
				icon: 'work',
				fn: () => this.init({despacho:true})
			}
		];
		aonParent.addSidenavOptions('FILTROS', filterOptions);

		getTimeControl().then(r => {
			aonParent.addSidenavWidgetHTML('CONTROL HORARIO','<aon-sign></aon-sign>');
			let aonHeader = this.getElement('aonHeader');
			aonHeader.timeControlStatus(r);
		});
	}

	init(filter) {
		let aonParent = this.getElement('aonParentMain');
		if(aonParent){
			aonParent.startLoader();
			getCompanies()
			.then( companies => {
					aonParent.stopLoader();
					if(companies.length ===1){
						this.companySelection(companies[0]);
					} else {
				  this.build(companies.filter(f => this.companyFilter(f, filter)));
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

 	build(companies) {
		let aonParent = this.getElement('aonParentMain');
		let content = this.createElement(TAG.DIV);
		let div = this.createElement(TAG.DIV);
		div.style.borderBottom = '1px solid #5f6368';
		div.style.marginTop = '15px';
		div.style.marginLeft = '20px';
		div.style.marginRight = '20px';
		div.style.paddingBottom = '10px';
		div.style.paddingLeft = '16px';
		div.innerHTML = 'EMPRESAS';
		content.appendChild(div);

		let ul = this.createElement('ul');
		ul.className = 'list-group';
		ul.style.marginLeft= '20px';
		ul.style.marginRight= '20px';

		for(let i = 0; i < companies.length; i++){
			ul.appendChild(this.buildLi(companies[i], 'transparent'));
		}
		content.appendChild(ul);
		aonParent.setContent(content);
	}

	buildLi(company, color) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonLi';
		li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
		li.addEventListener(EVENT.CLICK, () => {
			this.companySelection(company);
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

		if(company.parent) i.innerHTML = 'apartment';
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

	companySelection(company) {
		const BASE_ID = 'aonHeader';
		localStorage.setItem('company', JSON.stringify(company));

		let aonHeaderCompanyList = this.getElement(BASE_ID + 'CompanyList');
		aonHeaderCompanyList.style.display = 'block';

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

		let aonHeaderCompany = this.getElement(BASE_ID + 'Company');
		aonHeaderCompany.style.display = 'block';

		let aonHeaderCompanyName = this.getElement(BASE_ID + 'CompanyName');
		aonHeaderCompanyName.innerHTML = company.name;

		localStorage.setItem("aon_domain_id", company.id);
		localStorage.setItem("aon_domain_name", company.domain);

		this.clearElementById('aonMenu');
		let aonMenu = this.getElement('aonMenu');
		aonMenu.init();

		getUser().then(user => {
			localStorage.setItem('aon_domain_login', user.login);
			getUserAppRole().then(user => {
				let aonHeader = this.getElement('aonHeader');
				aonHeader.setAttribute('company', JSON.stringify(company));
				aonHeader.setAttribute('user', JSON.stringify(user));

				this.rootPanelHtml('<aon-desktop id="aonDesktop"></aon-desktop>');
				let aonDesktop = this.getElement('aonDesktop');
				aonDesktop.setAttribute('company', JSON.stringify(company));
				aonDesktop.setAttribute('user', JSON.stringify(user));
			});
		});
	}
}
if(!window.customElements.get('aon-parent')){
	window.customElements.define('aon-parent', AonParent);
}
