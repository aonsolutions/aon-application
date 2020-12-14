import {AonElement} from '../../components/AonElement.js';
import {getCompanies, getDomainNotice, getUserNotice} from  '../../services/service.js';
import {rootPanel} from '../../services/gwtLoader.js';

import '../../components/aon-icon.js';
import '../../components/aon-application.js';
import '../../components/aon-suggestion.js';

import '../signin/aon-sign.js';
import '../invoice/aon-invoice-panel.js';

import * as MSG from "../../environments/msg.js";

export class AonMobileDesktop extends AonElement {

	SUGGESTION;

	static get observedAttributes() {
		return ['company'];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	attributeChangedCallback(name, oldValue, newValue) {

	}

	constructor () {
		super();
		this.id = 'aonDesktop';
		this.SUGGESTION = this.id + 'Suggestion';
	}

	connectedCallback () {
		this.build();
  }

	build() {
		this.innerHTML = '';
		if(localStorage.getItem('company')) {
			let company = JSON.parse(localStorage.getItem('company'));
			localStorage.setItem('aon_domain_id', company.id);
			localStorage.setItem('aon_domain_name', company.domain);
			getDomainNotice().then(notice => {
				this.buildNotifications(notice);
			});
		} else {
			getUserNotice().then(notice => {
				this.buildNotifications(notice);
			});
		}
	}

	buildNotifications(notice) {
		let searchDiv = document.createElement('div');
		searchDiv.id = 'aonHeaderCompany';
		this.appendChild(searchDiv)
		searchDiv.innerHTML = `<aon-suggestion id="${this.SUGGESTION}" title="Búsqueda Empresas"></aon-suggestion>`;

		let searchSuggestion = this.getElement(this.SUGGESTION);

		if(localStorage.getItem('company')) {
			let company = JSON.parse(localStorage.getItem('company'));
			searchSuggestion.title = 'Empresa Seleccionada';
			searchSuggestion.value = company.name;
			searchSuggestion.readonly = true;
		}


		document.addEventListener('click', function(event) {
			let sg = document.getElementById(this.SUGGESTION);
			if(sg) {
				let isClickInside = sg.contains(event.target);
				if(!isClickInside){
					if(localStorage.getItem('company')) {
						sg.title = 'Empresa Seleccionada';
						sg.value = JSON.parse(localStorage.getItem('company')).name;
						sg.readonly = true;
					}
				}
			}
		});

		searchSuggestion.addIconButton('search', () => {
			searchSuggestion.title = 'Búsqueda Empresas';
			searchSuggestion.readonly = false;
			searchSuggestion.value = '';
			this.getElement(searchSuggestion.INPUT).focus();
		});
		searchSuggestion.addEventListener('keyup', () => {
			if(searchSuggestion.value.length > 2) {
				getCompanies().then( companies => searchSuggestion
					.buildOptions(companies.filter(f => this.companyFilter(f, {value: searchSuggestion.value})))
				);
			} else {
				searchSuggestion.closeOptions();
			}
		});

		searchSuggestion.addEventListener('select', (event) => {
				searchSuggestion.title = 'Empresa Seleccionada';
			let company = event.detail;
			searchSuggestion.setAttribute('readonly', true);
			localStorage.setItem('company', JSON.stringify(company));
			localStorage.setItem("aon_domain_id", company.id);
			localStorage.setItem("aon_domain_name", company.domain);
			this.build();
		});

		let div = document.createElement('div');
		div.style.paddingBottom = '25px';
		div.style.borderBottom = '1px solid #ebebeb';
		this.appendChild(div);

		let titleA = document.createElement('div');
		titleA.className = 'aonSidenavTitle';
		titleA.innerHTML = 'TAREAS PENDIENTES';
		div.appendChild(titleA);

		let ul = document.createElement('ul');
		ul.className = 'aonClip';
		div.appendChild(ul);

		let inboxCount = 0;
		if(notice.invoice && notice.invoice.inbox && notice.invoice.inbox.count && notice.invoice.inbox.count > 0) {
			inboxCount = notice.invoice.inbox.count;
		}

		let rejectedCount = 0;
		if(notice.invoice && notice.invoice.rejected && notice.invoice.rejected.count && notice.invoice.rejected.count > 0) {
			rejectedCount = notice.invoice.rejected.count;
		}

		ul.appendChild(this.buildNotificationsLi('Documentos sin leer', 'snippet_folder', 0, () => {}));
		ul.appendChild(this.buildNotificationsLi('Notificaciones', 'notifications', 0, () => {}));
		ul.appendChild(this.buildNotificationsLi(MSG.AON_MSG_PENDING_INVOICES, 'inbox', inboxCount, () => {
			if(inboxCount > 0) {
				rootPanel('<aon-invoice-panel></aon-invoice-panel>');
			}
		}));
		ul.appendChild(this.buildNotificationsLi(MSG.AON_MSG_REJECTED_INVOICES, 'report', rejectedCount, () => {
			if(rejectedCount > 0) {
				rootPanel('<aon-invoice-panel status="refused"></aon-invoice-panel>');
			}
		}));
		ul.appendChild(this.buildNotificationsLi('Solicitudes Abiertas', 'assignment', 0, () => {}));
		ul.appendChild(this.buildNotificationsLi('Solicitudes para ti', 'assignment_ind', 0, () => {}));

		let div2 = document.createElement('div');

		let titleB = document.createElement('div');
		titleB.className = 'aonSidenavTitle';
		titleB.innerHTML = 'CONTROL DE HORARIO';
		div2.appendChild(titleB);

		this.appendChild(div2);

		let div3 = document.createElement('div');
		div3.style.marginLeft = '25px';
		div3.innerHTML = '<aon-sign></aon-sign>'
		this.appendChild(div3);
	}

	buildNotificationsLi(name, icon, count, fn) {
		let li = document.createElement('li');
		li.className = 'aonAppMenuSidenavList aonOpacity';
		li.style.height = '40px';
		li.style.lineHeight = '40px';
		li.style.borderBottom = '1px solid #ddd';

		let i = document.createElement('i');
		i.className = 'material-icons aonVerticalMiddle';
		i.innerHTML = icon;
		li.appendChild(i);

		let span = document.createElement('span');
		span.className = 'aonMenuItemSpan';
		if(count > 0) {
			span.innerHTML = name + ' (' + count + ')';
			span.style.fontWeight = 'bold';
		} else span.innerHTML = name;

		li.appendChild(span);

		let sp = document.createElement('span');
		sp.style.position = 'absolute';
		sp.style.right = '0px';

		let i2 = document.createElement('i');
		i2.className = 'material-icons aonAvatar';
		i2.innerHTML = 'keyboard_arrow_right';
		sp.appendChild(i2);
		li.appendChild(sp);

		li.addEventListener('click', () => fn());
		return li;
	}

	companyFilter(f, q) {
		if(!q) {
			q = {
				inactive: false,
				active: true,
				shared: true
			};
		}
		let value = true;
		if(q && q.value) {
			const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
			const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
			value = document || name;
		}

		if(q && q.active && !q.inactive) {
			value = f.active && value;
		}

		if(q && q.inactive && !q.active) {
			value = !f.active && value;
		}

		if(q && q.shared) {
			// TODO
		}

		return value;
	}

}

window.customElements.define('aon-mobile-desktop', AonMobileDesktop);
